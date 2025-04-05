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
package de.am.analyze.common.exception;

import java.io.Serial;

/**
 * {@code ParseException} is an unchecked exception isn't needed to be declared in a method or constructor's throws clause.
 *
 * @author Martin Absmeier
 */
public class ParserException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6156491502289272669L;

    /**
     * Constructs a new {@code ParseException} with the specified detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to initCause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ParseException} with the specified detail message and cause.
     * Note that the detail message associated with cause is not automatically incorporated in this runtime exception's
     * detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method). (A null value is
     *                permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@code ParseException} with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     * This constructor is useful for runtime exceptions that are little more than wrappers for other throwable.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     */
    public ParserException(Throwable cause) {
        super(cause);
    }
}