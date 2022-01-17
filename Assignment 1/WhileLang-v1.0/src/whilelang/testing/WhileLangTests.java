package whilelang.testing;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import whilelang.ast.WhileFile;
import whilelang.compiler.DefiniteAssignment;
import whilelang.compiler.Lexer;
import whilelang.compiler.Parser;
import whilelang.compiler.TypeChecker;
import whilelang.compiler.UnreachableCode;
import whilelang.compiler.WhileCompiler;
import whilelang.util.Interpreter;
import whilelang.util.SyntaxError;
import whilelang.util.SyntaxError.InternalFailure;

@RunWith(Parameterized.class)
public class WhileLangTests {
private static final String WHILE_SRC_DIR = "tests/".replace('/', File.separatorChar);

	private final String testName;

	public WhileLangTests(String testName) {
		this.testName = testName;
	}

	// Here we enumerate all available test cases.
	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		ArrayList<Object[]> testcases = new ArrayList<>();
		for (File f : new File(WHILE_SRC_DIR).listFiles()) {
			if (f.isFile()) {
				String name = f.getName();
				if (name.endsWith(".while")) {
					// Get rid of ".while" extension
					String testName = name.substring(0, name.length() - 6);
					testcases.add(new Object[] { testName });
				}
			}
		}
		// Sort the result by filename
		Collections.sort(testcases, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				return ((String) o1[0]).compareTo((String) o2[0]);
			}
		});
		return testcases;
	}

	@Test
	public void test() throws IOException {
		File srcFile = new File(WHILE_SRC_DIR + testName + ".while");
		//
		if(testName.startsWith("Parsing_Invalid")) {
			parsingTest(srcFile);
		} else if(testName.startsWith("Typing_Invalid")) {
			typingTest(srcFile);
		} else if(testName.startsWith("DefiniteAssign_Invalid")) {
			definiteAssignmentTest(srcFile);
		} else if(testName.startsWith("Deadcode_Invalid")) {
			deadcodeTest(srcFile);
		} else {
			executionTest(srcFile);
		}
	}

	/**
	 * Run a test of the parse. The key here is that we are specifically
	 * expecting this component to fail (not any other).
	 *
	 * @param srcFile
	 * @throws IOException
	 */
	private void parsingTest(File srcFile) throws IOException {
		try {
			Lexer lexer = new Lexer(srcFile.getPath());
			Parser parser = new Parser(srcFile.getPath(), lexer.scan());
			WhileFile ast = parser.read();
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			// Success!
			e.outputSourceError(System.err);
		}
	}

	/**
	 * Run a test of the type checker. The key here is that we are specifically
	 * expecting this component to fail (not any other).
	 *
	 * @param srcFile
	 * @throws IOException
	 */
	private void typingTest(File srcFile) throws IOException {
		Lexer lexer = new Lexer(srcFile.getPath());
		Parser parser = new Parser(srcFile.getPath(), lexer.scan());
		WhileFile ast = parser.read();
		try {
			new TypeChecker().check(ast);
			fail();
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			// Success!
			e.outputSourceError(System.err);
		}
	}

	/**
	 * Run a test of the definite assignment checker. The key here is that we are
	 * specifically expecting this component to fail (not any other).
	 *
	 * @param srcFile
	 * @throws IOException
	 */
	private void definiteAssignmentTest(File srcFile) throws IOException {
		Lexer lexer = new Lexer(srcFile.getPath());
		Parser parser = new Parser(srcFile.getPath(), lexer.scan());
		WhileFile ast = parser.read();
		try {
			new DefiniteAssignment().check(ast);
			fail();
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			// Success!
			e.outputSourceError(System.err);
		}
	}

	/**
	 * Run a test of the unreachable codechecker. The key here is that we are
	 * specifically expecting this component to fail (not any other).
	 *
	 * @param srcFile
	 * @throws IOException
	 */
	private void deadcodeTest(File srcFile) throws IOException {
		Lexer lexer = new Lexer(srcFile.getPath());
		Parser parser = new Parser(srcFile.getPath(), lexer.scan());
		WhileFile ast = parser.read();
		try {
			new UnreachableCode().check(ast);
			fail();
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			// Success!
			e.outputSourceError(System.err);
		}
	}

	/**
	 * Run a test which is expected to compile and execute successfully.
	 *
	 * @param srcFile
	 * @throws IOException
	 */
	private void executionTest(File srcFile) throws IOException {
		try {
			WhileCompiler compiler = new WhileCompiler(srcFile);
			WhileFile ast = compiler.compile();
			new Interpreter().run(ast);
		} catch (SyntaxError e) {
			e.outputSourceError(System.err);
			throw e;
		}
	}
}
