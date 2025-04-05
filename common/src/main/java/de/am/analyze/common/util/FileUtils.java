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
package de.am.analyze.common.util;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * {@code FileUtils} is a convenience class to read source code files from filesystem.
 *
 * @author Martin Absmeier
 */
@Log4j2
public class FileUtils {

    /**
     * Retrieve all files from the specified {@code directory} matching the {@code extension}.
     *
     * @param directory the directory to be parsed
     * @param extension the extension of the file (e.g. 'java')
     * @return list with the found files
     * @throws IllegalArgumentException is thrown if parameter {@code directory} is not a directory
     */
    public static List<File> findFilesByExtension(File directory, String extension) throws IllegalArgumentException {
        requireNonNull(directory, "Parameter 'directory' must not be NULL.");
        requireNonNull(extension, "Parameter 'extension' must not be NULL.");

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + directory.getName());
        }

        List<File> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(directory.getPath()))) {
            files = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(extension))
                    .toList();
        } catch (IOException ex) {
            log.error("Can not find files due to: {}", ex.getMessage());
            log.error(ex);
        }

        return files;
    }

    // #################################################################################################################
    private FileUtils() {
        // We do not want an instance
    }
}