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

import static whilelang.util.SyntaxError.internalFailure;
import static whilelang.util.SyntaxError.syntaxError;

import java.util.*;

import whilelang.ast.Expr;
import whilelang.ast.Stmt;
import whilelang.ast.Type;
import whilelang.ast.WhileFile;

/**
 * A simple interpreter for WhileLang programs, which executes them in their
 * Abstract Syntax Tree form directly. The interpreter is not designed to be
 * efficient in anyway, however it's purpose is to provide a reference
 * implementation for the language.
 *
 * @author David J. Pearce
 *
 */
public class Interpreter {
	protected HashMap<String, WhileFile.Decl> declarations;
	protected WhileFile file;

	public void run(WhileFile wf) {
		// First, initialise the map of declaration names to their bodies.
		declarations = new HashMap<>();
		for(WhileFile.Decl decl : wf.declarations) {
			declarations.put(decl.name(), decl);
		}
		this.file = wf;

		// Second, pick the main method (if one exits) and execute it
		WhileFile.Decl main = declarations.get("main");
		if(main instanceof WhileFile.MethodDecl) {
			WhileFile.MethodDecl fd = (WhileFile.MethodDecl) main;
			execute(fd);
		} else {
			throw new IllegalArgumentException("Cannot find a main() function");
		}
	}

	/**
	 * Execute a given function with the given argument values. If the number of
	 * arguments is incorrect, then an exception is thrown.
	 *
	 * @param function
	 *            Function declaration to execute.
	 * @param arguments
	 *            Array of argument values.
	 */
	protected Object execute(WhileFile.MethodDecl function, Object... arguments) {
		// First, sanity check the number of arguments
		checkPrecondition(function.getParameters().size() == arguments.length,
				"invalid number of arguments supplied to execution of function \"" + function.getName() + "\"");

		// Second, construct the stack frame in which this function will
		// execute.
		HashMap<String,Object> frame = new HashMap<>();
		for(int i=0;i!=arguments.length;++i) {
			WhileFile.Parameter parameter = function.getParameters().get(i);
			frame.put(parameter.getName(),arguments[i]);
		}

		// Third, execute the function body!
		return execute(function.getBody(),frame);
	}

