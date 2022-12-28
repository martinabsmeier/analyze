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
package de.am.analyze.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * {@code AnalyzeConstants} stores all constants of analyze application.
 *
 * @author Martin Absmeier
 */
public class AnalyzeConstants {

    // common ##########################################################################################################
    public static final String EMPTY_STRING = "";
    public static final String SEPARATOR = "--------------------------------------------------------------------------------";
    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String USER_HOME_DIR = System.getProperty("user.home");
    public static final String DEFAULT_DELIMITER = ".";
    public static final String DEFAULT_DELIMITER_REGEX = "[.]";
    public static final String DEFAULT_REVISION_ID = "revision id";

    // parser ##########################################################################################################
    public static class JAVA {
        public static final String UNIQUE_NAME_DELIMITER = ".";
        public static final String LANG_PACKAGE = "java.lang";
        public static final String IO_PACKAGE = "java.io";
        public static final String DEFAULT_PACKAGE = "default";
        public static final String MODIFIER_PUBLIC = "public";
        public static final String MODIFIER_PROTECTED = "protected";
        public static final String MODIFIER_PRIVATE = "private";
        public static final String MODIFIER_STATIC = "static";
        public static final String MODIFIER_FINAL = "final";
        public static final String MODIFIER_NATIVE = "native";

        // #################################################################################################################
        private JAVA() { }
    }

    // #################################################################################################################
    private AnalyzeConstants() { }
}