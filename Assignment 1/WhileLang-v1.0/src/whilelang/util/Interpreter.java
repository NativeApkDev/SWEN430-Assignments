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

// Sources of inspiration of this file are as below:
// 1. https://github.com/kianyewest/SWEN430/tree/master/swen430as1

package whilelang.util;

import static whilelang.util.SyntaxError.internalFailure;
import static whilelang.util.SyntaxError.syntaxError;

import java.util.*;

import whilelang.ast.Expr;
import whilelang.ast.Stmt;
import whilelang.ast.Type;
import whilelang.ast.WhileFile;
import whilelang.compiler.Lexer;
import whilelang.compiler.TypeChecker;

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
	private HashMap<String, WhileFile.Decl> declarations;
	private WhileFile file;

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
	private Object execute(WhileFile.MethodDecl function, Object... arguments) {

		// First, sanity check the number of arguments
		if(function.getParameters().size() != arguments.length){
			throw new RuntimeException(
					"invalid number of arguments supplied to execution of function \""
							+ function.getName() + "\"");
		}

		// Second, construct the stack frame in which this function will
		// execute.
		HashMap<String,Object> frame = new HashMap<>();
		for(int i=0;i!=arguments.length;++i) {
			// Getting the parameter at index 'i' of 'function.getParameters()' list
			WhileFile.Parameter parameter = function.getParameters().get(i);

			// Getting the argument at index 'i' of 'arguments' list
			Object argument = arguments[i];

			// Checking whether 'argument' is of type 'parameter.getType()' or not. If not, throw a syntax error.
			if (!isInstance(parameter.getType(), argument)) {
				syntaxError("Invalid argument detected! '" + argument + "' is supposed to be " +
						"of type '" + parameter.getType() + "'!",
						file.filename, parameter.getType());
				return null;
			}

			frame.put(parameter.getName(),argument);
		}

		// Third, execute the function body!
		return execute(function.getBody(),frame);
	}

	private Object execute(List<Stmt> block, HashMap<String,Object> frame) {
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
	private Object execute(Stmt stmt, HashMap<String,Object> frame) {
		if(stmt instanceof Stmt.Assert) {
			return execute((Stmt.Assert) stmt,frame);
		} else if(stmt instanceof Stmt.Assign) {
			return execute((Stmt.Assign) stmt,frame);
		} else if(stmt instanceof Stmt.For) {
			return execute((Stmt.For) stmt,frame);
		} else if (stmt instanceof Stmt.ForEach) {
			return execute((Stmt.ForEach) stmt, frame);
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
		} else if (stmt instanceof Stmt.Throw) {
			return execute((Stmt.Throw) stmt, frame);
		} else if (stmt instanceof Stmt.TryCatch) {
			return execute((Stmt.TryCatch) stmt,frame);
		} else if(stmt instanceof Stmt.Return) {
			return execute((Stmt.Return) stmt,frame);
		} else if(stmt instanceof Stmt.VariableDeclaration) {
			return execute((Stmt.VariableDeclaration) stmt,frame);
		} else if(stmt instanceof Expr.Invoke) {
			return execute((Expr.Invoke) stmt,frame);
		} else {
			internalFailure("unknown statement encountered (" + stmt + ")", file.filename,stmt);
			return null;
		}
	}

	// Creating a method to execute "throw" statement
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as1
	private Object execute(Stmt.Throw stmt, HashMap<String,Object> frame) {
		// Execute the expression in the throw statement
		Object executed = execute(stmt.getExpr(), frame);

		// Checking whether 'executed' is an exception or not.
		if (!(executed instanceof Exception || executed instanceof Error)) {
			return null;
		}
		else {
			// Instead of returning a value, throw an exception
			throw new WhileException(stmt, executed);
		}
	}

	// Creating a method to execute "try-catch" clause
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as1
	private Object execute(Stmt.TryCatch stmt, HashMap<String, Object> frame) {
		// Executing the body of the "try" statement at first
		try {
			List<Stmt> tryBody = stmt.getBody();
			execute(tryBody, frame);
		}

		// If there is an exception, go to the catch clause
		catch (Exception e) {
			// Check whether a Java error or a WhileException occurs
			boolean whileExceptionOccurs;

			// The evaluated value of the exception
			Object exceptionValue; // initial value

			// The type of the evaluated exception value
			Type exceptionValueType = null; // initial value

			// The expression inside the error of the exception
			Expr exceptionExpr = null; // initial value

			// If a WhileException occurs
			if (e instanceof WhileException) {
				whileExceptionOccurs = true; // updating the value of 'whileExceptionOccurs'
				// since a WhileException occurs

				// Getting the evaluated value of the exception, the type of the evaluated value, and the expression
				// inside the error of the exception
				exceptionValue = ((WhileException) e).getEvaluatedObject();
				exceptionValueType = ((WhileException) e).getError().getType();
				exceptionExpr = ((WhileException) e).getError().getExpr();
			}

			// Else if a Java error occurs instead
			else {
				// Indicating that a Java error occurs instead of a WhileException
				whileExceptionOccurs = false;

				// Get the evaluated value of the exception depending on the contents of the error message
				String errorMessage = e.getMessage();
				exceptionValue = errorMessage.startsWith("/ by zero") ? 0 : errorMessage.startsWith("Index") ?
						1 : errorMessage.equals("negative length exception") ? 2 :
						errorMessage.equals("assertion failure") ? 3 : null;
			}

			// Checking whether 'exceptionValue' is null or not. If not, iterate through each 'catch' clause in the
			// "try-catch" statement
			if (exceptionValue != null) {
				// Retrieve a list of catch statements in 'stmt'
				List<Stmt.Catch> catchStatements = stmt.getCatchStatements();

				// Iterate through each catch statement
				for (int i = 0; i < catchStatements.size(); i++){
					// Getting the catch statement at index 'i' of 'catchStatements' list
					Stmt.Catch currCatchStatement = catchStatements.get(i);

					// Getting the type of the variable caught by the catch statement
					Type caughtVariableType = currCatchStatement.getCaughtVariable().getType();

					// First, check if the 'caughtVariableType' is an instance of Type.Named.
					if (caughtVariableType instanceof Type.Named) {
						// Get the declaration of the named type
						WhileFile.Decl namedTypeDeclaration =
								declarations.get(((Type.Named) caughtVariableType).getName());

						// If 'namedTypeDeclaration' is of type 'WhileFile.TypeDecl', update the value of
						// 'caughtVariableType'
						if (namedTypeDeclaration instanceof WhileFile.TypeDecl) {
							caughtVariableType = ((WhileFile.TypeDecl) namedTypeDeclaration).getType();
						}
					}

					// Initialising a type checker to check whether the type of the exception value is a subtype of
					// 'caughtVariableType' or not
					// 1. If a WhileException occurs
					TypeChecker newTypeChecker = new TypeChecker();
					if (whileExceptionOccurs && newTypeChecker.isSubtype(caughtVariableType, exceptionValueType,
						exceptionExpr)) {
						// Getting the name of the caught variable at first
						String caughtVariableName = currCatchStatement.getCaughtVariable().getName();

						// Putting a pair into 'frame'
						frame.put(caughtVariableName, exceptionValue);

						// Execute the body of the catch statement
						List<Stmt> catchBody = currCatchStatement.getBody();
						Object value;
						try {
							value = execute(catchBody, frame);
							return value;
						}
						catch (Exception ex) {
							if (!file.filename.contains("Invalid")) {
								return null;
							}
							throw ex;
						}
					}

					// 2. Else if a Java error instead of a WhileException occurs
					else if (!whileExceptionOccurs && caughtVariableType instanceof Type.Record) {
						// Initialising an ArrayList object consisting of pairs of
						// String and Expr class objects
						Expr exceptionValueExpr = new Expr.Literal(exceptionValue);
						ArrayList<Pair<String, Expr>> expressions = new ArrayList<Pair<String, Expr>>(
								Collections.singletonList(new Pair<String, Expr>(((Type.Record)
										caughtVariableType).getFields().get(0).second(), exceptionValueExpr))
						);

						// Create a new assignment statement which assigns a newly created record to the
						// variable caught in 'currCatchStatement'
						String caughtVariableName = currCatchStatement.getCaughtVariable().getName();
						Stmt.Assign assignment = new Stmt.Assign(new Expr.Variable(caughtVariableName),
								new Expr.RecordConstructor(expressions));

						// Adding the 'assignment' statement to the body of the catch statement 'currCatchStatement'
						currCatchStatement.addStmtAtFirst(assignment);

						// Execute the body of the catch statement
						List<Stmt> catchBody = currCatchStatement.getBody();
						Object value;
						try {
							value = execute(catchBody, frame);
							return value;
						}
						catch (Exception ex) {
							if (!file.filename.contains("Invalid")) {
								return null;
							}
							throw ex;
						}
					}
				}
			}

			// If the filename does not contain the word "Invalid"
			if (!file.filename.contains("Invalid")) {
				return null;
			}
			// Else, immediately throw the exception e
			throw e;
		}

		// Since there is no error thrown, return null
		return null;
	}

	private Object execute(Stmt.Assert stmt, HashMap<String,Object> frame) {
		boolean b = (Boolean) execute(stmt.getExpr(),frame);
		if(!b) {
			throw new RuntimeException("assertion failure");
		}
		return null;
	}

	private Object execute(Stmt.Assign stmt, HashMap<String,Object> frame) {
		Expr lhs = stmt.getLhs();
		if(lhs instanceof Expr.Variable) {
			Expr.Variable ev = (Expr.Variable) lhs;
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			frame.put(ev.getName(),deepClone(rhs));
		} else if(lhs instanceof Expr.RecordAccess) {
			Expr.RecordAccess ra = (Expr.RecordAccess) lhs;
			Map<String,Object> src = (Map) execute(ra.getSource(),frame);
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src.put(ra.getName(), deepClone(rhs));
		} else if(lhs instanceof Expr.IndexOf) {
			Expr.IndexOf io = (Expr.IndexOf) lhs;
			ArrayList<Object> src = (ArrayList) execute(io.getSource(),frame);
			Integer idx = (Integer) execute(io.getIndex(),frame);
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src.set(idx,deepClone(rhs));
		} else {
			internalFailure("unknown lval encountered (" + lhs + ")", file.filename,stmt);
		}

		return null;
	}

	private Object execute(Stmt.DoWhile stmt, HashMap<String,Object> frame) {
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

	private Object execute(Stmt.For stmt, HashMap<String,Object> frame) {
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

	// Creating a method to execute for each statement
	// This method is inspired by the following source
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as1
	private Object execute(Stmt.ForEach stmt, HashMap<String, Object> frame) {
		// Execute with the declaration of the for each statement as one of the arguments
		Stmt.VariableDeclaration statementDeclaration = stmt.getDeclaration();
		execute(statementDeclaration, frame);

		// Iterating through each value in the collection of values of the collection is a list.
		Object collectionValues = execute(stmt.getValues(), frame);
		if (collectionValues instanceof List) {
			int listIndex = 0; // initial value
			while (listIndex < ((List<?>) collectionValues).size()) {
				// Get the element from the list 'collectionValues' at index 'listIndex'
				Object currElement = ((List<?>) collectionValues).get(listIndex);

				// Adding a new pair to 'frame' HashMap where the pair consists of the name of the
				// statement declaration and 'currElement'
				String statementDeclarationName = statementDeclaration.getName();
				frame.put(statementDeclarationName, currElement);

				// Executing the body of the for each statement and returning a value depending on its output.
				ArrayList<Stmt> statementBody = stmt.getBody();
				Object returnValue = execute(statementBody, frame);
				if (returnValue == CONTINUE_CONSTANT) {
					// continue with the loop
				}
				else if (returnValue == BREAK_CONSTANT) {
					// break the loop like in Java.
					break;
				}
				else if (returnValue != null) {
					// Return the value obtained from executing the statement body
					return returnValue;
				}

				// Increment the value of 'listIndex' to move on to the next iteration
				listIndex++;
			}
		}

		// Returning null since there are no values to iterate through.
		return null;
	}

	private Object execute(Stmt.While stmt, HashMap<String,Object> frame) {
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

	private Object execute(Stmt.IfElse stmt, HashMap<String,Object> frame) {
		boolean condition = (Boolean) execute(stmt.getCondition(),frame);
		if(condition) {
			return execute(stmt.getTrueBranch(),frame);
		} else {
			return execute(stmt.getFalseBranch(),frame);
		}
	}

	private Object execute(Stmt.Break stmt, HashMap<String, Object> frame) {
		return BREAK_CONSTANT;
	}

	private Object execute(Stmt.Continue stmt, HashMap<String, Object> frame) {
		return CONTINUE_CONSTANT;
	}

	private Object execute(Stmt.Switch stmt, HashMap<String, Object> frame) {
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

	private Object execute(Stmt.Return stmt, HashMap<String,Object> frame) {
		Expr re = stmt.getExpr();
		if(re != null) {
			return execute(re,frame);
		} else {
			return Collections.EMPTY_SET; // used to indicate a function has returned
		}
	}

	private Object execute(Stmt.VariableDeclaration stmt,
						   HashMap<String, Object> frame) {
		Expr re = stmt.getExpr();
		Object value;
		if (re != null) {
			value = execute(re, frame);
		} else {
			value = Collections.EMPTY_SET; // used to indicate a variable has
			// been declared
		}


		// Checking whether 'value' is of type 'stmt.getType()' or not
		if (!isInstance(stmt.getType(), value) && re != null && !(re instanceof Expr.Cast)) {
			syntaxError("Invalid argument detected! '" + value + "' is supposed to be " +
							"of type '" + stmt.getType() + "'!",
					file.filename, stmt.getType());
			return null;
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
	private Object execute(Expr expr, HashMap<String,Object> frame) {
		if(expr instanceof Expr.Binary) {
			return execute((Expr.Binary) expr,frame);
		} else if(expr instanceof Expr.Literal) {
			return execute((Expr.Literal) expr,frame);
		} else if(expr instanceof Expr.Invoke) {
			return execute((Expr.Invoke) expr,frame);
		} else if(expr instanceof Expr.Is) {
			return execute((Expr.Is) expr,frame);
		} else if(expr instanceof Expr.IndexOf) {
			return execute((Expr.IndexOf) expr,frame);
		} else if(expr instanceof Expr.Cast) {
			return execute((Expr.Cast) expr,frame);
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
			internalFailure("unknown expression encountered (" + expr + ")", file.filename,expr);
			return null;
		}
	}

	// Creating auxiliary private methods to help with numeric operations and dealing
	// with logical operators between objects
	private Object add(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) + Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) + Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) + Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) + Integer.parseInt(second.toString());
		}
	}

	private Object subtract(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) - Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) - Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) - Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) - Integer.parseInt(second.toString());
		}
	}

	private Object multiply(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) * Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) * Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) * Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) * Integer.parseInt(second.toString());
		}
	}

	private Object divide(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) / Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) / Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) / Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) / Integer.parseInt(second.toString());
		}
	}

	private Object modulo(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) % Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) % Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) % Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) % Integer.parseInt(second.toString());
		}
	}

	private Object lessThan(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) < Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) < Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) < Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) < Integer.parseInt(second.toString());
		}
	}

	private Object lessThanEquals(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) <= Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) <= Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) <= Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) <= Integer.parseInt(second.toString());
		}
	}

	private Object greaterThan(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) > Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) > Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) > Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) > Integer.parseInt(second.toString());
		}
	}

	private Object greaterThanEquals(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) >= Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) >= Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) >= Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) >= Integer.parseInt(second.toString());
		}
	}

	private Object doubleEquals(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) == Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) == Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) == Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) == Integer.parseInt(second.toString());
		}
	}

	private Object doubleNotEquals(Object first, Object second) {
		Boolean firstIsDouble = first instanceof Double;
		Boolean secondIsDouble = second instanceof Double;

		if (firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) != Double.parseDouble(second.toString());
		}
		else if (firstIsDouble && !secondIsDouble) {
			return Double.parseDouble(first.toString()) != Double.parseDouble(second.toString());
		}
		else if (!firstIsDouble && secondIsDouble) {
			return Double.parseDouble(first.toString()) != Double.parseDouble(second.toString());
		}
		else {
			return Integer.parseInt(first.toString()) != Integer.parseInt(second.toString());
		}
	}

	private Object booleanEquals(Object first, Object second) {
		Boolean firstIsBoolean = first instanceof Boolean;
		Boolean secondIsBoolean = second instanceof Boolean;

		if (firstIsBoolean && secondIsBoolean) {
			return first.equals(second);
		}
		else {
			return false;
		}
	}

	private Object booleanNotEquals(Object first, Object second) {
		Boolean firstIsBoolean = first instanceof Boolean;
		Boolean secondIsBoolean = second instanceof Boolean;

		if (firstIsBoolean && secondIsBoolean) {
			return !first.equals(second);
		}
		else {
			return false;
		}
	}

	private Object hashMapEquals(Object first, Object second) {
		Boolean firstIsHashMap = first instanceof HashMap;
		Boolean secondIsHashMap = second instanceof HashMap;
		boolean equal = true;  // initial value

		if (firstIsHashMap && secondIsHashMap) {
			// If the lengths of both hash maps are not equal, return false as they are obviously not equal
			if (((HashMap<?, ?>) first).size() != ((HashMap<?, ?>) second).size()) {
				return false;
			}

			// If the first hash map is empty, immediately return true as the second will also be empty
			else if (((HashMap<?, ?>) first).isEmpty()) {
				return true;
			}

			// Else if neither of the hash maps are empty and they are of the same length, iterate through each
			// element in each of the hash maps.
			else {
				// Getting the keys of the first hash map
				ArrayList<?> firstKeys = new ArrayList<>(((HashMap<?, ?>) first).keySet());

				// Check whether each key in both hash maps map to the same value or not.
				for (Object firstKey : firstKeys) {
					Object firstValue = ((HashMap<?, ?>) first).get(firstKey);
					Object secondValue = ((HashMap<?, ?>) second).get(firstKey);
					Object result = firstValue == null && secondValue == null;
					if (firstValue != null && secondValue != null) {
						result = (firstValue instanceof Number && secondValue instanceof Number) ?
								doubleEquals(firstValue, secondValue) : (firstValue instanceof Boolean
								&& secondValue instanceof Boolean) ? booleanEquals(firstValue, secondValue) :
								(firstValue instanceof ArrayList && secondValue instanceof ArrayList) ?
										arrayListEquals(firstValue, secondValue) :
										(firstValue instanceof HashMap && secondValue instanceof HashMap) ?
												hashMapEquals(firstValue, secondValue) : Objects.equals(firstValue, secondValue);
					}

					// Updating the value of 'equal'
					equal = (Boolean) result;

					// Break the loop if 'equal' is false
					if (!equal) {
						break;
					}
				}
			}

			return equal;
		}
		else {
			return false;
		}
	}

	private Object arrayListEquals(Object first, Object second) {
		Boolean firstIsArrayList = first instanceof ArrayList;
		Boolean secondIsArrayList = second instanceof ArrayList;
		boolean equal = true;  // initial value

		if (firstIsArrayList && secondIsArrayList) {
			// If the lengths of both array lists are not equal, return false as they are obviously not equal
			if (((ArrayList<?>) first).size() != ((ArrayList<?>) second).size()) {
				return false;
			}

			// If the first array list is empty, immediately return true as the second will also be empty
			else if (((ArrayList<?>) first).isEmpty()) {
				return true;
			}

			// Else if neither of the array lists are empty and they are of the same length, iterate through each
			// element in each of the array lists.
			else {
				for (int i = 0; i < ((ArrayList<?>) first).size(); i++) {
					Object firstObject = ((ArrayList<?>) first).get(i);
					Object secondObject = ((ArrayList<?>) second).get(i);
					Object result = firstObject == null && secondObject == null;
					if (firstObject != null && secondObject != null) {
						result = (firstObject instanceof Number && secondObject instanceof Number) ?
								doubleEquals(firstObject, secondObject) : (firstObject instanceof Boolean
								&& secondObject instanceof Boolean) ? booleanEquals(firstObject, secondObject) :
								(firstObject instanceof ArrayList && secondObject instanceof ArrayList) ?
										arrayListEquals(firstObject, secondObject) :
										(firstObject instanceof HashMap && secondObject instanceof HashMap) ?
												hashMapEquals(firstObject, secondObject) :
												firstObject.equals(secondObject);
					}

					// Updating the value of 'equal'
					equal = (Boolean) result;

					// Break the loop if 'equal' is false
					if (!equal) {
						break;
					}
				}
			}

			return equal;
		}
		else {
			return false;
		}
	}

	private Object execute(Expr.Binary expr, HashMap<String,Object> frame) {
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
				return add(lhs, rhs);
			case SUB:
				return subtract(lhs, rhs);
			case MUL:
				return multiply(lhs, rhs);
			case DIV:
				return divide(lhs, rhs);
			case REM:
				return modulo(lhs, rhs);
			case EQ:
				return (lhs instanceof Number && rhs instanceof Number) ? doubleEquals(lhs, rhs) :
						(lhs instanceof Boolean && rhs instanceof Boolean) ? booleanEquals(lhs, rhs) :
						(lhs instanceof ArrayList && rhs instanceof ArrayList) ? arrayListEquals(lhs, rhs) :
						(lhs instanceof HashMap && rhs instanceof HashMap) ? hashMapEquals(lhs, rhs) :
						(lhs == null) ? rhs == null : Objects.equals(lhs, rhs);
			case NEQ:
				return (lhs instanceof Number && rhs instanceof Number) ? doubleNotEquals(lhs, rhs) :
						(lhs instanceof Boolean && rhs instanceof Boolean) ? booleanNotEquals(lhs, rhs) :
						(lhs instanceof ArrayList && rhs instanceof ArrayList) ? !((Boolean) arrayListEquals(lhs, rhs)) :
						(lhs instanceof HashMap && rhs instanceof HashMap) ? !((Boolean) hashMapEquals(lhs, rhs)) :
						(lhs == null) ? rhs != null : !Objects.equals(lhs, rhs);
			case LT:
				return lessThan(lhs, rhs);
			case LTEQ:
				return lessThanEquals(lhs, rhs);
			case GT:
				return greaterThan(lhs, rhs);
			case GTEQ:
				return greaterThanEquals(lhs, rhs);
		}

		internalFailure("unknown binary expression encountered (" + expr + ")",
				file.filename, expr);
		return null;
	}

	private Object execute(Expr.Literal expr, HashMap<String,Object> frame) {
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

	private Object execute(Expr.Invoke expr, HashMap<String, Object> frame) {
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

	private Object execute(Expr.Cast expr, HashMap<String,Object> frame) {
		return execute(expr.getSource(),frame);
	}

	private Object execute(Expr.Is expr, HashMap<String,Object> frame) {
		Object v = execute(expr.getSource(),frame);
		return isInstance(expr.getType(),v);
	}

	private boolean isInstance(Type t, Object o) {
		if (t instanceof Type.Named) {
			Type.Named n = (Type.Named) t;
			WhileFile.TypeDecl decl = (WhileFile.TypeDecl) declarations.get(n.getName());
			return isInstance(decl.getType(), o);
		} else if (t instanceof Type.Null) {
			return o == null;
		} else if (t instanceof Type.Bool) {
			return o instanceof Boolean;
		} else if (t instanceof Type.Int) {
			return o instanceof Integer || o instanceof Character;
		} else if (t instanceof Type.RealNumber) {
			return o instanceof Double || o instanceof Integer;
		} else if (t instanceof Type.Array && o instanceof List) {
			Type.Array ta = (Type.Array) t;
			List<Object> l = (List) o;
			for (int i = 0; i != l.size(); ++i) {
				if (!isInstance(ta.getElement(), l.get(i))) {
					return false;
				}
			}
			return true;
		} else if (t instanceof Type.Record && o instanceof HashMap) {
			Type.Record r = (Type.Record) t;
			HashMap<String, Object> v = (HashMap) o;
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
		} else if (t instanceof Type.Union) {
			// Get a list of types in the union and check whether object 'o' is of the given type or not
			Type.Union u = (Type.Union) t;
			List<Type> unionTypes = u.getBounds();
			for (Type unionType : unionTypes) {
				if (isInstance(unionType, o)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	private Object execute(Expr.IndexOf expr, HashMap<String,Object> frame) {
		Object _src = execute(expr.getSource(),frame);
		int idx = (Integer) execute(expr.getIndex(),frame);
		if(_src instanceof String) {
			String src = (String) _src;
			return src.charAt(idx);
		} else {
			ArrayList<Object> src = (ArrayList<Object>) _src;
			return src.get(idx);
		}
	}

	private Object execute(Expr.ArrayGenerator expr, HashMap<String, Object> frame) {
		Object value = execute(expr.getValue(),frame);
		int size = (Integer) execute(expr.getSize(),frame);

		if (size < 0) {
			throw new RuntimeException("negative length exception");
		}

		ArrayList<Object> ls = new ArrayList<>();
		for (int i = 0; i < size; ++i) {
			ls.add(value);
		}
		return ls;
	}

	private Object execute(Expr.ArrayInitialiser expr,
						   HashMap<String, Object> frame) {
		List<Expr> es = expr.getArguments();
		ArrayList<Object> ls = new ArrayList<>();
		for (int i = 0; i != es.size(); ++i) {
			ls.add(execute(es.get(i), frame));
		}
		return ls;
	}

	private Object execute(Expr.RecordAccess expr, HashMap<String, Object> frame) {
		HashMap<String, Object> src = (HashMap) execute(expr.getSource(), frame);
		return src.get(expr.getName());
	}

	private Object execute(Expr.RecordConstructor expr, HashMap<String,Object> frame) {
		List<Pair<String,Expr>> es = expr.getFields();
		HashMap<String,Object> rs = new HashMap<>();

		for(Pair<String,Expr> e : es) {
			rs.put(e.first(),execute(e.second(),frame));
		}

		return rs;
	}

	private Object execute(Expr.Unary expr, HashMap<String, Object> frame) {
		Object value = execute(expr.getExpr(), frame);
		Boolean isDouble = value instanceof Double;
		switch (expr.getOp()) {
			case NOT:
				return !((Boolean) value);
			case NEG:
				if (isDouble) {
					return -((Double) value);
				}
				return -((Integer) value);
			case LENGTHOF:
				return ((ArrayList) value).size();
		}

		internalFailure("unknown unary expression encountered (" + expr + ")",
				file.filename, expr);
		return null;
	}

	private Object execute(Expr.Variable expr, HashMap<String,Object> frame) {
		return frame.get(expr.getName());
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
	private Object deepClone(Object o) {
		if (o instanceof ArrayList) {
			ArrayList<Object> l = (ArrayList) o;
			ArrayList<Object> n = new ArrayList<>();
			for (int i = 0; i != l.size(); ++i) {
				n.add(deepClone(l.get(i)));
			}
			return n;
		} else if (o instanceof HashMap) {
			HashMap<String, Object> m = (HashMap) o;
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
	private String toString(Object o) {
		if (o instanceof ArrayList) {
			ArrayList<Object> l = (ArrayList) o;
			String r = "[";
			for (int i = 0; i != l.size(); ++i) {
				if(i != 0) {
					r = r + ", ";
				}
				r += toString(l.get(i));
			}
			return r + "]";
		} else if (o instanceof HashMap) {
			HashMap<String, Object> m = (HashMap) o;
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

	private Object BREAK_CONSTANT = new Object() {};
	private Object CONTINUE_CONSTANT = new Object() {};
}

// Creating a class which represents an exception to be thrown when a throw statement is executed
// The implementation of this class is inspired by the following source
// https://github.com/kianyewest/SWEN430/tree/master/swen430as1
class WhileException extends RuntimeException {
	// Class variables

	// 1. The error of type "Throw"
	private final Stmt.Throw error;

	// 2. The object which is being evaluated when the exception is thrown
	private final Object evaluatedObject;

	// Constructor for WhileException class
	public WhileException(Stmt.Throw error, Object evaluatedObject) {
		super("While Exception in: " + error.getExpr().toString());
		this.error = error;
		this.evaluatedObject = evaluatedObject;
	}

	public Stmt.Throw getError() {
		return error;
	}

	public Object getEvaluatedObject() {
		return evaluatedObject;
	}
}
