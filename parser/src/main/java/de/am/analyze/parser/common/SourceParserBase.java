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
package de.am.analyze.parser.common;

import de.am.analyze.common.component.Component;
import de.am.analyze.common.exception.ParserException;
import de.am.analyze.parser.SourceParser;
import de.am.analyze.parser.SourceType;
import de.am.analyze.parser.common.listener.ListenerBase;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.am.analyze.common.AnalyzeConstants.COMMON.SEPARATOR;
import static de.am.analyze.common.AnalyzeConstants.COMMON.USER_DIR;
import static de.am.analyze.common.AnalyzeConstants.COMMON.USER_HOME_DIR;
import static de.am.analyze.common.util.FileUtils.findFilesByExtension;
import static de.am.analyze.parser.SourceType.JAVA;
import static de.am.analyze.parser.SourceType.SCALA;
import static java.io.File.separator;
import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * {@code SourceParserBase} is the base class of all source code parsers and provides the common methods.
 *
 * @author Martin Absmeier
 */
@Log4j2
public abstract class SourceParserBase implements SourceParser {

    @Getter
    protected final ApplicationBase application;
    @Getter
    protected final SourceType sourceType;
    protected final ParseTreeWalker treeWalker;
    protected final StopWatch sw;
    protected Integer numberOfFiles;
    protected Integer countFiles;
    @Getter
    protected List<ListenerBase> listeners;
    @Getter
    protected List<Component> libraries;

    /**
     * Creates a new instance of {@code SourceParserBase}.
     *
     * @param application the underlying application (e.g. JavaApplication).
     * @param revisionId  revisionId the unique id of the source code
     * @param sourceType  source code type for which the parser is responsible
     * @param listeners   the listeners executed by the parser
     * @param libraries   the libraries to be initialized before parsing
     */
    protected SourceParserBase(ApplicationBase application, String revisionId, SourceType sourceType, List<ListenerBase> listeners, List<Component> libraries) {
        requireNonNull(application, "Parameter 'application' must be not NULL.");
        requireNonNull(revisionId, "Parameter 'revisionId' must be not NULL.");
        requireNonNull(sourceType, "Parameter 'sourceType' must be not NULL.");

        this.application = application;
        this.sourceType = sourceType;
        this.treeWalker = ParseTreeWalker.DEFAULT;
        this.sw = new StopWatch("Source parsing");
        this.listeners = new ArrayList<>();
        this.libraries = new ArrayList<>();

        // First initialize standard listener and libraries
        initListeners(revisionId);
        initLibraries();
        if (nonNull(listeners) && !listeners.isEmpty()) {
            for (ListenerBase listener : listeners) {
                if (!this.listeners.contains(listener)) {
                    this.listeners.add(listener);
                }
            }
        }
        if (nonNull(libraries) && !libraries.isEmpty()) {
            this.libraries.addAll(libraries);
        }
    }

    // #################################################################################################################

    @Override
    public void parseDirectory(File directory) {
        requireNonNull(directory, "Parameter 'directory' must be not NULL.");

        List<String> extensions = findExtensionsBySourceType(sourceType);
        extensions.forEach(extension -> {
            List<File> filesToParse = findFilesByExtension(directory, extension);
            if (!filesToParse.isEmpty()) {
                log.info("Reading files with extension [".concat(extension).concat("] from directory -> ").concat(directory.getAbsolutePath()));
                log.info(SEPARATOR);
                parseFiles(filesToParse);
            } else {
                log.info("No files found for extension [".concat(extension).concat("] in directory -> ").concat(directory.getAbsolutePath()));
                log.info(SEPARATOR);
            }
        });
    }