	protected Object execute(List<Stmt> block, HashMap<String,Object> frame) {
		for(int i=0;i!=block.size();i=i+1) {
			Object r = execute(block.get(i),frame);
			if(r != null) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Execute a given statement in a given stack frame.
	 *
	 * @param stmt
	 *            Statement to execute.
	 * @param frame
	 *            Stack frame mapping variables to their current value.
	 * @return
	 */
	protected Object execute(Stmt stmt, HashMap<String,Object> frame) {
		if(stmt instanceof Stmt.Assert) {
			return execute((Stmt.Assert) stmt,frame);
		} else if(stmt instanceof Stmt.Assign) {
			return execute((Stmt.Assign) stmt,frame);
		} else if(stmt instanceof Stmt.For) {
			return execute((Stmt.For) stmt,frame);
		} else if(stmt instanceof Stmt.Delete) {
			return execute((Stmt.Delete) stmt,frame);
		} else if(stmt instanceof Stmt.DoWhile) {
			return execute((Stmt.DoWhile) stmt,frame);
		} else if(stmt instanceof Stmt.While) {
			return execute((Stmt.While) stmt,frame);
		} else if(stmt instanceof Stmt.Switch) {
			return execute((Stmt.Switch) stmt,frame);
		} else if(stmt instanceof Stmt.Break) {
			return execute((Stmt.Break) stmt,frame);
		} else if(stmt instanceof Stmt.Continue) {
			return execute((Stmt.Continue) stmt,frame);
		} else if(stmt instanceof Stmt.IfElse) {
			return execute((Stmt.IfElse) stmt,frame);
		} else if(stmt instanceof Stmt.Return) {
			return execute((Stmt.Return) stmt,frame);
		} else if(stmt instanceof Stmt.VariableDeclaration) {
			return execute((Stmt.VariableDeclaration) stmt,frame);
		} else if(stmt instanceof Expr.Invoke) {
			return execute((Expr.Invoke) stmt,frame);
		} else {
			internalFailure("unknown statement encountered (" + stmt + ")", file.source,stmt);
			return null;
		}
	}

	protected Object execute(Stmt.Assert stmt, HashMap<String,Object> frame) {
		boolean b = (Boolean) execute(stmt.getExpr(),frame);
		checkPrecondition(b,"assertion failure");
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Object execute(Stmt.Assign stmt, HashMap<String,Object> frame) {
		Expr lhs = stmt.getLhs();
		if(lhs instanceof Expr.Variable) {
			Expr.Variable ev = (Expr.Variable) lhs;
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			frame.put(ev.getName(),deepClone(rhs));
		} else if(lhs instanceof Expr.RecordAccess) {
			Expr.RecordAccess ra = (Expr.RecordAccess) lhs;
			Map<String,Object> src = (Map<String, Object>) execute(ra.getSource(),frame);
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src.put(ra.getName(), deepClone(rhs));
		} else if(lhs instanceof Expr.ArrayAccess) {
			Expr.ArrayAccess io = (Expr.ArrayAccess) lhs;
			ArrayList<Object> src = (ArrayList<Object>) execute(io.getSource(),frame);
			Integer idx = (Integer) execute(io.getIndex(),frame);
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src.set(idx,deepClone(rhs));
		} else if(lhs instanceof Expr.Dereference) {
			Expr.Dereference dr = (Expr.Dereference) lhs;
			Object[] src = (Object[]) execute(dr.getSource(),frame);
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src[0] = deepClone(rhs);
		} else if(lhs instanceof Expr.FieldDereference) {
			Expr.FieldDereference dr = (Expr.FieldDereference) lhs;
			Object[] src = (Object[]) execute(dr.getSource(),frame);
			Map<String,Object> rec = (Map<String, Object>) src[0];
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			rec.put(dr.getName(), deepClone(rhs));
		} else {
			internalFailure("unknown lval encountered (" + lhs + ")", file.source,stmt);
		}

		return null;
	}

	protected Object execute(Stmt.Delete stmt, HashMap<String,Object> frame) {
		// Evaluate expression to produce reference
		Object[] ref = (Object[]) execute(stmt.getExpr(), frame);

		if (ref.length > 0 && ref[0] == String.valueOf(Math.sqrt(-1))){
			syntaxError("Variable '" + stmt.getExpr() + "' already deleted!", file.source, stmt.getExpr());
		}

		// Delete contents of reference if there are references remaining
		if (ref.length > 0) {
			ref[0] = String.valueOf(Math.sqrt(-1));
		}

		// Done
		return null;
	}

	protected Object execute(Stmt.DoWhile stmt, HashMap<String,Object> frame) {
		do {
			Object ret = execute(stmt.getBody(),frame);
			if(ret == BREAK_CONSTANT) {
				break;
			} else if(ret == CONTINUE_CONSTANT) {
				// continue :)
			} else if(ret != null) {
				return ret;
			}
		} while((Boolean) execute(stmt.getCondition(),frame));
		//
		return null;
	}

	protected Object execute(Stmt.For stmt, HashMap<String,Object> frame) {
		execute(stmt.getDeclaration(),frame);
		while((Boolean) execute(stmt.getCondition(),frame)) {
			Object ret = execute(stmt.getBody(),frame);
			if(ret == BREAK_CONSTANT) {
				break;
			} else if(ret == CONTINUE_CONSTANT) {
				// continue :)
			} else if(ret != null) {
				return ret;
			}
			execute(stmt.getIncrement(),frame);
		}
		return null;
	}

	protected Object execute(Stmt.While stmt, HashMap<String,Object> frame) {
		while((Boolean) execute(stmt.getCondition(),frame)) {
			Object ret = execute(stmt.getBody(),frame);
			if(ret == BREAK_CONSTANT) {
				break;
			} else if(ret == CONTINUE_CONSTANT) {
				// continue :)
			} else if(ret != null) {
				return ret;
			}
		}
		return null;
	}

	protected Object execute(Stmt.IfElse stmt, HashMap<String,Object> frame) {
		boolean condition = (Boolean) execute(stmt.getCondition(),frame);
		if(condition) {
			return execute(stmt.getTrueBranch(),frame);
		} else {
			return execute(stmt.getFalseBranch(),frame);
		}
	}

	protected Object execute(Stmt.Break stmt, HashMap<String, Object> frame) {
		return BREAK_CONSTANT;
	}

	protected Object execute(Stmt.Continue stmt, HashMap<String, Object> frame) {
		return CONTINUE_CONSTANT;
	}

	protected Object execute(Stmt.Switch stmt, HashMap<String, Object> frame) {
		boolean fallThru = false;
		Object value = execute(stmt.getExpr(), frame);
		for (Stmt.Case c : stmt.getCases()) {
			Expr e = c.getValue();
			if (fallThru || e == null || value.equals(execute(e, frame))) {
				Object ret = execute(c.getBody(), frame);
				if(ret == BREAK_CONSTANT) {
					break;
				} else if(ret != null) {
					return ret;
				}
				fallThru = true;
			}
		}
		return null;
	}

	protected Object execute(Stmt.Return stmt, HashMap<String,Object> frame) {
		Expr re = stmt.getExpr();
		if(re != null) {
			return execute(re,frame);
		} else {
			return Collections.EMPTY_SET; // used to indicate a function has returned
		}
	}

	protected Object execute(Stmt.VariableDeclaration stmt,
			HashMap<String, Object> frame) {
		Expr re = stmt.getExpr();
		Object value;
		if (re != null) {
			value = execute(re, frame);
		} else {
			value = Collections.EMPTY_SET; // used to indicate a variable has
											// been declared
		}
		// We need to perform a deep clone here to ensure the value
		// semantics used in While are preserved.
		frame.put(stmt.getName(), deepClone(value));
		return null;
	}

	/**
	 * Execute a given expression in a given stack frame.
	 *
	 * @param expr
	 *            Expression to execute.
	 * @param frame
	 *            Stack frame mapping variables to their current value.
	 * @return
	 */
	protected Object execute(Expr expr, HashMap<String,Object> frame) {
		if(expr instanceof Expr.Binary) {
			return execute((Expr.Binary) expr,frame);
		} else if(expr instanceof Expr.Literal) {
			return execute((Expr.Literal) expr,frame);
		} else if(expr instanceof Expr.Invoke) {
			return execute((Expr.Invoke) expr,frame);
		} else if(expr instanceof Expr.Is) {
			return execute((Expr.Is) expr,frame);
		} else if(expr instanceof Expr.ArrayAccess) {
			return execute((Expr.ArrayAccess) expr,frame);
		} else if(expr instanceof Expr.Cast) {
			return execute((Expr.Cast) expr,frame);
		} else if(expr instanceof Expr.Dereference) {
			return execute((Expr.Dereference) expr,frame);
		} else if(expr instanceof Expr.FieldDereference) {
			return execute((Expr.FieldDereference) expr,frame);
		} else if(expr instanceof Expr.ArrayGenerator) {
			return execute((Expr.ArrayGenerator) expr,frame);
		} else if(expr instanceof Expr.ArrayInitialiser) {
			return execute((Expr.ArrayInitialiser) expr,frame);
		} else if(expr instanceof Expr.RecordAccess) {
			return execute((Expr.RecordAccess) expr,frame);
		} else if(expr instanceof Expr.RecordConstructor) {
			return execute((Expr.RecordConstructor) expr,frame);
		} else if(expr instanceof Expr.Unary) {
			return execute((Expr.Unary) expr,frame);
		} else if(expr instanceof Expr.Variable) {
			return execute((Expr.Variable) expr,frame);
		} else {
			internalFailure("unknown expression encountered (" + expr + ")", file.source,expr);
			return null;
		}
	}

	@SuppressWarnings("incomplete-switch")
	protected Object execute(Expr.Binary expr, HashMap<String,Object> frame) {
		// First, deal with the short-circuiting operators first
		Object lhs = execute(expr.getLhs(), frame);

		switch (expr.getOp()) {
		case AND:
			return ((Boolean)lhs) && ((Boolean)execute(expr.getRhs(), frame));
		case OR:
			return ((Boolean)lhs) || ((Boolean)execute(expr.getRhs(), frame));
		}

		// Second, deal the rest.
		Object rhs = execute(expr.getRhs(), frame);

		switch (expr.getOp()) {
		case ADD:
			return ((Integer)lhs) + ((Integer)rhs);
		case SUB:
			return ((Integer)lhs) - ((Integer)rhs);
		case MUL:
			return ((Integer)lhs) * ((Integer)rhs);
		case DIV:
			checkPrecondition(((Integer) rhs) != 0, "division by zero");
			return ((Integer) lhs) / ((Integer) rhs);
		case REM:
			return ((Integer)lhs) % ((Integer)rhs);
		case EQ:
			return Objects.equals(lhs,rhs);
		case NEQ:
			return !Objects.equals(lhs,rhs);
		case LT:
			return ((Integer)lhs) < ((Integer)rhs);
		case LTEQ:
			return ((Integer)lhs) <= ((Integer)rhs);
		case GT:
			return ((Integer)lhs) > ((Integer)rhs);
		case GTEQ:
			return ((Integer)lhs) >= ((Integer)rhs);
		}

		internalFailure("unknown binary expression encountered (" + expr + ")",
				file.source, expr);
		return null;
	}

	protected Object execute(Expr.Literal expr, HashMap<String,Object> frame) {
		Object o = expr.getValue();
		// Check whether any coercions required
		if(o instanceof Character) {
			char c = ((Character)o);
			return c;
		} else if(o instanceof String) {
			String s = ((String)o);
			ArrayList<Integer> list = new ArrayList<>();
			for(int i=0;i!=s.length();++i) {
				list.add((int) s.charAt(i));
			}
			return list;
		}
		// Done
		return o;
	}

	protected Object execute(Expr.Invoke expr, HashMap<String, Object> frame) {
		List<Expr> arguments = expr.getArguments();
		Object[] values = new Object[arguments.size()];
		for (int i = 0; i != values.length; ++i) {
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			values[i] = deepClone(execute(arguments.get(i), frame));
		}
		WhileFile.MethodDecl fun = (WhileFile.MethodDecl) declarations.get(expr
				.getName());
		return execute(fun, values);
	}

	protected Object execute(Expr.Dereference expr, HashMap<String, Object> frame) {
		Object[] ref = (Object[]) execute(expr.getSource(), frame);
		return ref[0];
	}

	@SuppressWarnings("unchecked")
	protected Object execute(Expr.FieldDereference expr, HashMap<String, Object> frame) {
		Object[] ref = (Object[]) execute(expr.getSource(), frame);
		HashMap<String, Object> src = (HashMap<String, Object>) ref[0];
		return src.get(expr.getName());
	}

	protected Object execute(Expr.Cast expr, HashMap<String,Object> frame) {
		return execute(expr.getSource(),frame);
	}

	protected Object execute(Expr.Is expr, HashMap<String,Object> frame) {
		Object v = execute(expr.getSource(),frame);
		return isInstance(expr.getType(),v);
	}

	@SuppressWarnings("unchecked")
	protected boolean isInstance(Type t, Object o) {
		if (t instanceof Type.Named) {
			Type.Named n = (Type.Named) t;
			WhileFile.TypeDecl decl = (WhileFile.TypeDecl) declarations.get(n.getName());
			return isInstance(decl.getType(), o);
		} else if (t instanceof Type.Null) {
			return o == null;
		} else if (t instanceof Type.Bool) {
			return o instanceof Boolean;
		} else if (t instanceof Type.Int) {
			return o instanceof Integer;
		} else if (t instanceof Type.Array && o instanceof List) {
			Type.Array ta = (Type.Array) t;
			List<Object> l = (List<Object>) o;
			for (int i = 0; i != l.size(); ++i) {
				if (!isInstance(ta.getElement(), l.get(i))) {
					return false;
				}
			}
			return true;
		} else if (t instanceof Type.Record && o instanceof HashMap) {
			Type.Record r = (Type.Record) t;
			HashMap<String, Object> v = (HashMap<String, Object>) o;
			List<Pair<Type, String>> fields = r.getFields();
			if (v.keySet().size() == fields.size()) {
				for (int i = 0; i != fields.size(); ++i) {
					Pair<Type, String> field = fields.get(i);
					Object fv = v.get(field.second());
					if (!isInstance(field.first(), fv)) {
						return false;
					}
				}
				return true;
			}
		} else if (t instanceof Type.Reference && o instanceof Object[]) {
			Type.Reference r = (Type.Reference) t;
			Object[] v = (Object[]) o;
			return isInstance(r.getElement(), v[0]);
		}
		return false;
	}

	protected Object execute(Expr.ArrayAccess expr, HashMap<String,Object> frame) {
		Object _src = execute(expr.getSource(),frame);
		int idx = (Integer) execute(expr.getIndex(),frame);
		if(_src instanceof String) {
			String src = (String) _src;
			return src.charAt(idx);
		} else {
			ArrayList<Object> src = (ArrayList<Object>) _src;
			checkPrecondition(idx >= 0 && idx < src.size(), "index out-of-bounds");
			return src.get(idx);
		}
	}

	protected Object execute(Expr.ArrayGenerator expr, HashMap<String, Object> frame) {
		Object value = execute(expr.getValue(),frame);
		int size = (Integer) execute(expr.getSize(),frame);
		checkPrecondition(size >= 0, "negative array length");
		ArrayList<Object> ls = new ArrayList<>();
		for (int i = 0; i < size; ++i) {
			ls.add(value);
		}
		return ls;
	}

	protected Object execute(Expr.ArrayInitialiser expr,
			HashMap<String, Object> frame) {
		List<Expr> es = expr.getArguments();
		ArrayList<Object> ls = new ArrayList<>();
		for (int i = 0; i != es.size(); ++i) {
			ls.add(execute(es.get(i), frame));
		}
		return ls;
	}

	protected Object execute(Expr.RecordAccess expr, HashMap<String, Object> frame) {
		HashMap<String, Object> src = (HashMap<String, Object>) execute(expr.getSource(), frame);
		return src.get(expr.getName());
	}

	protected Object execute(Expr.RecordConstructor expr, HashMap<String,Object> frame) {
		List<Pair<String,Expr>> es = expr.getFields();
		HashMap<String,Object> rs = new HashMap<>();

		for(Pair<String,Expr> e : es) {
			rs.put(e.first(),execute(e.second(),frame));
		}

		return rs;
	}

	protected Object execute(Expr.Unary expr, HashMap<String, Object> frame) {
		Object value = execute(expr.getExpr(), frame);
		switch (expr.getOp()) {
		case NOT:
			return !((Boolean) value);
		case NEG:
			return -((Integer) value);
		case LENGTHOF:
			return ((ArrayList<?>) value).size();
		case NEW:
			return new Object[] {value};
		}

		internalFailure("unknown unary expression encountered (" + expr + ")",
				file.source, expr);
		return null;
	}

	protected Object execute(Expr.Variable expr, HashMap<String,Object> frame) {
		return frame.get(expr.getName());
	}

	protected void checkPrecondition(boolean b, String msg) {
		if(!b) {
			throw new Fault(msg);
		}
	}

	/**
	 * Perform a deep clone of the given object value. This is either a
	 * <code>Boolean</code>, <code>Integer</code>, , <code>Character</code>,
	 * <code>String</code>, <code>ArrayList</code> (for lists) or
	 * <code>HaspMap</code> (for records). Only the latter two need to be
	 * cloned, since the others are immutable.
	 *
	 * @param o
	 * @return
	 */
	protected Object deepClone(Object o) {
		if (o instanceof ArrayList) {
			ArrayList<Object> l = (ArrayList<Object>) o;
			ArrayList<Object> n = new ArrayList<>();
			for (int i = 0; i != l.size(); ++i) {
				n.add(deepClone(l.get(i)));
			}
			return n;
		} else if (o instanceof HashMap) {
			HashMap<String, Object> m = (HashMap<String, Object>) o;
			HashMap<String, Object> n = new HashMap<>();
			for (String field : m.keySet()) {
				n.put(field, deepClone(m.get(field)));
			}
			return n;
		} else {
			// other cases can be ignored
			return o;
		}
	}

	/**
	 * Convert the given object value to a string. This is either a
	 * <code>Boolean</code>, <code>Integer</code>, <code>Character</code>,
	 * <code>String</code>, <code>ArrayList</code> (for lists) or
	 * <code>HaspMap</code> (for records). The latter two must be treated
	 * recursively.
	 *
	 * @param o
	 * @return
	 */
	protected String toString(Object o) {
		if (o instanceof ArrayList) {
			ArrayList<Object> l = (ArrayList<Object>) o;
			String r = "[";
			for (int i = 0; i != l.size(); ++i) {
				if(i != 0) {
					r = r + ", ";
				}
				r += toString(l.get(i));
			}
			return r + "]";
		} else if (o instanceof HashMap) {
			HashMap<String, Object> m = (HashMap<String, Object>) o;
			String r = "{";
			boolean firstTime = true;
			ArrayList<String> fields = new ArrayList<>(m.keySet());
			Collections.sort(fields);
			for (String field : fields) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += field + ":" + toString(m.get(field));
			}
			return r + "}";
		} else if(o != null) {
			// other cases can use their default toString methods.
			return o.toString();
		} else {
			return "null";
		}
	}

	protected Object BREAK_CONSTANT = new Object() {};
	protected Object CONTINUE_CONSTANT = new Object() {};

	/**
	 * An exception which can be raised by a While program which faults in some way.
	 * For example, an assertion fails or an attempt is made to access an array
	 * index out-of-bounds.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Fault extends RuntimeException {
		public Fault(String msg) {
			super(msg);
		}
	}
}
