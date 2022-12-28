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
import de.am.analyze.parser.common.ApplicationBase;
import de.am.analyze.parser.common.ListenerBase;
import de.am.analyze.parser.common.SourceParserBase;
import de.am.analyze.parser.common.SourceParserResult;
import de.am.analyze.parser.common.SyntaxErrorListener;
import de.am.analyze.parser.generated.java.JavaLexer;
import de.am.analyze.parser.generated.java.JavaParser;
import de.am.analyze.parser.java.listener.JavaParamListener;
import de.am.analyze.parser.java.listener.JavaStructureListener;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static de.am.analyze.common.AnalyzeConstants.SEPARATOR;
import static java.util.Objects.requireNonNull;

/**
 * @author Martin Absmeier
 */
@Log4j2
public class JavaSourceParser extends SourceParserBase {

    /**
     * Creates a new instance of {@code JavaSourceParser} with the specified {@code libraries} class.
     *
     * @param revisionId revisionId the unique id of the source code
     * @param listeners  the listeners executed by the parser
     * @param libraries  the libraries to be initialized before parsing
     */
    @Builder
    public JavaSourceParser(String revisionId, List<ListenerBase> listeners, List<Component> libraries) {
        super(JavaApplication.getInstance(), revisionId, SourceType.JAVA, listeners, libraries);
    }

    @Override
    public void parseFiles(List<File> files) {
        requireNonNull(files, "Parameter 'files' must not be NULL.");

        log.info("Start parsing [{}] files.", files.size());
        log.info(SEPARATOR);

        List<SourceParserResult> parserResults = executeParser(files);

        if (!listeners.isEmpty()) {
            listeners.forEach(listener -> executeListener(parserResults, listener));
        }
    }

    @Override
    public void initLibraries() {
        List<Component> libs = getLibraries();
        if (!libs.isEmpty()) {
            libs.forEach(application::addLibrary);
        }
    }

    @Override
    public void initListeners(String revisionId) {
        addListener(new JavaStructureListener(revisionId));
        addListener(new JavaParamListener(revisionId));
    }

    @Override
    public SourceParserResult tryPredictionMode(File file, PredictionMode mode) throws IOException {
        String fileName = cleanupFileName(file.getAbsolutePath());

        JavaParser parser = buildParser(file, mode);
        SourceParserResult parserResult = SourceParserResult.builder()
            .parseTree(parser.compilationUnit())
            .sourceName(fileName)
            .build();

        log.info("Executed [{} with mode {} on file {} of {}] -> {}", this.getClass().getSimpleName(), mode, countFiles, numberOfFiles, fileName);

        return parserResult;
    }

    // #################################################################################################################
    // Private methods

    private JavaParser buildParser(File file, PredictionMode mode) throws IOException {
        JavaLexer lexer = new JavaLexer(CharStreams.fromFileName(file.getAbsolutePath()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new SyntaxErrorListener());

        JavaParser parser = new JavaParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());
        parser.getInterpreter().setPredictionMode(mode);

        return parser;
    }
}