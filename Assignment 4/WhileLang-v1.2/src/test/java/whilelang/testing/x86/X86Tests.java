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
package whilelang.testing.x86;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import jx86.io.AsmFileWriter;
import jx86.lang.Target;
import jx86.lang.X86File;
import whilelang.ast.Attribute;
import whilelang.ast.Expr;
import whilelang.ast.Stmt;
import whilelang.ast.Type;
import whilelang.ast.WhileFile;
import whilelang.compiler.DefiniteAssignment;
import whilelang.compiler.Lexer;
import whilelang.compiler.Parser;
import whilelang.compiler.TypeChecker;
import whilelang.compiler.UnreachableCode;
import whilelang.compiler.X86FileWriter;
import whilelang.testing.WhileLangTests;
import whilelang.util.AbstractSyntaxTree;
import whilelang.util.Pair;
import whilelang.util.SyntacticElement;
import whilelang.util.SyntaxError;
import whilelang.util.SyntaxError.InternalFailure;

public class X86Tests {
	/**
	 * Target architecture for testing. If you're running on a Mac then you will
	 * need to change this appropriately.
	 */
	public final static Target TARGET = Target.MACOS_X86_64;
	/**
	 * Determines the location of the While language source files to use for testing.
	 */
	private static final Path WHILE_SRC_DIR = Paths.get("tests/");
	/**
	 * Determines the location of the runtime library provided with X86 extension.
	 */
	private static final Path RUNTIME_LIBRARY = Paths.get("src/main/java/whilelang/runtime/runtime.c");
	/**
	 * Determines the path/name of the C compiler to use. You can change this if you
	 * have another compiler, such as clang installed.
	 */
	private static final String CC_COMMAND = "gcc";
	/**
	 * The extension given to assembly files on this platform (normally they have
	 * the extension ".s" on UNIX-based platforms).
	 */
	private static final String ASM_SUFFIX = ".s";
	/**
	 * The extension given to executable files on this platform (normally they have
	 * no extension on UNIX-based platforms, and ".exe" on Windows platforms).
	 */
	private static final String EXE_SUFFIX = "";

	@ParameterizedTest
	@MethodSource("part1Files")
	public void part_1(Path source) throws IOException {
		// Determine name of target file
		Path target = toExtension(source,EXE_SUFFIX);
		// Compile target file
		compileTest(source, target);
		// Try an execute target file
		executeTest(target);
	}

	@ParameterizedTest
	@MethodSource("part2Files")
	public void part_2(Path source) throws IOException {
		// Determine name of target file
		Path target = toExtension(source,EXE_SUFFIX);
		// Compile target file
		compileTest(source, target);
		// Try an execute target file
		executeTest(target);
	}

	@ParameterizedTest
	@MethodSource("part3Files")
	public void part_3(Path source) throws IOException {
		// Determine name of target file
		Path target = toExtension(source,EXE_SUFFIX);
		// Compile target file
		compileTest(source, target);
		// Try an execute target file
		executeTest(target);
	}

	@ParameterizedTest
	@MethodSource("part4Files")
	public void part_4(Path source) throws IOException {
		// Determine name of target file
		Path target = toExtension(source,EXE_SUFFIX);
		// Compile target file
		compileTest(source, target);
		// Try an execute target file
		executeTest(target);
	}

