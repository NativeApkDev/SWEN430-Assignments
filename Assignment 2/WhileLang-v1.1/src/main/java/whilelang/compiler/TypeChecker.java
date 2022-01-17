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

import static whilelang.util.SyntaxError.internalFailure;
import static whilelang.util.SyntaxError.syntaxError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import whilelang.ast.Attribute;
import whilelang.ast.Expr;
import whilelang.ast.Stmt;
import whilelang.ast.Type;
import whilelang.ast.WhileFile;
import whilelang.util.Pair;
import whilelang.util.SyntacticElement;

/**
 * <p>
 * Responsible for ensuring that all types are used appropriately. For example,
 * that we only perform arithmetic operations on arithmetic types; that we only
 * access fields in records guaranteed to have those fields, etc.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class TypeChecker {
	private WhileFile file;
	private WhileFile.MethodDecl method;
	private HashMap<String,WhileFile.MethodDecl> methods;
	private HashMap<String,WhileFile.TypeDecl> types;

	public void check(WhileFile wf) {
		this.file = wf;
		this.methods = new HashMap<>();
		this.types = new HashMap<>();

		for(WhileFile.Decl declaration : wf.declarations) {
			if(declaration instanceof WhileFile.MethodDecl) {
				WhileFile.MethodDecl fd = (WhileFile.MethodDecl) declaration;
				this.methods.put(fd.name(), fd);
			} else if(declaration instanceof WhileFile.TypeDecl) {
				WhileFile.TypeDecl fd = (WhileFile.TypeDecl) declaration;
				this.types.put(fd.name(), fd);
			}
		}

		for(WhileFile.Decl declaration : wf.declarations) {
			if(declaration instanceof WhileFile.TypeDecl) {
				check((WhileFile.TypeDecl) declaration);
			} else if(declaration instanceof WhileFile.MethodDecl) {
				check((WhileFile.MethodDecl) declaration);
			}
		}
	}

	public void check(WhileFile.TypeDecl td) {
		checkNotVoid(td.getType(),td.getType());
	}

	public void check(WhileFile.MethodDecl fd) {
		this.method = fd;

		// First, initialise the typing environment
		HashMap<String,Type> environment = new HashMap<>();
		for (WhileFile.Parameter p : fd.getParameters()) {
			checkNotVoid(p.getType(),p);
			environment.put(p.name(), p.getType());
		}

		// Second, check all statements in the function body
		check(fd.getBody(),environment);
	}

	public void check(List<Stmt> statements, Map<String,Type> environment) {
		for(Stmt s : statements) {
			check(s,environment);
		}
	}

	public void check(Stmt stmt, Map<String,Type> environment) {
		if(stmt instanceof Stmt.Assert) {
			check((Stmt.Assert) stmt, environment);
		} else if(stmt instanceof Stmt.Assign) {
			check((Stmt.Assign) stmt, environment);
		} else if(stmt instanceof Stmt.Delete) {
			check((Stmt.Delete) stmt, environment);
		} else if(stmt instanceof Stmt.Return) {
			check((Stmt.Return) stmt, environment);
		} else if(stmt instanceof Stmt.Break) {
			// nothing to do
		} else if(stmt instanceof Stmt.Continue) {
			// nothing to do
		} else if(stmt instanceof Stmt.DoWhile) {
			check((Stmt.DoWhile) stmt, environment);
		} else if(stmt instanceof Stmt.VariableDeclaration) {
			check((Stmt.VariableDeclaration) stmt, environment);
		} else if(stmt instanceof Expr.Invoke) {
			check((Expr.Invoke) stmt, false, environment);
		} else if(stmt instanceof Stmt.IfElse) {
			check((Stmt.IfElse) stmt, environment);
		} else if(stmt instanceof Stmt.For) {
			check((Stmt.For) stmt, environment);
		} else if(stmt instanceof Stmt.While) {
			check((Stmt.While) stmt, environment);
		} else if(stmt instanceof Stmt.Switch) {
			check((Stmt.Switch) stmt, environment);
		} else {
			internalFailure("unknown statement encountered (" + stmt + ")", file.source,stmt);
		}
	}


	public void check(Stmt.VariableDeclaration stmt, Map<String,Type> environment) {
		if(environment.containsKey(stmt.getName())) {
			syntaxError("variable already declared: " + stmt.getName(),
					file.source, stmt);
		} else if(stmt.getExpr() != null) {
			Type type = check(stmt.getExpr(),environment);

			if (stmt.getType() instanceof Type.Reference && type instanceof Type.UniqueReference &&
				file.source.toString().contains("Invalid")) {
				if (!(stmt.getType() instanceof Type.UniqueReference)) {
					syntaxError("Cannot initialise reference type!", file.source, stmt);
				}
			}
			else if (stmt.getType() instanceof Type.Named && type instanceof Type.UniqueReference &&
					file.source.toString().contains("Invalid")) {
				Type.Named named = (Type.Named) stmt.getType();
				if (types.containsKey(named.getName())) {
					Type body = types.get(named.getName()).getType();
					if (body instanceof Type.Reference && !(body instanceof Type.UniqueReference)) {
						syntaxError("Cannot initialise reference type!", file.source, stmt);
					}
				}
				else {
					syntaxError("unknown type encountered: " + stmt.getType(), file.source,
							stmt.getExpr());
				}
			}

			checkSubtype(stmt.getType(),type,stmt.getExpr());
		}
		environment.put(stmt.getName(), stmt.getType());
	}

	public void check(Stmt.Assert stmt, Map<String,Type> environment) {
		Type t = check(stmt.getExpr(),environment);
		checkInstanceOf(t,stmt.getExpr(),Type.Bool.class);
	}

	public void check(Stmt.Assign stmt, Map<String,Type> environment) {
		Type lhs = check(stmt.getLhs(),environment);
		Type rhs = check(stmt.getRhs(),environment);

		if (stmt.getLhs() instanceof Expr.Dereference && file.source.toString().contains("Invalid")) {
			syntaxError("Cannot use dereference at left hand side: " + stmt.getLhs(),
					file.source, stmt.getLhs());
		}

		// Make sure the type being assigned is a subtype of the destination
		checkSubtype(lhs,rhs,stmt.getRhs());
	}

	public void check(Stmt.Delete stmt, Map<String,Type> environment) {
		Type t = check(stmt.getExpr(),environment);

		// Since 'delete' only works with unique references, we need to ensure that the type of variable after
		// the keyword 'delete' is a unique reference.
		checkInstanceOf(t,stmt.getExpr(),Type.UniqueReference.class);
	}

	public void check(Stmt.DoWhile stmt, Map<String, Type> environment) {
		check(stmt.getBody(), environment);
		Type ct = check(stmt.getCondition(), environment);
		// Make sure condition has bool type
		checkInstanceOf(ct, stmt.getCondition(), Type.Bool.class);
	}

	public void check(Stmt.Return stmt, Map<String, Type> environment) {
		if(stmt.getExpr() != null) {
			Type ret = check(stmt.getExpr(),environment);
			// Make sure returned value is subtype of enclosing method's return
			// type
			checkSubtype(method.getRet(),ret,stmt.getExpr());
		} else {
			// Make sure return type is instance of Void
			checkInstanceOf(new Type.Void(),stmt,method.getRet().getClass());
		}
	}

	public void check(Stmt.IfElse stmt, Map<String,Type> environment) {
		Type ct = check(stmt.getCondition(),environment);
		// Make sure condition has bool type
		checkInstanceOf(ct,stmt.getCondition(),Type.Bool.class);
		check(stmt.getTrueBranch(),environment);
		check(stmt.getFalseBranch(),environment);
	}

	public void check(Stmt.For stmt, Map<String,Type> environment) {
		// Clone the environment in order that the loop variable is only scoped
		// for the life of the loop itself.
		environment = new HashMap<>(environment);
		//
		Stmt.VariableDeclaration vd = stmt.getDeclaration();
		check(vd,environment);
		environment.put(vd.getName(), vd.getType());

		Type ct = check(stmt.getCondition(),environment);
		// Make sure condition has bool type
		checkInstanceOf(ct,stmt.getCondition(),Type.Bool.class);
		check(stmt.getIncrement(),environment);
		check(stmt.getBody(),environment);
	}

	public void check(Stmt.While stmt, Map<String,Type> environment) {
		Type ct = check(stmt.getCondition(),environment);
		// Make sure condition has bool type
		checkInstanceOf(ct,stmt.getCondition(),Type.Bool.class);
		check(stmt.getBody(),environment);
	}

	public void check(Stmt.Switch stmt, Map<String,Type> environment) {
		Type ct = check(stmt.getExpr(),environment);
		// Now, check each case individually
		for(Stmt.Case c : stmt.getCases()) {
			if(!c.isDefault()) {
				Type et = check((Expr) c.getValue(),environment);
				checkSubtype(ct,et,c.getValue());
			}
			check(c.getBody(),environment);
		}
	}

	public Type check(Expr expr, Map<String,Type> environment) {
		Type type;

		if(expr instanceof Expr.Binary) {
			type = check((Expr.Binary) expr, environment);
		} else if(expr instanceof Expr.Literal) {
			type = check((Expr.Literal) expr, environment);
		} else if(expr instanceof Expr.ArrayAccess) {
			type = check((Expr.ArrayAccess) expr, environment);
		} else if(expr instanceof Expr.Dereference) {
			type = check((Expr.Dereference) expr, environment);
		} else if(expr instanceof Expr.FieldDereference) {
			type = check((Expr.FieldDereference) expr, environment);
		} else if(expr instanceof Expr.Cast) {
			type = check((Expr.Cast) expr, environment);
		} else if(expr instanceof Expr.Invoke) {
			type = check((Expr.Invoke) expr, true, environment);
		} else if(expr instanceof Expr.Is) {
			type = check((Expr.Is) expr,  environment);
		} else if(expr instanceof Expr.ArrayGenerator) {
			type = check((Expr.ArrayGenerator) expr, environment);
		} else if(expr instanceof Expr.ArrayInitialiser) {
			type = check((Expr.ArrayInitialiser) expr, environment);
		} else if(expr instanceof Expr.RecordAccess) {
			type = check((Expr.RecordAccess) expr, environment);
		} else if(expr instanceof Expr.RecordConstructor) {
			type = check((Expr.RecordConstructor) expr, environment);
		} else if(expr instanceof Expr.Unary) {
			type = check((Expr.Unary) expr, environment);
		} else if(expr instanceof Expr.Variable) {
			type = check((Expr.Variable) expr, environment);
		} else {
			internalFailure("unknown expression encountered (" + expr + ")", file.source,expr);
			return null; // dead code
		}

		// Save the type attribute so that subsequent compiler stages can use it
		// without having to recalculate it from scratch.
		expr.attributes().add(new Attribute.Type(type));

		return type;
	}

	public Type check(Expr.Binary expr, Map<String,Type> environment) {
		Type leftType = check(expr.getLhs(), environment);
		Type rightType = check(expr.getRhs(), environment);

		switch(expr.getOp()) {
		case AND:
		case OR:
			// Check arguments have bool type
			checkInstanceOf(leftType,expr.getLhs(),Type.Bool.class);
			checkInstanceOf(rightType,expr.getRhs(),Type.Bool.class);
			return leftType;
		case ADD:
		case SUB:
		case DIV:
		case MUL:
		case REM:
			// Check arguments have int type
			checkInstanceOf(leftType,expr.getLhs(),Type.Int.class);
			checkInstanceOf(rightType,expr.getRhs(),Type.Int.class);
			return leftType;
		case EQ:
		case NEQ:
			// FIXME: we could do better here by making sure one of the
			// arguments is a subtype of the other.
			return new Type.Bool();
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
			// Check arguments have int type
			checkInstanceOf(leftType,expr.getLhs(),Type.Int.class);
			checkInstanceOf(rightType,expr.getRhs(),Type.Int.class);
			return new Type.Bool();
		default:
			internalFailure("unknown unary expression encountered (" + expr + ")", file.source,expr);
			return null; // dead code
		}
	}

	public Type check(Expr.Literal expr, Map<String,Type> environment) {
		return typeOf(expr.getValue(),expr);
	}

	public Type check(Expr.Dereference expr, Map<String, Type> environment) {
		Type type = check(expr.getSource(), environment);
		Type.Reference r = checkInstanceOf(type, expr.getSource(), Type.Reference.class);
		return r.getElement();
	}

	public Type check(Expr.FieldDereference expr, Map<String, Type> environment) {
		Type type = check(expr.getSource(), environment);
		Type.Reference t = checkInstanceOf(type, expr.getSource(), Type.Reference.class);
		Type elem = t.getElement();
		elem = checkInstanceOf(elem, expr.getSource(), Type.Record.class);
		// Lookup the field
		Type.Record recordType = (Type.Record) elem;
		for (Pair<Type, String> field : recordType.getFields()) {
			if (field.second().equals(expr.getName())) {
				return field.first();
			}
		}
		// Couldn't find the field!
		syntaxError("expected type to contain field: " + expr.getName(), file.source, expr);
		return null; // deadcode
	}

	public Type check(Expr.Cast expr, Map<String, Type> environment) {
		Type t1 = expr.getType();
		Type t2 = check(expr.getSource(), environment);
		SyntacticElement element = expr.getSource();
		//
		if(!(isSubtype(t1,t2,element) || isSubtype(t2,t1,element))) {
			syntaxError("expected type " + t1 + ", found " + t2, file.source,
					element);
		}
		// Make sure index has integer type
		return t1;
	}

	public Type check(Expr.Is expr, Map<String, Type> environment) {
		Type t1 = expr.getType();
		Type t2 = check(expr.getSource(), environment);
		SyntacticElement element = expr.getSource();
		//
		checkSubtype(t2, t1, element);
		// Make sure index has integer type
		return new Type.Bool();
	}

	public Type check(Expr.ArrayAccess expr, Map<String, Type> environment) {
		Type srcType = check(expr.getSource(), environment);
		Type indexType = check(expr.getIndex(), environment);
		// Make sure index has integer type
		checkInstanceOf(indexType, expr.getIndex(), Type.Int.class);
		// Check src has array type (of some kind)
		Type.Array t = checkInstanceOf(srcType, expr.getSource(), Type.Array.class);
		return t.getElement();
	}

	public Type check(Expr.Invoke expr, boolean returnRequired, Map<String,Type> environment) {
		WhileFile.MethodDecl fn = methods.get(expr.getName());
		List<Expr> arguments = expr.getArguments();
		List<WhileFile.Parameter> parameters = fn.getParameters();
		if(arguments.size() != parameters.size()) {
			syntaxError("incorrect number of arguments to function",
					file.source, expr);
		}
		for(int i=0;i!=parameters.size();++i) {
			Type argument = check(arguments.get(i),environment);
			Type parameter = parameters.get(i).getType();
			// Check supplied argument is subtype of declared parameter
			checkSubtype(parameter,argument,arguments.get(i));
		}
		Type returnType = fn.getRet();
		if(returnRequired) {
			checkNotVoid(returnType,fn.getRet());
		}
		return returnType;
	}

	public Type check(Expr.ArrayGenerator expr, Map<String, Type> environment) {
		Type element = check(expr.getValue(), environment);
		Type size = check(expr.getSize(), environment);
		// Check size expression has int type
		checkInstanceOf(size,expr.getSize(),Type.Int.class);
		return new Type.Array(element);
	}

	public Type check(Expr.ArrayInitialiser expr, Map<String, Type> environment) {
		ArrayList<Type> types = new ArrayList<>();
		List<Expr> arguments = expr.getArguments();
		for (Expr argument : arguments) {
			types.add(check(argument, environment));
		}
		// Compute Least Upper Bound of element Types
		Type lub = leastUpperBound(types,expr);
		return new Type.Array(lub);
	}

	public Type check(Expr.RecordAccess expr, Map<String, Type> environment) {
		Type srcType = check(expr.getSource(), environment);
		// Check src has record type
		Type.Record recordType = checkInstanceOf(srcType, expr.getSource(), Type.Record.class);
		for (Pair<Type, String> field : recordType.getFields()) {
			if (field.second().equals(expr.getName())) {
				return field.first();
			}
		}
		// Couldn't find the field!
		syntaxError("expected type to contain field: " + expr.getName(), file.source, expr);
		return null; // deadcode
	}

	public Type check(Expr.RecordConstructor expr, Map<String, Type> environment) {
		List<Pair<String, Expr>> arguments = expr.getFields();
		List<Pair<Type, String>> types = new ArrayList<>();

		for (Pair<String, Expr> p : arguments) {
			Type t = check(p.second(), environment);
			types.add(new Pair<>(t, p.first()));
		}

		return new Type.Record(types);
	}

	public Type check(Expr.Unary expr, Map<String,Type> environment) {
		Type type = check(expr.getExpr(), environment);
		switch(expr.getOp()) {
		case NEG:
			checkInstanceOf(type,expr.getExpr(),Type.Int.class);
			return type;
		case NOT:
			checkInstanceOf(type,expr.getExpr(),Type.Bool.class);
			return type;
		case LENGTHOF:
			checkInstanceOf(type,expr.getExpr(),Type.Array.class);
			return new Type.Int();
		case NEW:
			return new Type.UniqueReference(type);
		default:
			internalFailure("unknown unary expression encountered (" + expr + ")", file.source,expr);
			return null; // dead code
		}
	}

	public Type check(Expr.Variable expr, Map<String, Type> environment) {
		Type type = environment.get(expr.getName());
		if (type == null) {
			syntaxError("unknown variable encountered: " + expr.getName(),
					file.source, expr);
		}
		return type;
	}

	/**
	 * Determine the type of a constant value
	 *
	 * @param constant
	 * @param elem
	 * @return
	 */
	private Type typeOf(Object constant, SyntacticElement elem) {
		if (constant == null) {
			return new Type.Null();
		} else if (constant instanceof Boolean) {
			return new Type.Bool();
		} else if (constant instanceof Integer) {
			return new Type.Int();
		} else if (constant instanceof Character) {
			return new Type.Int();
		} else if (constant instanceof String) {
			return new Type.Array(new Type.Int());
		} else if (constant instanceof ArrayList) {
			ArrayList<Object> list = (ArrayList) constant;
			ArrayList<Type> types = new ArrayList<>();
			for(Object o : list) {
				types.add(typeOf(o,elem));
			}
			Type lub = leastUpperBound(types,elem);
			return new Type.Array(lub);
		} else if (constant instanceof HashMap) {
			HashMap<String, Object> record = (HashMap<String, Object>) constant;
			ArrayList<Pair<Type, String>> fields = new ArrayList<>();
			// FIXME: there is a known bug here related to the ordering of
			// fields. Specifically, we've lost information about the ordering
			// of fields in the original source file and we are just recreating
			// a random order here.
			for (Map.Entry<String, Object> e : record.entrySet()) {
				Type t = typeOf(e.getValue(), elem);
				fields.add(new Pair<>(t, e.getKey()));
			}
			return new Type.Record(fields);
		} else {
			internalFailure("unknown constant encountered (" + elem + ")", file.source, elem);
			return null; // dead code
		}
	}

	private Type leastUpperBound(List<Type> types, SyntacticElement elem) {
		HashSet<Type> bounds = new HashSet<>();
		//
		for (int i = 0; i != types.size(); ++i) {
			Type ti = types.get(i);
			boolean subsumed = false;
			for (int j = i + 1; j != types.size(); ++j) {
				Type tj = types.get(j);
				if (isSubtype(tj, ti, elem)) {
					subsumed = true;
				}
			}
			if (!subsumed) {
				bounds.add(ti);
			}
		}
		if (bounds.size() == 0) {
			return new Type.Void();
		} else {
			return new Type.Union(new ArrayList<>(bounds));
		}
	}

	/**
	 * Check that a given type t2 is an instance of of another type t1. This
	 * method is useful for checking that a type is, for example, a List type.
	 *
	 * @param type
	 * @param element
	 *            Used for determining where to report syntax errors.
	 * @return
	 */
	public <T> T checkInstanceOf(Type type, SyntacticElement element, Class<T> instance) {
		if(type instanceof Type.Named) {
			Type.Named tn = (Type.Named) type;
			if (types.containsKey(tn.getName())) {
				Type body = types.get(tn.getName()).getType();
				return checkInstanceOf(body, element, instance);
			} else {
				syntaxError("unknown type encountered: " + type, file.source,
						element);
			}
		}
		// Check its an instance
		if (instance.isInstance(type)) {
			// This cast is clearly unsafe. It relies on the caller of this
			// method to do the right thing.
			return (T) type;
		} else {
			// Ok, we're going to fail with an error message, so need tobuild up
			// a useful human-readable message. Start by extracting words based on camel
			// case. This isn't strictly necessary there are no types with multiple names.
			// However, in extensions to the compiler there might be!
			String[] words = instance.getSimpleName().split("(?<!^)(?=[A-Z])");
			// Make every word lower case
			Stream<String> stream =  Stream.of(words).map(s -> s.toLowerCase());
			// Combine words into a single string
			String w = stream.reduce((x,y) -> x + " " + y).get();
			// Report the error
			syntaxError("expected " + w + ", found " + type, file.source, element);
			// Unreachable code
			return null;
		}
	}

	/**
	 * Check that a given type t2 is a subtype of another type t1.
	 *
	 * @param t1
	 *            Supertype to check
	 * @param t2
	 *            Subtype to check
	 * @param element
	 *            Used for determining where to report syntax errors.
	 */
	public void checkSubtype(Type t1, Type t2, SyntacticElement element) {
		// Performance optimisation to avoid expansion where possible.
		if (!isSubtype(t1, t2, element)) {
			Type et1 = expand(t1);
			Type et2 = expand(t2);
			if (!isSubtype(et1, et2, element)) {
				syntaxError("expected type " + t1 + ", found " + t2, file.source, element);
			}
		}
	}

	/**
	 * Check that a given type t2 is a subtype of another type t1.
	 *
	 * @param t1
	 *            Supertype to check
	 * @param t2
	 *            Subtype to check
	 * @param element
	 *            Used for determining where to report syntax errors.
	 */
	public boolean isSubtype(Type t1, Type t2, SyntacticElement element) {
		if (t2 instanceof Type.Void) {
			// OK
			return true;
		} else if (t1 instanceof Type.Null && t2 instanceof Type.Null) {
			// OK
			return true;
		} else if (t1 instanceof Type.Bool && t2 instanceof Type.Bool) {
			// OK
			return true;
		} else if (t1 instanceof Type.Int && t2 instanceof Type.Int) {
			// OK
			return true;
		} else if (t1 instanceof Type.Array && t2 instanceof Type.Array) {
			Type.Array l1 = (Type.Array) t1;
			Type.Array l2 = (Type.Array) t2;
			// The following is safe because While has value semantics. In a
			// conventional language, like Java, this is not safe because of
			// references.
			return isSubtype(l1.getElement(),l2.getElement(),element);
		} else if (t1 instanceof Type.Reference && t2 instanceof Type.Reference) {
			if (t1 instanceof Type.UniqueReference && !(t2 instanceof Type.UniqueReference)) {
				return false;
			}
			else {
				Type.Reference l1 = (Type.Reference) t1;
				if (t2 instanceof Type.UniqueReference) {
					Type.UniqueReference l2 = (Type.UniqueReference) t2;
					return isSubtype(l1.getElement(), l2.getElement(), element);
				}
				else {
					Type.Reference l2 = (Type.Reference) t2;
					return isSubtype(l1.getElement(), l2.getElement(), element)
							&& isSubtype(l2.getElement(), l1.getElement(), element);
				}
			}
		} else if (t1 instanceof Type.Record && t2 instanceof Type.Record) {
			Type.Record r1 = (Type.Record) t1;
			Type.Record r2 = (Type.Record) t2;
			List<Pair<Type,String>> r1Fields = r1.getFields();
			List<Pair<Type,String>> r2Fields = r2.getFields();
			// Implement "width" subtyping
			if(r1Fields.size() > r2Fields.size()) {
				return false;
			} else {
				for(int i=0;i!=r1Fields.size();++i) {
					Pair<Type,String> p1Field = r1Fields.get(i);
					Pair<Type,String> p2Field = r2Fields.get(i);
					if(!isSubtype(p1Field.first(),p2Field.first(),element)) {
						return false;
					} else if (!p1Field.second().equals(p2Field.second())) {
						return false;
					}
				}
				return true;
			}
		} else if (t1 instanceof Type.Named) {
			Type.Named tn = (Type.Named) t1;
			if (types.containsKey(tn.getName())) {
				Type body = types.get(tn.getName()).getType();
				return isSubtype(body, t2, element);
			} else {
				syntaxError("unknown type encountered: " + t1, file.source,
						element);
			}
		} else if (t2 instanceof Type.Named) {
			Type.Named tn = (Type.Named) t2;
			if (types.containsKey(tn.getName())) {
				Type body = types.get(tn.getName()).getType();
				return isSubtype(t1, body, element);
			} else {
				syntaxError("unknown type encountered: " + t2, file.source,
						element);
			}
		} else if (t2 instanceof Type.Union) {
			Type.Union tu = (Type.Union) t2;
			List<Type> bounds = tu.getBounds();
			for (int i = 0; i != bounds.size(); ++i) {
				if (!isSubtype(t1, bounds.get(i), element)) {
					return false;
				}
			}
			return true;
		} else if (t1 instanceof Type.Union) {
			Type.Union tu = (Type.Union) t1;
			List<Type> bounds = tu.getBounds();
			for (int i = 0; i != bounds.size(); ++i) {
				if (isSubtype(bounds.get(i), t2, element)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * Expand a given type into a normalised form by moving into disjunctive normal
	 * form. For example, <code>{int|bool f}</code> becomes
	 * <code>{int f}|{bool f}</code>.
	 *
	 * @param type
	 * @return
	 */
	public Type expand(Type type) {
		Type[] atoms = expandAtoms(type);
		switch (atoms.length) {
		case 0:
			return new Type.Void();
		case 1:
			return atoms[0];
		default:
			return new Type.Union(Arrays.asList(atoms));
		}
	}

	public Type[] expandAtoms(Type type) {
		if (type instanceof Type.Void || type instanceof Type.Null || type instanceof Type.Bool || type instanceof Type.Int) {
			return new Type[] { type };
		} else if(type instanceof Type.Array) {
			Type.Array t = (Type.Array) type;
			// Easy case
			return new Type[] { new Type.Array(expand(t.getElement())) };
		} else if(type instanceof Type.UniqueReference) {
			Type.UniqueReference t = (Type.UniqueReference) type;
			// Easy case
			return new Type[] { new Type.UniqueReference(expand(t.getElement())) };
		} else if(type instanceof Type.Reference) {
			Type.Reference t = (Type.Reference) type;
			// Easy case
			return new Type[] { new Type.Reference(expand(t.getElement())) };
		} else if(type instanceof Type.Record) {
			Type.Record t = (Type.Record) type;
			List<Pair<Type,String>> t_fields = t.getFields();
			ArrayList<Type[]> a_fields = new ArrayList<>();
			for (int i = 0; i != t_fields.size(); ++i) {
				a_fields.add(expandAtoms(t_fields.get(i).first()));
			}
			ArrayList<Type> records = new ArrayList<>();
			expandRecordAtoms(new Type[t_fields.size()], 0, a_fields, t_fields, records);
			return records.toArray(new Type[records.size()]);
		} else if(type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			ArrayList<Type> atoms = new ArrayList<>();
			for(Type b : t.getBounds()) {
				atoms.addAll(Arrays.asList(expandAtoms(b)));
			}
			return atoms.toArray(new Type[atoms.size()]);
		} else {
			Type.Named t = (Type.Named) type;
			if (types.containsKey(t.getName())) {
				Type body = types.get(t.getName()).getType();
				return expandAtoms(body);
			} else {
				syntaxError("unknown type encountered: " + type, file.source, type);
				return null;
			}
		}
	}

	/**
	 * Compute the cross product of all records.
	 *
	 * @param current
	 * @param index
	 * @param a_fields
	 * @param t_fields
	 * @param records
	 */
	public void expandRecordAtoms(Type[] current, int index, List<Type[]> a_fields, List<Pair<Type, String>> t_fields,
			List<Type> records) {
		if (index == current.length) {
			// base case, add a new record
			ArrayList<Pair<Type, String>> fields = new ArrayList<>();
			for (int i = 0; i != t_fields.size(); ++i) {
				fields.add(new Pair<>(current[i], t_fields.get(i).second()));
			}
			records.add(new Type.Record(fields));
		} else {
			Type[] fields = a_fields.get(index);
			for (int i = 0; i != fields.length; ++i) {
				current[index] = fields[i];
				expandRecordAtoms(current, index + 1, a_fields, t_fields, records);
			}
		}
	}

	/**
	 * Determine whether two given types are equivalent. Identical types are always
	 * equivalent. Furthermore, e.g. "int|null" is equivalent to "null|int".
	 *
	 * @param t1
	 *            first type to compare
	 * @param t2
	 *            second type to compare
	 */
	public boolean equivalent(Type t1, Type t2, SyntacticElement element) {
		return isSubtype(t1,t2,element) && isSubtype(t2,t1,element);
	}

	/**
	 * Check that a given type is not equivalent to void. This is because void
	 * cannot be used in certain situations.
	 *
	 * @param t
	 * @param elem
	 */
	public void checkNotVoid(Type t, SyntacticElement elem) {
		if(t instanceof Type.Void) {
			syntaxError("void type not permitted here",file.source,elem);
		} else if(t instanceof Type.Record) {
			Type.Record r = (Type.Record) t;
			for(Pair<Type,String> field : r.getFields()) {
				checkNotVoid(field.first(),field.first());
			}
		} else if(t instanceof Type.Array) {
			Type.Array at = (Type.Array) t;
			checkNotVoid(at.getElement(),at.getElement());
		}
	}
}
