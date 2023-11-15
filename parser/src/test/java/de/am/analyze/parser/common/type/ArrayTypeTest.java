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

import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.DOUBLE;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.INT;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.LONG;
import static de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum.SHORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases of {@link ArrayType} class.
 *
 * @author Martin Absmeier
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArrayTypeTest {

    @Test
    void getNameDimensionOne() {
        ArrayType actual = new ArrayType(new PrimitiveType(LONG), 1);
        assertEquals("Array(Primitive(long), [])", actual.getName());
    }

    @Test
    void getNameDimensionThree() {
        ArrayType actual = new ArrayType(new PrimitiveType(SHORT), 3);
        assertEquals("Array(Primitive(short), [][][])", actual.getName());
    }

    @Test
    void getUniqueIdentifier() {
        ArrayType actual = new ArrayType(new PrimitiveType(DOUBLE), 2);
        assertEquals("Array(Primitive(double), [][])", actual.getUniqueIdentifier());
    }

    @Test
    void canUpcastNull() {
        ArrayType actual = new ArrayType(new PrimitiveType(INT), 2);
        assertFalse(actual.canUpcast(null));
    }

    @Test
    void canUpcastBaseTypeNotEqual() {
        ArrayType actual = new ArrayType(new PrimitiveType(INT), 2);
        assertFalse(actual.canUpcast(new PrimitiveType(INT)));
    }

    @Test
    void canUpcastDimensionNotEqual() {
        ArrayType arrayOne = new ArrayType(new PrimitiveType(INT), 2);
        ArrayType arrayTwo = new ArrayType(new PrimitiveType(INT), 1);
        assertFalse(arrayOne.canUpcast(arrayTwo));
    }

    @Test
    void canUpcastIntToLong() {
        ArrayType intArray = new ArrayType(new PrimitiveType(INT), 2);
        ArrayType longArray = new ArrayType(new PrimitiveType(LONG), 2);
        assertTrue(intArray.canUpcast(longArray));
    }

    @Test
    void canUpcastLongToInt() {
        ArrayType longArray = new ArrayType(new PrimitiveType(LONG), 2);
        ArrayType intArray = new ArrayType(new PrimitiveType(INT), 2);
        assertFalse(longArray.canUpcast(intArray));
    }
}