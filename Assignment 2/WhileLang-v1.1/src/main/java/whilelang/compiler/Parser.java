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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import whilelang.ast.Attribute;
import whilelang.ast.Expr;
import whilelang.ast.Stmt;
import whilelang.ast.Type;
import whilelang.ast.WhileFile;
import whilelang.ast.WhileFile.Decl;
import whilelang.ast.WhileFile.Parameter;
import whilelang.ast.WhileFile.TypeDecl;
import whilelang.compiler.Lexer.*;
import whilelang.util.Pair;
import whilelang.util.SyntaxError;

public class Parser {
	protected Path whileFile;
	protected ArrayList<Token> tokens;
	protected HashMap<String,WhileFile.MethodDecl> userDefinedMethods;
	protected HashSet<String> userDefinedTypes;
	protected int index;

	public Parser(Path path, List<Token> tokens) {
		this.whileFile = path;
		this.tokens = new ArrayList<>(tokens);
		this.userDefinedMethods = new HashMap<>();
		this.userDefinedTypes = new HashSet<>();
	}

	/**
	 * Parse a given source file to produce its Abstract Syntax Tree
	 * representation.
	 *
	 * @return
	 */
	public WhileFile read() {
		ArrayList<Decl> decls = new ArrayList<>();

		while (!(tokens.get(index) instanceof EndOfStream)) {
			Token t = tokens.get(index);
			if (t instanceof Keyword) {
				if (t.text.equals("type")) {
					decls.add(parseTypeDeclaration());
				} else {
					decls.add(parseMethodDeclaration());
				}
			} else {
				decls.add(parseMethodDeclaration());
			}
		}

		return new WhileFile(whileFile, decls);
	}

