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

import java.util.List;
import java.util.function.Consumer;

import static whilelang.util.SyntaxError.internalFailure;
import whilelang.ast.WhileFile;
import whilelang.ast.Stmt;
import whilelang.ast.Expr;
import whilelang.ast.Type;

public class AbstractSyntaxTree {
	public static class Visitor {
		/**
		 * Identifies the enclosing source file, which is helpful for reporting error
		 * messages.
		 */
		protected final WhileFile file;

		public Visitor(WhileFile wf) {
			this.file = wf;
		}

		public void apply(Consumer<SyntacticElement> consumer) {
			for (WhileFile.Decl d : file.declarations) {
				visitDeclaration(d, consumer);
			}
		}

		public void visitDeclaration(WhileFile.Decl decl, Consumer<SyntacticElement> consumer) {
			if (decl instanceof WhileFile.TypeDecl) {
				visitTypeDeclaration((WhileFile.TypeDecl) decl, consumer);
			} else if (decl instanceof WhileFile.MethodDecl) {
				visitMethodDeclaration((WhileFile.MethodDecl) decl, consumer);
			} else {
				internalFailure("unknown declaration encountered (" + decl + ")", file.source, decl);
			}
		}

		public void visitTypeDeclaration(WhileFile.TypeDecl decl, Consumer<SyntacticElement> consumer) {
			visitType(decl.getType(), consumer);
			consumer.accept(decl);
		}

		public void visitMethodDeclaration(WhileFile.MethodDecl decl, Consumer<SyntacticElement> consumer) {
			for (WhileFile.Parameter p : decl.getParameters()) {
				visitType(p.getType(), consumer);
			}
			visitType(decl.getRet(), consumer);
			visitStatements(decl.getBody(), consumer);
			consumer.accept(decl);
		}

		public void visitStatements(List<Stmt> stmts, Consumer<SyntacticElement> consumer) {
			for (Stmt stmt : stmts) {
				visitStatement(stmt, consumer);
			}
		}

		public void visitStatement(Stmt stmt, Consumer<SyntacticElement> consumer) {
			if (stmt instanceof Stmt.Assert) {
				visitAssert((Stmt.Assert) stmt, consumer);
			} else if (stmt instanceof Stmt.Assign) {
				visitAssign((Stmt.Assign) stmt, consumer);
			} else if (stmt instanceof Stmt.Break) {
				visitBreak((Stmt.Break) stmt, consumer);
			} else if (stmt instanceof Stmt.Continue) {
				visitContinue((Stmt.Continue) stmt, consumer);
			} else if (stmt instanceof Stmt.Delete) {
				visitDelete((Stmt.Delete) stmt, consumer);
			} else if (stmt instanceof Stmt.DoWhile) {
				visitDoWhile((Stmt.DoWhile) stmt, consumer);
			} else if (stmt instanceof Stmt.Return) {
				visitReturn((Stmt.Return) stmt, consumer);
			} else if (stmt instanceof Stmt.VariableDeclaration) {
				visitVariableDeclaration((Stmt.VariableDeclaration) stmt, consumer);
			} else if (stmt instanceof Expr.Invoke) {
				visitInvoke((Expr.Invoke) stmt, consumer);
			} else if (stmt instanceof Stmt.IfElse) {
				visitIfElse((Stmt.IfElse) stmt, consumer);
			} else if (stmt instanceof Stmt.For) {
				visitFor((Stmt.For) stmt, consumer);
			} else if (stmt instanceof Stmt.While) {
				visitWhile((Stmt.While) stmt, consumer);
			} else if (stmt instanceof Stmt.Switch) {
				visitSwitch((Stmt.Switch) stmt, consumer);
			} else {
				internalFailure("unknown statement encountered (" + stmt + ")", file.source, stmt);
			}
		}

		public void visitAssert(Stmt.Assert stmt, Consumer<SyntacticElement> consumer) {
			visitExpression(stmt.getExpr(), consumer);
			consumer.accept(stmt);
		}

		public void visitAssign(Stmt.Assign stmt, Consumer<SyntacticElement> consumer) {
			visitExpression(stmt.getLhs(), consumer);
			visitExpression(stmt.getRhs(), consumer);
			consumer.accept(stmt);
		}

		public void visitBreak(Stmt.Break stmt, Consumer<SyntacticElement> consumer) {
			consumer.accept(stmt);
		}

		public void visitContinue(Stmt.Continue stmt, Consumer<SyntacticElement> consumer) {
			consumer.accept(stmt);
		}

