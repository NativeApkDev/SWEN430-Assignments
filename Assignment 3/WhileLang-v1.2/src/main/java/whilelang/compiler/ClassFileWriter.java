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

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import jasm.attributes.SourceFile;
import jasm.lang.Bytecode;
import jasm.lang.Bytecode.IfMode;
import jasm.lang.ClassFile;
import jasm.lang.JvmType;
import jasm.lang.JvmTypes;
import jasm.lang.Modifier;
import jasm.verifier.ClassFileVerifier;
import whilelang.ast.*;
import whilelang.util.Pair;

import static jasm.lang.Bytecode.InvokeMode.VIRTUAL;
import static jasm.lang.JvmTypes.JAVA_LANG_OBJECT;

/**
 * Responsible for translating a While source file into a JVM Class file.
 *
 * @author David J. Pearce
 *
 */

/**
 * This file is inspired by the following sources:
 * 1. https://github.com/kianyewest/SWEN430/tree/master/swen430as3
 * 2. https://ecs.wgtn.ac.nz/foswiki/pub/Courses/SWEN430_2021T2/LectureSchedule/JavaBytecode2.pdf
 * */

public class ClassFileWriter {
	// Look in the Java Virtual Machine spec for information about this this
	// number
	private static int CLASS_VERSION = 49;
	/**
	 * Enable additional debugging information by exploiting the ClassFileVerifier
	 * provided with Jasm.
	 */
	private boolean debug = false;
	/**
	 * The Jasm classfile writer to which we will write our compiled While file.
	 * This takes care of lots of the messy bits of working with the JVM.
	 */
	private jasm.io.ClassFileWriter writer;
	/**
	 * Maps each declared type to its body
	 */
	private HashMap<String,Type> declaredTypes;

	/**
	 * Maps each declared method to its JvmType
	 */
	private HashMap<String,JvmType.Function> methodTypes;

	/**
	 * Construct a ClassFileWriter which will compile a given WhileFile into a
	 * JVM class file of the given name.
	 *
	 * @param classFile
	 * @throws FileNotFoundException
	 */
	public ClassFileWriter(Path p) throws FileNotFoundException {
		writer = new jasm.io.ClassFileWriter(new FileOutputStream(p.toFile()));
		declaredTypes = new HashMap<>();
		methodTypes = new HashMap<>();
	}

	public ClassFileWriter setDebug(boolean flag) {
		this.debug = flag;
		return this;
	}

	public void write(WhileFile sourceFile) throws IOException {
		String moduleName = sourceFile.source.toFile().getName().replace(".while", "");
		// Modifiers for class
		List<Modifier> modifiers = Arrays.asList(Modifier.ACC_PUBLIC, Modifier.ACC_FINAL);
		// List of interfaces implemented by class
		List<JvmType.Clazz> implemented = new ArrayList<>();
		// Base class for this class
		JvmType.Clazz superClass = JAVA_LANG_OBJECT;
		// The class name for this class
		JvmType.Clazz owner = new JvmType.Clazz(moduleName);
		// Create the class!
		ClassFile cf = new ClassFile(CLASS_VERSION, owner, superClass, implemented, modifiers);
		// Add an attribute to the generated class file which indicates the
		// source file from which it was generated. This is useful for getting
		// better error messages out of the JVM.
		cf.attributes().add(new SourceFile(sourceFile.source.toString()));
		// Now, we need to write out all methods defined in the WhileFile. We
		// don't need to worry about other forms of declaration though, as they
		// have no meaning on the JVM.
		for(WhileFile.Decl d : sourceFile.declarations) {
			if(d instanceof WhileFile.MethodDecl) {
				ClassFile.Method m = translate((WhileFile.MethodDecl) d, owner);
				cf.methods().add(m);
			} else if(d instanceof WhileFile.TypeDecl) {
				// Add the type to the map of declared types
				WhileFile.TypeDecl td = (WhileFile.TypeDecl) d;
				declaredTypes.put(td.getName(), td.getType());
			}
		}
		if(debug) {
			new ClassFileVerifier().apply(cf);
		}
		// Finally, write the generated classfile to disk
		writer.write(cf);
	}

	private static boolean returnsJvmTypeUnionReference;
	private static boolean assignsJvmTypeUnionReference;

