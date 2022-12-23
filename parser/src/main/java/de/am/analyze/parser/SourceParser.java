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
package de.am.analyze.parser;


import de.am.analyze.common.component.Component;
import de.am.analyze.parser.common.ListenerBase;
import de.am.analyze.parser.common.SourceParserResult;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface of all source code parsers.
 *
 * @author Martin Absmeier
 */
public interface SourceParser {

    /**
     * Parses the files in the specified {@code directory}.
     *
     * @param directory the directory to be parsed
     */
    void parseDirectory(File directory);

    /**
     * Parses the files specified by {@code files}.
     *
     * @param files the files to be parsed
     */
    void parseFiles(List<File> files);

    /**
     * Add the specified {@code listener} if the list does not contain the listener already.
     *
     * @param listener the listener to be added
     */
    void addListener(ListenerBase listener);

    /**
     * Removes all listeners.
     */
    void clearListeners();

    /**
     * Add the specified {@code library} if the list does not contain the library already.
     *
     * @param library the library to be added
     */
    void addLibrary(Component library);

    /**
     * Removes all libraries.
     */
    void clearLibraries();

    /**
     * Parses the specified source code file {@code file} with the prediction mode {@code mode}.
     *
     * @param file the file to be parsed
     * @param mode the prediction mode to be used
     * @return the result of the parser
     * @throws IOException if the source code file can not be accessed
     */
    SourceParserResult tryPredictionMode(File file, PredictionMode mode) throws IOException;

    /**
     * Initializes the parser with standard libraries of the respective programming language,
     */
    void initLibraries();

    /**
     * Initializes the parser with standard listeners that are always executed.
     *
     * @param revisionId revisionId the unique id of the source code
     */
    void initListeners(String revisionId);
}