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
package whilelang.testing.jvm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import whilelang.ast.Expr;
import whilelang.ast.Stmt;
import whilelang.ast.Type;
import whilelang.ast.WhileFile;
import whilelang.compiler.ClassFileWriter;
import whilelang.compiler.DefiniteAssignment;
import whilelang.compiler.Lexer;
import whilelang.compiler.Parser;
import whilelang.compiler.TypeChecker;
import whilelang.compiler.UnreachableCode;
import whilelang.testing.WhileLangTests;
import whilelang.util.AbstractSyntaxTree;
import whilelang.util.SyntacticElement;
import whilelang.util.SyntaxError;
import whilelang.util.SyntaxError.InternalFailure;

public class JvmTests {
	private static final Path WHILE_SRC_DIR = Paths.get("tests/");
	private static final boolean DEBUG = false; // can be helpful for debugging

	@ParameterizedTest
	@MethodSource("part1Files")
	public void part_1(Path source) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Determine name of target file
		Path target = toClass(source);
		// Compile target file
		compileTest(source, target);
		// Try an execute target file
		executeTest(target);
	}

	@ParameterizedTest
	@MethodSource("part2Files")
	public void part_2(Path source) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Determine name of target file
		Path target = toClass(source);
		// Compile target file
		compileTest(source, target);
		// Try an execute target file
		executeTest(target);
	}

	@ParameterizedTest
	@MethodSource("part3Files")
	public void part_3(Path source) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Determine name of target file
		Path target = toClass(source);
		// Compile target file
		compileTest(source, target);
		// Try an execute target file
		executeTest(target);
	}

	@ParameterizedTest
	@MethodSource("part4Files")
	public void part_4(Path source) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Determine name of target file
		Path target = toClass(source);
		// Compile target file
		compileTest(source, target);
		// Try an execute target file
		executeTest(target);
	}

	/**
	 * Compiler the source file into a JVM classfile
	 *
	 * @param source Path to the source file
	 * @param target Path where the class file should be created.
	 * @throws IOException
	 */
	public static void compileTest(Path source, Path target) throws IOException {
		try {
			// Run standard parts of the compiler
			Lexer lexer = new Lexer(source);
			Parser parser = new Parser(source, lexer.scan());
			WhileFile ast = parser.read();
			new TypeChecker().check(ast);
			new DefiniteAssignment(ast).apply();
			new UnreachableCode().check(ast);
			// Finally generate a class file
			new ClassFileWriter(target).setDebug(DEBUG).write(ast);
		} catch (InternalFailure e) {
			// Report error
			e.outputSourceError(System.err);
			// Just rethrow
			throw e;
		} catch (SyntaxError e) {
			// Report error
			e.outputSourceError(System.err);
			// Just rethrow
			throw e;
		}
	}

	/**
	 * Execute the generate class file on the JVM using reflection.
	 *
	 * @param filename
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void executeTest(Path p) throws IOException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Determine the test's filename
		String testname = p.getFileName().toString().replace(".class", "");
		// Now, we attempt to run the test using reflection
		Class<?> testClass = Class.forName(testname);
		Method m = testClass.getMethod("main");
		m.invoke(null);
	}

	private static Stream<Path> part1Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> !f.contains("Invalid") && !contains(f, PART2_ELEMENTS)
				&& !contains(f, PART3_ELEMENTS) && !contains(f, PART4_ELEMENTS) && !contains(f, OTHER_ELEMENTS));
	}

	private static Stream<Path> part2Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> !f.contains("Invalid") && contains(f, PART2_ELEMENTS)
				&& !contains(f, PART3_ELEMENTS) && !contains(f, PART4_ELEMENTS) && !contains(f, OTHER_ELEMENTS));
	}

	private static Stream<Path> part3Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> !f.contains("Invalid") && contains(f, PART3_ELEMENTS)
				&& !contains(f, PART4_ELEMENTS) && !contains(f, OTHER_ELEMENTS));
	}

	private static Stream<Path> part4Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR,
				f -> !f.contains("Invalid") && contains(f, PART4_ELEMENTS) && !contains(f, OTHER_ELEMENTS));
	}


	/**
	 * Elements relevant for part 2 of the assignment.
	 */
	private static Class<?>[] PART2_ELEMENTS = new Class<?>[]{
			Stmt.DoWhile.class,
			Stmt.For.class,
			Stmt.While.class,
			Stmt.Switch.class,
	};

	/**
	 * Elements relevant for part 3 of the assignment.
	 */
	private static Class<?>[] PART3_ELEMENTS = new Class<?>[]{
			Expr.ArrayAccess.class,
			Expr.ArrayInitialiser.class,
			Expr.ArrayGenerator.class,
			Expr.Dereference.class,
			Expr.FieldDereference.class,
			Expr.RecordAccess.class,
			Expr.RecordConstructor.class,
			//
			Type.Array.class,
			Type.Record.class,
			Type.Reference.class,
			Type.Union.class,
	};

	/**
	 * Elements relevant for part 4 of the assignment.
	 */
	private static Class<?>[] PART4_ELEMENTS = new Class<?>[]{
			Expr.Cast.class,
			Type.Null.class,
			Type.Union.class,
	};

	/**
	 * Elements we're ignoring for this assignment.
	 */
	private static Class<?>[] OTHER_ELEMENTS = new Class<?>[]{
			Expr.Is.class,
	};

	/**
	 * Convert a path for a While source file into a path for the corresponding class
	 * file.
	 *
	 * @param p
	 * @return
	 */
	private static Path toClass(Path p) {
		String f = p.toString().replace(".while", ".class");
		return Paths.get(f);
	}


	/**
	 * Check whether a give file contains a set of elements.
	 *
	 * @param file
	 * @param elements
	 * @return
	 * @throws IOException
	 */
	@SafeVarargs
	private static boolean contains(String fn, Class<?>... elements) {
		try {
			Path p = Paths.get(fn);
			Lexer lexer = new Lexer(p);
			Parser parser = new Parser(p, lexer.scan());
			WhileFile ast = parser.read();
			AbstractSyntaxTree.Visitor v = new AbstractSyntaxTree.Visitor(ast);
			ElementFinder f = new ElementFinder(elements);
			v.apply(f);
			return f.result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class ElementFinder implements Consumer<SyntacticElement> {
		private final Class<?>[] elements;
		public boolean result;

		public ElementFinder(Class<?>... elements) {
			this.elements = elements;
		}

		@Override
		public void accept(SyntacticElement element) {
			for (int i = 0; i != elements.length; ++i) {
				if (elements[i].isInstance(element)) {
					result = true;
					break;
				}
			}
		}
	}
}
