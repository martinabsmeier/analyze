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
import de.am.analyze.common.component.type.ComponentType;
import de.am.analyze.parser.SourceParserFactory;
import de.am.analyze.parser.java.listener.JavaStructureListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.File;
import java.util.List;

import static de.am.analyze.common.AnalyzeConstants.DEFAULT_REVISION_ID;
import static de.am.analyze.common.AnalyzeConstants.USER_DIR;
import static de.am.analyze.common.component.type.ComponentType.JAVA_CLASS;
import static de.am.analyze.common.component.type.ComponentType.JAVA_CONSTRUCTOR;
import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit test cases of {@link JavaStructureListener} class.
 *
 * @author Martin Absmeier
 */
@TestInstance(Lifecycle.PER_CLASS)
class JavaStructureListenerTest {

    private JavaApplication application;

    @BeforeAll
    void beforeAll() {
        String path = USER_DIR.concat(separator)
            .concat("src").concat(separator)
            .concat("test").concat(separator)
            .concat("resources").concat(separator)
            .concat("java").concat(separator)
            .concat("structure").concat(separator);

        JavaSourceParser parser = SourceParserFactory.createJavaSourceParser(DEFAULT_REVISION_ID, null, null);
        parser.parseDirectory(new File(path));

        application = (JavaApplication) parser.getApplication();
    }

    @Test
    void checkNumberOfClasses() {
        List<Component> classes = application.findAllComponentsByType(JAVA_CLASS);
        assertEquals(4, classes.size(), "We expect three classes.");
    }

    @Test
    void checkNumberOfConstructors() {
        List<Component> constructors = application.findAllComponentsByType(JAVA_CONSTRUCTOR);
        assertEquals(4, constructors.size(), "We expect three constructors.");
    }

    @Test
    void checkFields() {
        Component oakTree = application.findComponentByUniqueCoordinate("java.structure.OakTree");
        List<Component> fields = oakTree.findChildrenByType(ComponentType.JAVA_FIELD);
        assertEquals(2, fields.size());
    }
}