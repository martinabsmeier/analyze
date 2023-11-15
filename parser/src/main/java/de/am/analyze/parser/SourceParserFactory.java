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
import de.am.analyze.parser.common.listener.ListenerBase;
import de.am.analyze.parser.java.JavaSourceParser;

import java.util.List;

/**
 * @author Martin Absmeier
 */
public class SourceParserFactory {

    /**
     * Creates a new instance of {@link JavaSourceParser} class.
     *
     * @param revisionId revisionId the unique id of the source code
     * @param listeners  the listeners executed by the parser
     * @param libraries  the libraries to be initialized before parsing
     * @return the created instance
     */
    public static JavaSourceParser createJavaSourceParser(String revisionId, List<ListenerBase> listeners, List<Component> libraries) {
        return JavaSourceParser.builder().revisionId(revisionId).listeners(listeners).libraries(libraries).build();
    }

    // #################################################################################################################
    private SourceParserFactory() {
        // We do not need an instance.
    }
}