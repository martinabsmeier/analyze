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
package de.am.analyze.parser.java.listener;

import de.am.analyze.parser.java.JavaParsingContext;
import lombok.extern.log4j.Log4j2;

/**
 * FIXME {@code JavaParamListener}
 *
 * @author Martin Absmeier
 */
@Log4j2
public class JavaParamListener extends JavaBaseListener {

    /**
     * Creates a new instance of {@code JavaListenerBase} class.
     *
     * @param revisionId revisionId the unique id of the source code
     */
    public JavaParamListener(String revisionId) {
        super(revisionId, JavaParsingContext.builder().revisionId(revisionId).build());
    }
}