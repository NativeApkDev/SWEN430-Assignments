package whilelang.compiler;

import java.util.HashMap;
import java.util.Map;

import whilelang.ast.Stmt;
import whilelang.ast.Expr;
import whilelang.ast.Type;
import whilelang.ast.WhileFile;
import whilelang.ast.WhileFile.MethodDecl;
import whilelang.util.DataFlowAnalysis;
import static whilelang.util.SyntaxError.syntaxError;

/**
 * <p>
 * Responsible for enforcing the ownership invariant for unique references. That
 * is, for a given reference <code>p</code> there is no other reference (unique
 * or otherwise) that refers to the same object. This analysis is assumed to run
 * <i>after</i> type checking as it makes use of the type attributes inferred by
 * that phase.
 * </p>
 *
 * <p>
 * This analysis is implemented in the style of a <i>dataflow analysis</i>
 * because it must determine a specific type for a given variable at each point
 * in the program. For example, consider this program:
 * </p>
 *
 * <pre>
 *  &int:1 p = new 123;
 *  assert *p == 123;
 *  delete p;
 * </pre>
 * <p>
 * After the first statement, the analysis determines the type of <code>p</code>
 * to be <code>&int:1</code> and this remains true after the second statement.
 * However, after the third statement, the analysis determines the current type
 * of <code>p</code> as <code>void</code>. This reflects the fact that
 * <code>p</code> has been <i>consumed</i> by the <code>delete</code> statement.
 * As another example, consider the following:
 * </p>
 *
 * <pre>
 *  type Vec is { int len, &(int[]):1 data }
 *
 *  Vec p = {len:0, data: new [1,2,3]};
 *  delete p.data;
 *  assert p.len == 0;
 * </pre>
 * <p>
 * This is a more complex example which illustrates <i>partial consumption</i>.
 * Specifically, the type of <code>p</code> is
 * <code>{int len, &(int[]):1 data}</code> after the initialiser. However, after
 * the <code>delete</code> statement this becomes
 * <code>{int len, void data}</code> which reflects the fact that
 * <code>p.data</code> has been consumed. Since not all of <code>p</code> was
 * consumed we say that <code>p</code> has been <i>partially consumed</i>.
 *
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class UniquenessAnalysis extends DataFlowAnalysis<UniquenessAnalysis.Environment>{

	public UniquenessAnalysis(WhileFile file) {
		super(file, new TransferFunction(file));
	}

	public static class TransferFunction extends DataFlowAnalysis.TransferFunction<Environment> {
		/**
		 * Identifies which variable expressions found in the given program are
		 * "consumed". This means that their value will be stored somewhere permanently
		 * (at least, beyond the lifetime of the enclosing expression).
		 */
		private ConsumptionAnalysis analysis;
		private boolean lhsDeleted = false; // initial value

		public TransferFunction(WhileFile file) {
			super(file.source);
			// apply the move analysis
			analysis = new ConsumptionAnalysis(file);
		}

		@Override
		public Environment initialise(MethodDecl decl) {
			// Initialise a new environment based on the declared parameter types of the
			// method.
			HashMap<String, Type> map = new HashMap<>();
			for (WhileFile.Parameter p : decl.getParameters()) {
				map.put(p.getName(), p.getType());
			}
			return new Environment(map);
		}

		@Override
		public Environment transfer(Stmt.VariableDeclaration stmt, Environment env) {
			// Declare variable to have given type
			env = env.declare(stmt.getName(), stmt.getType());
			// Initialise (if appropriate)
			if (stmt.getExpr() != null) {
				env = transfer(stmt.getExpr(), env);
				env = env.write(stmt.getName(), stmt.getType());
			}
			//
			return env;
		}

		@Override
		public Environment transfer(Stmt.Assign stmt, Environment env) {
			if (stmt.getLhs() instanceof Expr.Variable) {
				boolean d = analysis.isDeleted((Expr.Variable) stmt.getLhs());
				if (d) {
					lhsDeleted = true;
				}
			}
			else if (stmt.getLhs() instanceof Expr.RecordAccess) {
				boolean d = analysis.isDeleted((Expr.RecordAccess) stmt.getLhs());
				if (d) {
					lhsDeleted = true;
				}
			}
			else if (stmt.getLhs() instanceof Expr.FieldDereference) {
				boolean d = analysis.isDeleted((Expr.FieldDereference) stmt.getLhs());
				if (d) {
					lhsDeleted = true;
				}
			}

			env = transfer(stmt.getLhs(), env);
			return transfer(stmt.getRhs(), env);
		}

		@Override
		public Environment transfer(Expr.RecordAccess expr, Environment env) {
			boolean c = analysis.isConsumed(expr);
			boolean d = analysis.isDeleted(expr);
			if (c && !lhsDeleted) {
				syntaxError("variable already moved", source, expr);
			}
			else if (lhsDeleted && d) {
				analysis.recordAccessDeleted.remove(expr);
				analysis.recordAccessConsumed.remove(expr);
				lhsDeleted = false;
			}
			return env;
		}

		@Override
		public Environment transfer(Expr.FieldDereference expr, Environment env) {
			boolean c = analysis.isConsumed(expr);
			boolean d = analysis.isDeleted(expr);
			if (c && !lhsDeleted) {
				syntaxError("variable already moved", source, expr);
			}
			else if (lhsDeleted && d) {
				analysis.fieldDereferencesDeleted.remove(expr);
				analysis.fieldDereferencesConsumed.remove(expr);
				lhsDeleted = false;
			}
			return env;
		}

		@Override
		public Environment transfer(Expr.Variable expr, Environment env) {
			boolean c = analysis.isConsumed(expr);
			// Determine current type of variable
			Type type = env.read(expr.getName());
			// If variable has been moved, then we have a problem.
			if (type == VOID && !lhsDeleted) {
				syntaxError("variable already moved", source, expr);
			} else if (c) {
				// Signal variable as moved
				env = env.write(expr.getName(), VOID);
			}
			else if (lhsDeleted) {
				// Restore the type of the variable at the left hand side to its original type
				env = env.write(expr.getName(), env.declared.get(expr.getName()));
				analysis.deleted.remove(expr);
				analysis.consumed.remove(expr);
				lhsDeleted = false;
			}
			return env;
		}
	}

	/**
	 * <p>
	 * Provides information about the current status of a variable. In particular,
	 * it records the variables declared type along with its <i>current</i> type.
	 * The current type may be <code>void</code> if the variable has been moved, or
	 * may contain <code>void</code> if the variable has been partially moved.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> This class is implemented in a functional style to ensure safe
	 * operation with the underlying dataflow analysis framework.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Environment implements DataFlowAnalysis.Information<Environment> {
		private final Map<String,Type> declared;
		private final Map<String,Type> current;

		/**
		 * Construct an environment from an initial typing of variables. This is likely
		 * to stem from the parameters defined for a given method.
		 *
		 * @param types
		 */
		public Environment(Map<String,Type> types) {
			this.declared = new HashMap<>(types);
			this.current = new HashMap<>(types);
		}

		/**
		 * Creating second constructor for Environment class.
		 *
		 * @param declared
		 * @param current
		 * */
		public Environment(Map<String,Type> declared, Map<String,Type> current){
			this.declared = declared;
			this.current = current;
		}

		/**
		 * Provides a copy constructor for this class.
		 *
		 * @param other
		 */
		private Environment(Environment other) {
			this.declared = new HashMap<>(other.declared);
			this.current = new HashMap<>(other.current);
		}

		@Override
		public Environment merge(Environment other) {
			// Merging the 'declared' maps
			HashMap<String, Type> resultDeclared = (HashMap<String, Type>) declared;
			for (Map.Entry<String, Type> entry : other.declared.entrySet()){
				if (!resultDeclared.containsKey(entry.getKey())) {
					resultDeclared.put(entry.getKey(), entry.getValue());
				}
				else if (entry.getValue().equals(VOID)) {
					resultDeclared.put(entry.getKey(), entry.getValue());
				}
			}

			// Merging the 'current' maps
			HashMap<String, Type> resultCurrent = (HashMap<String, Type>) current;
			for (Map.Entry<String, Type> entry : other.current.entrySet()) {
				if (!resultCurrent.containsKey(entry.getKey())) {
					resultCurrent.put(entry.getKey(), entry.getValue());
				}
				else if (entry.getValue().equals(VOID)) {
					resultCurrent.put(entry.getKey(), entry.getValue());
				}
			}

			return new Environment(resultDeclared, resultCurrent);
		}

		/**
		 * Get the declared type of a given variable.
		 *
		 * @param name
		 * @return
		 */
		public Type get(String name) {
			return declared.get(name);
		}

		/**
		 * Read the current type of a given variable.
		 *
		 * @param name
		 * @return
		 */
		public Type read(String name) {
			return current.get(name);
		}

		/**
		 * Declare a new variable in this environment. The current type for this
		 * variable is initially set to <code>void</code>.
		 *
		 * @param name
		 * @param type
		 * @return
		 */
		public Environment declare(String name, Type type) {
			Environment nenv = new Environment(this);
			nenv.declared.put(name, type);
			nenv.current.put(name, VOID);
			return nenv;
		}

		/**
		 * Update the current type of a given variable in this environment. The
		 * assumption is that this variable has been previously declared.
		 *
		 * @param name
		 * @param type
		 * @return
		 */
		public Environment write(String name, Type type) {
			if(!declared.containsKey(name)) {
				throw new IllegalArgumentException("attempt to write undeclared variable");
			}
			Environment nenv = new Environment(this);
			nenv.current.replace(name, type);
			return nenv;
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Environment) {
				Environment e = (Environment) o;
				return declared.equals(e.declared) && current.equals(e.current);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return declared.hashCode() ^ current.hashCode();
		}

		@Override
		public String toString() {
			return declared + ":" + current;
		}
	}

	private static final Type.Void VOID = new Type.Void();
}
