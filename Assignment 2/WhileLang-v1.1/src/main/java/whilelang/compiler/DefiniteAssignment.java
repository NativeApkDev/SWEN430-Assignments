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
package whilelang.compiler;

import java.nio.file.Path;
import java.util.HashSet;

import whilelang.ast.Expr;
import whilelang.ast.Stmt;
import whilelang.ast.WhileFile;
import whilelang.util.DataFlowAnalysis;
import static whilelang.util.SyntaxError.syntaxError;

/**
 * Responsible for checking that all variables are defined before they are used.
 * The algorithm for checking this involves a depth-first search through the
 * control-flow graph of the method. Throughout this, a list of the defined
 * variables is maintained.
 *
 * @author David J. Pearce
 *
 */
public class DefiniteAssignment extends DataFlowAnalysis<DefiniteAssignment.Defs> {

	public DefiniteAssignment(WhileFile file) {
		super(file, new TransferFunction(file.source));
	}

	/**
	 * The transfer function used for definite assignment. This describes how the
	 * set of definitely assigned variables is affected by the statements and
	 * expressions in the program.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class TransferFunction extends DataFlowAnalysis.TransferFunction<Defs> {

		public TransferFunction(Path source) {
			super(source);
		}

		@Override
		public Defs initialise(WhileFile.MethodDecl fd) {
			// First, initialise the environment with all parameters (since these
			// are assumed to be definitely assigned)
			Defs environment = new Defs();
			for (WhileFile.Parameter p : fd.getParameters()) {
				environment = environment.add(p.name());
			}
			return environment;
		}

		@Override
		public Defs transfer(Stmt.Assign stmt, Defs environment) {
			// Check whether assigning to a variable or not. This is important because, if
			// we are, then this affects the set of definitely assigned variables.
			if(stmt.getLhs() instanceof Expr.Variable) {
				Expr.Variable var = (Expr.Variable) stmt.getLhs();
				// Transfer across right-hand side first, since this cannot utilise information
				// about the variable being assigned.
				environment = transfer(stmt.getRhs(), environment);
				// Add assigned variable to the list.
				environment = environment.add(var.getName());
			} else {
				// Transfer left-hand side first, as this follows semantics.
				environment = transfer(stmt.getLhs(), environment);
				environment = transfer(stmt.getRhs(), environment);
			}
			return environment;
		}

		@Override
		public Defs transfer(Stmt.VariableDeclaration stmt, Defs environment) {
			Expr initialiser = stmt.getExpr();
			// If there is an initialiser then the variable in question is definitely
			// assigned.
			if (initialiser != null) {
				// Transfer across the initialiser, allowing us to detect any problems there.
				environment = transfer(initialiser, environment);
				// Record new defined variable
				environment = environment.add(stmt.getName());
			}
			//
			return environment;
		}

		@Override
		public Defs transfer(Expr.Variable expr, Defs environment) {
			if (!environment.contains(expr.getName())) {
				// This variable is not definitely assigned.
				syntaxError("variable " + expr.getName() + " is not definitely assigned", source, expr);
			}
			return environment;
		}
	}

	/**
	 * A simple class representing an immutable set of definitely assigned
	 * variables.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Defs implements DataFlowAnalysis.Information<Defs> {
		private HashSet<String> variables;

		public Defs() {
			this.variables = new HashSet<>();
		}

		public Defs(Defs defs) {
			this.variables = new HashSet<>(defs.variables);
		}

		public boolean contains(String var) {
			return variables.contains(var);
		}

		/**
		 * Add a variable to the set of definitely assigned variables, producing an
		 * updated set.
		 *
		 * @param var
		 * @return
		 */
		public Defs add(String var) {
			Defs r = new Defs(this);
			r.variables.add(var);
			return r;
		}

		/**
		 * Remove a variable from the set of definitely assigned variables, producing an
		 * updated set.
		 *
		 * @param var
		 * @return
		 */
		public Defs remove(String var) {
			Defs r = new Defs(this);
			r.variables.remove(var);
			return r;
		}

		/**
		 * Join two sets together, where the result contains a variable only if it is
		 * definitely assigned on both branches.
		 *
		 * @param other
		 * @return
		 */
		@Override
		public Defs merge(Defs other) {
			Defs r = new Defs();
			for (String var : variables) {
				if (other.contains(var)) {
					r.variables.add(var);
				}
			}
			return r;
		}


		@Override
		public boolean equals(Object o) {
			if(o instanceof Defs) {
				Defs d = (Defs) o;
				return variables.equals(d.variables);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return variables.hashCode();
		}

		/**
		 * Useful for debugging
		 */
		@Override
		public String toString() {
			return variables.toString();
		}
	}
}
