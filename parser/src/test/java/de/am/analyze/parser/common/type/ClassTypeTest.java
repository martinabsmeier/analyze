/*
 * Copyright 2023 Martin Absmeier
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
package de.am.analyze.parser.common.type;

import de.am.analyze.common.AnalyzeConstants.DEFAULT;
import de.am.analyze.common.component.Component;
import de.am.analyze.parser.SourceParserFactory;
import de.am.analyze.parser.java.JavaApplication;
import de.am.analyze.parser.java.JavaSourceParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;

import static de.am.analyze.common.AnalyzeConstants.COMMON.USER_DIR;
import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test cases of {@link ClassType} class.
 *
 * @author Martin Absmeier
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClassTypeTest {

    private static final String OAK_TREE = "java.structure.OakTree";
    private JavaApplication application;

    @BeforeAll
    void beforeAll() {
        String path = USER_DIR.concat(separator)
            .concat("src").concat(separator)
            .concat("test").concat(separator)
            .concat("resources").concat(separator)
            .concat("java").concat(separator)
            .concat("structure").concat(separator);

        JavaSourceParser parser = SourceParserFactory.createJavaSourceParser(DEFAULT.REVISION_ID, null, null);
        parser.parseDirectory(new File(path));

        application = (JavaApplication) parser.getApplication();
    }

    @Disabled
    void createClassTypeNull() {
        NullPointerException actual = assertThrows(NullPointerException.class, () -> new ClassType(null));
        assertEquals("Parameter 'relatedComponent' must be not NULL.", actual.getMessage());
    }

    @Disabled
    void getName() {
        ClassType actual = new ClassType(application.findComponentByUniqueCoordinate(OAK_TREE));
        assertEquals("Class(java.structure.OakTree)", actual.getName());
    }

    @Disabled
    void getUniqueIdentifier() {
        ClassType actual = new ClassType(application.findComponentByUniqueCoordinate(OAK_TREE));
        assertEquals("Class(java.structure.OakTree)", actual.getUniqueIdentifier());
    }

    @Disabled
    void getRelatedComponent() {
        Component expected = application.findComponentByUniqueCoordinate(OAK_TREE);
        ClassType actual = new ClassType(application.findComponentByUniqueCoordinate(OAK_TREE));
        assertEquals(expected, actual.getRelatedComponent());
    }

    @Disabled
    void canUpcastNull() {
        ClassType actual = new ClassType(application.findComponentByUniqueCoordinate(OAK_TREE));
        assertFalse(actual.canUpcast(null));
    }
}