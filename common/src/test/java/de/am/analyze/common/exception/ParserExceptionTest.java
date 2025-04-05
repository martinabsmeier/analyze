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
package de.am.analyze.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases of {@link ParserException} class.
 *
 * @author Martin Absmeier
 */
class ParserExceptionTest {

    private static final String MESSAGE = "TestMessage";
    private static final NullPointerException NPE = new NullPointerException();

    @Test
    void constructorMessage() {
        ParserException actual = new ParserException(MESSAGE);
        assertEquals(MESSAGE, actual.getMessage());
    }

    @Test
    void constructorMessageAndCause() {
        ParserException actual = new ParserException(MESSAGE, NPE);
        assertEquals(MESSAGE, actual.getMessage());
        assertInstanceOf(NullPointerException.class, actual.getCause());
    }

    @Test
    void constructorCause() {
        ParserException actual = new ParserException(NPE);
        assertInstanceOf(NullPointerException.class, actual.getCause());
    }
}