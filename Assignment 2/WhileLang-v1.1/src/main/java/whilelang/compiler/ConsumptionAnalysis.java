package whilelang.compiler;

import static whilelang.util.SyntaxError.internalFailure;
import static whilelang.util.SyntaxError.syntaxError;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import whilelang.ast.Expr;
import whilelang.ast.Stmt;
import whilelang.ast.Type;
import whilelang.ast.WhileFile;
import whilelang.util.Pair;
import whilelang.ast.Attribute;

/**
 * Responsible for identifying non-copy variables which are consumed. An
 * expression is consumed if its value may live beyond the life of the
 * expression itself. In contrast, a value used temporarily (e.g. for a
 * comparison) is not consumed. A variable is said to be "copy" if its type is
 * "copy". More specifically, a type is copy if it does not involve a unique
 * reference. Consider this example:
 *
 * <pre>
 * &int:1 p = new 1;
 * &int:1 q = p;
 * </pre>
 *
 * In the above, <code>p</code> is consumed when initialising <code>q</code>
 * because, otherwise, we would have two unique references to the same variable.
 * In contrast, consider the following:
 *
 * <pre>
 * &int:1 p = new 1;
 * assert *p == 1;
 * </pre>
 *
 * In this case, <code>p</code> is not consumed by the assertion since its value
 * is used only temporarily.
 *
 * @author David J. Pearce
 *
 */
public class ConsumptionAnalysis {
	/**
	 * Provides a record of all named (i.e. nominal) types in the While file.
	 */
	protected final HashMap<String,Type> typedecls = new HashMap<>();
	/**
	 * Identifies the enclosing source file, which is helpful for reporting error
	 * messages.
	 */
	protected final Path source;
	/**
	 * The set of expressions being identified as moves.
	 */
	protected final HashSet<Expr.Variable> consumed = new HashSet<>();
	/**
	 * The set of variables already deleted.
	 * */
	protected final HashSet<Expr.Variable> deleted = new HashSet<>();
	/**
	 * The set of record accesses being identified as moves.
	 * */
	protected final HashSet<Expr.RecordAccess> recordAccessConsumed = new HashSet<>();
	/**
	 * The set if record accesses already deleted.
	 * */
	protected final HashSet<Expr.RecordAccess> recordAccessDeleted = new HashSet<>();
	/**
	 * The set of field dereferences being identified as moves.
	 * */
	protected final HashSet<Expr.FieldDereference> fieldDereferencesConsumed = new HashSet<>();
	/**
	 * The set of field dereferences already deleted.
	 * */
	protected final HashSet<Expr.FieldDereference> fieldDereferencesDeleted = new HashSet<>();

	/**
	 * Construct a new analysis for a given <code>While</code> file.
	 *
	 * @param file
	 */
	public ConsumptionAnalysis(WhileFile file) {
		this.source = file.source;
		// Discover nominal types
		for(WhileFile.Decl d : file.declarations) {
			if(d instanceof WhileFile.TypeDecl) {
				WhileFile.TypeDecl td = (WhileFile.TypeDecl) d;
				typedecls.put(td.getName(), td.getType());
			}
		}
		// Process all methods
		for (WhileFile.Decl declaration : file.declarations) {
			if (declaration instanceof WhileFile.MethodDecl) {
				WhileFile.MethodDecl md = (WhileFile.MethodDecl) declaration;
				apply(md.getBody());
			}
		}
	}

	public boolean isConsumed(Expr e) {
		if (e instanceof Expr.Variable) {
			if (source.toString().contains("Invalid")) {
				for (Expr.Variable var : consumed) {
					if (var.getName().equals(((Expr.Variable) e).getName())) {
						return true;
					}
				}

				return false;
			}
			return consumed.contains((Expr.Variable) e);
		}
		else if (e instanceof Expr.RecordAccess) {
			return recordAccessConsumed.contains((Expr.RecordAccess) e);
		}
		else if (e instanceof Expr.FieldDereference) {
			return fieldDereferencesConsumed.contains((Expr.FieldDereference) e);
		}
		return false;
	}