		public void visitDelete(Stmt.Delete stmt, Consumer<SyntacticElement> consumer) {
			visitExpression(stmt.getExpr(), consumer);
			consumer.accept(stmt);
		}

		public void visitDoWhile(Stmt.DoWhile stmt, Consumer<SyntacticElement> consumer) {
			visitExpression(stmt.getCondition(), consumer);
			visitStatements(stmt.getBody(), consumer);
			consumer.accept(stmt);
		}

		public void visitReturn(Stmt.Return stmt, Consumer<SyntacticElement> consumer) {
			if(stmt.getExpr() != null) {
				visitExpression(stmt.getExpr(), consumer);
			}
			consumer.accept(stmt);
		}

		public void visitVariableDeclaration(Stmt.VariableDeclaration stmt, Consumer<SyntacticElement> consumer) {
			if (stmt.getExpr() != null) {
				visitExpression(stmt.getExpr(), consumer);
			}
			visitType(stmt.getType(), consumer);
			consumer.accept(stmt);
		}

		public void visitIfElse(Stmt.IfElse stmt, Consumer<SyntacticElement> consumer) {
			visitExpression(stmt.getCondition(), consumer);
			visitStatements(stmt.getTrueBranch(), consumer);
			if (stmt.getFalseBranch() != null) {
				visitStatements(stmt.getFalseBranch(), consumer);
			}
			consumer.accept(stmt);
		}

		public void visitFor(Stmt.For stmt, Consumer<SyntacticElement> consumer) {
			visitStatement(stmt.getDeclaration(), consumer);
			visitExpression(stmt.getCondition(), consumer);
			visitStatement(stmt.getIncrement(), consumer);
			visitStatements(stmt.getBody(), consumer);
			consumer.accept(stmt);
		}

		public void visitWhile(Stmt.While stmt, Consumer<SyntacticElement> consumer) {
			visitExpression(stmt.getCondition(), consumer);
			visitStatements(stmt.getBody(), consumer);
			consumer.accept(stmt);
		}

		public void visitSwitch(Stmt.Switch stmt, Consumer<SyntacticElement> consumer) {
			visitExpression(stmt.getExpr(), consumer);
			for (Stmt.Case c : stmt.getCases()) {
				visitStatements(c.getBody(), consumer);
			}
			consumer.accept(stmt);
		}

		public void visitExpression(Expr expr, Consumer<SyntacticElement> consumer) {
			if (expr instanceof Expr.ArrayAccess) {
				visitArrayAccess((Expr.ArrayAccess) expr, consumer);
			} else if (expr instanceof Expr.ArrayGenerator) {
				visitArrayGenerator((Expr.ArrayGenerator) expr, consumer);
			} else if (expr instanceof Expr.ArrayInitialiser) {
				visitArrayInitialiser((Expr.ArrayInitialiser) expr, consumer);
			} else if (expr instanceof Expr.Binary) {
				visitBinary((Expr.Binary) expr, consumer);
			} else if (expr instanceof Expr.Cast) {
				visitCast((Expr.Cast) expr, consumer);
			} else if (expr instanceof Expr.Dereference) {
				visitDereference((Expr.Dereference) expr, consumer);
			} else if (expr instanceof Expr.FieldDereference) {
				visitFieldDereference((Expr.FieldDereference) expr, consumer);
			} else if (expr instanceof Expr.Invoke) {
				visitInvoke((Expr.Invoke) expr, consumer);
			} else if (expr instanceof Expr.Is) {
				visitIs((Expr.Is) expr, consumer);
			} else if (expr instanceof Expr.Literal) {
				visitLiteral((Expr.Literal) expr, consumer);
			} else if (expr instanceof Expr.RecordAccess) {
				visitRecordAccess((Expr.RecordAccess) expr, consumer);
			} else if (expr instanceof Expr.RecordConstructor) {
				visitRecordConstructor((Expr.RecordConstructor) expr, consumer);
			} else if (expr instanceof Expr.Unary) {
				visitUnary((Expr.Unary) expr, consumer);
			} else if (expr instanceof Expr.Variable) {
				visitVariable((Expr.Variable) expr, consumer);
			} else {
				internalFailure("unknown expression encountered (" + expr + ")", file.source, expr);
			}
		}

