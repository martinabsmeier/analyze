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

import de.am.analyze.common.component.ComponentAttribute.ComponentAttributeBuilder;
import org.junit.jupiter.api.Test;

import static de.am.analyze.common.component.type.ComponentAttributeType.COLUMN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases of {@link ComponentAttribute} class.
 *
 * @author Martin Absmeier
 */
class ComponentAttributeTest {

    @Test
    void noArgsConstructor() {
        ComponentAttribute actual = new ComponentAttribute();

        assertNotNull(actual);
    }

    @Test
    void constructorType_Null() {
        ComponentAttributeBuilder builder = ComponentAttribute.builder().type(null).value("value");
        NullPointerException actual = assertThrows(NullPointerException.class, builder::build);

        assertEquals("Parameter 'type' must not be NULL.", actual.getMessage());
    }

    @Test
    void constructorValue_Null() {
        ComponentAttributeBuilder builder = ComponentAttribute.builder().type(COLUMN).value(null);
        NullPointerException actual = assertThrows(NullPointerException.class, builder::build);

        assertEquals("Parameter 'value' must not be NULL.", actual.getMessage());
    }
    @Test
    void getValue() {
        ComponentAttribute actual = ComponentAttribute.builder().type(COLUMN).value("value").build();

        assertEquals("value", actual.getValue());
    }

    @Test
    void getType() {
        ComponentAttribute actual = ComponentAttribute.builder().type(COLUMN).value("value").build();

        assertEquals(COLUMN, actual.getType());
    }

    @Test
    void isType_Null() {
        ComponentAttribute attribute = ComponentAttribute.builder().type(COLUMN).value("value").build();
        NullPointerException actual = assertThrows(NullPointerException.class, () -> attribute.isType(null));

        assertEquals("Parameter 'type' must not be NULL.", actual.getMessage());
    }

    @Test
    void isType() {
        ComponentAttribute actual = ComponentAttribute.builder().type(COLUMN).value("value").build();

        assertTrue(actual.isType(COLUMN));
    }

    @Test
    void testToString() {
        ComponentAttribute actual = ComponentAttribute.builder().type(COLUMN).value("value").build();

        assertEquals("COLUMN -> value", actual.toString());
    }
}