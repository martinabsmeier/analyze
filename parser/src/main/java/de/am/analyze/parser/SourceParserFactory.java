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
import de.am.analyze.common.exception.ParserException;
import de.am.analyze.parser.common.ListenerBase;
import de.am.analyze.parser.java.JavaSourceParser;

import java.util.List;

/**
 * @author Martin Absmeier
 */
public class SourceParserFactory {

    public static SourceParser createParser(SourceType sourceType, List<ListenerBase> listeners, List<Component> libraries) {
        SourceParser parser = findParserByType(sourceType);
        parser.setListeners(listeners);
        parser.setLibraries(libraries);

        return parser;
    }

    // #################################################################################################################
    private static SourceParser findParserByType(SourceType sourceType) {
        switch (sourceType) {
            case JAVA:
                return JavaSourceParser.builder().build();

            default:
                throw new ParserException("Can not find parser for source type: " + sourceType.name());
        }
    }
}