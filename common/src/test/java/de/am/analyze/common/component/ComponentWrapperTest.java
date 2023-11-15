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

import de.am.analyze.common.component.ComponentWrapper.ComponentWrapperBuilder;
import de.am.analyze.common.component.type.ComponentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases of {@link ComponentWrapper} class.
 *
 * @author Martin Absmeier
 */
class ComponentWrapperTest {

    @Test
    void constructor_ComponentNull() {
        ComponentWrapperBuilder builder = ComponentWrapper.builder().component(null);
        NullPointerException actual = assertThrows(NullPointerException.class, builder::build);

        assertEquals("Parameter 'component' must not be NULL.", actual.getMessage());
    }

    @Test
    void equals_IsSameObject() {
        Component component = Component.builder().type(ComponentType.JAVA_CLASS).value("FooClass").build();
        ComponentWrapper actual = ComponentWrapper.builder().component(component).build();

        assertTrue(actual.equals(actual));
    }

    @Test
    void equals_OtherIsNull() {
        Component component = Component.builder().type(ComponentType.JAVA_CLASS).value("FooClass").build();
        ComponentWrapper actual = ComponentWrapper.builder().component(component).build();

        assertFalse(actual.equals(null));
    }

    @Test
    void equals_OtherIsAnotherClass() {
        Component component = Component.builder().type(ComponentType.JAVA_CLASS).value("FooClass").build();
        ComponentWrapper actual = ComponentWrapper.builder().component(component).build();

        assertFalse(actual.equals("someString"));
    }

    @Test
    void equals() {
        Component component = Component.builder().type(ComponentType.JAVA_CLASS).value("FooClass").build();
        ComponentWrapper wrapper1 = ComponentWrapper.builder().component(component).build();
        ComponentWrapper wrapper2 = ComponentWrapper.builder().component(component).build();

        assertTrue(wrapper1.equals(wrapper2));
    }

    @Test
    void testHashCode() {
        Component component = Component.builder().type(ComponentType.JAVA_CLASS).value("FooClass").build();
        ComponentWrapper actual = ComponentWrapper.builder().component(component).build();

        assertEquals(414358673, actual.hashCode());
    }

    @Test
    void testToString() {
        Component component = Component.builder().type(ComponentType.JAVA_CLASS).value("FooClass").build();
        ComponentWrapper actual = ComponentWrapper.builder().component(component).build();

        assertEquals("FooClass", actual.toString());
    }
}