	/**
	 * Parse a type declaration of the following form:
	 *
	 * <pre>
	 * TypeDecl ::= 'type' Ident 'is' Type
	 * </pre>
	 *
	 * @return
	 */
	protected Decl parseTypeDeclaration() {
		int start = index;
		matchKeyword("type");

		Identifier name = match(Identifier.class,"an identifier");
		if(userDefinedTypes.contains(name.text)) {
			syntaxError("type already declared",name);
		}
		matchKeyword("is");

		Type t = parseType();
		int end = index;
		userDefinedTypes.add(name.text);
		return new TypeDecl(t, name.text, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a method declaration, of the form:
	 *
	 * <pre>
	 * MethodDecl ::= Type Ident '(' Parameters ')' '{' Stmt* '}'
	 *
	 * Parameters ::= [Type Ident (',' Type Ident)* ]
	 * </pre>
	 *
	 * @return
	 */
	protected WhileFile.MethodDecl parseMethodDeclaration() {
		int start = index;

		Type returnType = parseType();
		Identifier name = match(Identifier.class,"an identifier");
		if(userDefinedMethods.containsKey(name.text)) {
			syntaxError("method already declared",name);
		}

		Context context = new Context();
		match("(");

		// Now build up the parameter types
		List<Parameter> parameters = new ArrayList<>();
		boolean firstTime = true;
		while (!(tokens.get(index) instanceof RightBrace)) {
			if (!firstTime) {
				match(",");
			}
			firstTime = false;
			int parameterStart = index;
			Type parameterType = parseType();
			Identifier parameterName = match(Identifier.class,"an identifier");
			if(context.isDeclared(parameterName.text)) {
				syntaxError("parameter " + parameterName.text + " already declared",parameterName);
			} else {
				context.declare(parameterName.text);
			}
			parameters.add(new Parameter(parameterType, parameterName.text, sourceAttr(parameterStart, index - 1)));

		}

		match(")");
		List<Stmt> stmts = parseStatementBlock(context);
		WhileFile.MethodDecl m = new WhileFile.MethodDecl(name.text, returnType, parameters, stmts, sourceAttr(start, index - 1));
		userDefinedMethods.put(name.text,m);
		return m;
	}

	/**
	 * Parse a block of zero or more statements, of the form:
	 *
	 * <pre>
	 * StmtBlock ::= '{' Stmt* '}'
	 * </pre>
	 *
	 * @return
	 */
	protected List<Stmt> parseStatementBlock(Context context) {
		match("{");

		ArrayList<Stmt> stmts = new ArrayList<>();
		while (!(tokens.get(index) instanceof RightCurly)) {
			stmts.add(parseStatement(context));
		}

		match("}");

		return stmts;
	}

	/**
	 * Parse a given statement.
	 *
	 * @return
	 */
	protected Stmt parseStatement(Context context) {
		checkNotEof();
		Token token = tokens.get(index);
		if (token.text.equals("do")) {
			return parseDoWhileStmt(context);
		} else if (token.text.equals("if")) {
			return parseIfStmt(context);
		} else if (token.text.equals("while")) {
			return parseWhileStmt(context);
		} else if (token.text.equals("for")) {
			return parseForStmt(context);
		} else if (token.text.equals("switch")) {
			return parseSwitchStmt(context);
		} else {
			Stmt stmt = parseUnitStatement(context);
			match(";");
			return stmt;
		}
	}

	/**
	 * Parse a unit statement.
	 *
	 * @param context
	 * @return
	 */
	protected Stmt parseUnitStatement(Context context) {
		checkNotEof();
		Token token = tokens.get(index);
		Stmt stmt;
		if (token.text.equals("assert")) {
			return parseAssertStmt(context);
		} else if (token.text.equals("delete")) {
			return parseDeleteStmt(context);
		} else if (token.text.equals("return")) {
			return parseReturnStmt(context);
		} else if (token.text.equals("break")) {
			return parseBreakStmt(context);
		} else if (token.text.equals("continue")) {
			return parseContinueStmt(context);
		} else if (isTypeAhead(index)) {
			return parseVariableDeclaration(context);
		} else if ((index + 1) < tokens.size() && tokens.get(index) instanceof Identifier
				&& tokens.get(index + 1) instanceof LeftBrace) {
			// must be a method invocation
			return parseInvokeExprOrStmt(context);
		} else {
			// invocation or assignment
			int start = index;
			Expr t = parseExpr(context);
			if (t instanceof Expr.Invoke) {
				stmt = (Expr.Invoke) t;
			} else {
				index = start;
				stmt = parseAssignStmt(context);
			}
		}
		return stmt;
	}


	/**
	 * Check whether there is a type starting at the given index. This is useful
	 * for distinguishing variable declarations from invocations and
	 * assignments.
	 *
	 * @param index
	 * @return
	 */
	protected boolean isTypeAhead(int index) {
		if (index >= tokens.size()) {
			return false;
		}
		Token lookahead = tokens.get(index);
		if (lookahead instanceof Keyword) {
			return lookahead.text.equals("null") || lookahead.text.equals("bool") || lookahead.text.equals("int")
					|| lookahead.text.equals("char") || lookahead.text.equals("string");
		} else if (lookahead instanceof Identifier) {
			Identifier id = (Identifier) lookahead;
			return userDefinedTypes.contains(id.text);
		} else if (lookahead instanceof LeftCurly) {
			return isTypeAhead(index + 1);
		} else if (lookahead instanceof LeftSquare) {
			return isTypeAhead(index + 1);
		} else if (lookahead instanceof LeftBrace) {
			return isTypeAhead(index + 1);
		} else if(lookahead instanceof Ampersand) {
			return isTypeAhead(index + 1);
		}

		return false;
	}

	/**
	 * Parse an assert statement, of the form:
	 *
	 * <pre>
	 * AssertStmt ::= 'assert'  Expr ';'
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt.Assert parseAssertStmt(Context context) {
		int start = index;
		// Every assert statement begins with assert keyword!
		matchKeyword("assert");
		Expr e = parseExpr(context);
		// Done.
		return new Stmt.Assert(e, sourceAttr(start, index - 1));
	}

	/**
	 * Parse an assignment statement, of the form:
	 *
	 * <pre>
	 * AssignStmt ::= LVal '=' Expr ';'
	 *
	 * LVal ::= Ident
	 *       | LVal '.' Ident
	 *       | LVal '[' Expr ']'
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt parseAssignStmt(Context context) {
		// standard assignment
		int start = index;
		Expr lhs = parseExpr(context);
		if (!(lhs instanceof Expr.LVal)) {
			syntaxError("expecting lval, found " + lhs + ".", lhs);
		}
		match("=");
		Expr rhs = parseExpr(context);
		int end = index;
		return new Stmt.Assign((Expr.LVal) lhs, rhs, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a variable declaration, of the form:
	 *
	 * <pre>
	 * VarDecl ::= Type Ident [ '=' Expr ] ';'
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt.VariableDeclaration parseVariableDeclaration(Context context) {
		int start = index;
		// Every variable declaration consists of a declared type and variable
		// name.
		Type type = parseType();
		Identifier id = match(Identifier.class,"an identifier");
		if(context.isDeclared(id.text)) {
			syntaxError("variable " + id.text + " already declared",id);
		} else {
			context.declare(id.text);
		}
		// A variable declaration may optionally be assigned an initialiser
		// expression.
		Expr initialiser = null;
		if (tokens.get(index) instanceof Equals) {
			match("=");
			initialiser = parseExpr(context);
		}
		// Done.
		return new Stmt.VariableDeclaration(type, id.text, initialiser, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a delete statement, of the form:
	 *
	 * <pre>
	 * AssertStmt ::= 'delete'  Expr ';'
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt.Delete parseDeleteStmt(Context context) {
		int start = index;
		// Every assert statement begins with assert keyword!
		matchKeyword("delete");
		Expr e = parseExpr(context);
		// Done.
		return new Stmt.Delete(e, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a Do-While statement of the form:
	 *
	 * <pre>
	 * DoWhileStmt ::= 'do' StmtBlock 'while' '(' Expr ')' ';'
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt parseDoWhileStmt(Context context) {
		int start = index;
		matchKeyword("do");
		List<Stmt> blk = parseStatementBlock(context.setInLoop().clone());
		matchKeyword("while");
		Expr condition = parseExpr(context);
		int end = index;
		match(";");
		return new Stmt.DoWhile(blk, condition, sourceAttr(start, end - 1));
	}

	/**
	 * Parse an if statement, of the form:
	 *
	 * <pre>
	 * IfStmt ::= 'if' '(' Expr ')' StmtBlock ElseIf* [Else]
	 *
	 * ElseIf ::= 'else' 'if' '(' Expr ')' StmtBlock
	 *
	 * Else ::= 'else' StmtBlock
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt parseIfStmt(Context context) {
		int start = index;
		matchKeyword("if");
		Expr c = parseExpr(context);
		int end = index;
		List<Stmt> tblk = parseStatementBlock(context.clone());
		List<Stmt> fblk = Collections.emptyList();

		if (tokens.get(index).text.equals("else")) {
			matchKeyword("else");

			if (tokens.get(index).text.equals("if")) {
				Stmt if2 = parseIfStmt(context);
				fblk = new ArrayList<>();
				fblk.add(if2);
			} else {
				fblk = parseStatementBlock(context.clone());
			}
		}

		return new Stmt.IfElse(c, tblk, fblk, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a return statement, of the form:
	 *
	 * <pre>
	 * ReturnStmt ::= 'return' [ Expr ] ';'
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt.Return parseReturnStmt(Context context) {
		int start = index;
		// Every return statement begins with the return keyword!
		matchKeyword("return");
		Expr e = null;
		// A return statement may optionally have a return expression.
		if (!(tokens.get(index) instanceof SemiColon)) {
			e = parseExpr(context);
		}
		// Done.
		return new Stmt.Return(e, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a While statement of the form:
	 *
	 * <pre>
	 * WhileStmt ::= 'while' '(' Expr ')' StmtBlock
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt parseWhileStmt(Context context) {
		int start = index;
		matchKeyword("while");
		Expr condition = parseExpr(context);
		int end = index;
		List<Stmt> blk = parseStatementBlock(context.setInLoop().clone());
		return new Stmt.While(condition, blk, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a for statement, of the form:
	 *
	 * <pre>
	 * ForStmt ::=
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt parseForStmt(Context context) {
		// Clone context to ensure variables declared in this loop are scoped only for
		// the loop.
		context = context.clone();
		//
		int start = index;
		matchKeyword("for");
		match("(");
		Stmt.VariableDeclaration declaration = parseVariableDeclaration(context);
		match(";");
		Expr condition = parseExpr(context);
		match(";");
		Stmt increment = parseUnitStatement(context);
		int end = index;
		match(")");
		List<Stmt> blk = parseStatementBlock(context.setInLoop());

		return new Stmt.For(declaration, condition, increment, blk, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a Switch statement of the form:
	 *
	 * <pre>
	 * SwitchStmt ::= 'switch' '(' Expr ')' '{' CaseBlock+ [DefaultBlock] '}'
	 *
	 * CaseBlock ::= 'case' Expr ':' Stmt*
	 *
	 * DefaultBlock ::= 'default' ':' Stmt*
	 * </pre>
	 *
	 * @return
	 */
	protected Stmt parseSwitchStmt(Context context) {
		int start = index;
		matchKeyword("switch");
		Expr expr = parseExpr(context);
		int end = index;
		match("{");
		List<Stmt.Case> cases = parseSwitchCases(context.setInSwitch());
		match("}");

		return new Stmt.Switch(expr, cases, sourceAttr(start, end - 1));
	}

	protected Stmt parseBreakStmt(Context context) {
		int start = index;
		Keyword k = matchKeyword("break");
		if(!context.inLoop() && !context.inSwitch()) {
			syntaxError("break outside switch or loop",k);
		}
		return new Stmt.Break(sourceAttr(start, index - 1));
	}

	protected Stmt parseContinueStmt(Context context) {
		int start = index;
		Keyword k = matchKeyword("continue");
		if(!context.inLoop()) {
			syntaxError("continue outside of loop",k);
		}
		return new Stmt.Continue(sourceAttr(start, index - 1));
	}
	/**
	 * Parse the list of zero or more case blocks which make up a switch
	 * statement.
	 *
	 * @return
	 */
	protected List<Stmt.Case> parseSwitchCases(Context context) {
		ArrayList<Stmt.Case> cases = new ArrayList<>();
		HashSet<Object> values = new HashSet<>();

		while(!(tokens.get(index) instanceof RightCurly)) {
			int start = index;
			Expr.Literal value;
			Token lookahead = tokens.get(index);
			if(lookahead.text.equals("case")) {
				// This is a case block
				matchKeyword("case");
				value = parseConstant();
				if(values.contains(value.getValue())) {
					syntaxError("duplicate case",value);
				} else {
					values.add(value.getValue());
				}
				match(":");
			} else {
				// This must be a default block
				matchKeyword("default");
				match(":");
				value = null;
			}
			int end = index;
			// Parse the case body
			ArrayList<Stmt> body = new ArrayList<>();
			while (!(tokens.get(index) instanceof RightCurly)
					&& !(tokens.get(index).text.equals("case")) && !(tokens.get(index).text.equals("default"))) {
				body.add(parseStatement(context));
			}
			cases.add(new Stmt.Case(value, body, sourceAttr(start,end-1)));
		}
		return cases;
	}

	protected Expr.Literal parseConstant() {
		Expr e = parseExpr(new Context());
		Object constant = parseConstant(e);
		return new Expr.Literal(constant,e.attributes());
	}

	protected Object parseConstant(Expr e) {
		if(e instanceof Expr.Literal) {
			return ((Expr.Literal) e).getValue();
		} else if(e instanceof Expr.ArrayInitialiser) {
			Expr.ArrayInitialiser ai = (Expr.ArrayInitialiser) e;
			ArrayList<Object> vals = new ArrayList<>();
			for(Expr element : ai.getArguments()) {
				vals.add(parseConstant(element));
			}
			return vals;
		} else if(e instanceof Expr.RecordConstructor) {
			Expr.RecordConstructor rc = (Expr.RecordConstructor) e;
			HashMap<String,Object> vals = new HashMap<>();
			for(Pair<String,Expr> p : rc.getFields()) {
				vals.put(p.first(), parseConstant(p.second()));
			}
			return vals;
		} else {
			// Problem
			syntaxError("constant expression expected", e);
			return null;
		}
	}

	protected Expr parseExpr(Context context) {
		checkNotEof();
		int start = index;
		Expr c1 = parseLogicalConnective(context);
		// Look for type test
		if (tokens.get(index).text.equals("is")) {
			matchKeyword("is");
			Type type = parseType();
			return new Expr.Is(c1,type, sourceAttr(start, index - 1));
		} else {
			return c1;
		}
	}

	protected Expr parseLogicalConnective(Context context) {
		checkNotEof();
		int start = index;
		// Parse term
		Expr expr = parseComparator(context);
		// See whether there is an infix operator trailing after term
		Token lookahead = tryAndMatch(INFIX_CONNECTIVES);
		if (lookahead != null) {
			Expr.BOp op = getInfixOperator(lookahead);
			// Yes, there is so try and parse operator sequence.
			do {
				Expr t = parseComparator(context);
				expr = new Expr.Binary(op, expr, t, sourceAttr(start, index - 1));
			} while (tryAndMatch(lookahead.getClass()) != null);
			// Check for ambiguous operator expression
			if ((lookahead = tryAndMatch(INFIX_CONNECTIVES)) != null) {
				// If we get here, then it means we parsed a sequence of 1 or
				// more operators of the same kind. But, now, we find another
				// operator of a different kind.
				syntaxError("ambiguous expression encountered (braces required)", lookahead);
			}
			//
		}
		return expr;
	}

	protected Expr parseComparator(Context context) {
		checkNotEof();
		int start = index;
		// Parse term
		Expr expr = parseArithmeticOperator(context);
		// See whether there is an infix operator trailing after term
		Token lookahead = tryAndMatch(INFIX_COMPARATORS);
		if (lookahead != null) {
			Expr.BOp op = getInfixOperator(lookahead);
			Expr t = parseArithmeticOperator(context);
			expr = new Expr.Binary(op, expr, t, sourceAttr(start, index - 1));
		}
		return expr;
	}

	protected Expr parseArithmeticOperator(Context context) {
		checkNotEof();
		int start = index;
		// Parse term
		Expr expr = parseIndexTerm(context);
		// See whether there is an infix operator trailing after term
		Token lookahead = tryAndMatch(INFIX_OPERATORS);
		if (lookahead != null) {
			Expr.BOp op = getInfixOperator(lookahead);
			// Yes, there is so try and parse operator sequence.
			do {
				Expr t = parseIndexTerm(context);
				expr = new Expr.Binary(op, expr, t, sourceAttr(start, index - 1));
			} while (tryAndMatch(lookahead.getClass()) != null);
			// Check for ambiguous operator expression
			if ((lookahead = tryAndMatch(INFIX_OPERATORS)) != null) {
				// If we get here, then it means we parsed a sequence of 1 or
				// more operators of the same kind. But, now, we find another
				// operator of a different kind.
				syntaxError("ambiguous expression encountered (braces required)", lookahead);
			}
			//
		}
		return expr;
	}

	protected Expr parseIndexTerm(Context context) {
		checkNotEof();
		int start = index;
		Expr lhs = parseTerm(context);

		Token lookahead = tokens.get(index);

		while (lookahead instanceof LeftSquare || lookahead instanceof Dot || lookahead instanceof MinusGreater) {
			if (lookahead instanceof LeftSquare) {
				match("[");
				Expr rhs = parseExpr(context);
				match("]");
				lhs = new Expr.ArrayAccess(lhs, rhs, sourceAttr(start, index - 1));
			} else if(lookahead instanceof Dot){
				match(".");
				String name = match(Identifier.class,"an identifier").text;
				lhs = new Expr.RecordAccess(lhs, name, sourceAttr(start, index - 1));
			} else {
				match("->");
				String name = match(Identifier.class,"an identifier").text;
				lhs = new Expr.FieldDereference(lhs, name, sourceAttr(start, index - 1));
			}
			lookahead = tokens.get(index);
		}

		return lhs;
	}

	protected Expr parseTerm(Context context) {
		checkNotEof();

		int start = index;
		Token lookahead = tokens.get(index);

		if (lookahead instanceof LeftBrace) {
			match("(");
			// First, attempt to parse cast expression
			try {
				Type t = parseType();
				match(")");
				Expr e = parseExpr(context);
				return new Expr.Cast(t,e);
			} catch(Exception ex) {
				// Now, attempt to parse brace
				index = start;
				match("(");
				Expr e = parseExpr(context);
				checkNotEof();
				match(")");
				return e;
			}
		} else if ((index + 1) < tokens.size() && lookahead instanceof Identifier
				&& tokens.get(index + 1) instanceof LeftBrace) {
			// must be a method invocation
			return parseInvokeExprOrStmt(context);
		} else if (lookahead.text.equals("null")) {
			matchKeyword("null");
			return new Expr.Literal(null, sourceAttr(start, index - 1));
		} else if (lookahead.text.equals("true")) {
			matchKeyword("true");
			return new Expr.Literal(true, sourceAttr(start, index - 1));
		} else if (lookahead.text.equals("false")) {
			matchKeyword("false");
			return new Expr.Literal(false, sourceAttr(start, index - 1));
		} else if (lookahead instanceof Identifier) {
			return parseVariable(context);
		} else if (lookahead instanceof Lexer.Char) {
			char val = match(Lexer.Char.class, "a character").value;
			return new Expr.Literal(Character.valueOf(val), sourceAttr(start, index - 1));
		} else if (lookahead instanceof Int) {
			int val = match(Int.class, "an integer").value;
			return new Expr.Literal(val, sourceAttr(start, index - 1));
		} else if (lookahead instanceof Strung) {
			return parseString();
		} else if (lookahead instanceof Minus) {
			return parseNegationExpr(context);
		} else if (lookahead instanceof Bar) {
			return parseArrayLengthExpr(context);
		} else if (lookahead instanceof LeftSquare) {
			return parseArrayInitialiserOrGeneratorExpr(context);
		} else if (lookahead instanceof LeftCurly) {
			return parseRecordInitialiserExpr(context);
		} else if (lookahead instanceof Shreak) {
			match("!");
			return new Expr.Unary(Expr.UOp.NOT, parseTerm(context), sourceAttr(start, index - 1));
		} else if (lookahead instanceof Star) {
			match("*");
			return new Expr.Dereference(parseTerm(context), sourceAttr(start, index - 1));
		} else if (lookahead.text.equals("new")) {
			matchKeyword("new");
			return new Expr.Unary(Expr.UOp.NEW, parseExpr(context), sourceAttr(start, index - 1));
		}
		syntaxError("unrecognised term (\"" + lookahead.text + "\")", lookahead);
		return null;
	}

	protected Expr parseVariable(Context context) {
		int start = index;
		Identifier var = match(Identifier.class,"an identifier");
		if(context.isDeclared(var.text)) {
			return new Expr.Variable(var.text, sourceAttr(start, index - 1));
		} else {
			syntaxError("unknown variable " + var.text, var);
			return null;
		}
	}

	protected Expr parseArrayInitialiserOrGeneratorExpr(Context context) {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<>();
		match("[");
		checkNotEof();
		Token token = tokens.get(index);
		// Check for array generator expression
		if (!(token instanceof RightSquare)) {
			exprs.add(parseExpr(context));
			checkNotEof();
			token = tokens.get(index);
			if (token instanceof SemiColon) {
				// Array generator
				match(";");
				exprs.add(parseExpr(context));
				checkNotEof();
				match("]");
				return new Expr.ArrayGenerator(exprs.get(0), exprs.get(1), sourceAttr(start, index - 1));
			} else {
				// Array initialiser
				while (!(token instanceof RightSquare)) {
					match(",");
					exprs.add(parseExpr(context));
					checkNotEof();
					token = tokens.get(index);
				}
			}
		}

		match("]");
		return new Expr.ArrayInitialiser(exprs, sourceAttr(start, index - 1));
	}

	protected Expr parseRecordInitialiserExpr(Context context) {
		int start = index;
		match("{");
		HashSet<String> keys = new HashSet<>();
		ArrayList<Pair<String, Expr>> exprs = new ArrayList<>();
		checkNotEof();
		Token token = tokens.get(index);
		boolean firstTime = true;
		while (!(token instanceof RightCurly)) {
			if (!firstTime) {
				match(",");
			}
			firstTime = false;

			checkNotEof();
			token = tokens.get(index);
			Identifier n = match(Identifier.class,"an identifier");

			if (keys.contains(n.text)) {
				syntaxError("duplicate tuple key", n);
			}

			match(":");

			Expr e = parseExpr(context);
			exprs.add(new Pair<>(n.text, e));
			keys.add(n.text);
			checkNotEof();
			token = tokens.get(index);
		}
		match("}");
		return new Expr.RecordConstructor(exprs, sourceAttr(start, index - 1));
	}

	protected Expr parseArrayLengthExpr(Context context) {
		int start = index;
		match("|");
		Expr e = parseIndexTerm(context);
		match("|");
		return new Expr.Unary(Expr.UOp.LENGTHOF, e, sourceAttr(start, index - 1));
	}

	protected Expr parseNegationExpr(Context context) {
		int start = index;
		match("-");
		Expr e = parseIndexTerm(context);

		if (e instanceof Expr.Literal) {
			Expr.Literal c = (Expr.Literal) e;
			if (c.getValue() instanceof Integer) {
				int bi = (Integer) c.getValue();
				return new Expr.Literal(-bi, sourceAttr(start, index));
			}
		}

		return new Expr.Unary(Expr.UOp.NEG, e, sourceAttr(start, index));
	}

	protected Expr.Invoke parseInvokeExprOrStmt(Context context) {
		int start = index;
		Identifier name = match(Identifier.class,"an identifier");
		if(!userDefinedMethods.containsKey(name.text)) {
			syntaxError("unknown method " + name.text + "()",name);
		}
		match("(");
		boolean firstTime = true;
		ArrayList<Expr> args = new ArrayList<>();
		while (!(tokens.get(index) instanceof RightBrace)) {
			if (!firstTime) {
				match(",");
			} else {
				firstTime = false;
			}
			Expr e = parseExpr(context);

			args.add(e);
		}
		match(")");
		WhileFile.MethodDecl m = userDefinedMethods.get(name.text);
		Expr.Invoke invoke = new Expr.Invoke(name.text, args, sourceAttr(start, index - 1));

		if(m.getParameters().size() != args.size()) {
			syntaxError("incorrect number of arguments provided",invoke);
		}

		return invoke;
	}

	protected Expr parseString() {
		int start = index;
		String s = match(Strung.class, "a string").string;
		return new Expr.Literal(s, sourceAttr(start, index - 1));
	}

	protected Type parseType() {
		int start = index;
		checkNotEof();
		ArrayList<Type> types = new ArrayList<>();
		// Determine base type
		types.add(parseArrayOrReferenceType());
		// Determine array level (if any)
		while (tokens.get(index) instanceof Bar) {
			match("|");
			types.add(parseArrayOrReferenceType());
		}
		// Done
		if (types.size() == 1) {
			return types.get(0);
		} else {
			return new Type.Union(types, sourceAttr(start, index - 1));
		}
	}

	protected Type parseArrayOrReferenceType() {
		// Start parsing from index 'index'
		int start  = index;

		checkNotEof();
		Token lookahead = tokens.get(index);
		if(lookahead instanceof Ampersand) {
			Type t;
			// Try parsing unique reference type first
			try {
				t = parseUniqueReferenceType();
			}

			// If there is a syntax error, return reference type instead
			catch (SyntaxError syntaxError) {
				// Redo parsing by resetting the value of 'index' to start
				index = start;

				// Try parsing reference type instead
				t = parseReferenceType();
			}

			return t;
		} else {
			return parseArrayType();
		}
	}

	/**
	 * Creating a method to parse a unique reference type
	 *
	 * @return A 'Type.UniqueReference' class instance.
	 * */
	protected Type parseUniqueReferenceType() {
		// Start parsing from index 'index'
		int start = index;

		// Ensuring that an ampersand is available since all reference types start with ampersands.
		match("&");

		// Parsing the type inside the brackets in case brackets are available.
		Type bracketedType = parseBracketedType();

		// Ensuring that a colon followed with '1' exists.
		match(":");
		match("1");

		// Returning the unique reference type
		return new Type.UniqueReference(bracketedType, sourceAttr(start, index - 1));
	}

	protected Type parseReferenceType() {
		int start = index;
		match("&");
		Type t = parseBracketedType();
		// record type
		return new Type.Reference(t, sourceAttr(start, index - 1));
	}

	protected Type parseArrayType() {
		int start = index;
		// Determine base type
		Type type = parseBracketedType();
		// Determine array level (if any)
		while (tokens.get(index) instanceof LeftSquare) {
			match("[");
			match("]");
			type = new Type.Array(type, sourceAttr(start, index - 1));
		}
		// Done
		return type;
	}

	protected Type parseBracketedType() {
		checkNotEof();
		Token lookahead = tokens.get(index);
		if(lookahead instanceof LeftBrace) {
			match("(");
			Type t = parseType();
			match(")");
			return t;
		} else {
			return parseBaseType();
		}
	}


	/**
	 * Parse a "base" type. That is any type which could be the element of an
	 * array type.
	 *
	 * @return
	 */
	protected Type parseBaseType() {
		int start = index;
		Token token = tokens.get(index);
		if (token.text.equals("int")) {
			matchKeyword("int");
			return new Type.Int(sourceAttr(start, index - 1));
		} else if (token.text.equals("void")) {
			matchKeyword("void");
			return new Type.Void(sourceAttr(start, index - 1));
		} else if (token.text.equals("null")) {
			matchKeyword("null");
			return new Type.Null(sourceAttr(start, index - 1));
		} else if (token.text.equals("bool")) {
			matchKeyword("bool");
			return new Type.Bool(sourceAttr(start, index - 1));
		} else if (token instanceof LeftCurly) {
			// record type
			return parseRecordType();
		} else {
			Identifier id = match(Identifier.class,"an identifier");
			if(userDefinedTypes.contains(id.text)) {
				return new Type.Named(id.text, sourceAttr(start, index - 1));
			} else {
				syntaxError("unknown type " + id.text,id);
				return null;
			}
		}
	}

	/**
	 * Parse a record type, which takes the form:
	 *
	 * <pre>
	 * RecordType ::= '{' Type Indent ( ',' Type Indent )* '}'
	 * </pre>
	 *
	 * This function additionally checks that no two fields have the same name.
	 *
	 * @return
	 */
	protected Type.Record parseRecordType() {
		int start = index;
		match("{");
		// The fields set tracks the field names we've already seen
		HashSet<String> fields = new HashSet<>();
		ArrayList<Pair<Type,String>> types = new ArrayList<>();

		Token token = tokens.get(index);
		boolean firstTime = true;
		while (!(token instanceof RightCurly)) {
			if (!firstTime) {
				match(",");
			}
			firstTime = false;
			checkNotEof();

			token = tokens.get(index);
			Type type = parseType();
			Identifier n = match(Identifier.class,"an identifier");

			if (fields.contains(n.text)) {
				syntaxError("duplicate field", n);
			}
			types.add(new Pair<>(type,n.text));
			fields.add(n.text);
			checkNotEof();
			token = tokens.get(index);
		}
		match("}");
		return new Type.Record(types, sourceAttr(start, index - 1));
	}

	/**
	 * Convert a given token into an infix operator kind.
	 *
	 * @param token
	 * @return
	 */
	protected Expr.BOp getInfixOperator(Token token) {
		if(token instanceof LogicalAnd) {
			return Expr.BOp.AND;
		} else if(token instanceof LogicalOr) {
			return Expr.BOp.OR;
		} else if(token instanceof LessEquals) {
			return Expr.BOp.LTEQ;
		} else if(token instanceof LeftAngle) {
			return Expr.BOp.LT;
		} else if(token instanceof GreaterEquals) {
			return Expr.BOp.GTEQ;
		} else if(token instanceof RightAngle) {
			return Expr.BOp.GT;
		} else if(token instanceof EqualsEquals) {
			return Expr.BOp.EQ;
		} else if(token instanceof NotEquals) {
			return Expr.BOp.NEQ;
		} else if(token instanceof Plus) {
			return Expr.BOp.ADD;
		} else if(token instanceof Minus) {
			return Expr.BOp.SUB;
		} else if(token instanceof Star) {
			return Expr.BOp.MUL;
		} else if(token instanceof RightSlash) {
			return Expr.BOp.DIV;
		} else if(token instanceof Percent) {
			return Expr.BOp.REM;
		} else {
			throw new IllegalArgumentException("invalid binary operator '" + token + "'");
		}
	}

	/**
	 * The set of token kinds which correspond to binary infix conectives.
	 */
	@SuppressWarnings("unchecked")
	protected static Class<? extends Token>[] INFIX_CONNECTIVES = new Class[] {
			LogicalAnd.class, LogicalOr.class
	};

	/**
	 * The set of token kinds which correspond to binary infix comparators.
	 */
	@SuppressWarnings("unchecked")
	protected static Class<? extends Token>[] INFIX_COMPARATORS = new Class[] {
			LessEquals.class, LeftAngle.class, GreaterEquals.class, RightAngle.class, EqualsEquals.class,
			NotEquals.class };

	/**
	 * The set of token kinds which correspond to binary or n-ary infix
	 * operators.
	 */
	@SuppressWarnings("unchecked")
	protected static Class<? extends Token>[] INFIX_OPERATORS = new Class[] {
			Plus.class, Minus.class, Star.class, RightSlash.class, Percent.class };


	/**
	 * Check have not reached the end of file yet. This method is called in
	 * positions where we are expecting something to follow.
	 */
	protected void checkNotEof() {
		if (index >= tokens.size()) {
			throw new SyntaxError("unexpected end-of-file", whileFile, index - 1, index - 1);
		}
		return;
	}

	/**
	 * Attempt to match a given token(s) from a list of candidate tokens. Note that,
	 * in the case it fails to match, then the index will be unchanged. This latter
	 * point is important, otherwise we could accidentally gobble up some important
	 * indentation. If more than one kind is provided then this will try to match
	 * any of them.
	 *
	 * @param kinds
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Token tryAndMatch(Class<? extends Token>... kinds) {
		if (index < tokens.size()) {
			Token lookahead = tokens.get(index);
			for (int i = 0; i != kinds.length; ++i) {
				if (kinds[i].isInstance(lookahead)) {
					index = index + 1;
					return lookahead;
				}
			}
		}
		return null;
	}

	protected Token match(String op) {
		checkNotEof();
		Token t = tokens.get(index);
		if (!t.text.equals(op)) {
			syntaxError("expecting '" + op + "', found '" + t.text + "'", t);
		}
		index = index + 1;
		return t;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Token> T match(Class<T> c, String name) {
		checkNotEof();
		Token t = tokens.get(index);
		if (!c.isInstance(t)) {
			syntaxError("expecting " + name + ", found '" + t.text + "'", t);
		}
		index = index + 1;
		return (T) t;
	}

	protected Keyword matchKeyword(String keyword) {
		checkNotEof();
		Token t = tokens.get(index);
		if (t instanceof Keyword) {
			if (t.text.equals(keyword)) {
				index = index + 1;
				return (Keyword) t;
			}
		}
		syntaxError("keyword " + keyword + " expected.", t);
		return null;
	}

	protected Attribute.Source sourceAttr(int start, int end) {
		Token t1 = tokens.get(start);
		Token t2 = tokens.get(end);
		return new Attribute.Source(t1.start, t2.end());
	}

	protected void syntaxError(String msg, Expr e) {
		Attribute.Source loc = e.attribute(Attribute.Source.class);
		throw new SyntaxError(msg, whileFile, loc.start, loc.end);
	}

	protected void syntaxError(String msg, Token t) {
		throw new SyntaxError(msg, whileFile, t.start, t.start + t.text.length() - 1);
	}

	/**
	 * Provides information about the current context in which the parser is
	 * operating.
	 *
	 * @author David J. Pearce
	 *
	 */
	protected static class Context {
		/**
		 * indicates whether the current context is within a loop or not.
		 */
		protected final boolean inLoop;

		/**
		 * indicates whether the current context is within a switch or not.
		 */
		protected final boolean inSwitch;

		/**
		 * indicates the set of declared variables within the current context;
		 */
		protected final Set<String> environment;

		public Context() {
			this.inLoop = false;
			this.inSwitch = false;
			this.environment = new HashSet<>();
		}

		protected Context(boolean inLoop, boolean inSwitch, Set<String> environment) {
			this.inLoop = inLoop;
			this.inSwitch = inSwitch;
			this.environment = environment;
		}

		/**
		 * Check whether the given context is within an enclosing loop or not.
		 *
		 * @return
		 */
		public boolean inLoop() {
			return inLoop;
		}

		/**
		 * Check whether the given context is within an enclosing switch or not.
		 *
		 * @return
		 */
		public boolean inSwitch() {
			return inSwitch;
		}

		/**
		 * Check whether a given variable is declared in this context or not.
		 *
		 * @param variable
		 * @return
		 */
		public boolean isDeclared(String variable) {
			return environment.contains(variable);
		}

		public void declare(String variable) {
			environment.add(variable);
		}

		/**
		 * Create a copy of this context which is enclosed within a loop
		 * statement.
		 *
		 * @return
		 */
		public Context setInLoop() {
			return new Context(true,inSwitch,environment);
		}

		/**
		 * Create a copy of this context which is enclosed within a switch
		 * statement.
		 *
		 * @return
		 */
		public Context setInSwitch() {
			return new Context(inLoop,true,environment);
		}

		/**
		 * Create a new clone of this context
		 */
		@Override
		public Context clone() {
			return new Context(inLoop,inSwitch,new HashSet<>(environment));
		}
	}
}
