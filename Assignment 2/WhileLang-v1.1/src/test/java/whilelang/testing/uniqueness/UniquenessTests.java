package whilelang.testing.uniqueness;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import whilelang.ast.WhileFile;
import whilelang.compiler.DefiniteAssignment;
import whilelang.compiler.Lexer;
import whilelang.compiler.Parser;
import whilelang.compiler.TypeChecker;
import whilelang.compiler.UniquenessAnalysis;
import whilelang.compiler.UnreachableCode;
import whilelang.testing.WhileLangTests;
import whilelang.util.Interpreter;
import whilelang.util.SyntaxError;
import whilelang.util.SyntaxError.InternalFailure;

public class UniquenessTests {
	private static final Path WHILE_SRC_DIR = Paths.get("tests/uniqueness/");

	@ParameterizedTest
	@MethodSource("part1Files")
	public void part_1(Path p) throws IOException {
		test(p);
	}

	@ParameterizedTest
	@MethodSource("part2Files")
	public void part_2(Path p) throws IOException {
		test(p);
	}

	@ParameterizedTest
	@MethodSource("part3Files")
	public void part_3(Path p) throws IOException {
		test(p);
	}

	private void test(Path p) throws IOException {
		// Determine whether running valid or invalid test
		boolean invalid = p.toString().contains("Invalid");
		//
		try {
			Lexer lexer = new Lexer(p);
			Parser parser = new Parser(p, lexer.scan());
			WhileFile ast = parser.read();
			new TypeChecker().check(ast);
			new DefiniteAssignment(ast).apply();
			new UnreachableCode().check(ast);
			new UniquenessAnalysis(ast).apply();
			new Interpreter().run(ast);
			// Respond based on kind of test
			if(invalid) {
				fail();
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			// Report error
			e.outputSourceError(System.err);
			// Determine success
			if(invalid) {
				return;
			} else {
				throw e;
			}
		} catch (Interpreter.Fault e) {
			if(invalid) {
				return;
			} else {
				throw e;
			}
		}
	}

	private static Stream<Path> part1Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> f.contains("UniqueParsing"));
	}

	private static Stream<Path> part2Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> f.contains("UniqueTyping"));
	}

	private static Stream<Path> part3Files() throws IOException {
		return WhileLangTests.readTestFiles(WHILE_SRC_DIR, f -> f.contains("UniqueAnalysis"));
	}
}