	/**
	 * Translate a given WhileFile method into a ClassFile method.
	 *
	 * @param decl
	 */
	private ClassFile.Method translate(WhileFile.MethodDecl method, JvmType.Clazz owner) {
		Type returnType = method.getRet();
		JvmType jvmReturnType = toJvmType(returnType);
		returnsJvmTypeUnionReference = jvmReturnType instanceof JvmType.Reference &&
		returnType instanceof Type.Union;
		// Modifiers for method
		List<Modifier> modifiers = Arrays.asList(Modifier.ACC_PUBLIC, Modifier.ACC_STATIC);
		// Construct type for method
		JvmType.Function ft = constructMethodType(method);
		// Construct method object
		ClassFile.Method cm = new ClassFile.Method(method.name(), ft, modifiers);
		// Generate bytecodes representing method body
		Context context = new Context(owner,constructMethodEnvironment(method));
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		translate(method.getBody(),context,bytecodes);
		// Handle methods with missing return statements, as these need a
		// bytecode
		addReturnAsNecessary(method,bytecodes);
		//
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes, Collections.EMPTY_LIST, cm);
		// Finally, add the jvm Code attribute to this method
		cm.attributes().add(code);
		// Done
		return cm;
	}

	/**
	 * Translate a list of statements in the While language into a series of
	 * bytecodes which implement their behaviour. The result indicates whether
	 * or not execution will fall-through to the next statement after this.
	 *
	 * @param stmts
	 *            The list of statements being translated
	 * @param environment
	 *            The current translation context
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 */
	private void translate(List<Stmt> stmts, Context context, List<Bytecode> bytecodes) {
		for(Stmt s : stmts) {
			translate(s,context,bytecodes);
		}
	}

	/**
	 * Translate a given statement in the While language into a series of one of
	 * more bytecodes which implement its behaviour.
	 *
	 * @param stmt
	 *            The statement being translated
	 * @param environment
	 *            The current translation context
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 */
	private void translate(Stmt stmt, Context context, List<Bytecode> bytecodes) {
		if(stmt instanceof Stmt.Assert) {
			translate((Stmt.Assert) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.Assign) {
			translate((Stmt.Assign) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.Break) {
			translate((Stmt.Break) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.Continue) {
			translate((Stmt.Continue) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.For) {
			translate((Stmt.For) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.IfElse) {
			translate((Stmt.IfElse) stmt, context, bytecodes);
		} else if(stmt instanceof Expr.Invoke) {
			translate((Expr.Invoke) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.While) {
			translate((Stmt.While) stmt, context, bytecodes);
		} else if (stmt instanceof Stmt.DoWhile) {
			translate((Stmt.DoWhile) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.Return) {
			translate((Stmt.Return) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.Switch) {
			translate((Stmt.Switch) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.VariableDeclaration) {
			translate((Stmt.VariableDeclaration) stmt, context, bytecodes);
		} else if(stmt instanceof Stmt.Delete) {
			translate((Stmt.Delete) stmt, context, bytecodes);
		} else {
			throw new IllegalArgumentException("Unknown statement encountered: " + stmt);
		}
	}

	private void translate(Stmt.Assert stmt, Context context, List<Bytecode> bytecodes) {
		String label = freshLabel();
		translate(stmt.getExpr(), context, bytecodes);
		bytecodes.add(new Bytecode.If(IfMode.NE, label));
		// If the assertion fails, through runtime exception
		constructObject(JvmTypes.JAVA_LANG_RUNTIMEEXCEPTION, bytecodes);
		bytecodes.add(new Bytecode.Throw());
		bytecodes.add(new Bytecode.Label(label));
	}

	private void translate(Stmt.Assign stmt, Context context, List<Bytecode> bytecodes) {
		// translate assignment
		translateAssignmentHelper(stmt.getLhs(),stmt.getRhs(),context,bytecodes);
	}

	// Initialising important variables to be used throughout the class
	private int loopIndex = 0;
	private final String INCREMENT_TEXT = "increment";
	private final String BREAK_TEXT = "break";
	private final String CONDITIONAL_TEXT = "conditional";

	// Creating a method to translate 'delete' statements into bytecodes.
	private void translate(Stmt.Delete stmt, Context context, List<Bytecode> bytecodes) {
		// Getting the reference expression to be deleted.
		Expr source = stmt.getExpr();
		Expr index = new Expr.Literal(0, new Attribute.Type(new Type.Int()));

		// Translating the source and index of 'Expr.Dereference' expression into bytecodes
		translate(source, context, bytecodes);
		translate(index, context, bytecodes);

		// Translating the right-hand side of the expression
		Expr rhs = new Expr.Literal(null, new Attribute.Type(new Type.Null()));
		translate(rhs, context, bytecodes);

		// Creating a bytecode which returns an element and then add it to 'bytecodes' list
		Bytecode returnElementBytecode = new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST, "set",
				new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT, JvmTypes.INT, JvmTypes.JAVA_LANG_OBJECT),
				VIRTUAL);
		bytecodes.add(returnElementBytecode);

		// Creating a bytecode which pops the element returned by the set and then add it to 'bytecodes' list
		Bytecode popElementBytecode = new Bytecode.Pop(JvmTypes.JAVA_LANG_BOOLEAN);
		bytecodes.add(popElementBytecode);
	}

	// Creating a method to translate 'break' statements into bytecodes.
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Stmt.Break stmt, Context context, List<Bytecode> bytecodes) {
		// Creating a bytecode for the break statement
		Bytecode breakStatementBytecode = new Bytecode.Goto(context.peekMostRecentLoop().get(BREAK_TEXT));
		bytecodes.add(breakStatementBytecode);
	}

	// Creating a method to translate 'continue' statements into bytecodes.
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Stmt.Continue stmt, Context context, List<Bytecode> bytecodes) {
		// Creating a bytecode for the continue statement
		Bytecode continueStatementBytecode = new Bytecode.Goto(context.peekMostRecentLoop().get(INCREMENT_TEXT));
		bytecodes.add(continueStatementBytecode);
	}

	// Creating a method to translate 'for' statements into bytecodes.
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Stmt.For stmt, Context context, List<Bytecode> bytecodes) {
		// Getting the loop index variable declaration
		Stmt.VariableDeclaration loopVariableDeclaration = stmt.getDeclaration();

		// Translating the loop variable declaration into bytecodes
		translate(loopVariableDeclaration, context, bytecodes);

		// Initialising necessary labels to be used throughout the for loop
		String conditionLabel = freshLabel() + "_conditional";
		String incrementLabel = freshLabel() + "_increment";
		String trueLabel = freshLabel() + "_true";
		String exitLabel = freshLabel() + "_exit_for_loop_" + (loopIndex++);

		// Gathering information regarding the for loop
		context.pushLoop(new HashMap<String, String>() {{
			put(CONDITIONAL_TEXT, conditionLabel);
			put(INCREMENT_TEXT, incrementLabel);
			put(BREAK_TEXT, exitLabel);
		}});

		// Dealing with the condition of the for loop
		// 1. Create a bytecode for the condition of the for loop and then add it to the list of bytecodes
		Bytecode conditionLabelBytecode = new Bytecode.Label(conditionLabel);
		bytecodes.add(conditionLabelBytecode);

		// 2. Translating the condition of the for loop into bytecodes
		Expr forLoopCondition = stmt.getCondition();
		translate(forLoopCondition, context, bytecodes);

		// 3. Adding bytecodes related to matching the loop variable with the loop condition
		// to the list of bytecodes
		// a. Bytecode using not equals to logical operator
		Bytecode ifBytecode = new Bytecode.If(IfMode.NE, trueLabel);

		// b. Bytecode to exit the for loop
		Bytecode gotoExitBytecode = new Bytecode.Goto(exitLabel);

		// c. Since the condition is true, run the for loop body and then increment the loop variable
		Bytecode trueLabelBytecode = new Bytecode.Label(trueLabel);

		// d. Creating a list of bytecodes to be added
		List<Bytecode> toBeAdded = Arrays.asList(ifBytecode, gotoExitBytecode, trueLabelBytecode);

		// e. Add the newly created bytecodes to 'bytecodes'
		bytecodes.addAll(toBeAdded);

		// Dealing with each statement in the body of the for loop
		List<Stmt> forLoopBody = stmt.getBody();
		int i = 0; // initial value
		while (i < forLoopBody.size()) {
			// Getting the statement at index 'i' of the list 'forLoopBody'
			Stmt currStatement = forLoopBody.get(i);

			// Translating the current statement into bytecodes
			translate(currStatement, context, bytecodes);

			// Moving on to the next iteration
			i++;
		}

		// Incrementing the value of the loop variable to move on to the next iteration
		// 1. Creating a bytecode which represents the end of an iteration
		Bytecode endIterationBytecode = new Bytecode.Label(incrementLabel);

		// 2. Insert 'endIterationBytecode' to 'bytecodes' to move on to the next iteration
		bytecodes.add(endIterationBytecode);

		// 3. Translating the statement which increments the loop variable to move on to the next iteration
		Stmt increment = stmt.getIncrement();
		translate(increment, context, bytecodes);

		// 4. Check whether the for loop condition is still met or not
		Bytecode gotoConditionBytecode = new Bytecode.Goto(conditionLabel);
		Bytecode exitLoopBytecode = new Bytecode.Label(exitLabel);
		List<Bytecode> toBeAdded2 = Arrays.asList(gotoConditionBytecode, exitLoopBytecode);
		bytecodes.addAll(toBeAdded2);

		// 5. Pop the loop out of the list of loops in the context to indicate that we have finished executing
		// the loop.
		context.popLoop();
	}

	private void translate(Stmt.IfElse stmt, Context context, List<Bytecode> bytecodes) {
		String trueBranch = freshLabel();
		String exitLabel = freshLabel();
		translate(stmt.getCondition(),context,bytecodes);
		bytecodes.add(new Bytecode.If(IfMode.NE, trueBranch));
		// translate the false branch
		translate(stmt.getFalseBranch(),context,bytecodes);
		if(!allPathsReturn(stmt.getFalseBranch())) {
			bytecodes.add(new Bytecode.Goto(exitLabel));
		}
		// translate true branch
		bytecodes.add(new Bytecode.Label(trueBranch));
		translate(stmt.getTrueBranch(),context,bytecodes);
		bytecodes.add(new Bytecode.Label(exitLabel));
	}

	// Creating a method to translate a 'while' statement into bytecodes
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Stmt.While stmt, Context context, List<Bytecode> bytecodes) {
		// Initialising necessary labels to be used throughout the while loop
		String conditionLabel = freshLabel() + "_conditional";
		String trueLabel = freshLabel() + "_true";
		String exitLabel = freshLabel() + "_exit";

		// Gathering information regarding the while loop
		context.pushLoop(new HashMap<String, String>() {{
			put(CONDITIONAL_TEXT, conditionLabel);
			put(INCREMENT_TEXT, conditionLabel);
			put(BREAK_TEXT, exitLabel);
		}});

		// Dealing with the condition of the while loop
		// 1. Create a bytecode for the condition of the while loop and then add it to the list of bytecodes
		Bytecode conditionLabelBytecode = new Bytecode.Label(conditionLabel);
		bytecodes.add(conditionLabelBytecode);

		// 2. Translating the condition of the while loop into bytecodes
		Expr whileLoopCondition = stmt.getCondition();
		translate(whileLoopCondition, context, bytecodes);

		// 3. Adding bytecodes related to matching the loop variable with the loop condition
		// to the list of bytecodes
		// a. Bytecode using not equals to logical operator
		Bytecode ifBytecode = new Bytecode.If(IfMode.NE, trueLabel);

		// b. Bytecode to exit the while loop
		Bytecode gotoExitBytecode = new Bytecode.Goto(exitLabel);

		// c. Since the condition is true, run the while loop body and then increment the loop variable
		Bytecode trueLabelBytecode = new Bytecode.Label(trueLabel);

		// d. Creating a list of bytecodes to be added
		List<Bytecode> toBeAdded = Arrays.asList(ifBytecode, gotoExitBytecode, trueLabelBytecode);

		// e. Add the newly created bytecodes to 'bytecodes'
		bytecodes.addAll(toBeAdded);

		// Dealing with each statement in the body of the while loop
		List<Stmt> whileLoopBody = stmt.getBody();
		int i = 0; // initial value
		while (i < whileLoopBody.size()) {
			// Getting the statement at index 'i' of the list 'whileLoopBody'
			Stmt currStatement = whileLoopBody.get(i);

			// Translating the current statement into bytecodes
			translate(currStatement, context, bytecodes);

			// Moving on to the next iteration
			i++;
		}

		// Checking whether the while loop condition is still met or not
		Bytecode gotoConditionBytecode = new Bytecode.Goto(conditionLabel);
		Bytecode exitLoopBytecode = new Bytecode.Label(exitLabel);
		List<Bytecode> toBeAdded2 = Arrays.asList(gotoConditionBytecode, exitLoopBytecode);
		bytecodes.addAll(toBeAdded2);

		// Popping the loop out of the list of loops in the context to indicate that we have finished executing
		// the loop.
		context.popLoop();
	}

	// Creating a method to translate a 'do-while' statement into bytecodes
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Stmt.DoWhile stmt, Context context, List<Bytecode> bytecodes) {
		// Initialising necessary labels to be used throughout the do-while loop
		String conditionLabel = freshLabel() + "_conditional";
		String trueLabel = freshLabel() + "_true";
		String exitLabel = freshLabel() + "_exit";

		// Gathering information regarding the do-while loop
		context.pushLoop(new HashMap<String, String>() {{
			put(CONDITIONAL_TEXT, conditionLabel);
			put(INCREMENT_TEXT, conditionLabel);
			put(BREAK_TEXT, exitLabel);
		}});

		// Dealing with each statement in the body of the do-while loop to ensure that the body of the do-while loop
		// is executed at least once.
		List<Stmt> doWhileLoopBody = stmt.getBody();

		// Translating the body of the do-while loop into bytecodes
		translate(doWhileLoopBody, context, bytecodes);

		// Dealing with the condition of the do-while loop
		// 1. Create a bytecode for the condition of the do-while loop and then add it to the list of bytecodes
		Bytecode conditionLabelBytecode = new Bytecode.Label(conditionLabel);
		bytecodes.add(conditionLabelBytecode);

		// 2. Translating the condition of the do-while loop into bytecodes
		Expr doWhileLoopCondition = stmt.getCondition();
		translate(doWhileLoopCondition, context, bytecodes);

		// 3. Adding bytecodes related to matching the loop variable with the loop condition
		// to the list of bytecodes
		// a. Bytecode using not equals to logical operator
		Bytecode ifBytecode = new Bytecode.If(IfMode.NE, trueLabel);

		// b. Bytecode to exit the do-while loop
		Bytecode gotoExitBytecode = new Bytecode.Goto(exitLabel);

		// c. Since the condition is true, run the while loop body and then increment the loop variable
		Bytecode trueLabelBytecode = new Bytecode.Label(trueLabel);

		// d. Creating a list of bytecodes to be added
		List<Bytecode> toBeAdded = Arrays.asList(ifBytecode, gotoExitBytecode, trueLabelBytecode);

		// e. Add the newly created bytecodes to 'bytecodes'
		bytecodes.addAll(toBeAdded);

		// Translating the do-while loop body into bytecodes
		for (Stmt currStmt : doWhileLoopBody) {
			if (currStmt instanceof Stmt.VariableDeclaration) {
				Stmt.VariableDeclaration variableDeclaration = (Stmt.VariableDeclaration) currStmt;
				Expr rhs = variableDeclaration.getExpr();
				if(rhs != null) {
					Expr.LVal lhs = new Expr.Variable(variableDeclaration.getName());
					translateAssignmentHelper(lhs,rhs,context,bytecodes);
				}
			}
			else {
				translate(currStmt, context, bytecodes);
			}
		}

		// Checking whether the do-while loop condition is still met or not
		Bytecode gotoConditionBytecode = new Bytecode.Goto(conditionLabel);
		Bytecode exitLoopBytecode = new Bytecode.Label(exitLabel);
		List<Bytecode> toBeAdded2 = Arrays.asList(gotoConditionBytecode, exitLoopBytecode);
		bytecodes.addAll(toBeAdded2);

		// Popping the loop out of the list of loops in the context to indicate that we have finished executing
		// the loop.
		context.popLoop();
	}

	private void translate(Stmt.Return stmt, Context context, List<Bytecode> bytecodes) {
		Expr expr = stmt.getExpr();
		if(expr != null) {
			// Determine type of returned expression
			Attribute.Type attr = expr.attribute(Attribute.Type.class);

			// Translate returned expression
			translate(expr,context,bytecodes);

			if (returnsJvmTypeUnionReference) {
				// Converting into an object
				Type attributeType = attr.type;
				boxAsNecessary(attributeType, bytecodes);
				bytecodes.add(new Bytecode.Return(toBoxedJvmType(attr.type)));
			}
			else {
				// Add return bytecode
				bytecodes.add(new Bytecode.Return(toJvmType(attr.type)));
			}
		} else {
			bytecodes.add(new Bytecode.Return(null));
		}
	}

	// Creating a method to translate 'switch' statements into bytecodes.
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Stmt.Switch stmt, Context context, List<Bytecode> bytecodes) {
		// Initialising necessary labels to be used throughout the switch statement
		String conditionLabel = freshLabel() + "_conditional";
		String exitLabel = freshLabel() + "_exit";

		// Gathering information regarding the switch statement
		context.pushLoop(new HashMap<String, String>() {{
			put(CONDITIONAL_TEXT, conditionLabel);
			put(BREAK_TEXT, exitLabel);
		}});

		// Converting the type of the attribute in the switch statement into a JVM type
		Attribute.Type attribute = stmt.getExpr().attribute(Attribute.Type.class);
		JvmType jvmType = toJvmType(attribute.type);

		// Creating labels for each case in the switch statement
		HashMap<Stmt.Case, String> labelsPerCase = new HashMap<>();
		int i = 0; // initial value
		List<Stmt.Case> switchCases = stmt.getCases();
		while (i < switchCases.size()) {
			// Getting the case at index 'i' of the list 'switchCases'
			Stmt.Case currCase = switchCases.get(i);

			// Generating a label for the current case
			if (currCase.isDefault()) {
				labelsPerCase.put(currCase, freshLabel() + "_default");
			}
			else {
				labelsPerCase.put(currCase, freshLabel() + "_" + currCase.getValue().toString());
			}

			// Move on to the next iteration.
			i++;
		}

		// Pushing each case in the switch case statement into the stack
		i = 0; // Reset the value of 'i' to iterate through each case in the switch case statement again
		while (i < switchCases.size()) {
			// Getting the case at index 'i' of the list 'switchCases'
			Stmt.Case currCase = switchCases.get(i);

			// Determining what to be pushed into then stack depending on whether the current case is the
			// default case or not
			// 1. If the current case is not the default case
			if (!currCase.isDefault()) {
				// Add an attribute to the list of attributes in 'currCase.getValue()'
				Attribute.Type attributeToAdd = new Attribute.Type(attribute.type);
				currCase.getValue().attributes().add(attributeToAdd);

				// Translating the binary expression comparing the expression in the switch with the current case
				// into bytecodes
				Expr currentExpression = stmt.getExpr();
				Expr.Literal switchCaseValue = currCase.getValue();
				translate(new Expr.Binary(Expr.BOp.EQ, currentExpression, switchCaseValue), context, bytecodes);

				// Creating an "IF" statement bytecode to be added to the list of bytecodes
				Bytecode ifBytecode = new Bytecode.If(IfMode.NE, labelsPerCase.get(currCase));
				bytecodes.add(ifBytecode);
			}

			// 2. Else if the current case is not the default case
			else {
				// Creating a bytecode to go to the switch label body
				Bytecode gotoBytecode = new Bytecode.Goto(labelsPerCase.get(currCase));
				bytecodes.add(gotoBytecode);
			}

			// Move on to the next iteration
			i++;
		}

		// Add a bytecode to exit the switch case after checking all cases without break and return keywords.
		Map<String, String> mostRecentLoop = context.peekMostRecentLoop();
		Bytecode goToExitBytecode = new Bytecode.Goto(mostRecentLoop.get(BREAK_TEXT));
		bytecodes.add(goToExitBytecode);

		// Creating a label for the body of each case in the switch case statement
		i = 0; // Reset the value of 'i' to iterate through each case in the switch case statement again
		while (i < switchCases.size()) {
			// Getting the case at index 'i' of the list 'switchCases'
			Stmt.Case currCase = switchCases.get(i);

			// Label for the case body
			String bodyLabel = labelsPerCase.get(currCase);
			Bytecode bodyLabelBytecode = new Bytecode.Label(bodyLabel);
			bytecodes.add(bodyLabelBytecode);

			// Translate the body of the switch case into bytecodes
			List<Stmt> currCaseBody = currCase.getBody();
			translate(currCaseBody, context, bytecodes);

			// Move on to the next iteration
			i++;
		}

		// Dealing with the case that either break or return statements exist
		Map<String, String> mostRecentLoop2 = context.peekMostRecentLoop();
		Bytecode exitSwitchCaseBytecode = new Bytecode.Label(mostRecentLoop2.get(BREAK_TEXT));
		bytecodes.add(exitSwitchCaseBytecode);

		// Popping the loop out of the list of loops in the context to indicate that we have finished executing
		// the loop.
		context.popLoop();
	}

	private static boolean leftSideTypeReference;

	private void translate(Stmt.VariableDeclaration stmt, Context context, List<Bytecode> bytecodes) {
		Expr rhs = stmt.getExpr();
		// Declare the variable in the context
		context.declareRegister(stmt.getName());
		//

		Type assignedType = stmt.getType();
		JvmType jvmAssignedType = toJvmType(assignedType);
		assignsJvmTypeUnionReference = jvmAssignedType instanceof JvmType.Reference &&
				assignedType instanceof Type.Union;
		leftSideTypeReference = assignedType instanceof Type.Reference;
		if(rhs != null) {
			Expr.LVal lhs = new Expr.Variable(stmt.getName());
			translateAssignmentHelper(lhs,rhs,context,bytecodes);
		}
	}

	/**
	 * Implement an assignment from a given expression to a given lval. This
	 * code is split out because it is used both in translating assignment
	 * statements and variable declarations. In particular, this code is pretty
	 * tricky to get right because it needs to handle cloning of compound data,
	 * and boxing of primitive data (in some cases).
	 *
	 * @param lhs
	 *            Expression being assigned to
	 * @param rhs
	 *            Expression being assigned
	 * @param context
	 *            The current translation context
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 */
	// This function is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translateAssignmentHelper(Expr.LVal lhs, Expr rhs, Context context,
			List<Bytecode> bytecodes) {
		// Determine type of assigned expression
		Attribute.Type attr = rhs.attribute(Attribute.Type.class);
		Type attrType = attr.type;
		JvmType rhsType = toJvmType(attrType);
		//
		if (lhs instanceof Expr.Variable) {
			Expr.Variable var = (Expr.Variable) lhs;

			// Loading the right-hand side of the expression
			translate(rhs, context, bytecodes);

			if (assignsJvmTypeUnionReference) {
				boxAsNecessary(attrType, bytecodes);
				rhsType = toBoxedJvmType(attrType);
			}

			// Cloning the type at the right-hand side of the expression as necessary
			if (rhsType != JvmTypes.JAVA_LANG_INTEGER && rhsType != JvmTypes.JAVA_LANG_BOOLEAN &&
			!leftSideTypeReference) {
				cloneAsNecessary(rhsType, bytecodes);
			}

			// Creating a new bytecode to store the type of the right-hand side of the expression and adding the
			// bytecode to the list 'bytecodes'.
			int register = context.getRegister(var.getName());
			bytecodes.add(new Bytecode.Store(register, rhsType));
		} else if (lhs instanceof Expr.ArrayAccess) {
			// Getting the 'ArrayAccess' expression
			Expr.ArrayAccess arrayAccessExpr = (Expr.ArrayAccess) lhs;

			// Getting the source and index of 'ArrayAccess' expression
			Expr source = arrayAccessExpr.getSource();
			Expr index = arrayAccessExpr.getIndex();

			// Translating the source and index of 'ArrayAccess' expression into bytecodes
			translate(source, context, bytecodes);
			translate(index, context, bytecodes);

			// Translating the right-hand side of the expression
			translate(rhs, context, bytecodes);

			// Converting into an object
			Type attributeType = attr.type;
			boxAsNecessary(attributeType, bytecodes);

			// Creating a bytecode which returns an element and then add it to 'bytecodes' list
			Bytecode returnElementBytecode = new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST, "set",
					new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT, JvmTypes.INT, JvmTypes.JAVA_LANG_OBJECT),
					VIRTUAL);
			bytecodes.add(returnElementBytecode);

			// Creating a bytecode which pops the element returned by the set and then add it to 'bytecodes' list
			Bytecode popElementBytecode = new Bytecode.Pop(JvmTypes.JAVA_LANG_BOOLEAN);
			bytecodes.add(popElementBytecode);
		} else if (lhs instanceof Expr.RecordAccess) {
			// Getting the 'RecordAccess' expression
			Expr.RecordAccess recordAccessExpr = (Expr.RecordAccess) lhs;

			// Getting the source and name of 'RecordAccess' expression
			Expr source = recordAccessExpr.getSource();
			String fieldName = recordAccessExpr.getName();

			// Translating the source of 'RecordAccess' expression into bytecodes
			translate(source, context, bytecodes);

			// Putting the name of the field in the 'RecordAccess' expression in a hash map
			addToHashMap(fieldName, rhs, context, bytecodes);

			// Translating the right-hand side of the expression
			translate(rhs, context, bytecodes);

			// Cloning the right-hand side of the expression
			cloneAsNecessary(rhsType, bytecodes);
		}
		else if (lhs instanceof Expr.Dereference) {
			// Getting the "Expr.Dereference" expression
			Expr.Dereference dereferenceExpr = (Expr.Dereference) lhs;

			// Getting the source and index of 'Expr.Dereference' expression
			Expr source = dereferenceExpr.getSource();
			Expr index = new Expr.Literal(0, new Attribute.Type(new Type.Int()));

			// Translating the source and index of 'Expr.Dereference' expression into bytecodes
			translate(source, context, bytecodes);
			translate(index, context, bytecodes);

			// Translating the right-hand side of the expression
			translate(rhs, context, bytecodes);

			// Converting into an object
			Type attributeType = attr.type;
			boxAsNecessary(attributeType, bytecodes);

			// Creating a bytecode which returns an element and then add it to 'bytecodes' list
			Bytecode returnElementBytecode = new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST, "set",
					new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT, JvmTypes.INT, JvmTypes.JAVA_LANG_OBJECT),
					VIRTUAL);
			bytecodes.add(returnElementBytecode);

			// Creating a bytecode which pops the element returned by the set and then add it to 'bytecodes' list
			Bytecode popElementBytecode = new Bytecode.Pop(JvmTypes.JAVA_LANG_BOOLEAN);
			bytecodes.add(popElementBytecode);
		}
		else if (lhs instanceof Expr.FieldDereference) {
			// Getting the "Expr.FieldDereference" expression
			Expr.FieldDereference fieldDereferenceExpr = (Expr.FieldDereference) lhs;

			// Getting the source, field name, and index of the field dereference expression.
			Expr source = fieldDereferenceExpr.getSource();
			Expr index = new Expr.Literal(0, new Attribute.Type(new Type.Int()));

			// Translating the source and index of 'Expr.FieldDereference' expression into bytecodes
			translate(source, context, bytecodes);
			translate(index, context, bytecodes);

			// Get value out of ArrayList
			JvmType.Function ft = new JvmType.Function(JAVA_LANG_OBJECT, JvmTypes.INT);
			bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST,"get",ft, VIRTUAL));

			// Cast the object into hashmap
			bytecodes.add(new Bytecode.CheckCast(JAVA_UTIL_HASHMAP));

			// Putting the name of the field in the 'Expr.FieldDereference' expression in a hash map
			String fieldName = fieldDereferenceExpr.getName();
			addToHashMap(fieldName, rhs, context, bytecodes);
		}
		else {
			throw new IllegalArgumentException("unknown lval encountered");
		}
	}

	/**
	 * Translate a given expression in the While language into a series of one
	 * of more bytecodes which implement its behaviour. The result of the
	 * expression should be left on the top of the stack.
	 *
	 * @param stmts
	 * @param bytecodes
	 */
	// Notes: Need to translate the expression Dereference
	private void translate(Expr expr, Context context, List<Bytecode> bytecodes) {
		if(expr instanceof Expr.ArrayGenerator) {
			translate((Expr.ArrayGenerator) expr, context, bytecodes);
		} else if(expr instanceof Expr.ArrayInitialiser) {
			translate((Expr.ArrayInitialiser) expr, context, bytecodes);
		} else if(expr instanceof Expr.Binary) {
			translate((Expr.Binary) expr, context, bytecodes);
		} else if(expr instanceof Expr.Literal) {
			translate((Expr.Literal) expr, context, bytecodes);
		} else if(expr instanceof Expr.ArrayAccess) {
			translate((Expr.ArrayAccess) expr, context, bytecodes);
		} else if(expr instanceof Expr.Invoke) {
			translate((Expr.Invoke) expr, context, bytecodes);
		} else if(expr instanceof Expr.RecordAccess) {
			translate((Expr.RecordAccess) expr, context, bytecodes);
		} else if(expr instanceof Expr.RecordConstructor) {
			translate((Expr.RecordConstructor) expr, context, bytecodes);
		} else if(expr instanceof Expr.Unary) {
			translate((Expr.Unary) expr, context, bytecodes);
		} else if(expr instanceof Expr.Variable) {
			translate((Expr.Variable) expr, context, bytecodes);
		} else if(expr instanceof Expr.Cast) {
			translate((Expr.Cast) expr, context, bytecodes);
		} else if(expr instanceof Expr.Dereference) {
			translate((Expr.Dereference) expr, context, bytecodes);
		} else if(expr instanceof Expr.FieldDereference) {
			translate((Expr.FieldDereference) expr, context, bytecodes);
		} else {
			throw new IllegalArgumentException("Unknown expression encountered: " + expr);
		}
	}

	// Creating a method to translate a cast expression into bytecodes.
	private void translate(Expr.Cast expr, Context context, List<Bytecode> bytecodes) {
		// Getting the type to be casted to
		Type castTo = expr.getType();
		while (castTo instanceof Type.Named) {
			castTo = this.declaredTypes.get(((Type.Named) castTo).getName());
		}

		// Getting the type to be casted from
		Type castFrom = expr.getSource().attribute(Attribute.Type.class).type;

		JvmType jvmTypeCastFrom = toJvmType(castFrom);

		// Translating the source of the expression into bytecodes
		translate(expr.getSource(), context, bytecodes);

		// Cloning the source of the "Expr.Cast" expression as necessary
		cloneAsNecessary(jvmTypeCastFrom, bytecodes);

		if (!(castFrom.getClass().equals(castTo.getClass()))) {
			// Add a read conversion for casting
			addReadConversion(castTo, bytecodes);
		}
	}

	// Creating a method to translate an array generator into bytecodes.
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Expr.ArrayGenerator expr, Context context, List<Bytecode> bytecodes) {
		// Creating a new array and getting the array register index
		int arrayRegisterIndex = createArrayType(context, bytecodes);

		// Creating an increment containing characters that names of variables cannot contain.
		// This is to avoid having the increment overlapping with any existing variables
		String nameOfIncrement = "!" + freshLabel() + "#$!";
		context.declareRegister(nameOfIncrement);
		int incrementRegisterIndex = context.getRegister(nameOfIncrement);

		// Storing the value '0' in the increment and adding the newly created bytecodes to the list 'bytecodes'
		Bytecode loadConstZeroBytecode = new Bytecode.LoadConst(0);
		Bytecode storeConstZeroBytecode = new Bytecode.Store(incrementRegisterIndex, new JvmType.Int());
		List<Bytecode> toBeAdded = Arrays.asList(loadConstZeroBytecode, storeConstZeroBytecode);
		bytecodes.addAll(toBeAdded);

		// Initialising important labels
		String trueLabel = freshLabel() + "_true";
		String conditionLabel = freshLabel() + "_conditional";
		String exitLabel = freshLabel() + "_exit";

		// Creating a bytecode related to the condition label
		Bytecode conditionLabelBytecode = new Bytecode.Label(conditionLabel);
		bytecodes.add(conditionLabelBytecode);

		// Translating the size of the array being generated into bytecodes
		Expr arraySize = expr.getSize();
		translate(arraySize, context, bytecodes);

		// Creating a bytecode to load from a local variable at slot 'incrementRegisterIndex'
		Bytecode loadIncrementBytecode = new Bytecode.Load(incrementRegisterIndex, new JvmType.Int());
		bytecodes.add(loadIncrementBytecode);

		// Creating bytecodes related to condition checking
		Bytecode checkConditionBytecode = new Bytecode.IfCmp(Bytecode.IfCmp.GT, new JvmType.Int(), trueLabel);
		Bytecode gotoExitBytecode = new Bytecode.Goto(exitLabel);
		Bytecode trueLabelBytecode = new Bytecode.Label(trueLabel);
		List<Bytecode> toBeAdded2 = Arrays.asList(checkConditionBytecode, gotoExitBytecode, trueLabelBytecode);
		bytecodes.addAll(toBeAdded2);

		// Put the item in the array generator into the array
		Expr itemValue = expr.getValue();
		addToArray(itemValue, arrayRegisterIndex, context, bytecodes);

		// Creating a bytecode to increment the index of the array
		Bytecode incrementIndexBytecode = new Bytecode.Iinc(incrementRegisterIndex, 1);

		// Creating a bytecode to go to the condition label
		Bytecode gotoConditionBytecode = new Bytecode.Goto(conditionLabel);

		// Creating a bytecode for exit label
		Bytecode exitLabelBytecode = new Bytecode.Label(exitLabel);

		// Creating a bytecode to get the array
		Bytecode loadArrayBytecode = new Bytecode.Load(arrayRegisterIndex, JAVA_UTIL_ARRAYLIST);

		// Adding the newly created bytecodes to the list 'bytecodes'
		List<Bytecode> toBeAdded3 = Arrays.asList(incrementIndexBytecode, gotoConditionBytecode,
				exitLabelBytecode, loadArrayBytecode);
		bytecodes.addAll(toBeAdded3);
	}

	// Creating a method to put an object in an array
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void addToArray(Expr expr, int arrayRegisterIndex, Context context, List<Bytecode> bytecodes) {
		// Getting the type of the expression 'expr'
		Type expressionType = expr.attribute(Attribute.Type.class).type;

		// Getting the array using a bytecode and then add the bytecode to the list 'bytecodes'
		Bytecode getArrayBytecode = new Bytecode.Load(arrayRegisterIndex, JAVA_UTIL_ARRAYLIST);
		bytecodes.add(getArrayBytecode);

		// Translating the expression 'expr' into bytecodes
		translate(expr, context, bytecodes);

		// Boxing the type 'expressionType' as necessary
		boxAsNecessary(expressionType, bytecodes);

		// Adding the object into the array using a bytecode
		Bytecode addToArrayBytecode = new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST, "add",
				new JvmType.Function(new JvmType.Primitive.Bool(), JAVA_LANG_OBJECT), VIRTUAL);
		bytecodes.add(addToArrayBytecode);

		// Popping an element from the array
		Bytecode popFromArrayBytecode = new Bytecode.Pop(JvmTypes.JAVA_LANG_BOOLEAN);
		bytecodes.add(popFromArrayBytecode);
	}

	// Creating a method to put an object in a hash map
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	public void addToHashMap(String keyName, Expr expression, Context context, List<Bytecode> bytecodes) {
		// Insert the name of the key into the stack
		Bytecode insertKeyBytecode = new Bytecode.LoadConst(keyName);
		bytecodes.add(insertKeyBytecode);

		// Translating the expression 'expression' into bytecodes
		translate(expression, context, bytecodes);

		// Getting the type of the expression 'expression'
		Type expressionType = expression.attribute(Attribute.Type.class).type;

		// Converting the expression 'expression' into an object
		boxAsNecessary(expressionType, bytecodes);

		// Converting 'expressionType' into a JVM type
		JvmType expressionJvmType = toJvmType(expressionType);

		// Cloning 'expressionJvmType' as necessary
		cloneAsNecessary(expressionJvmType, bytecodes);

		// Creating a bytecode to put a key-value pair into a hash map. Then, add the newly create bytecode
		// to the list 'bytecodes'
		Bytecode putInHashMapBytecode = new Bytecode.Invoke(JAVA_UTIL_HASHMAP, "put",
				new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT, JvmTypes.JAVA_LANG_OBJECT,
						JvmTypes.JAVA_LANG_OBJECT), VIRTUAL);
		bytecodes.add(putInHashMapBytecode);

		// Popping an element from the hash map
		Bytecode popFromArrayBytecode = new Bytecode.Pop(JvmTypes.JAVA_LANG_OBJECT);
		bytecodes.add(popFromArrayBytecode);
	}

	// Translating an array access expression into bytecodes.
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Expr.ArrayInitialiser expr, Context context, List<Bytecode> bytecodes) {
		// Creating an array and getting the array register index
		int arrayRegisterIndex = createArrayType(context, bytecodes);

		// Put each argument in the array initialiser into the array
		int i = 0; // initial value
		while (i < expr.getArguments().size()) {
			// Getting the argument at index 'i' of the list 'expr.getArguments()'
			Expr currArgument = expr.getArguments().get(i);

			// Placing 'currArgument' in an array
			addToArray(currArgument, arrayRegisterIndex, context, bytecodes);

			// Moving on to the next iteration
			i++;
		}

		// Getting the array
		Bytecode getArrayBytecode = new Bytecode.Load(arrayRegisterIndex, JAVA_UTIL_ARRAYLIST);
		bytecodes.add(getArrayBytecode);
	}

	// Creating a method to check if a JvmType is an array or not
	private boolean isArray(JvmType jvmType) {
		return jvmType == JAVA_UTIL_ARRAYLIST;
	}

	// Creating a method to check if a JvmType is a record or not
	private boolean isRecord(JvmType jvmType) {
		return jvmType == JAVA_UTIL_HASHMAP;
	}

	// Creating a method to check whether a binary operator checks for equality or not
    private boolean checksForEquality(Expr.BOp operator) {
	    return operator == Expr.BOp.EQ || operator == Expr.BOp.NEQ;
    }

    // Creating a method to check whether a binary operator is a logical connective or not
    private boolean isLogicalConnective(Expr.BOp operator) {
	    return operator == Expr.BOp.AND || operator == Expr.BOp.OR;
    }

    // Creating a method to check whether a binary operator is a mathematical operator or not
    private boolean isMathematicalOperator(Expr.BOp operator) {
	    return operator == Expr.BOp.ADD || operator == Expr.BOp.SUB || operator == Expr.BOp.MUL ||
                operator == Expr.BOp.DIV || operator == Expr.BOp.REM;
    }

    // Creating a method to check whether a binary operator is a comparator or not
    private boolean isComparator(Expr.BOp operator) {
	    return operator == Expr.BOp.EQ || operator == Expr.BOp.NEQ || operator == Expr.BOp.LT ||
                operator == Expr.BOp.LTEQ || operator == Expr.BOp.GT || operator == Expr.BOp.GTEQ;
    }

	// The following method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Expr.Binary expr, Context context, List<Bytecode> bytecodes) {;
		// Initialising a string to be used as the exit label of the expression
		String exitLabel = freshLabel() + "_exit_" + expr.getOp();

		// Converting both the left and right-hand sides of the binary expression into JVM types.
		Expr lhsExpression = expr.getLhs();
		Expr rhsExpression = expr.getRhs();
		Type lhsAttributeType = lhsExpression.attribute(Attribute.Type.class).type;
		JvmType lhsType = toJvmType(lhsAttributeType);
		Type rhsAttributeType = rhsExpression.attribute(Attribute.Type.class).type;
		JvmType rhsType = toJvmType(rhsAttributeType);

		// Creating a string representing the label to do right-hand side
		String doRhs = freshLabel() + "do_rhs";

		// If the binary operator 'AND' is used, try translating the left-hand side first. If the condition at the
		// left-hand side cannot be translated without throwing an error, exit due to failure
		if (expr.getOp() == Expr.BOp.AND) {
			translate(lhsExpression, context, bytecodes);

			// Else, since the first condition succeeds, proceed with doing the right-hand side.
			// 1. Initialising the bytecode related to the if mode, with 'not equal to' as the operator
			Bytecode ifBytecode = new Bytecode.If(IfMode.NE, doRhs);

			// 2. Initialising the bytecode to load the constant 'false'
			Bytecode loadFalseConstant = new Bytecode.LoadConst(false);

			// 3. Initialising the bytecode to go to the exit label
			Bytecode gotoExitLabel = new Bytecode.Goto(exitLabel);

			// Adding the newly created bytecodes to the list 'bytecodes'
			List<Bytecode> rhsRelatedBytecodes =
					Arrays.asList(ifBytecode, loadFalseConstant, gotoExitLabel);
			bytecodes.addAll(rhsRelatedBytecodes);
		}

		// Adding the label to do the right-hand side and then try to translate the expressions at both sides
		Bytecode doRhsBytecode = new Bytecode.Label(doRhs);
		bytecodes.add(doRhsBytecode);

		translate(lhsExpression, context, bytecodes);
		translate(rhsExpression, context, bytecodes);

		// Boxing LHS or RHS depending on which is necessary
		if (lhsType instanceof JvmType.Reference && !(rhsType instanceof JvmType.Reference)) {
			boxAsNecessary(rhsAttributeType, bytecodes);
		}
		else if (rhsType instanceof JvmType.Reference && !(lhsType instanceof JvmType.Reference)) {
			boxAsNecessary(lhsAttributeType, bytecodes);
		}

		// TODO: Need to use collectionComparisonToBytecodes for comparison of two Union types

		// If either or both the expressions in the binary expression are lists or records or unions
        if (isRecord(lhsType) || isRecord(rhsType) || isArray(lhsType) || isArray(rhsType)) {
            // If both the left and right-hand sides are of the same type, attempt comparison
			// (e.g. union of int and int[]).
            if (lhsType == rhsType || lhsType.getClass().isAssignableFrom(rhsType.getClass()) ||
			rhsType.getClass().isAssignableFrom(lhsType.getClass())) {
				assert lhsType instanceof JvmType.Reference;
				collectionComparisonToBytecodes((JvmType.Reference) lhsType, expr.getOp(), context,
                        bytecodes);
            }

            // Else, do not attempt comparison
            else {
                // Check whether 'expr.getOp()' checks for equality or not
                if (!checksForEquality(expr.getOp())) {
                    // Throw an illegal argument exception
                    throw new IllegalArgumentException("An unknown binary operator is detected!");
                }
                else {
                    // Add a new bytecode depending on the encountered binary operator
                    Bytecode newBytecode = new Bytecode.LoadConst(expr.getOp() == Expr.BOp.NEQ);
                    bytecodes.add(newBytecode);

                    // Exit the method
                    return;
                }
            }

            // Exit the method
            return;
        }

        // Convert the binary operator to int
        int binOpInt = binaryOperatorToInt(expr.getOp());

        // Adding bytecodes depending on what type of binary operator 'expr.getOp()' is
        // 1. If 'expr.getOp()' is either a logical connective or a mathematical operator
        if (isLogicalConnective(expr.getOp()) || isMathematicalOperator(expr.getOp())) {
            // Create a new Bytecode representing the binary operator itself
            Bytecode binaryOperatorBytecode = new Bytecode.BinOp(binOpInt, lhsType);
            bytecodes.add(binaryOperatorBytecode);
        }

        // 2. Else if 'expr.getOp()' is a comparator
        else if (isComparator(expr.getOp())) {
            // Creating a true label
            String trueLabel = freshLabel() + "_true_" + binOpInt;

            // 1. Initialising a bytecode related to 'if comparator'
            Bytecode ifComparatorBytecode = new Bytecode.IfCmp(binOpInt, lhsType, trueLabel);

            // 2. Initialising a bytecode to load the constant 'false'
            Bytecode falseConstBytecode = new Bytecode.LoadConst(false);

            // 3. Initialising the bytecode to go to the exit label
            Bytecode gotoExitLabelBytecode = new Bytecode.Goto(exitLabel);

            // 4. Initialising the bytecode to go to the true label
            Bytecode trueLabelBytecode = new Bytecode.Label(trueLabel);

            // 5. Initialising a bytecode to load the constant 'true'
            Bytecode trueConstBytecode = new Bytecode.LoadConst(true);

            // Adding the newly created bytecodes to the list 'bytecodes'
            List<Bytecode> bytecodesToAdd =
                    Arrays.asList(ifComparatorBytecode, falseConstBytecode,
                            gotoExitLabelBytecode, trueLabelBytecode, trueConstBytecode);
            bytecodes.addAll(bytecodesToAdd);
        }

        // 3. Else, throw an illegal argument exception
        else {
            // Throw an illegal argument exception
            throw new IllegalArgumentException("An unknown binary operator is detected!");
        }

		// Adding the exit label into the list of bytecodes
		Bytecode exitLabelBytecode = new Bytecode.Label(exitLabel);
		bytecodes.add(exitLabelBytecode);
	}

	// Creating a method to translate comparisons between collection types into bytecodes
    private void collectionComparisonToBytecodes(JvmType.Reference type, Expr.BOp operator, Context context,
                                                 List<Bytecode> bytecodes) {
	    // Initialising a bytecode which checks for equality
        Bytecode equalityCheckerBytecode = new Bytecode.Invoke(type, "equals",
                new JvmType.Function(new JvmType.Primitive.Bool(), JvmTypes.JAVA_LANG_OBJECT), VIRTUAL);

        // Adding the newly initialised bytecode to the list of bytecodes
        bytecodes.add(equalityCheckerBytecode);

        // Else, check whether 'operator' checks for equality or not
        if (!checksForEquality(operator)) {
            // Throw an illegal argument exception
            throw new IllegalArgumentException("An unknown binary operator is detected!");
        }
        else {
            // Call 'translateNotHelper' method if the type of binary operator 'operator' is NEQ
            if (operator == Expr.BOp.NEQ) {
                translateNotHelper(bytecodes);
            }
        }
    }

	// Creating a method to convert a binary operator to an integer.
	// The following method is inspired by the folowing source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private int binaryOperatorToInt(Expr.BOp operator) {
		int result = operator == Expr.BOp.AND ? Bytecode.BinOp.AND : operator == Expr.BOp.OR ? Bytecode.BinOp.OR :
				operator == Expr.BOp.EQ ? Bytecode.IfCmp.EQ : operator == Expr.BOp.NEQ ?
				Bytecode.IfCmp.NE : operator == Expr.BOp.LT ? Bytecode.IfCmp.LT : operator == Expr.BOp.LTEQ ?
				Bytecode.IfCmp.LE : operator == Expr.BOp.GT ? Bytecode.IfCmp.GT : operator == Expr.BOp.GTEQ ?
				Bytecode.IfCmp.GE : operator == Expr.BOp.ADD ? Bytecode.BinOp.ADD : operator == Expr.BOp.SUB ?
				Bytecode.BinOp.SUB : operator == Expr.BOp.MUL ? Bytecode.BinOp.MUL : operator == Expr.BOp.DIV ?
				Bytecode.BinOp.DIV : operator == Expr.BOp.REM ? Bytecode.BinOp.REM : -Integer.MAX_VALUE;

		// If the value of 'result' is null, throw a runtime exception.
		if (result == -Integer.MAX_VALUE) {
			throw new RuntimeException("Error: unexpected operator '" + operator + "' detected!");
		}
		return result;
	}

	// Creating a method to check if a particular type is a named type or not
	private boolean isNamedType(Type type) {
		return type instanceof Type.Named;
	}

	// Creating a method to check if a particular type is an array type or not
	private boolean isArrayType(Type type) {
		return type instanceof Type.Array;
	}

	// Creating a method to check if a particular type is a record type or not
	private boolean isRecordType(Type type) {
		return type instanceof Type.Record;
	}

	// Creating a method to create an array
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private int createArrayType(Context context, List<Bytecode> bytecodes) {
		// Construct the array type object
		constructObject(JAVA_UTIL_ARRAYLIST, bytecodes);

		// Get the register index
		String registerName = freshLabel() + "_array";
		int registerIndex = context.declareRegister(registerName);

		// Creating a new bytecode to be added to the list of bytecodes
		Bytecode toBeAdded = new Bytecode.Store(registerIndex, JAVA_UTIL_ARRAYLIST);
		bytecodes.add(toBeAdded);

		// Return the register index
		return registerIndex;
	}

	// Creating a method to create a hash map
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private int createHashMapType(Context context, List<Bytecode> bytecodes) {
		// Construct the Hash Map type object
		constructObject(JAVA_UTIL_HASHMAP, bytecodes);

		// Get the register index
		String registerName = freshLabel() + "hashmap";
		int registerIndex = context.declareRegister(registerName);

		// Creating a new bytecode to be added to the list of bytecodes
		Bytecode toBeAdded = new Bytecode.Store(registerIndex, JAVA_UTIL_HASHMAP);
		bytecodes.add(toBeAdded);

		// Return the register index
		return registerIndex;
	}

	// Creating a method to create an array or a hash map and then return a register index
	// depending on the type of object created.
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private int createCollectionType(Type type, Context context, List<Bytecode> bytecodes) {
		// Checking whether 'type' is either an array or a record or neither
		boolean isArrayType = type instanceof Type.Array;
		boolean isRecordType = type instanceof Type.Record;

		// If 'type' is neither an array nor a record
		if (!isArrayType && !isRecordType) {
			// Throw an illegal argument exception
			throw new IllegalArgumentException("Type '" + type + "' is neither an array nor a record type!");
		}

		// Else, construct the collection type object
		constructObject(isArrayType ? JAVA_UTIL_ARRAYLIST : JAVA_UTIL_HASHMAP, bytecodes);

		// Get the register index
		String registerName = isArrayType ? (freshLabel() + "_array") : (freshLabel() + "_hashmap");
		int registerIndex = context.declareRegister(registerName);

		// Creating a new bytecode to be added to the list of bytecodes
		Bytecode toBeAdded = new Bytecode.Store(registerIndex, isArrayType ? JAVA_UTIL_ARRAYLIST :
				JAVA_UTIL_HASHMAP);
		bytecodes.add(toBeAdded);

		// Return the register index
		return registerIndex;
	}

	// Translating a literal expression into bytecodes.
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Expr.Literal expr, Context context, List<Bytecode> bytecodes) {
		// Getting the 'value' in the 'Expr.Literal' type expression 'expr'
		Object value = expr.getValue();

		// Getting the type of the object 'value' in While programming language
		Type type = expr.attribute(Attribute.Type.class).type;

		// Checking the case the type 'type' is an instance of 'Type.Named'. This is to get a While
		// programming language type in the named type
		while (isNamedType(type)) {
			// Get the declared type based on the name of the named type
			Type.Named namedType = (Type.Named) type;
			type = declaredTypes.get(namedType.getName());
		}

		// Dealing with the case if 'type' is an array
		if (isArrayType(type)) {
			// If 'value' is a List
			if (value instanceof List) {
				// Get the type of the element in the array type 'type'
				Type.Array arrayType = (Type.Array) type;
				Type typeOfElement = arrayType.getElement();

				// Getting the array register index
				int arrayRegisterIndex = createCollectionType(type, context, bytecodes);

				// Initialise a list to be iterated through
				List valueList = (List) value;
				int i = 0; // initial value
				while (i < valueList.size()) {
					// Put the object in the List into an array
					Object currentObject = valueList.get(i);
					putObjectInCollection(arrayType, "", currentObject, typeOfElement,
							arrayRegisterIndex, bytecodes);

					// Move on to the next iteration
					i++;
				}

				// Loading the element at the register index
				Bytecode loadRegisterIndexBytecode = new Bytecode.Load(arrayRegisterIndex, JAVA_UTIL_ARRAYLIST);

				// Adding the newly created bytecode to the list 'bytecodes'
				bytecodes.add(loadRegisterIndexBytecode);
			}

			// Else if 'value' is a string
			else if (value instanceof String) {
				// Getting a list of characters in the string 'value'
				char[] characters = ((String) value).toCharArray();

				// Initialising a list of array arguments
				ArrayList<Expr> arrayArguments = new ArrayList<>();

				// Initialising a list of character attributes
				List<Attribute> characterAttributes = Arrays.asList(new Attribute.Type(new Type.Int()));

				// Iterating through each character in 'characters'
				int i = 0; // initial value
				while (i < characters.length) {
					// Get the character at index 'i' in 'characters'
					char currChar = characters[i];

					// Adding an expression in relation to 'currChar' to 'arrayArguments'
					arrayArguments.add(new Expr.Literal(currChar, characterAttributes));

					// Move on to the next iteration
					i++;
				}

				// Translating the array initialiser into bytecodes
				translate(new Expr.ArrayInitialiser(arrayArguments,
						new Attribute.Type(new Type.Array(new Type.Int()))), context, bytecodes);
			}
		}

		// Dealing with the case if 'type' is a record
		else if (isRecordType(type)) {
			// Immediately throw a runtime exception if 'value' is not a Map class object
			if (!(value instanceof Map)) {
				throw new RuntimeException("Invalid argument detected! '" + value + "' " +
						"is not of a known record type!");
			}
			else {
				// Initialising record type
				Type.Record recordType = (Type.Record) type;

				// Getting all the fields in the record type
				List<Pair<Type, String>> recordTypeFields = recordType.getFields();

				// Mapping the name into the type
				HashMap<String, Type> nameToType = new HashMap<>();

				// Add all pairs from 'recordTypeFields' to 'nameToType'
				int i = 0; // initial value
				while (i < recordTypeFields.size()) {
					// Getting the pair at index 'i' in 'recordTypeFields'
					Pair<Type, String> currentPair = recordTypeFields.get(i);
					String pairName = currentPair.second();
					Type pairType = currentPair.first();

					// Add the pair of name and type to 'nameToType'
					nameToType.put(pairName, pairType);

					// Moving on to the next iteration
					i++;
				}

				// Getting the register index of the map
				int mapRegisterIndex = createCollectionType(type, context, bytecodes);

				// Adding each object to the hash map
				Map<String, Object> mapObject = (Map<String, Object>) value;
				for (Map.Entry<String, Object> currEntry : mapObject.entrySet()) {
					putObjectInCollection(type, currEntry.getKey(), currEntry.getValue(),
							nameToType.get(currEntry.getKey()), mapRegisterIndex, bytecodes);
				}

				// Insert a new bytecode to the list of bytecodes
				Bytecode newBytecode = new Bytecode.Load(mapRegisterIndex, JAVA_UTIL_HASHMAP);
				bytecodes.add(newBytecode);
			}
		}

		// Else, it is an easy case. We only need to create a bytecode based on the constant 'value'
		else {
			bytecodes.add(new Bytecode.LoadConst(value));
		}
	}

	// Creating a method to put an object in either an array or a hash map
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	public void putObjectInCollection(Type collectionType, String keyName, Object object, Type objectType,
									  int registerIndex, List<Bytecode> bytecodes) {
		// Considering the case 'collectionType' is an array
		if (isArrayType(collectionType)) {
			// Getting the array
			Bytecode loadArrayBytecode = new Bytecode.Load(registerIndex, JAVA_UTIL_ARRAYLIST);

			// Load a constant with object 'object' and then place it on a stack
			Bytecode objectConstantBytecode = new Bytecode.LoadConst(object);

			// Adding the recently created bytecodes to the list of bytecodes
			List<Bytecode> toBeAdded = Arrays.asList(loadArrayBytecode, objectConstantBytecode);
			bytecodes.addAll(toBeAdded);

			// Box the type of the object added to the list as necessary
			boxAsNecessary(objectType, bytecodes);

			// Creating a new object to be added to an array
			Bytecode invocationBytecode = new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST, "add",
					new JvmType.Function(new JvmType.Primitive.Bool(), JAVA_LANG_OBJECT), VIRTUAL);

			// Pop an object from the array
			Bytecode popBytecode = new Bytecode.Pop(JvmTypes.JAVA_LANG_BOOLEAN);

			// Adding the newly created bytecodes to the list 'bytecodes'
			List<Bytecode> toBeAdded2 = Arrays.asList(invocationBytecode, popBytecode);
			bytecodes.addAll(toBeAdded2);
		}

		// Considering the case 'collectionType' is a record
		else if (isRecordType(collectionType)) {
			// Getting the hash map
			Bytecode loadHashMapBytecode = new Bytecode.Load(registerIndex, JAVA_UTIL_HASHMAP);

			// Loading the key 'keyName'
			Bytecode hashMapKeyBytecode = new Bytecode.LoadConst(keyName);

			// Loading the value 'object'
			Bytecode objectValueBytecode = new Bytecode.LoadConst(object);

			// Adding the recently created bytecodes to the list of bytecodes
			List<Bytecode> toBeAdded = Arrays.asList(loadHashMapBytecode, hashMapKeyBytecode,
					objectValueBytecode);
			bytecodes.addAll(toBeAdded);

			// Box the type of the object added to the list as necessary
			boxAsNecessary(objectType, bytecodes);

			// Adding the key-value pair into the hash map
			Bytecode putKeyValuePairBytecode = new Bytecode.Invoke(JAVA_UTIL_HASHMAP, "put",
					new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT),
					VIRTUAL);

			// Pop an object from the array
			Bytecode popBytecode = new Bytecode.Pop(JvmTypes.JAVA_LANG_OBJECT);

			// Adding the newly created bytecodes to the list 'bytecodes'
			List<Bytecode> toBeAdded2 = Arrays.asList(putKeyValuePairBytecode, popBytecode);
			bytecodes.addAll(toBeAdded2);
		}

		// Else, throw an IllegalArgumentException
		else {
			throw new IllegalArgumentException("Invalid argument detected! Type '" + collectionType +
					"' is not a valid collection type!");
		}
	}


	// Creating a method to translate a dereference expression into bytecodes.
	private void translate(Expr.Dereference expr, Context context, List<Bytecode> bytecodes) {
		Attribute.Type attr = expr.getSource().attribute(Attribute.Type.class);
		// Getting the source and index of the dereference expression.
		Expr source = expr.getSource();
		Expr index = new Expr.Literal(0, new Attribute.Type(new Type.Int()));
		translate(source, context, bytecodes);
		translate(index, context, bytecodes);

		// Get value out of ArrayList
		JvmType.Function ft = new JvmType.Function(JAVA_LANG_OBJECT, JvmTypes.INT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST,"get",ft, VIRTUAL));

		// unbox the element value on the stack
		Type elementType = ((Type.Reference) attr.type).getElement();
		addReadConversion(elementType,bytecodes);
	}


	// Creating a method to translate 'Expr.ArrayAccess' type expression into bytecode.
	// This method is inspired by the lecture slide #15 of the following source.
	// https://ecs.wgtn.ac.nz/foswiki/pub/Courses/SWEN430_2021T2/LectureSchedule/JavaBytecode2.pdf
	private void translate(Expr.ArrayAccess expr, Context context, List<Bytecode> bytecodes) {
		Attribute.Type attr = expr.getSource().attribute(Attribute.Type.class);
		// Translate source and index expressions
		translate(expr.getSource(),context,bytecodes);
		translate(expr.getIndex(),context,bytecodes);
		// Get value out of ArrayList
		JvmType.Function ft = new JvmType.Function(JAVA_LANG_OBJECT, JvmTypes.INT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST,"get",ft, VIRTUAL));
		// unbox the element value on the stack
		Type elementType;
		if (attr.type instanceof Type.Named) {
			elementType = ((Type.Array) declaredTypes.get(((Type.Named) attr.type).getName())).getElement();
		}
		else {
			elementType = ((Type.Array) attr.type).getElement();
		}
		assert elementType != null;
		addReadConversion(elementType,bytecodes);
	}

	// Translating an "Expr.Invoke" expression into bytecodes
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Expr.Invoke expr, Context context, List<Bytecode> bytecodes) {
		// Getting the name of the method being invoked
		JvmType.Function type = methodTypes.get(expr.getName());

		// Getting the list of arguments passed to the invoked method
		List<Expr> arguments = expr.getArguments();

		// Iterating through each argument passed to the invoked method
		for(int i=0;i!=arguments.size();++i) {
			// Getting the expression at index 'i' of the list 'bytecodes'
			Expr currExpr = arguments.get(i);

			// Translating 'currExpr' into bytecodes
			translate(currExpr,context,bytecodes);

			// Translating the type of 'currExpr' into a JVM type
			Type argumentType = currExpr.attribute(Attribute.Type.class).type;

			// Boxing the type of the argument if necessary
			JvmType parameterType = type.parameterTypes().get(i);
			if (parameterType instanceof JvmType.Reference) {
				boxAsNecessary(argumentType, bytecodes);
			}

			JvmType argumentJvmType = toJvmType(argumentType);

			// Cloning 'argumentType' as necessary
			if (argumentType instanceof Type.Reference) {
				continue;
			}
			else if (argumentType instanceof Type.Array) {
				Type.Array arrayType = (Type.Array) argumentType;
				if (arrayType.getElement() instanceof Type.Reference) {
					continue;
				}
				else {
					cloneAsNecessary(argumentJvmType, bytecodes);
				}
			}
			else {
				cloneAsNecessary(argumentJvmType, bytecodes);
			}
		}

		// Creating a new bytecode and then add it to the list 'bytecodes'
		Bytecode toBeAdded = new Bytecode.Invoke(context.getEnclosingClass(), expr.getName(), type,
				Bytecode.InvokeMode.STATIC);
		bytecodes.add(toBeAdded);
	}

	// Creating a method to translate a field dereference expression into bytecodes.
	private void translate(Expr.FieldDereference expr, Context context, List<Bytecode> bytecodes) {
		//get hashmap
		Attribute.Type attr = expr.getSource().attribute(Attribute.Type.class);
		// Getting the source and index of the dereference expression.
		Expr source = expr.getSource();
		Expr index = new Expr.Literal(0, new Attribute.Type(new Type.Int()));
		translate(source, context, bytecodes);
		translate(index, context, bytecodes);

		// Get value out of ArrayList
		JvmType.Function ft = new JvmType.Function(JAVA_LANG_OBJECT, JvmTypes.INT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST,"get",ft, VIRTUAL));

		// unbox the element value on the stack
		Type elementType = ((Type.Reference) attr.type).getElement();
		addReadConversion(elementType,bytecodes);

		//put string
		bytecodes.add(new Bytecode.LoadConst(expr.getName()));
		//get method
		JvmType.Function boxMethodType =
				new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT, JvmTypes.JAVA_LANG_OBJECT);

		//add object to array
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_HASHMAP, "get", boxMethodType,VIRTUAL));

		//cast to expected type
		Attribute.Type newAttr = expr.attribute(Attribute.Type.class);
		Type t = newAttr.type;
		addReadConversion(t,bytecodes);
	}

	// Translating a record access expression into bytecodes
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Expr.RecordAccess expr, Context context, List<Bytecode> bytecodes) {
		// Translating the record into bytecodes
		Expr record = expr.getSource();
		translate(record, context, bytecodes);

		// Creating a bytecode with the name of the field being accessed by the RecordAccess expression and
		// adding the bytecode to the list 'bytecodes'
		String recordFieldName = expr.getName();
		Bytecode accessFieldBytecode = new Bytecode.LoadConst(recordFieldName);
		bytecodes.add(accessFieldBytecode);

		// Creating a bytecode to get the value mapped from key 'recordFieldName' in the hash map
		Bytecode getValueFromKeyBytecode = new Bytecode.Invoke(JAVA_UTIL_HASHMAP, "get",
				new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT, JvmTypes.JAVA_LANG_OBJECT), VIRTUAL);
		bytecodes.add(getValueFromKeyBytecode);

		// Adding the read conversion of the type of the attribute of the expression 'expr'
		Type expectedType = expr.attribute(Attribute.Type.class).type;
		addReadConversion(expectedType, bytecodes);
	}

	// Translating a record constructor expression into bytecodes
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Expr.RecordConstructor expr, Context context, List<Bytecode> bytecodes) {
		// Creating a hash map and then getting the register index
		int hashMapRegisterIndex = createHashMapType(context, bytecodes);

		// Iterating through each field in the record being constructed
		List<Pair<String, Expr>> recordTypeFields = expr.getFields();
		int i = 0; // initial value
		while (i < recordTypeFields.size()) {
			// Getting the field at index 'i' of the list 'recordTypeFields'
			Pair<String, Expr> currentField = recordTypeFields.get(i);

			// Getting the name of the field and the expression in 'currentField'
			String currentFieldName = currentField.first();
			Expr currentFieldExpr = currentField.second();

			// Creating a bytecode to get the hash map and then add it to the list 'bytecodes'
			Bytecode getHashMapBytecode = new Bytecode.Load(hashMapRegisterIndex, JAVA_UTIL_HASHMAP);
			bytecodes.add(getHashMapBytecode);

			// Placing the key-value pair into the hash map
			addToHashMap(currentFieldName, currentFieldExpr, context, bytecodes);

			// Moving on to the next iteration
			i++;
		}

		// Creating a bytecode to get the hash map and then add it to the list 'bytecodes'
		Bytecode getHashMapBytecode = new Bytecode.Load(hashMapRegisterIndex, JAVA_UTIL_HASHMAP);
		bytecodes.add(getHashMapBytecode);
	}

	// Translating a unary expression into bytecodes
	// This method is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private void translate(Expr.Unary expr, Context context, List<Bytecode> bytecodes) {
		if (expr.getOp() == Expr.UOp.NOT) {
			translate(expr.getExpr(),context,bytecodes);
			translateNotHelper(bytecodes);
		}
		else if (expr.getOp() == Expr.UOp.NEG) {
			translate(expr.getExpr(),context,bytecodes);
			bytecodes.add(new Bytecode.Neg(JvmTypes.INT));
		}
		else if (expr.getOp() == Expr.UOp.LENGTHOF) {
			translate(expr.getExpr(),context,bytecodes);
			Bytecode toBeAdded = new Bytecode.Invoke(JAVA_UTIL_ARRAYLIST, "size",
					new JvmType.Function(JvmTypes.INT), VIRTUAL);
			bytecodes.add(toBeAdded);
		}
		else if (expr.getOp() == Expr.UOp.NEW) {
			// Initialising bytecodes to be added for the reference type.
			// java.lang.Integer object does not have init()
			// We currently have 2 values on the stack, supposed to be 1.
			Type refType = expr.getExpr().attribute(Attribute.Type.class).type;
			JvmType refJvmType = toJvmType(refType);
			JvmType.Clazz owner = (JvmType.Clazz) toBoxedJvmType(refType);

			// TODO: Need to load the constant in expr.getExpr()
			int registerIndex = createArrayType(context, bytecodes);
			addToArray(expr.getExpr(), registerIndex, context, bytecodes);
			bytecodes.add(new Bytecode.Load(registerIndex, JAVA_UTIL_ARRAYLIST));
		}
		else {
			throw new IllegalArgumentException("unknown unary operator encountered");
		}
	}

	private void translateNotHelper(List<Bytecode> bytecodes) {
		String trueBranch = freshLabel();
		String exitLabel = freshLabel();
		bytecodes.add(new Bytecode.If(IfMode.EQ, trueBranch));
		bytecodes.add(new Bytecode.LoadConst(false));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueBranch));
		bytecodes.add(new Bytecode.LoadConst(true));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}

	private void translate(Expr.Variable expr, Context context, List<Bytecode> bytecodes) {
		// Determine type of variable
		Attribute.Type attr = expr.attribute(Attribute.Type.class);
		JvmType type = toJvmType(attr.type);
		int register = context.getRegister(expr.getName());
		bytecodes.add(new Bytecode.Load(register, type));
	}

	/**
	 * This method is responsible for ensuring that the last bytecode in a
	 * method is a return bytecode. This is only necessary (and valid) in the
	 * case of a method which returns void.
	 *
	 * @param body
	 * @param bytecodes
	 */
	private void addReturnAsNecessary(WhileFile.MethodDecl md, List<Bytecode> bytecodes) {
		if(!allPathsReturn(md.getBody())) {
			bytecodes.add(new Bytecode.Return(null));
		}
	}

	/**
	 * Check whether every path through a given statement block ends in a return
	 * or not.  This is helpful in a few places.
	 *
	 * @param stmts
	 * @return
	 */
	private boolean allPathsReturn(List<Stmt> stmts) {
		for(Stmt stmt : stmts) {
			if(allPathsReturn(stmt)) {
				return true;
			}
		}
		return false;
	}

	private boolean allPathsReturn(Stmt stmt) {
		if(stmt instanceof Stmt.IfElse) {
			Stmt.IfElse ife = (Stmt.IfElse) stmt;
			return allPathsReturn(ife.getTrueBranch()) && allPathsReturn(ife.getFalseBranch());
		} else if(stmt instanceof Stmt.Return) {
			return true;
		}
		return false;
	}

	/**
	 * Clone the element on top of the stack, if it is of an appropriate type
	 * (i.e. is not a primitive).
	 *
	 * @param type
	 *            The type of the element on the top of the stack.
	 * @param context
	 *            The current translation context
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 */
	private void cloneAsNecessary(JvmType type, List<Bytecode> bytecodes) {
		if(type instanceof JvmType.Primitive || type == JvmTypes.JAVA_LANG_STRING || type == JvmTypes.NULL ||
		type == JAVA_LANG_OBJECT) {
			// no need to do anything in the case of a primitive or null type
		} else {
			// Invoke the clone function on the datatype in question
			JvmType.Function ft = new JvmType.Function(JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke((JvmType.Reference) type, "clone", ft, VIRTUAL));
			bytecodes.add(new Bytecode.CheckCast(type));
		}
	}

	/**
	 * Box the element on top of the stack, if it is of an appropriate type
	 * (i.e. is not a primitive).
	 *
	 * @param from
	 *            The type of the element we are converting from (i.e. on the
	 *            top of the stack).
	 * @param context
	 *            The current translation context
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 */
	private void boxAsNecessary(Type from, List<Bytecode> bytecodes) {
		JvmType.Clazz owner;
		JvmType jvmType = toJvmType(from);

		if(jvmType instanceof JvmType.Reference) {
			// Only need to box primitive types
			return;
		} else if(jvmType instanceof JvmType.Bool) {
			owner = JvmTypes.JAVA_LANG_BOOLEAN;
		} else if(jvmType instanceof JvmType.Char) {
			owner = JvmTypes.JAVA_LANG_CHARACTER;
		} else if(jvmType instanceof JvmType.Int) {
			owner = JvmTypes.JAVA_LANG_INTEGER;
		} else {
			throw new IllegalArgumentException("unknown primitive type encountered: " + jvmType);
		}

		String boxMethodName = "valueOf";
		JvmType.Function boxMethodType = new JvmType.Function(owner,jvmType);
		bytecodes.add(new Bytecode.Invoke(owner, boxMethodName, boxMethodType,
				Bytecode.InvokeMode.STATIC));
	}

	/**
	 * The element on the top of the stack has been read out of a compound data
	 * structure, such as an ArrayList or HashMap representing an array or
	 * record. This value has type Object, and we want to convert it into its
	 * correct form. At a minimum, this requires casting it into the expected
	 * type. This may also require unboxing the element if it is representing a
	 * primitive type.
	 *
	 * @param to
	 *            The type of the element we are converting to (i.e. that we
	 *            want to be on the top of the stack).
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 */
	private void addReadConversion(Type to, List<Bytecode> bytecodes) {
		// First cast to the boxed jvm type
		JvmType.Reference boxedJvmType = toBoxedJvmType(to);
		bytecodes.add(new Bytecode.CheckCast(boxedJvmType));
		// Second, unbox as necessary
		unboxAsNecessary(boxedJvmType,bytecodes);
	}

	/**
	 * Unbox a reference type when appropriate. That is, when it represented a
	 * boxed primitive type.
	 *
	 * @param jvmType
	 * @param bytecodes
	 */
	private void unboxAsNecessary(JvmType.Reference jvmType, List<Bytecode> bytecodes) {
		String unboxMethodName;
		JvmType.Primitive unboxedJvmType;

		if (jvmType.equals(JvmTypes.JAVA_LANG_BOOLEAN)) {
			unboxMethodName = "booleanValue";
			unboxedJvmType = JvmTypes.BOOL;
		} else if (jvmType.equals(JvmTypes.JAVA_LANG_CHARACTER)) {
			unboxMethodName = "charValue";
			unboxedJvmType = JvmTypes.CHAR;
		} else if (jvmType.equals(JvmTypes.JAVA_LANG_INTEGER)) {
			unboxMethodName = "intValue";
			unboxedJvmType = JvmTypes.INT;
		} else {
			return; // not necessary to unbox
		}
		JvmType.Function unboxMethodType = new JvmType.Function(unboxedJvmType);
		bytecodes.add(new Bytecode.Invoke(jvmType, unboxMethodName, unboxMethodType, VIRTUAL));
	}

	/**
	 * The construct method provides a generic way to construct a Java object
	 * using a default constructor which accepts no arguments.
	 *
	 * @param owner
	 *            The class type to construct
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 */
	private void constructObject(JvmType.Clazz owner, List<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(owner));
		bytecodes.add(new Bytecode.Dup(owner));
		JvmType.Function ftype = new JvmType.Function(JvmTypes.VOID, Collections.EMPTY_LIST);
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
	}

	/**
	 * Construct the JVM function type for this method declaration.
	 *
	 * @param method
	 * @return
	 */
	private JvmType.Function constructMethodType(WhileFile.MethodDecl method) {
		List<JvmType> parameterTypes = new ArrayList<JvmType>();
		// Convert each parameter type
		for(WhileFile.Parameter p : method.getParameters()) {
			JvmType jpt = toJvmType(p.getType());
			parameterTypes.add(jpt);
		}
		// convert the return type
		JvmType returnType = toJvmType(method.getRet());
		//
		JvmType.Function ft = new JvmType.Function(returnType, parameterTypes);
		methodTypes.put(method.getName(), ft);
		return ft;
	}

	/**
	 * Construct an initial context for the given method. In essence, this
	 * just maps every parameter to the corresponding JVM register, as these are
	 * automatically assigned by the JVM when the method is in invoked.
	 *
	 * @param method
	 * @return
	 */
	private Map<String,Integer> constructMethodEnvironment(WhileFile.MethodDecl method) {
		HashMap<String,Integer> environment = new HashMap<String,Integer>();
		int index = 0;
		for(WhileFile.Parameter p : method.getParameters()) {
			environment.put(p.getName(), index++);
		}
		return environment;
	}


	/**
	 * Check whether a While type is a primitive or not
	 *
	 * @param type
	 * @return
	 */
	private boolean isPrimitive(Type type) {
		if(type instanceof Type.Record || type instanceof Type.Array) {
			return false;
		} else if(type instanceof Type.Named) {
			Type.Named d = (Type.Named) type;
			return isPrimitive(declaredTypes.get(d.getName()));
		} else {
			return true;
		}
	}

	/**
	 * Get a new label name which has not been used before.
	 *
	 * @return
	 */
	private String freshLabel() {
		return "label" + fresh++;
	}

	private static int fresh = 0;

	/**
	 * Convert a While type into its JVM type.
	 *
	 * @param t
	 * @return
	 */
	private JvmType toJvmType(Type t) {
		if(t instanceof Type.Void) {
			return JvmTypes.VOID;
		} else if(t instanceof Type.Bool) {
			return JvmTypes.BOOL;
		} else if(t instanceof Type.Int) {
			return JvmTypes.INT;
		} else if(t instanceof Type.Named) {
			Type.Named d = (Type.Named) t;
			return toJvmType(declaredTypes.get(d.getName()));
		} else if(t instanceof Type.Array) {
			return JAVA_UTIL_ARRAYLIST;
		} else if(t instanceof Type.Record) {
			return JAVA_UTIL_HASHMAP;
		} else if (t instanceof Type.Reference) {
			// Type.Reference r = (Type.Reference) t; // reference is a boxed value
			// return toBoxedJvmType(r.getElement()); // need to decide appropriate JvmType to represent reference types
			return JAVA_UTIL_ARRAYLIST;
		} else if (t instanceof Type.Null) {
			return JvmTypes.JAVA_LANG_OBJECT;
		} else if (t instanceof Type.Union) {
			if (unionContainsAllSameType((Type.Union) t)) {
				if (((Type.Union) t).getBounds().get(0) instanceof Type.Null) {
					return JvmTypes.JAVA_LANG_OBJECT;
				}
				else {
					return toJvmType(((Type.Union) t).getBounds().get(0));
				}
			}
			else if (unionContainsAllArrayType((Type.Union) t)) {
				return JAVA_UTIL_ARRAYLIST;
			}
			else if (unionContainsAllRecordType((Type.Union) t)) {
				return JAVA_UTIL_HASHMAP;
			}
			return JvmTypes.JAVA_LANG_OBJECT;
		} else {
			throw new IllegalArgumentException("Unknown type encountered: " + t);
		}
	}

	/**
	 * Convert a While type into its boxed JVM type.
	 *
	 * @param t
	 * @return
	 */
	private JvmType.Reference toBoxedJvmType(Type t) {
		if(t instanceof Type.Bool) {
			return JvmTypes.JAVA_LANG_BOOLEAN;
		} else if(t instanceof Type.Int) {
			return JvmTypes.JAVA_LANG_INTEGER;
		} else if(t instanceof Type.Named) {
			Type.Named d = (Type.Named) t;
			return toBoxedJvmType(declaredTypes.get(d.getName()));
		} else if(t instanceof Type.Array) {
			return JAVA_UTIL_ARRAYLIST;
		} else if(t instanceof Type.Record) {
			return JAVA_UTIL_HASHMAP;
		} else if (t instanceof Type.Reference) {
			// Type.Reference r = (Type.Reference) t;
			// return toBoxedJvmType(r.getElement());
			return JAVA_UTIL_ARRAYLIST;
		} else if (t instanceof Type.Null) {
			return JvmTypes.JAVA_LANG_OBJECT;
		} else if (t instanceof Type.Union) {
			if (unionContainsAllSameType((Type.Union) t)) {
				if (((Type.Union) t).getBounds().get(0) instanceof Type.Null) {
					return JvmTypes.JAVA_LANG_OBJECT;
				}
				else {
					return toBoxedJvmType(((Type.Union) t).getBounds().get(0));
				}
			}
			else if (unionContainsAllArrayType((Type.Union) t)) {
				return JAVA_UTIL_ARRAYLIST;
			}
			else if (unionContainsAllRecordType((Type.Union) t)) {
				return JAVA_UTIL_HASHMAP;
			}
			return JAVA_LANG_OBJECT;
		} else {
			throw new IllegalArgumentException("Unknown type encountered: " + t);
		}
	}

	// Creating a method to check whether a Type.Union object contains a bound of type null or not.
	private boolean unionContainsNullType(Type.Union unionType) {
		for (Type currType : unionType.getBounds()) {
			if (currType instanceof Type.Null) {
				return true;
			}
		}

		return false;
	}

	// Creating a method to check whether a Type.Union object contains all bounds of the same type or not.
	private boolean unionContainsAllSameType(Type.Union unionType) {
		if (unionType.getBounds().size() == 0) {
			return true;
		}
		Type first = unionType.getBounds().get(0);
		for (Type type : unionType.getBounds()) {
			if (type.getClass() != first.getClass()) {
				return false;
			}
		}

		return true;
	}

	// Creating a method to check whether a Type.Union object contains all bounds of type 'Type.Array' or not
	private boolean unionContainsAllArrayType(Type.Union unionType) {
		for (Type currType : unionType.getBounds()) {
			if (!(currType instanceof Type.Array)) {
				return false;
			}
		}

		return true;
	}

	// Creating a method to check whether a Type.Union object contains all bounds of type 'Type.Record' or not
	private boolean unionContainsAllRecordType(Type.Union unionType) {
		for (Type currType : unionType.getBounds()) {
			if (!(currType instanceof Type.Record)) {
				return false;
			}
		}

		return true;
	}

	// A few helpful constants not defined in JvmTypes
	private static final JvmType.Clazz JAVA_UTIL_LIST = new JvmType.Clazz("java.util","List");
	private static final JvmType.Clazz JAVA_UTIL_ARRAYLIST = new JvmType.Clazz("java.util","ArrayList");
	private static final JvmType.Clazz JAVA_UTIL_HASHMAP = new JvmType.Clazz("java.util","HashMap");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTION = new JvmType.Clazz("java.util","Collection");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTIONS = new JvmType.Clazz("java.util","Collections");

	/**
	 * Provides useful contextual information which passed down through the
	 * translation process.
	 *
	 * @author David J. Pearce
	 *
	 */
	// This class is inspired by the following source.
	// https://github.com/kianyewest/SWEN430/tree/master/swen430as3
	private static class Context {
		/**
		 * The type of the enclosing class. This is needed to invoke methods
		 * within the same class.
		 */
		private final JvmType.Clazz enclosingClass;

		/**
		 * Maps each declared variable to a jvm register index
		 */
		private final Map<String,Integer> environment;

		/**
		 * A stack to deal with the loop.
		 * */
		private final Stack<Map<String, String>> loopStack;

		public Context(JvmType.Clazz enclosingClass, Map<String,Integer> environment) {
			this.enclosingClass = enclosingClass;
			this.environment = environment;
			loopStack = new Stack<>();
		}

		public Context(Context context) {
			this.enclosingClass = context.enclosingClass;
			this.environment = new HashMap<String,Integer>(context.environment);
			loopStack = new Stack<>();
		}

		/**
		 * Get the enclosing class for this translation context.
		 *
		 * @return
		 */
		public JvmType.Clazz getEnclosingClass() {
			return enclosingClass;
		}

		/**
		 * Declare a new variable in the given context. This basically allocated
		 * the given variable to the next available register slot.
		 *
		 * @param var
		 * @param environment
		 * @return
		 */
		public int declareRegister(String var) {
			int register = environment.size();
			environment.put(var, register);
			return register;
		}

		/**
		 * Return the register index associated with a given variable which has
		 * been previously declared.
		 *
		 * @param var
		 * @return
		 */
		public int getRegister(String var) {
			return environment.get(var);
		}

		// Creating methods which deal with the loop stack
		// 1. A method to pop from the stack if it is not empty
		public boolean popLoop() {
			// If the stack is not empty, pop from the stack
			if (!loopStack.isEmpty()) {
				loopStack.pop();
				return true;
			}
			return false;
		}

		// 2. A method to push an element to the stack
		public void pushLoop(HashMap<String, String> loop) {
			loopStack.add(loop);
		}

		// 3. Creating a method to peek from the stack to see the most recent loop
		public Map<String, String> peekMostRecentLoop() {
			return loopStack.peek();
		}
	}
}
