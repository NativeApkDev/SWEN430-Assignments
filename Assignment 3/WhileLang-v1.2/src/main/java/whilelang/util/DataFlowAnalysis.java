// This file is part of the WhileLang Compiler (wlc).
//
// The WhileLang Compiler is free software; you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// The WhileLang Compiler is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE. See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public
// License along with the WhileLang Compiler. If not, see
// <http://www.gnu.org/licenses/>
//
// Copyright 2013, David James Pearce.
package whilelang.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static whilelang.util.SyntaxError.internalFailure;
import whilelang.ast.Stmt;
import whilelang.ast.WhileFile;
import whilelang.ast.Expr;

/**
 * <p>
 * An abstract class designed to simply the development of <i>data flow
 * analyses</i>. Such analyses compute information over the Control-Flow Graph
 * of a program by combining information at <i>join points</i> in the CFG. A
 * good example is computing <i>definite assignment</i> for the following
 * program:
 * </p>
 *
 * <pre>
 * int f(int x) {
 *    int z;
 *    if x > 0 {
 *      z = 1;
 *    }
 *    return z;
 * }
 * </pre>
 *
 * <p>
 * The definite assignment analysis propagates information about which variables
 * have been definitely assignmedn on all paths leading to a given statement. It
 * is a conservative analysis which ignores <i>unreachable paths</i>. At meet
 * points in the CFG it computes the intersection of definite assignment
 * information as, for a variable to be definitely assigned at a given point, it
 * must be definitely assigned on all paths leading to that point.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class DataFlowAnalysis<T extends DataFlowAnalysis.Information<T>> {
	/**
	 * The file being analysed.
	 */
	private final WhileFile file;
	/**
	 * The transfer function being used for the analysis.
	 */
	private final TransferFunction<T> fn;

	public DataFlowAnalysis(WhileFile file, TransferFunction<T> fn) {
		this.file = file;
		this.fn = fn;
	}

	/**
	 * Check a given source file.
	 *
	 * @param wf The source file to be checked.
	 */
	public void apply() {
		for (WhileFile.Decl declaration : file.declarations) {
			if (declaration instanceof WhileFile.MethodDecl) {
				WhileFile.MethodDecl md = (WhileFile.MethodDecl) declaration;
				T data = fn.initialise(md);
				fn.apply(md.getBody(), data);
			}
		}
	}

	/**
	 * <p>
	 * Represents an abstract function that transforms information from across a
	 * given statement. For example, in the definite assignment analysis this
	 * updates the definitely assigned sets based on the given statement.
	 * <p>
	 * <p>
	 * Whilst this is largely abstract, it does handle some tricky aspects related
	 * to control-flow automatically for us.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static abstract class TransferFunction<T extends Information<T>> {
		/**
		 * Identifies the enclosing source file, which is helpful for reporting error
		 * messages.
		 */
		protected final Path source;

		public TransferFunction(Path source) {
			this.source = source;
		}

		public Flow<T> apply(Stmt stmt, T data) {
			if (stmt instanceof Stmt.Assert) {
				return apply((Stmt.Assert) stmt, data);
			} else if (stmt instanceof Stmt.Assign) {
				return apply((Stmt.Assign) stmt, data);
			} else if (stmt instanceof Stmt.Break) {
				return apply((Stmt.Break) stmt, data);
			} else if (stmt instanceof Stmt.Continue) {
				return apply((Stmt.Continue) stmt, data);
			} else if (stmt instanceof Stmt.Delete) {
				return apply((Stmt.Delete) stmt, data);
			} else if (stmt instanceof Stmt.DoWhile) {
				return apply((Stmt.DoWhile) stmt, data);
			} else if (stmt instanceof Stmt.Return) {
				return apply((Stmt.Return) stmt, data);
			} else if (stmt instanceof Stmt.VariableDeclaration) {
				return apply((Stmt.VariableDeclaration) stmt, data);
			} else if (stmt instanceof Expr.Invoke) {
				return apply((Expr.Invoke) stmt, data);
			} else if (stmt instanceof Stmt.IfElse) {
				return apply((Stmt.IfElse) stmt, data);
			} else if (stmt instanceof Stmt.For) {
				return apply((Stmt.For) stmt, data);
			} else if (stmt instanceof Stmt.While) {
				return apply((Stmt.While) stmt, data);
			} else if (stmt instanceof Stmt.Switch) {
				return apply((Stmt.Switch) stmt, data);
			} else {
				internalFailure("unknown statement encountered (" + stmt + ")", source, stmt);
				return null;
			}
		}

		public Flow<T> apply(Stmt.Assert stmt, T data) {
			data = transfer(stmt, data);
			return new Flow<T>(data, null, null);
		}

		public Flow<T> apply(Stmt.Assign stmt, T data) {
			data = transfer(stmt, data);
			return new Flow<T>(data, null, null);
		}

		public Flow<T> apply(Stmt.Break stmt, T data) {
			return new Flow<T>(null, data, null);
		}

		public Flow<T> apply(Stmt.Continue stmt, T data) {
			return new Flow<T>(null, null, data);
		}

		public Flow<T> apply(Stmt.Delete stmt, T data) {
			data = transfer(stmt, data);
			return new Flow<T>(data, null, null);
		}

		public Flow<T> apply(Stmt.DoWhile stmt, T data) {
			Flow<T> f1 = apply(stmt.getBody(), data);
			T loop = join(f1.nextFlow, f1.continueFlow);
			Flow<T> f2 = loopFixPoint(stmt.getCondition(), stmt.getBody(), loop);
			T next = join(f1.breakFlow, f2.nextFlow);
			return new Flow<>(next, null, null);
		}

		public Flow<T> apply(Stmt.For stmt, T data) {
			// Apply effect of variable declation on incoming flow
			data = apply(stmt.getDeclaration(), data).nextFlow;
			//
			ArrayList<Stmt> body = new ArrayList<>(stmt.getBody());
			body.add(stmt.getIncrement());
			// Reuse fix point computation
			return loopFixPoint(stmt.getCondition(), body, data);
		}

		public Flow<T> apply(Stmt.IfElse stmt, T data) {
			// Transfer across the condition
			data = transfer(stmt.getCondition(), data);
			// Transfer across true/false branch
			Flow<T> left = apply(stmt.getTrueBranch(), data);
			Flow<T> right = apply(stmt.getFalseBranch(), data);
			// Combine together
			return left.merge(right);
		}

		public Flow<T> apply(Expr.Invoke stmt, T data) {
			data = transfer(stmt, data);
			return new Flow<T>(data, null, null);
		}

		public Flow<T> apply(Stmt.Return stmt, T data) {
			// Transfer across return value!
			if(stmt.getExpr() != null) {
				transfer(stmt.getExpr(), data);
			}
			return new Flow<>(null,null,null);
		}

		public Flow<T> apply(Stmt.Switch stmt, T data) {
			List<Stmt.Case> cases = stmt.getCases();
			data = transfer(stmt.getExpr(), data);
			T nextFlow = data;
			T breakFlow = null;
			T contFlow = null;
			for (int i = 0; i != cases.size(); ++i) {
				Stmt.Case ith = cases.get(i);
				Flow<T> ithflow = apply(ith.getBody(), data);
				breakFlow = join(breakFlow, ithflow.breakFlow);
				contFlow = join(contFlow, ithflow.continueFlow);
				if (ith.isDefault()) {
					nextFlow = ithflow.nextFlow;
				}
			}
			nextFlow = join(breakFlow, nextFlow);
			//
			return new Flow<>(nextFlow, null, contFlow);
		}

		public Flow<T> apply(Stmt.VariableDeclaration stmt, T data) {
			data = transfer(stmt, data);
			return new Flow<T>(data, null, null);
		}

		public Flow<T> apply(Stmt.While stmt, T data) {
			return loopFixPoint(stmt.getCondition(),stmt.getBody(),data);
		}

		/**
		 * Iterate a While loop to a fix point. This function is separated out so that
		 * it can be reused for all loop types.
		 *
		 * @param condition
		 * @param body
		 * @param data
		 * @return
		 */
		private Flow<T> loopFixPoint(Expr condition, List<Stmt> body, T data) {
			Flow<T> init = new Flow<>(data, null, null);
			// iterate until fixpoint reached
			Flow<T> p = fixPoint(init, X -> {
				// Apply effect of condition on X
				T tmp = transfer(condition, X.nextFlow);
				// Apply effect of loop body on X
				Flow<T> flow = apply(body, tmp);
				// Compute the carried loop flow
				T loopFlow = join(flow.nextFlow, flow.continueFlow);
				// Extract break flow
				T breakFlow = flow.breakFlow;
				// Combine continue and next flow
				return new Flow<>(join(data, loopFlow), breakFlow, null);
			});
			// Combine next and break flow to compute the flow that can follow after this
			// loop has completed.
			T next = join(p.nextFlow, p.breakFlow);
			// Done
			return new Flow<>(next, null, null);
		}

		/**
		 * Apply this transfer function across a sequence of zero or more statements.
		 *
		 * @param stmts
		 * @param data
		 * @return
		 */
		public Flow<T> apply(List<Stmt> stmts, T data) {
			T nextFlow = data;
			T breakFlow = null;
			T contFlow = null;
			//
			for(int i=0;i!=stmts.size();++i) {
				Flow<T> flow = apply(stmts.get(i), nextFlow);
				nextFlow = flow.nextFlow;
				breakFlow = join(breakFlow, flow.breakFlow);
				contFlow = join(contFlow, flow.continueFlow);
			}
			//
			return new Flow<>(nextFlow,breakFlow,contFlow);
		}

		/**
		 * Initialise the flow set for a given method declaration.
		 *
		 * @param decl
		 * @return
		 */
		public abstract T initialise(WhileFile.MethodDecl decl);

		/**
		 * Transfer a given piece of information across across an <i>assertion</i>.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Stmt.Assert stmt, T data) {
			return transfer(stmt.getExpr(), data);
		}

		/**
		 * Transfer a given piece of information across across an <i>assignment</i>.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Stmt.Assign stmt, T data) {
			data = transfer(stmt.getLhs(), data);
			return transfer(stmt.getRhs(), data);
		}

		/**
		 * Transfer a given piece of information across across a <i>deallocation</i>.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Stmt.Delete stmt, T data)  {
			return transfer(stmt.getExpr(), data);
		}

		/**
		 * Transfer a given piece of information across across a <i>variable
		 * declaration</i>.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Stmt.VariableDeclaration stmt, T data)  {
			if(stmt.getExpr() != null) {
				return transfer(stmt.getExpr(), data);
			} else {
				return data;
			}
		}

		/**
		 * Transfer a given piece of information across across an arbitrary
		 * <i>expression</i>.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr expr, T data) {
			if (expr instanceof Expr.ArrayAccess) {
				return transfer((Expr.ArrayAccess) expr, data);
			} else if (expr instanceof Expr.ArrayGenerator) {
				return transfer((Expr.ArrayGenerator) expr, data);
			} else if (expr instanceof Expr.ArrayInitialiser) {
				return transfer((Expr.ArrayInitialiser) expr, data);
			} else if (expr instanceof Expr.Binary) {
				return transfer((Expr.Binary) expr, data);
			} else if (expr instanceof Expr.Cast) {
				return transfer((Expr.Cast) expr, data);
			} else if (expr instanceof Expr.Dereference) {
				return transfer((Expr.Dereference) expr, data);
			} else if (expr instanceof Expr.FieldDereference) {
				return transfer((Expr.FieldDereference) expr, data);
			} else if (expr instanceof Expr.Invoke) {
				return transfer((Expr.Invoke) expr, data);
			} else if (expr instanceof Expr.Is) {
				return transfer((Expr.Is) expr, data);
			} else if (expr instanceof Expr.Literal) {
				return transfer((Expr.Literal) expr, data);
			} else if (expr instanceof Expr.RecordAccess) {
				return transfer((Expr.RecordAccess) expr, data);
			} else if (expr instanceof Expr.RecordConstructor) {
				return transfer((Expr.RecordConstructor) expr, data);
			} else if (expr instanceof Expr.Unary) {
				return transfer((Expr.Unary) expr, data);
			} else if (expr instanceof Expr.Variable) {
				return transfer((Expr.Variable) expr, data);
			} else {
				internalFailure("unknown expression encountered (" + expr + ")", source, expr);
				return null;
			}
		}

		/**
		 * Transfer a given piece of information across across an <i>array access</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.ArrayAccess expr, T data) {
			T lhs = transfer(expr.getSource(), data);
			return transfer(expr.getIndex(), lhs);
		}

		/**
		 * Transfer a given piece of information across across an <i>array generator</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.ArrayGenerator expr, T data) {
			T lhs = transfer(expr.getValue(), data);
			return transfer(expr.getSize(), lhs);
		}

		/**
		 * Transfer a given piece of information across across an <i>array
		 * initialiser</i> expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.ArrayInitialiser expr, T data) {
			for(Expr e : expr.getArguments()) {
				data = transfer(e,data);
			}
			return data;
		}

		/**
		 * Transfer a given piece of information across across a <i>binary</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.Binary expr, T data) {
			T lhs = transfer(expr.getLhs(), data);
			return transfer(expr.getRhs(), lhs);
		}

		/**
		 * Transfer a given piece of information across across a <i>cast</i> expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.Cast expr, T data) {
			return transfer(expr.getSource(),data);
		}

		/**
		 * Transfer a given piece of information across across a <i>dereference</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.Dereference expr, T data) {
			return transfer(expr.getSource(),data);
		}

		/**
		 * Transfer a given piece of information across across a <i>field
		 * dereference</i> expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.FieldDereference expr, T data) {
			return transfer(expr.getSource(),data);
		}

		/**
		 * Transfer a given piece of information across across an <i>invoke</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.Invoke expr, T data) {
			for(Expr e : expr.getArguments()) {
				data = transfer(e,data);
			}
			return data;
		}

		/**
		 * Transfer a given piece of information across across a <i>runtime type
		 * test</i> expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.Is expr, T data) {
			return transfer(expr.getSource(),data);
		}

		/**
		 * Transfer a given piece of information across across a <i>literal</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.Literal expr, T data) {
			return data;
		}

		/**
		 * Transfer a given piece of information across across a <i>record access</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.RecordAccess expr, T data) {
			return transfer(expr.getSource(),data);
		}

		/**
		 * Transfer a given piece of information across across a <i>record
		 * constructor</i> expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.RecordConstructor expr, T data) {
			for (Pair<String, Expr> p : expr.getFields()) {
				data = transfer(p.second(), data);
			}
			return data;
		}

		/**
		 * Transfer a given piece of information across across a <i>unary</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.Unary expr, T data) {
			return transfer(expr.getExpr(),data);
		}

		/**
		 * Transfer a given piece of information across across a <i>variable</i>
		 * expression.
		 *
		 * @param stmt The statement on which the function is being applied.
		 * @param data Input data for the function.
		 * @return
		 */
		public T transfer(Expr.Variable expr, T data) {
			return data;
		}
	}

	/**
	 * A flow value is an internal structure used to manage the flow of information
	 * between statements in a method. In particular, it handles the possibility of
	 * flow from non-sequential branching out of a given construct (e.g. from a
	 * break statement within a loop up to the loop).
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	protected static class Flow<T extends Information<T>> {
		/**
		 * Represents the information that flows to the next statement in sequence.
		 */
		public final T nextFlow;
		/**
		 * Represents the information that flows to the enclosing break destination.
		 */
		public final T breakFlow;
		/**
		 * Represents the information that flows to the enclosing continue destination.
		 */
		public final T continueFlow;

		public Flow(T nextFlow, T breakFlow, T continueFlow) {
			this.nextFlow = nextFlow;
			this.breakFlow = breakFlow;
			this.continueFlow = continueFlow;
		}

		public Flow<T> merge(Flow<T> other) {
			T n = join(nextFlow,other.nextFlow);
			T b = join(breakFlow,other.breakFlow);
			T c = join(continueFlow, other.continueFlow);
			return new Flow<T>(n, b, c);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Flow) {
				Flow<?> f = (Flow<?>) o;
				return Objects.equals(nextFlow, f.nextFlow) && Objects.equals(breakFlow, f.breakFlow)
						&& Objects.equals(continueFlow, f.continueFlow);
			}
			return false;
		}

		@Override
		public int hashCode() {
			int r = 0;
			r += (nextFlow == null) ? 0 : nextFlow.hashCode();
			r += (breakFlow == null) ? 0 : breakFlow.hashCode();
			r += (continueFlow == null) ? 0 : continueFlow.hashCode();
			return r;
		}
	}

	/**
	 * An abstraction of the information computed by this dataflow analysis. The key
	 * property is that it must be <i>mergeable</i> in order that information can be
	 * combined at meet points in the control-flow graph.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public interface Information<T>{
		T merge(T other);
	}


	/**
	 * A simple helper to handle <code>null</code> flow safely.
	 *
	 * @param <S>
	 * @param left
	 * @param right
	 * @return
	 */
	private static <S extends Information<S>> S join(S left, S right) {
		if (left == null) {
			return right;
		} else if (right == null) {
			return left;
		} else {
			return left.merge(right);
		}
	}

	/**
	 * Iteratively compute the fixpoint of a given function <code>f</code> applied
	 * to an initial value <code>X</code>. That is, the point where
	 * <code>X = f(X)</code>.
	 *
	 * @param <T>
	 * @param X
	 * @param f
	 * @return
	 */
	private static <T> T fixPoint(T X, Function<T,T> f) {
		T last;
		do {
			last = X;
			X = f.apply(X);
		} while(!X.equals(last));
		// Done
		return X;
	}
}