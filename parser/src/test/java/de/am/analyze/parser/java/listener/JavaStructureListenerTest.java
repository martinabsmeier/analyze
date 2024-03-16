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
package de.am.analyze.parser.java.listener;

import de.am.analyze.common.component.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.am.analyze.common.component.type.ComponentType.JAVA_CLASS;
import static de.am.analyze.common.component.type.ComponentType.JAVA_CONSTRUCTOR;
import static de.am.analyze.common.component.type.ComponentType.JAVA_ENUM;
import static de.am.analyze.common.component.type.ComponentType.JAVA_ENUM_CONSTANT;
import static de.am.analyze.common.component.type.ComponentType.JAVA_INTERFACE;
import static de.am.analyze.common.component.type.ComponentType.JAVA_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit test cases of {@link JavaStructureListener} class.
 *
 * @author Martin Absmeier
 */
class JavaStructureListenerTest extends AbstractListenerTest {

    @Test
    void checkNumberOfClasses() {
        List<Component> classes = application.findAllComponentsByType(JAVA_CLASS);
        assertEquals(7, classes.size(), "We expect seven classes.");
    }

    @Test
    void checkNumberOfInterfaces() {
        List<Component> interfaces = application.findAllComponentsByType(JAVA_INTERFACE);
        assertEquals(1, interfaces.size(), "We expect one interface.");
    }

    @Test
    void checkNumberOfInterfaceMethods() {
        Component treeInterface = application.findComponentByUniqueCoordinate("java.structure.Tree");
        List<Component> treeInterfaceMethods = treeInterface.findChildrenByType(JAVA_METHOD);
        assertEquals(6, treeInterfaceMethods.size(), "We expect six methods.");
    }

    @Test
    void checkNumberOfClassMethods() {
        Component oakTreeClass = application.findComponentByUniqueCoordinate("java.structure.OakTree");
        List<Component> oakTreeClassMethods = oakTreeClass.findChildrenByType(JAVA_METHOD);
        assertEquals(5, oakTreeClassMethods.size(), "We expect five methods.");
    }

    @Test
    void checkNumberOfEnumerations() {
        List<Component> enumerations = application.findAllComponentsByType(JAVA_ENUM);
        assertEquals(1, enumerations.size(), "We expect one enumeration.");
    }

    @Test
    void checkNumberOfEnumConstants() {
        Component enumeration = application.findAllComponentsByType(JAVA_ENUM).get(0);
        List<Component> enumConstants = enumeration.findChildrenByType(JAVA_ENUM_CONSTANT);
        assertEquals(5, enumConstants.size(), "We expect five enum constants.");
    }

    @Test
    void checkNumberOfConstructors() {
        List<Component> constructors = application.findAllComponentsByType(JAVA_CONSTRUCTOR);
        assertEquals(7, constructors.size(), "We expect four constructors.");
    }
}