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

import static de.am.analyze.parser.common.type.Type.INT;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals("Primitive(int)", actual.getCoordinate());
    }
}