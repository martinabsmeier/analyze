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
package de.am.analyze.parser.common.listener;

import de.am.analyze.common.AnalyzeConstants.DEFAULT;
import de.am.analyze.common.component.Component;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * {@code ListenerBase} defines a programming language independent interface for all listeners.
 *
 * @author Martin Absmeier
 */
public interface ListenerBase extends ParseTreeListener {

    /**
     * Return the parsing result of the listener.
     *
     * @return the parsing result
     */
    Component getResult();

    /**
     * Set the name of the source code file.
     *
     * @param sourceName the name of the source code file
     */
    void setSourceName(String sourceName);

    /**
     * Resets the parsing context so that the next listener can run.
     * <b>The libraries required for parsing must be initialized here.</b>
     */
    void reset();

    /**
     * Calculate a checksum for the specified {@code sourceCode} parameter.
     * Uses the {@link DEFAULT#ALGORITHM} to calculate the checksum.
     *
     * @param sourceCode the source code
     * @return the {@link DEFAULT#ALGORITHM} checksum
     */
    default String calculateChecksum(String sourceCode) {
        try {
            byte[] checksum = MessageDigest.getInstance(DEFAULT.ALGORITHM).digest(sourceCode.getBytes(DEFAULT.ENCODING));
            return String.format("%032x", new BigInteger(1, checksum));
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }
}