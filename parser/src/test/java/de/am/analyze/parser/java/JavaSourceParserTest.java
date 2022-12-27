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

import de.am.analyze.common.AnalyzeConstants;
import de.am.analyze.parser.SourceParserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static java.io.File.separator;

/**
 * JUnit test cases of {@link JavaSourceParser} class.
 *
 * @author Martin Absmeier
 */
class JavaSourceParserTest {

    private final String TEMURIN_11 = "temurin-11-src";
    private final String TEMURIN_17 = "temurin-17-src";

    private File dirToParse;

    @BeforeEach
    void setUp() {
        String path = AnalyzeConstants.USER_HOME_DIR.concat(separator)
            .concat("develop").concat(separator)
            .concat("library").concat(separator)
            .concat(TEMURIN_11).concat(separator)
            .concat("java.base").concat(separator)
            .concat("java").concat(separator)
            .concat("lang").concat(separator);
        dirToParse = new File(path);
    }

    @Test
    void parseDirectory() {
        JavaSourceParser parser = SourceParserFactory.createJavaSourceParser("JavaSourceParserTest", null, null);
        parser.parseDirectory(dirToParse);
    }
}