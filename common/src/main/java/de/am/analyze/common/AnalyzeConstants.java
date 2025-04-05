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

    /**
     * Contains all common constants
     */
    public static class COMMON {
        /**
         * Standard separator of lines used for logging.
         */
        public static final String SEPARATOR = "--------------------------------------------------------------------------------";

        /**
         * The directory of the user
         */
        public static final String USER_DIR = System.getProperty("user.dir");

        /**
         * The home directory of the application
         */
        public static final String HOME_DIR = System.getProperty("user.home");

        private COMMON() {
            // We do not want an instance
        }
    }

    // #################################################################################################################

    /**
     * Contains all default constants
     */
    public static class DEFAULT {
        /**
         * The default algorithm to calculate the checksum.
         */
        public static final String ALGORITHM = "SHA-512";

        /**
         * The default encoding.
         */
        public static final Charset ENCODING = StandardCharsets.UTF_8;

        /**
         * The default revision id if there is no one.
         */
        public static final String REVISION_ID = "v-0.0.1";

        private DEFAULT() {
            // We do not want an instance
        }
    }

    // #################################################################################################################

    /**
     * Contains all java constants
     */
    public static class JAVA {

        /**
         * The default delimiter of the unique coordinate.
         */
        public static final String DELIMITER = ".";

        /**
         * The regular expression to detect the delimiter.
         */
        public static final String DELIMITER_REGEX = "[.]";

        /**
         * The default package if no package is specified.
         */
        public static final String DEFAULT_PACKAGE = "default";

        /**
         * The java.lang package qualifier. (This package is always visible)
         */
        public static final String LANG_PACKAGE = "java.lang";

        /**
         * The java.io package qualifier. (This package is always visible)
         */
        public static final String IO_PACKAGE = "java.io";

        /**
         * The public modifier constant
         */
        public static final String MODIFIER_PUBLIC = "public";

        /**
         * The abstract modifier constant
         */
        public static final String MODIFIER_ABSTRACT = "abstract";

        /**
         * The protected modifier constant
         */
        public static final String MODIFIER_PROTECTED = "protected";

        /**
         * The private modifier constant
         */
        public static final String MODIFIER_PRIVATE = "private";

        /**
         * The static modifier constant
         */
        public static final String MODIFIER_STATIC = "static";

        /**
         * The final modifier constant
         */
        public static final String MODIFIER_FINAL = "final";

        /**
         * The native modifier constant
         */
        public static final String MODIFIER_NATIVE = "native";

        /**
         * The default modifier constant
         */
        public static final String MODIFIER_DEFAULT = "default";

        private JAVA() {
            // We do not want an instance
        }
    }

    // #################################################################################################################

    /**
     * Contains all scala constants
     */
    public static class SCALA {

        private SCALA() {
            // We do not want an instance
        }
    }

    // #################################################################################################################
    private AnalyzeConstants() {
        // We do not want an instance
    }
}