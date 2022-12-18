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
package de.am.analyze.parser.java;

import de.am.analyze.common.component.Component;
import de.am.analyze.parser.SourceType;
import de.am.analyze.parser.common.SourceParserBase;
import de.am.analyze.parser.common.SourceParserResult;
import lombok.Builder;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static de.am.analyze.common.AnalyzeConstants.SEPARATOR;
import static java.util.Objects.requireNonNull;

/**
 * @author Martin Absmeier
 */
public class JavaSourceParser extends SourceParserBase {
    private static final Logger LOGGER = LogManager.getLogger(JavaSourceParser.class);

    /**
     * Creates a new instance of {@code JavaSourceParser} with the specified {@code libraries} class.
     *
     * @param libraries the libraries to be initialized before parsing
     */
    @Builder
    public JavaSourceParser(List<Component> libraries) {
        super(JavaApplication.getInstance(), SourceType.JAVA);
        initLibraries(libraries);
    }

    @Override
    public void parseFiles(List<File> files) {
        requireNonNull(files, "NULL is not permitted as a value for the 'files' parameter.");

        LOGGER.info("Start parsing [" + files.size() + "] files.");
        LOGGER.info(SEPARATOR);

        List<SourceParserResult> parserResults = executeParser(files);
        if (listeners.isEmpty()) {
            listeners.forEach(listener -> executeListener(parserResults, listener));
        } else {
            listeners.forEach(listener -> executeListener(parserResults, listener));
        }
    }

    @Override
    public void initLibraries(List<Component> libraries) {
        if (!libraries.isEmpty()) {
            libraries.forEach(application::addLibrary);
        }
    }

    @Override
    public void initListeners() {
        // addListener(new JavaDeclarationListener(revisionId));
    }

    @Override
    public SourceParserResult tryPredictionMode(File file, PredictionMode mode) throws IOException {
        return null;
    }
}