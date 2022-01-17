package whilelang.testing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import whilelang.ast.WhileFile;
import whilelang.compiler.DefiniteAssignment;
import whilelang.compiler.Lexer;
import whilelang.compiler.DefiniteAssignment;
import whilelang.compiler.Parser;
import whilelang.compiler.TypeChecker;
import whilelang.compiler.UnreachableCode;
import whilelang.util.Interpreter;
import whilelang.util.SyntaxError;
import whilelang.util.SyntaxError.InternalFailure;

public class WhileLangTests {
	/**
	 * Identifies where the test files are stored.
	 */
	private static final Path WHILE_SRC_DIR = Paths.get("tests/");
	/**
	 * Represents the different kinds of tests that we support. In particular,
	 * different tests which test different parts of the compiler.
	 *
	 * @author David J. Pearce
	 *
	 */
	enum Kind {
		/**
		 * Ensures parser correctly catches an error.
		 */
		Parsing,
		/**
		 * Ensures type checker correctly catches an error.
		 */
		Typing,
		/**
		 * Ensures definite assignment checker checker correctly catches an error.
		 */
		DefiniteAssignment,
		/**
		 * Ensures unreachable code checker checker correctly catches an error.
		 */
		UnreachableCode,
		/**
		 * Ensures all checks pass and test executes without raising an exception.
		 */
		Execution
	}

	@ParameterizedTest
	@MethodSource("data")
	public void test(Path whileFile) throws IOException {
		//File srcFile = new File(WHILE_SRC_DIR + );
		// Determine kind of test we are conducting.
		Kind kind = determineTestKind(whileFile);
		WhileFile ast;
		// First, attempt to parse the relevant While file. For parsing tests, we are
		// expecting them to fail during parsing and hence we account for this
		// separately.
		try {
			Lexer lexer = new Lexer(whileFile);
			Parser parser = new Parser(whileFile, lexer.scan());
			ast = parser.read();
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			// Success!
			e.outputSourceError(System.err);
			// Sanity check failure occurred in the right phase.
			if(kind != Kind.Parsing) {
				throw e;
			} else {
				return;
			}
		}
		// Second, either apply one of the compiler phases to the resulting AST and
		// catch the execpted error, or attempt to execute the AST and asssume no errors
		// should occur.
		try {
			switch (kind) {
			case Parsing:
				// If we get here, then its a test failure as the parsing phase should have
				// thrown an exception already.
				fail();
			case Typing:
				new TypeChecker().check(ast);
				fail();
			case DefiniteAssignment:
				new DefiniteAssignment(ast).apply();
				fail();
			case UnreachableCode:
				new UnreachableCode().check(ast);
				fail();
			case Execution:
				// run all checks
				new TypeChecker().check(ast);
				new DefiniteAssignment(ast).apply();
				new UnreachableCode().check(ast);
				// execute program
				new Interpreter().run(ast);
				break;
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			// Print out details
			e.outputSourceError(System.err);
			// Sanity check failure occurred in the right phase.
			if(kind == Kind.Execution) {
				throw e;
			} else {
				return;
			}
		}
	}

	/**
	 * Determine the kind of a given test. This is currently based on the filename
	 * of the test.
	 *
	 * @param whileFile
	 * @return
	 */
	private static Kind determineTestKind(Path whileFile) {
		String n = whileFile.toString();
		if (n.contains("Parsing_Invalid")) {
			return Kind.Parsing;
		} else if (n.contains("Typing_Invalid")) {
			return Kind.Typing;
		} else if (n.contains("DefiniteAssign_Invalid")) {
			return Kind.DefiniteAssignment;
		} else if (n.contains("Deadcode_Invalid")) {
			return Kind.UnreachableCode;
		} else {
			return Kind.Execution;
		}
	}

	// Here we enumerate all available test cases.
	private static Stream<Path> data() throws IOException {
		return readTestFiles(WHILE_SRC_DIR);
	}

	public static Stream<Path> readTestFiles(Path dir) throws IOException {
		return readTestFiles(dir,f -> true);
	}

	public static Stream<Path> readTestFiles(Path dir, Predicate<String> filter) throws IOException {
		ArrayList<Path> testcases = new ArrayList<>();
		//
		Files.walk(dir,1).forEach(f -> {
			if (f.toString().endsWith(".while") && filter.test(f.toString())) {
				testcases.add(f);
			}
		});
		// Sort the result by filename
		Collections.sort(testcases);
		//
		return testcases.stream();
	}
}
