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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.BOOLEAN;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.CHAR;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.DOUBLE;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.FLOAT;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.INT;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.LONG;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.SHORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases of {@link PrimitiveType} class.
 *
 * @author Martin Absmeier
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrimitiveTypeTest {

    @Test
    void getUniqueIdentifier() {
        PrimitiveType actual = new PrimitiveType(INT);
        assertEquals("Primitive(integer)", actual.getUniqueIdentifier());
    }

    @Test
    void canUpcastNull() {
        PrimitiveType actual = new PrimitiveType(INT);
        assertFalse(actual.canUpcast(null));
    }

    @Test
    void canUpcastBaseTypeNotPrimitive() {
        PrimitiveType actual = new PrimitiveType(INT);
        assertFalse(actual.canUpcast(new NullType()));
    }

    @Test
    void canUpcastBoolean() {
        PrimitiveType actual = new PrimitiveType(BOOLEAN);
        assertTrue(actual.canUpcast(new PrimitiveType(BOOLEAN)));
    }

    @Test
    void canUpcastCharTrue() {
        PrimitiveType actual = new PrimitiveType(CHAR);
        assertTrue(actual.canUpcast(new PrimitiveType(LONG)));
    }

    @Test
    void canUpcastCharFalse() {
        PrimitiveType actual = new PrimitiveType(CHAR);
        assertFalse(actual.canUpcast(new PrimitiveType(BOOLEAN)));
    }

    @Test
    void canUpcastShortTrue() {
        PrimitiveType actual = new PrimitiveType(SHORT);
        assertTrue(actual.canUpcast(new PrimitiveType(LONG)));
    }

    @Test
    void canUpcastShortFalse() {
        PrimitiveType actual = new PrimitiveType(SHORT);
        assertFalse(actual.canUpcast(new PrimitiveType(BOOLEAN)));
    }

    @Test
    void canUpcastIntTrue() {
        PrimitiveType actual = new PrimitiveType(INT);
        assertTrue(actual.canUpcast(new PrimitiveType(LONG)));
    }

    @Test
    void canUpcastIntFalse() {
        PrimitiveType actual = new PrimitiveType(INT);
        assertFalse(actual.canUpcast(new PrimitiveType(BOOLEAN)));
    }

    @Test
    void canUpcastLongTrue() {
        PrimitiveType actual = new PrimitiveType(LONG);
        assertTrue(actual.canUpcast(new PrimitiveType(FLOAT)));
    }

    @Test
    void canUpcastLongFalse() {
        PrimitiveType actual = new PrimitiveType(LONG);
        assertFalse(actual.canUpcast(new PrimitiveType(BOOLEAN)));
    }

    @Test
    void canUpcastFloatTrue() {
        PrimitiveType actual = new PrimitiveType(FLOAT);
        assertTrue(actual.canUpcast(new PrimitiveType(DOUBLE)));
    }

    @Test
    void canUpcastFloatFalse() {
        PrimitiveType actual = new PrimitiveType(FLOAT);
        assertFalse(actual.canUpcast(new PrimitiveType(BOOLEAN)));
    }

    @Test
    void canUpcastDoubleTrue() {
        PrimitiveType actual = new PrimitiveType(DOUBLE);
        assertTrue(actual.canUpcast(new PrimitiveType(DOUBLE)));
    }

    @Test
    void canUpcastDoubleFalse() {
        PrimitiveType actual = new PrimitiveType(DOUBLE);
        assertFalse(actual.canUpcast(new PrimitiveType(BOOLEAN)));
    }
}