package de.am.analyze.parser.java.listener;

import de.am.analyze.parser.SourceParserFactory;
import de.am.analyze.parser.common.ApplicationBase;
import de.am.analyze.parser.java.JavaSourceParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.File;

import static de.am.analyze.common.AnalyzeConstants.COMMON.USER_DIR;
import static de.am.analyze.common.AnalyzeConstants.DEFAULT.REVISION_ID;
import static java.io.File.separator;

/**
 * JUnit test cases of {@link JavaTypeListener} class.
 *
 * @author Martin Absmeier
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractListenerTest {

    protected ApplicationBase application;

    @BeforeAll
    void beforeAll() {
        String path = USER_DIR.concat(separator)
                .concat("src").concat(separator)
                .concat("test").concat(separator)
                .concat("resources").concat(separator)
                .concat("java").concat(separator)
                .concat("structure").concat(separator);

        JavaSourceParser parser = SourceParserFactory.createJavaSourceParser(REVISION_ID, null, null);
        parser.parseDirectory(new File(path));

        application = parser.getApplication();
    }
}