	/**
	 * Compile the While source file into a native executable. This is done by first
	 * using jx86 to generate an intermediate assembly language file, and the
	 * compiling this using the specified C compiler (GCC by default) to generate an
	 * executable.
	 *
	 * @param source Path to the source file
	 * @param target Path where the class file should be created.
	 * @throws IOException
	 */
	public static void compileTest(Path source, Path target) throws IOException {
		// Determine filename for assembly file (".s" extension).
		Path asmTarget = toExtension(source, ASM_SUFFIX);
		//
		try {
			// Run standard parts of the compiler
			Lexer lexer = new Lexer(source);
			Parser parser = new Parser(source, lexer.scan());
			WhileFile ast = parser.read();
			new TypeChecker().check(ast);
			new DefiniteAssignment(ast).apply();
			new UnreachableCode().check(ast);
			X86File file = new X86FileWriter(TARGET).build(ast);
			new AsmFileWriter(asmTarget.toFile()).write(file);
			// Second, we need to compile the assembly file with gcc
			compileWithCC(WHILE_SRC_DIR, target, asmTarget, RUNTIME_LIBRARY);
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
	 * Compile one or more source files (e.g. C files or assembly files) into a
	 * given target executable using GCC.
	 *
	 * @param dir    Directory where files are located, and where the target
	 *               executable should be placed.
	 * @param target Filename of target executable.
	 * @param files  One or more source files to use for compilation.
	 */
	public static void compileWithCC(Path dir, Path target, Path... files) {
		try {
			String[] args = new String[files.length + 4];
			args[0] = CC_COMMAND;
			args[1] = "-Wno-format";
			args[2] = "-o";
			args[3] = target.toString();
			for (int i = 0; i != files.length; ++i) {
				args[i + 4] = files[i].toString();
			}
			Process p = Runtime.getRuntime().exec(args);
			StringBuffer syserr = new StringBuffer();
			StringBuffer sysout = new StringBuffer();
			new StreamGrabber(p.getErrorStream(), syserr);
			new StreamGrabber(p.getInputStream(), sysout);
			int exitCode = p.waitFor();
			// stream
			if (exitCode != 0) {
				System.err
				.println("============================================================");
				System.err.println(Arrays.toString(args));
				System.err
				.println("============================================================");
				System.err.println(sysout); // propagate anything from stdout
				System.err.println(syserr); // propagate anything from stderr
				fail("Problem running gcc to compile test");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Problem running gcc to compile test");
		}
	}

	/**
	 * Run the executable generated for a given test, whilst returning any stdout
	 * generated by the process.
	 *
	 * @param target Path to executable to run.
	 * @throws IOException
	 */
	public static String executeTest(Path target) throws IOException {
		try {
			Process p = Runtime.getRuntime().exec(target.toString());
			StringBuffer syserr = new StringBuffer();
			StringBuffer sysout = new StringBuffer();
			new StreamGrabber(p.getErrorStream(), syserr);
			new StreamGrabber(p.getInputStream(), sysout);
			int exitCode = p.waitFor();
			if (exitCode != 0) {
				System.err.println("============================================================");
				System.err.println(target);
				System.err.println("============================================================");
				System.err.println(syserr); // propagate anything from the error
				fail("Assertion failure running native executable");
			}
			// stream
			return sysout.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Problem running native executable");
			return null;
		}
	}

	private static Stream<Path> part1Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> !f.contains("Invalid") && !contains(f, PART2_ELEMENTS)
				&& !contains(f, PART3_ELEMENTS) && !containsNestedCompoundConstructor(f) && !contains(f, OTHER_ELEMENTS));
	}

	private static Stream<Path> part2Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> !f.contains("Invalid") && contains(f, PART2_ELEMENTS)
				&& !contains(f, PART3_ELEMENTS) && !containsNestedCompoundConstructor(f) && !contains(f, OTHER_ELEMENTS));
	}

	private static Stream<Path> part3Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> !f.contains("Invalid") && contains(f, PART3_ELEMENTS)
				&& !containsNestedCompoundConstructor(f) && !contains(f, OTHER_ELEMENTS));
	}

	private static Stream<Path> part4Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR,
				f -> !f.contains("Invalid") && containsNestedCompoundConstructor(f) && !contains(f, OTHER_ELEMENTS));
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
		Type.Array.class,
		Type.Record.class,
	};

	/**
	 * Elements we're ignoring for this assignment.
	 */
	private static Class<?>[] OTHER_ELEMENTS = new Class<?>[]{
		Expr.Is.class,
		Expr.Cast.class,
		Type.Null.class,
		Type.Union.class,
		Type.Reference.class,
	};

	/**
	 * Replace the ".while" extension on a given path with an alternative suffix
	 * (e.g. ".s").
	 *
	 * @param p
	 * @return
	 */
	private static Path toExtension(Path p, String suffix) {
		String f = p.toString().replace(".while", suffix);
		return Paths.get(f);
	}

	/**
	 * Determine whether a given While file constrains an instance of a given set of
	 * classes.  Just one instance of one of the classes is enough.
	 *
	 * @param file
	 * @param elements
	 * @return
	 * @throws IOException
	 */
	@SafeVarargs
	private static boolean contains(String filename, Class<?>... elements) {
		// Construct a "boxed" result
		final boolean[] result = new boolean[1];
		// Construct a suitable consumer
		Consumer<SyntacticElement> fn = element -> {
			for (int i = 0; i != elements.length; ++i) {
				if (elements[i].isInstance(element)) {
					result[0] = true;
					break;
				}
			}
		};
		// Run the AST visitor
		visit(filename,fn);
		// Done
		return result[0];
	}


	/**
	 * Determine whether a given source file contains a nested compound constructor
	 * (e.g. <code>[[1],[2]]</code> or <code>{f:[1]}</code>, etc.
	 *
	 * @param filenam
	 * @return
	 */
	private static boolean containsNestedCompoundConstructor(String filename) {
		// Construct a "boxed" result
		final boolean[] result = new boolean[1];
		// Construct a suitable consumer
		Consumer<SyntacticElement> fn = element -> {
			if(element instanceof Expr) {
				Expr expr = (Expr) element;
				if (isNestedCompoundConstructor(expr)) {
					result[0] = true;
				}
			}
		};
		// Run the AST visitor
		visit(filename,fn);
		// Done
		return result[0];
	}

	/**
	 * Apply a given consumer to every node in the AbstractSyntaxTree of a given
	 * file.
	 *
	 * @param filename
	 * @param fn
	 */
	private static void visit(String filename, Consumer<SyntacticElement> fn) {
		try {
			Path p = Paths.get(filename);
			Lexer lexer = new Lexer(p);
			Parser parser = new Parser(p, lexer.scan());
			WhileFile ast = parser.read();
			AbstractSyntaxTree.Visitor v = new AbstractSyntaxTree.Visitor(ast);
			v.apply(fn);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determine whether the given expression is a nested compound constructor (e.g.
	 * <code>[[1],[2]]</code> or <code>{f:[1]}</code>, etc.
	 *
	 * @param type
	 * @return
	 */
	public static boolean isNestedCompoundConstructor(Expr expr) {
		if (expr instanceof Expr.ArrayGenerator) {
			Expr.ArrayGenerator e = (Expr.ArrayGenerator) expr;
			return isCompoundConstructor(e.getValue());
		} else if (expr instanceof Expr.ArrayInitialiser) {
			Expr.ArrayInitialiser e = (Expr.ArrayInitialiser) expr;
			for (Expr element : e.getArguments()) {
				if (isCompoundConstructor(element)) {
					return true;
				}
			}
		} else if (expr instanceof Expr.RecordConstructor) {
			Expr.RecordConstructor e = (Expr.RecordConstructor) expr;
			for (Pair<String, Expr> field : e.getFields()) {
				if (isCompoundConstructor(field.second())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determine whether a given expression is a compound constructor or not (e.g
	 * <code>[1,2,3]</code>, <code>{f:1}</code> or <code>"hello"</code>.
	 *
	 *
	 * @param type
	 * @return
	 */
	public static boolean isCompoundConstructor(Expr expr) {
		if(expr instanceof Expr.ArrayGenerator) {
			return true;
		} else if(expr instanceof Expr.ArrayInitialiser) {
			return true;
		} else if(expr instanceof Expr.RecordConstructor) {
			return true;
		} else if(expr instanceof Expr.Literal) {
			Expr.Literal e = (Expr.Literal) expr;
			return e.getValue() instanceof String;
		} else {
			return false;
		}
	}

	/**
	 * Grab everything produced by a given input stream until the End-Of-File
	 * (EOF) is reached. This is implemented as a separate thread to ensure that
	 * reading from other streams can happen concurrently. For example, we can
	 * read concurrently from <code>stdin</code> and <code>stderr</code> for
	 * some process without blocking that process.
	 *
	 * @author David J. Pearce
	 *
	 */
	static public class StreamGrabber extends Thread {
		private InputStream input;
		private StringBuffer buffer;

		StreamGrabber(InputStream input, StringBuffer buffer) {
			this.input = input;
			this.buffer = buffer;
			start();
		}

		@Override
		public void run() {
			try {
				int nextChar;
				// keep reading!!
				while ((nextChar = input.read()) != -1) {
					buffer.append((char) nextChar);
				}
			} catch (IOException ioe) {
			}
		}
	}
}