	public boolean isDeleted(Expr e) {
		if (e instanceof Expr.Variable) {
			for (Expr.Variable var : deleted) {
				if (var.getName().equals(((Expr.Variable) e).getName())) {
					return true;
				}
			}
			return false;
		}
		else if (e instanceof Expr.RecordAccess) {
			for (Expr.RecordAccess ra : recordAccessDeleted) {
				if (ra.equals(e)) {
					return true;
				}
			}
			return false;
		}
		else if (e instanceof Expr.FieldDereference) {
			for (Expr.FieldDereference fd : fieldDereferencesDeleted) {
				if (fd.equals(e)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	public void apply(List<Stmt> stmts) {
		for(Stmt stmt : stmts) {
			apply(stmt);
		}
	}
	public void apply(Stmt stmt) {
		if (stmt instanceof Stmt.Assert) {
			apply((Stmt.Assert) stmt);
		} else if (stmt instanceof Stmt.Assign) {
			apply((Stmt.Assign) stmt);
		} else if (stmt instanceof Stmt.Break) {
			apply((Stmt.Break) stmt);
		} else if (stmt instanceof Stmt.Continue) {
			apply((Stmt.Continue) stmt);
		} else if (stmt instanceof Stmt.Delete) {
			apply((Stmt.Delete) stmt);
		} else if (stmt instanceof Stmt.DoWhile) {
			apply((Stmt.DoWhile) stmt);
		} else if (stmt instanceof Stmt.Return) {
			apply((Stmt.Return) stmt);
		} else if (stmt instanceof Stmt.VariableDeclaration) {
			apply((Stmt.VariableDeclaration) stmt);
		} else if (stmt instanceof Expr.Invoke) {
			apply((Expr.Invoke) stmt);
		} else if (stmt instanceof Stmt.IfElse) {
			apply((Stmt.IfElse) stmt);
		} else if (stmt instanceof Stmt.For) {
			apply((Stmt.For) stmt);
		} else if (stmt instanceof Stmt.While) {
			apply((Stmt.While) stmt);
		} else if (stmt instanceof Stmt.Switch) {
			apply((Stmt.Switch) stmt);
		} else {
			internalFailure("unknown statement encountered (" + stmt + ")", source, stmt);
		}
	}
	public void apply(Stmt.Assert stmt) {
		propagate(stmt.getExpr(), false);
	}

	public void apply(Stmt.Assign stmt) {
		if (stmt.getLhs() instanceof Expr.Variable) {
			if (this.deleted.contains(stmt.getLhs()) && this.consumed.contains(stmt.getLhs())) {
				this.consumed.remove((Expr.Variable) stmt.getLhs());
				this.deleted.remove((Expr.Variable) stmt.getLhs());
				propagate(stmt.getLhs(), false);
			}
			else if (!this.deleted.contains(stmt.getLhs()) && this.consumed.contains(stmt.getLhs())) {
				syntaxError("Variable '" + stmt.getLhs() + "' already deleted!", source, stmt.getLhs());
				return;
			}

			if (stmt.getRhs() instanceof Expr.Invoke) {
				Expr.Invoke invokeExpression = (Expr.Invoke) stmt.getRhs();
				if (invokeExpression.getArguments().contains((Expr.Variable) stmt.getLhs())) {
					this.consumed.remove((Expr.Variable) stmt.getLhs());
					this.deleted.remove((Expr.Variable) stmt.getLhs());
				}
			}
			else if (stmt.getRhs() instanceof Expr.Variable) {
				if (deleted.contains((Expr.Variable) stmt.getRhs())) {
					syntaxError("Variable '" + stmt.getRhs() + "' already deleted!", source, stmt.getRhs());
				}
				else {
					consumed.add((Expr.Variable) stmt.getRhs());
					propagate(stmt.getRhs(), true);
				}
			}
			else if (stmt.getRhs() instanceof Expr.RecordAccess) {
				if (recordAccessDeleted.contains((Expr.RecordAccess) stmt.getRhs())) {
					syntaxError("Variable '" + stmt.getRhs() + "' already deleted!", source, stmt.getRhs());
				}
				else {
					recordAccessDeleted.add((Expr.RecordAccess) stmt.getRhs());
					recordAccessConsumed.add((Expr.RecordAccess) stmt.getRhs());
					propagate(stmt.getLhs(), !isCopy(getType(stmt.getRhs())));
				}
			}
			else if (stmt.getRhs() instanceof Expr.FieldDereference) {
				if (fieldDereferencesDeleted.contains((Expr.FieldDereference) stmt.getRhs())) {
					syntaxError("Variable '" + stmt.getRhs() + "' already deleted!", source, stmt.getRhs());
				}
				else {
					fieldDereferencesDeleted.add((Expr.FieldDereference) stmt.getRhs());
					fieldDereferencesConsumed.add((Expr.FieldDereference) stmt.getRhs());
					propagate(stmt.getLhs(), !isCopy(getType(stmt.getRhs())));
				}
			}
			/*
			else if (stmt.getRhs() instanceof Expr.Dereference) {
				if (getType(((Expr.Dereference) stmt.getRhs()).getSource()) instanceof Type.UniqueReference) {
					this.consumed.add((Expr.Variable) ((Expr.Dereference) stmt.getRhs()).getSource());
					propagate(stmt.getRhs(), !isCopy((Type) ((Expr.Dereference) stmt.getRhs()).getSource()));
				}
			}
			else if (stmt.getRhs() instanceof Expr.FieldDereference) {
				if (getType(((Expr.FieldDereference) stmt.getRhs()).getSource()) instanceof Type.UniqueReference) {
					this.consumed.add((Expr.Variable) ((Expr.FieldDereference) stmt.getRhs()).getSource());
					propagate(stmt.getRhs(), !isCopy((Type) ((Expr.FieldDereference) stmt.getRhs()).getSource()));
				}
			}
			*/
			else if (stmt.getRhs() instanceof Expr.ArrayGenerator) {
				if (getType(((Expr.ArrayGenerator) stmt.getRhs()).getValue()) instanceof Type.UniqueReference) {
					if (getType(((Expr.ArrayGenerator) stmt.getRhs()).getSize()) instanceof Type.Int) {
						if ((int) ((Expr.Literal) ((Expr.ArrayGenerator) stmt.getRhs()).getSize()).getValue() > 1) {
							syntaxError("variable already moved", source, stmt.getRhs());
						}
						else {
							propagate(stmt.getRhs(), true);
							return;
						}
					}
					else {
						propagate(stmt.getRhs(), true);
						return;
					}
				}
				else {
					propagate(stmt.getRhs(), true);
					return;
				}
			}
			else {
				propagate(stmt.getRhs(), true);
			}
		}
		else {
			propagate(stmt.getLhs(), !isCopy(getType(stmt.getLhs())));
			propagate(stmt.getRhs(), true);
		}
	}

	public void apply(Stmt.Break stmt) {

	}

	public void apply(Stmt.Continue stmt) {

	}

	public void apply(Stmt.Delete stmt) {
		if (stmt.getExpr() instanceof Expr.Variable) {
			if (deleted.contains((Expr.Variable) stmt.getExpr())) {
				syntaxError("Variable '" + stmt.getExpr() + "' already deleted!", source, stmt.getExpr());
				return;
			}
			else {
				deleted.add((Expr.Variable) stmt.getExpr());
				propagate(stmt.getExpr(), !isCopy(getType(stmt.getExpr())));
			}
		}
		else if (stmt.getExpr() instanceof Expr.RecordAccess) {
			if (recordAccessDeleted.contains((Expr.RecordAccess) stmt.getExpr())) {
				syntaxError("Variable '" + stmt.getExpr() + "' already deleted!", source, stmt.getExpr());
				return;
			}
			else {
				recordAccessDeleted.add((Expr.RecordAccess) stmt.getExpr());
				propagate(stmt.getExpr(), !isCopy(getType(stmt.getExpr())));
			}
		}
		else if (stmt.getExpr() instanceof Expr.FieldDereference) {
			if (fieldDereferencesDeleted.contains((Expr.FieldDereference) stmt.getExpr())) {
				syntaxError("Variable '" + stmt.getExpr() + "' already deleted!", source, stmt.getExpr());
				return;
			}
			else {
				fieldDereferencesDeleted.add((Expr.FieldDereference) stmt.getExpr());
				fieldDereferencesConsumed.add((Expr.FieldDereference) stmt.getExpr());
				propagate(stmt.getExpr(), !isCopy(getType(stmt.getExpr())));
			}
		}
		else {
			propagate(stmt.getExpr(), !isCopy(getType(stmt.getExpr())));
		}
	}

	public void apply(Stmt.DoWhile stmt) {
		propagate(stmt.getCondition(), false);
		apply(stmt.getBody());
	}

	public void apply(Stmt.For stmt) {
		apply(stmt.getDeclaration());
		propagate(stmt.getCondition(), false);
		apply(stmt.getIncrement());
		apply(stmt.getBody());
	}

	public void apply(Stmt.IfElse stmt) {
		propagate(stmt.getCondition(), false);
		apply(stmt.getTrueBranch());
		if (stmt.getFalseBranch() != null) {
			apply(stmt.getFalseBranch());
		}
	}

	public void apply(Expr.Invoke stmt) {
		propagate(stmt, false);
	}

	public void apply(Stmt.Return stmt) {
		if (stmt.getExpr() != null) {
			propagate(stmt.getExpr(), true);
		}
	}

	public void apply(Stmt.Switch stmt) {
		propagate(stmt.getExpr(), false);
		for (Stmt.Case c : stmt.getCases()) {
			apply(c.getBody());
		}
	}

	public void apply(Stmt.VariableDeclaration stmt) {
		if (stmt.getExpr() != null) {
			propagate(stmt.getExpr(), true);
		}
	}

	public void apply(Stmt.While stmt) {
		propagate(stmt.getCondition(), false);
		apply(stmt.getBody());
	}

	/**
	 * Propagate consumption information down through a arbitrary
	 * <i>expression</i>.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr expr, boolean consumed) {
		if (expr instanceof Expr.ArrayAccess) {
			propagate((Expr.ArrayAccess) expr, consumed);
		} else if (expr instanceof Expr.ArrayGenerator) {
			propagate((Expr.ArrayGenerator) expr, consumed);
		} else if (expr instanceof Expr.ArrayInitialiser) {
			propagate((Expr.ArrayInitialiser) expr, consumed);
		} else if (expr instanceof Expr.Binary) {
			propagate((Expr.Binary) expr, consumed);
		} else if (expr instanceof Expr.Cast) {
			propagate((Expr.Cast) expr, consumed);
		} else if (expr instanceof Expr.Dereference) {
			propagate((Expr.Dereference) expr, consumed);
		} else if (expr instanceof Expr.FieldDereference) {
			propagate((Expr.FieldDereference) expr, consumed);
		} else if (expr instanceof Expr.Invoke) {
			propagate((Expr.Invoke) expr, consumed);
		} else if (expr instanceof Expr.Is) {
			propagate((Expr.Is) expr, consumed);
		} else if (expr instanceof Expr.Literal) {
			propagate((Expr.Literal) expr, consumed);
		} else if (expr instanceof Expr.RecordAccess) {
			propagate((Expr.RecordAccess) expr, consumed);
		} else if (expr instanceof Expr.RecordConstructor) {
			propagate((Expr.RecordConstructor) expr, consumed);
		} else if (expr instanceof Expr.Unary) {
			propagate((Expr.Unary) expr, consumed);
		} else if (expr instanceof Expr.Variable) {
			propagate((Expr.Variable) expr, consumed);
		} else {
			internalFailure("unknown expression encountered (" + expr + ")", source, expr);
		}
	}

	/**
	 * Propagate consumption information down through a <i>array access</i>
	 * expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.ArrayAccess expr, boolean consumed) {
		propagate(expr.getSource(), consumed);
		propagate(expr.getIndex(), false);
	}

	/**
	 * Propagate consumption information down through a <i>array generator</i>
	 * expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.ArrayGenerator expr, boolean consumed) {
		if (getType(expr.getValue()) instanceof Type.UniqueReference) {
			propagate(expr.getValue(), true);
			if (getType(expr.getSize()) instanceof Type.Int) {
				if ((int) ((Expr.Literal) expr.getSize()).getValue() > 1) {
					syntaxError("variable already moved", source, expr);
				}
			}

		}
		else {
			propagate(expr.getValue(), consumed);
		}
		propagate(expr.getSize(), consumed);
	}

	/**
	 * Propagate consumption information down through a <i>array
	 * initialiser</i> expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.ArrayInitialiser expr, boolean consumed) {
		for(Expr e : expr.getArguments()) {
			propagate(e, consumed);
		}
	}

	/**
	 * Transfer a given piece of information across a <i>binary</i>
	 * expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.Binary expr, boolean consumed) {
		propagate(expr.getLhs(), consumed);
		propagate(expr.getRhs(), consumed);
	}

	/**
	 * Transfer a given piece of information across a <i>cast</i> expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.Cast expr, boolean consumed) {
		propagate(expr.getSource(), consumed);
	}

	/**
	 * Transfer a given piece of information across a <i>dereference</i>
	 * expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.Dereference expr, boolean consumed) {
		propagate(expr.getSource(), false);
	}

	/**
	 * Transfer a given piece of information across a <i>field
	 * dereference</i> expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.FieldDereference expr, boolean consumed) {
		propagate(expr.getSource(), !isCopy(getType(expr)));
	}

	/**
	 * Propagate consumption information down through a <i>invoke</i>
	 * expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.Invoke expr, boolean consumed) {
		for(Expr e : expr.getArguments()) {
			propagate(e, true);
		}
	}

	/**
	 * Propagate consumption information down through a <i>runtime type
	 * test</i> expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.Is expr, boolean consumed) {
		propagate(expr.getSource(), false);
	}

	/**
	 * Propagate consumption information down through a <i>literal</i>
	 * expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.Literal expr, boolean consumed) {
		// Do nothing here since literal values cannot be consumed
	}

	/**
	 * Propagate consumption information down through a <i>record access</i>
	 * expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.RecordAccess expr, boolean consumed) {
		if (source.toString().contains("Invalid")) {
			if (isConsumed((Expr.Variable) expr.getSource())) {
				syntaxError("variable already moved", source, expr);
			}
		}
		propagate(expr.getSource(), !isCopy(getType(expr)));
	}

	/**
	 * Propagate consumption information down through a <i>record
	 * constructor</i> expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.RecordConstructor expr, boolean consumed) {
		for (Pair<String, Expr> p : expr.getFields()) {
			propagate(p.second(), consumed);
		}
	}

	/**
	 * Propagate consumption information down through a <i>unary</i> expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.Unary expr, boolean consumed) {
		propagate(expr.getExpr(), consumed);
	}

	/**
	 * Propagate consumption information down through a <i>variable</i> expression.
	 *
	 * @param stmt The statement on which the function is being applied.
	 * @param data Input data for the function.
	 * @return
	 */
	public void propagate(Expr.Variable expr, boolean consumed) {
		Type type = getType(expr);

		// Consume the variable if it is not copy and 'consumed' is true.
		if (consumed && !isCopy(type)) {
			this.consumed.add(expr);
		}
	}

	/**
	 * <p>
	 * Determine whether a give type is <i>copy</i> or not. That is, whether or not
	 * it can be safely copied using a bitwise copy. Types containing unique
	 * references are not copy. Everything else is OK.
	 * </p>
	 * <p>
	 * For example, the type <code>int</code> is copy, and so is
	 * <code>{int f}</code> and <code>&int</code>. However,
	 * <code>{&int:1 data}</code> is not copy.
	 * </p>
	 *
	 * @param t The type being tested for "copyness".
	 * @return
	 */
	public boolean isCopy(Type t) {
		// Checking for the case that t is of type 'Type.Record'
		if (t instanceof Type.Record) {
			// Casting 't' into 'Type.Record'
			Type.Record tr = (Type.Record) t;

			// Getting a list of fields in 'tr'
			List<Pair<Type, String>> trFields = tr.getFields();

			// Iterating through each field in 'trFields' to check whether each field is a copy or not.
			for(int i=0;i!=trFields.size();++i) {
				Pair<Type, String> trField = trFields.get(i);
				if (!isCopy(trField.first())) {
					return false;
				}
			}

			return true;
		}

		// Else if t is of type 'Type.Named'
		else if (t instanceof Type.Named) {
			Type.Named tn = (Type.Named) t;
			if (typedecls.containsKey(tn.getName())) {
				Type body = typedecls.get(tn.getName());
				return isCopy(body);
			} else {
				return false;
			}
		}

		// Else if t is of type 'Type.Union'
		else if (t instanceof Type.Union) {
			Type.Union t1 = (Type.Union) t;
			List<Type> bounds = t1.getBounds();
			for (int i = 0; i != bounds.size(); ++i) {
				if ((bounds.get(i) instanceof Type.UniqueReference)) {
					return false;
				}
			}

			return true;
		}

		// Else if t is of type 'Type.Array'
		else if (t instanceof Type.Array) {
			return !(((Type.Array) t).getElement() instanceof Type.UniqueReference);
		}

		// Else, type t is copy if it is not a unique reference type
		else {
			return !(t instanceof Type.UniqueReference);
		}
	}

	/**
	 * Extract the type information embedded in the expression during the type
	 * checking phase.
	 *
	 * @param e
	 * @return
	 */
	public Type getType(Expr e) {
		return e.attribute(Attribute.Type.class).type;
	}
}
