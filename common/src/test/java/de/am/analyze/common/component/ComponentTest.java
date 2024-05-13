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
package de.am.analyze.common.component;

import de.am.analyze.common.component.Component.ComponentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.am.analyze.common.component.type.ComponentAttributeType.*;
import static de.am.analyze.common.component.type.ComponentType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases of {@link Component} class.
 *
 * @author Martin Absmeier
 */
class ComponentTest {

    private Component component;

    @BeforeEach
    void beforeEach() {
        component = Component.builder().type(JAVA_CLASS).value("Clazz").build();
    }

    // #################################################################################################################

    @Test
    void constructor_TypeNull() {
        ComponentBuilder builder = Component.builder().type(null).value("value");
        NullPointerException npe = assertThrows(NullPointerException.class, builder::build);

        assertEquals("Parameter 'type' must not be NULL.", npe.getMessage());
    }

    @Test
    void constructor_ValueNull() {
        ComponentBuilder builder = Component.builder().type(JAVA_CLASS).value(null);
        NullPointerException npe = assertThrows(NullPointerException.class, builder::build);

        assertEquals("Parameter 'value' must not be NULL.", npe.getMessage());
    }


    @Test
    void addChild() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> component.addChild(null));
        assertEquals("Parameter 'child' must not be NULL.", npe.getMessage());

        component.addChild(Component.builder().type(JAVA_METHOD).value("doFoo").build());
        assertTrue(component.hasChildren());
    }

    @Test
    void findChildrenByValue() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> component.findChildrenByValue(null));
        assertEquals("Parameter 'value' must not be NULL.", npe.getMessage());

        component.addChild(Component.builder().type(JAVA_METHOD).value("doFoo").build());
        assertTrue(component.hasChildren());
        List<Component> children = component.findChildrenByValue("doFoo");
        assertEquals(1, children.size());
    }

    @Test
    void findChildrenByType() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> component.findChildrenByType(null));
        assertEquals("Parameter 'type' must not be NULL.", npe.getMessage());

        component.addChild(Component.builder().type(JAVA_METHOD).value("doFoo").build());
        assertTrue(component.hasChildren());
        List<Component> children = component.findChildrenByType(JAVA_METHOD);
        assertEquals(1, children.size());
    }

    @Test
    void findChildByComponent() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> component.findChildByComponent(null));
        assertEquals("Parameter 'component' must not be NULL.", npe.getMessage());

        component.addChild(Component.builder().type(JAVA_METHOD).value("doFoo").build());
        assertTrue(component.hasChildren());
        Component child = component.findChildByComponent(Component.builder().type(JAVA_METHOD).value("doFoo").build());
        assertNotNull(child);
    }

    @Test
    void childrenNotContains() {
        assertTrue(component.childrenNotContains(null));

        component.addChild(Component.builder().type(JAVA_METHOD).value("doFoo").build());
        assertFalse(component.childrenNotContains(Component.builder().type(JAVA_METHOD).value("doFoo").build()));

        component.addChild(Component.builder().type(JAVA_METHOD).value("doFoo").build());
        assertTrue(component.childrenNotContains(Component.builder().type(JAVA_METHOD).value("doBar").build()));
    }

    @Test
    void addAttribute() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> component.addAttribute(null));
        assertEquals("Parameter 'attribute' must not be NULL.", npe.getMessage());

        component.addAttribute(ComponentAttribute.builder().type(JAVA_PACKAGE_NAME).value("de.am.analyze").build());
        assertTrue(component.hasAttributes());
    }

    @Test
    void findAttributesByType() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> component.findAttributesByType(null));
        assertEquals("Parameter 'type' must not be NULL.", npe.getMessage());

        component.addAttribute(ComponentAttribute.builder().type(JAVA_PACKAGE_NAME).value("de.am.analyze").build());
        assertTrue(component.hasAttributes());
        List<ComponentAttribute> attributes = component.findAttributesByType(JAVA_PACKAGE_NAME);
        assertFalse(attributes.isEmpty());
    }

    @Test
    void findAttributesByTypeAndValue() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> component.findAttributesByTypeAndValue(null, "value"));
        assertEquals("Parameter 'type' must not be NULL.", npe.getMessage());

        npe = assertThrows(NullPointerException.class, () -> component.findAttributesByTypeAndValue(JAVA_PACKAGE_NAME, null));
        assertEquals("Parameter 'value' must not be NULL.", npe.getMessage());

        component.addAttribute(ComponentAttribute.builder().type(JAVA_PACKAGE_NAME).value("de.am.analyze").build());
        assertTrue(component.hasAttributes());
        List<ComponentAttribute> attributes = component.findAttributesByTypeAndValue(JAVA_PACKAGE_NAME, "de.am.analyze");
        assertFalse(attributes.isEmpty());
    }


    @Test
    void findParentByType_BranchOne() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> component.findFirstParentByType(null));
        assertEquals("Parameter 'type' must not be NULL.", npe.getMessage());

        Component aMethod = Component.builder().type(JAVA_METHOD).value("method").build();
        Component aClass = Component.builder().type(JAVA_CLASS).value("clazz").build();
        aClass.addChild(aMethod);
        Component aInterface = Component.builder().type(JAVA_INTERFACE).value("interface").build();
        aInterface.addChild(aClass);
        component.addChild(aInterface);

        Component parent = aMethod.findFirstParentByType(JAVA_CLASS);
        assertNotNull(parent);

        parent = aMethod.findFirstParentByType(JAVA_INTERFACE);
        assertNotNull(parent);

        parent = aMethod.findFirstParentByType(JAVA_ENUM);
        assertNull(parent);
    }

    @Test
    void getParents() {
        Component aMethod = Component.builder().type(JAVA_METHOD).value("method").build();
        Component aClass = Component.builder().type(JAVA_CLASS).value("clazz").build();
        aClass.addChild(aMethod);
        Component aInterface = Component.builder().type(JAVA_INTERFACE).value("interface").build();
        aInterface.addChild(aClass);
        component.addChild(aInterface);

        List<Component> parents = aMethod.getParents();
        assertEquals(3, parents.size());
    }

    @Test
    void getCoordinate() {
        Component method = Component.builder().type(JAVA_METHOD).value("getUniqueCoordinate").build();
        method.addAttribute(ComponentAttribute.builder().type(JAVA_RETURN_TYPE).value("String").build());
        method.addChild(Component.builder().type(JAVA_PARAMETER).value("").build());
        Component parameter = Component.builder().type(JAVA_PARAMETER).value("arg0").build();
        parameter.addAttribute(ComponentAttribute.builder().type(JAVA_TYPE).value("Integer").build());
        method.addChild(parameter);

        Component clazz = Component.builder().type(JAVA_CLASS).value("Component").build();
        clazz.addChild(method);

        Component pck4 = createJavaPackage("common");
        pck4.addChild(clazz);
        Component pck3 = createJavaPackage("analyze");
        pck3.addChild(pck4);
        Component pck2 = createJavaPackage("am");
        pck2.addChild(pck3);
        Component pck1 = createJavaPackage("de");
        pck1.addChild(pck2);

        System.out.println(method.getUniqueCoordinate());

        assertEquals("de.am.analyze.common.Component.getUniqueCoordinate", method.getUniqueCoordinate());
    }

    // #################################################################################################################
    private Component createJavaPackage(String value) {
        return Component.builder().type(JAVA_PACKAGE).value(value).build();
    }
}