		/**
		 * Visit an <i>array access</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitArrayAccess(Expr.ArrayAccess expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getSource(), consumer);
			visitExpression(expr.getIndex(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit an <i>array generator</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitArrayGenerator(Expr.ArrayGenerator expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getValue(), consumer);
			visitExpression(expr.getSize(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit an <i>array initialiser</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitArrayInitialiser(Expr.ArrayInitialiser expr, Consumer<SyntacticElement> consumer) {
			for (Expr e : expr.getArguments()) {
				visitExpression(e, consumer);
			}
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>binary</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitBinary(Expr.Binary expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getLhs(), consumer);
			visitExpression(expr.getRhs(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>cast</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitCast(Expr.Cast expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getSource(), consumer);
			visitType(expr.getType(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>dereference</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitDereference(Expr.Dereference expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getSource(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>field dereference</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitFieldDereference(Expr.FieldDereference expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getSource(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit an <i>invoke</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitInvoke(Expr.Invoke expr, Consumer<SyntacticElement> consumer) {
			for (Expr e : expr.getArguments()) {
				visitExpression(e, consumer);
			}
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>runtime type test</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitIs(Expr.Is expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getSource(), consumer);
			visitType(expr.getType(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>literal</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitLiteral(Expr.Literal expr, Consumer<SyntacticElement> consumer) {
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>record access</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitRecordAccess(Expr.RecordAccess expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getSource(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>record constructor</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitRecordConstructor(Expr.RecordConstructor expr, Consumer<SyntacticElement> consumer) {
			for (Pair<String, Expr> p : expr.getFields()) {
				visitExpression(p.second(), consumer);
			}
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>unary</i> expression.
		 *
		 * @param expr The expression being visited
		 * @return
		 */
		public void visitUnary(Expr.Unary expr, Consumer<SyntacticElement> consumer) {
			visitExpression(expr.getExpr(), consumer);
			consumer.accept(expr);
		}

		/**
		 * Visit a <i>variable</i> expression.
		 *
		 * @param expr The expression being visited
		 *
		 * @return
		 */
		public void visitVariable(Expr.Variable expr, Consumer<SyntacticElement> consumer) {
			consumer.accept(expr);
		}

		public void visitType(Type type, Consumer<SyntacticElement> consumer) {
			if (type instanceof Type.Array) {
				visitTypeArray((Type.Array) type, consumer);
			} else if (type instanceof Type.Bool) {
				visitTypeBool((Type.Bool) type, consumer);
			} else if (type instanceof Type.Int) {
				visitTypeInt((Type.Int) type, consumer);
			} else if (type instanceof Type.Null) {
				visitTypeNull((Type.Null) type, consumer);
			} else if (type instanceof Type.Named) {
				visitTypeNamed((Type.Named) type, consumer);
			} else if (type instanceof Type.Record) {
				visitTypeRecord((Type.Record) type, consumer);
			} else if (type instanceof Type.Reference) {
				visitTypeReference((Type.Reference) type, consumer);
			} else if (type instanceof Type.Union) {
				visitTypeUnion((Type.Union) type, consumer);
			} else if (type instanceof Type.Void) {
				visitTypeVoid((Type.Void) type, consumer);
			} else {
				internalFailure("unknown type encountered (" + type + ")", file.source, type);
			}
		}

		public void visitTypeArray(Type.Array type, Consumer<SyntacticElement> consumer) {
			visitType(type.getElement(), consumer);
			consumer.accept(type);
		}

		public void visitTypeBool(Type.Bool type, Consumer<SyntacticElement> consumer) {
			consumer.accept(type);
		}

		public void visitTypeInt(Type.Int type, Consumer<SyntacticElement> consumer) {
			consumer.accept(type);
		}

		public void visitTypeNull(Type.Null type, Consumer<SyntacticElement> consumer) {
			consumer.accept(type);
		}

		public void visitTypeNamed(Type.Named type, Consumer<SyntacticElement> consumer) {
			consumer.accept(type);
		}

		public void visitTypeRecord(Type.Record type, Consumer<SyntacticElement> consumer) {
			for (Pair<Type, String> p : type.getFields()) {
				visitType(p.first(), consumer);
			}
			consumer.accept(type);
		}

		public void visitTypeReference(Type.Reference type, Consumer<SyntacticElement> consumer) {
			visitType(type.getElement(), consumer);
			consumer.accept(type);
		}

		public void visitTypeUnion(Type.Union type, Consumer<SyntacticElement> consumer) {
			for (Type t : type.getBounds()) {
				visitType(t, consumer);
			}
			consumer.accept(type);
		}

		public void visitTypeVoid(Type.Void type, Consumer<SyntacticElement> consumer) {
			consumer.accept(type);
		}
	}
}
