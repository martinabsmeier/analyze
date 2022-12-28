/*
 * Copyright 2022 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.am.analyze.parser.java;

import de.am.analyze.common.component.Component;
import de.am.analyze.parser.SourceParserFactory;
import de.am.analyze.parser.common.ApplicationBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.File;

import static de.am.analyze.common.AnalyzeConstants.USER_DIR;
import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JUnit test cases for constructors of java classes.
 *
 * @author Martin Absmeier
 */
@TestInstance(Lifecycle.PER_CLASS)
class JavaConstructorTest {

    private File directory;

    @BeforeAll
    void beforeAll() {
        String path = USER_DIR.concat(separator)
            .concat("src").concat(separator)
            .concat("test").concat(separator)
            .concat("resources").concat(separator)
            .concat("java").concat(separator)
            .concat("constructor").concat(separator);
        directory = new File(path);

    }

    @Test
    void parseDirectory() {
        JavaSourceParser parser = SourceParserFactory.createJavaSourceParser("ParseConstructorTest", null, null);
        assertNotNull(parser, "We expect an parser instance.");

        parser.parseDirectory(directory);
        ApplicationBase application = parser.getApplication();
        Component components = application.getComponents();
    }
}