    @Override
    public void addListener(ListenerBase listener) {
        requireNonNull(listener, "Parameter 'listener' must be not NULL.");
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void clearListeners() {
        this.listeners.clear();
    }

    @Override
    public void addLibrary(Component library) {
        requireNonNull(library, "Parameter 'library' must be not NULL.");
        if (!this.libraries.contains(library)) {
            this.libraries.add(library);
        }
    }

    @Override
    public void clearLibraries() {
        this.libraries.clear();
    }

    // #################################################################################################################

    /**
     * Parses the source code and returns the {@link SourceParserResult}.
     *
     * @param file the source code to be parsed
     * @return the parser result or NULL if the file can not be parsed
     */
    @Synchronized
    protected SourceParserResult parseFile(File file) {
        try {
            return tryPredictionMode(file, PredictionMode.LL);
        } catch (IOException e) {
            try {
                return tryPredictionMode(file, PredictionMode.LL_EXACT_AMBIG_DETECTION);
            } catch (IOException ex) {
                String fileName = cleanupFileName(file.getAbsolutePath());
                log.error(format("Can not parse file [{0}] due to: ", fileName), ex);
            }
        }

        return null;
    }

    /**
     * Executes the parser for all specified {@code files}.
     *
     * @param files the files to be parsed
     * @return the parsing results
     */
    @Synchronized
    protected List<SourceParserResult> executeParser(List<File> files) {
        countFiles = 1;
        numberOfFiles = files.size();

        if (!sw.isRunning()) {
            sw.start("Parse summery");
        }

        List<SourceParserResult> parserResults = new ArrayList<>(numberOfFiles);
        files.stream()
                .map(this::parseFile)
                .filter(Objects::nonNull)
                .forEach(parserResult -> {
                    countFiles++;
                    parserResults.add(parserResult);
                });

        sw.stop();
        log.info(SEPARATOR);
        log.info("{} processed {} files.", this.getClass().getSimpleName(), files.size());
        log.info(SEPARATOR);

        return parserResults;
    }

    /**
     * Execute the specified {@code listener} on all specified {@code parserResults}.
     *
     * @param parserResults the parser results
     * @param listener      the listener to be executed
     */
    @Synchronized
    protected void executeListener(List<SourceParserResult> parserResults, ListenerBase listener) {
        countFiles = 1;
        numberOfFiles = parserResults.size();

        String listenerName = listener.getClass().getSimpleName();
        log.info("Execute listener {} on {} files.", listenerName, numberOfFiles);
        log.info(SEPARATOR);

        if (!sw.isRunning()) {
            sw.start(listenerName);
        }

        parserResults.forEach(parserResult -> {
            listener.setSourceName(parserResult.getSourceName());
            treeWalker.walk(listener, parserResult.getParseTree());
            application.mergeWithApplication(listener.getResult());
            listener.reset();

            log.info("Executed [{} on file {} of {}] -> {}", listenerName, countFiles, numberOfFiles, parserResult.getSourceName());
            countFiles++;
        });

        sw.stop();
        log.info(SEPARATOR);
        log.info("{} processed {} files.", listenerName, numberOfFiles);
        log.info(SEPARATOR);
    }

    // #################################################################################################################
    private List<String> findExtensionsBySourceType(SourceType type) {
        if (JAVA.equals(type)) {
            return List.of("java");
        } else if (SCALA.equals(type)) {
            return List.of("scala");
        }
        throw new ParserException("Can not find extension for type: " + type.name());
    }

    @Synchronized
    protected String cleanupFileName(String fileName) {
        if (fileName.contains(USER_DIR)) {
            fileName = fileName.replace(USER_DIR, "");
        }
        if (fileName.contains(USER_HOME_DIR)) {
            fileName = fileName.replace(USER_HOME_DIR, "");
        }

        String resPath = format("{0}src{1}main{2}resources{3}", separator, separator, separator, separator);
        if (fileName.contains(resPath)) {
            fileName = fileName.replace(resPath, "");
        }
        resPath = format("{0}src{1}test{2}resources{3}", separator, separator, separator, separator);
        if (fileName.contains(resPath)) {
            fileName = fileName.replace(resPath, "");
        }

        return fileName;
    }
}