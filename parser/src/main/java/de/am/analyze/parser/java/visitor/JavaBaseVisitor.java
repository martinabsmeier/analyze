/*
 * Copyright 2023 Martin Absmeier
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
package de.am.analyze.parser.java.visitor;

import de.am.analyze.generated.parser.java.JavaParserBaseVisitor;
import de.am.analyze.parser.java.JavaApplication;
import de.am.analyze.parser.java.JavaParsingContext;

/**
 * {@code JavaBaseVisitor} is responsible for implementing the default behaviour.
 *
 * @author Martin Absmeier
 */
public class JavaBaseVisitor<T> extends JavaParserBaseVisitor<T> {

    protected JavaApplication application;
    protected JavaParsingContext parsingContext;

    /**
     * Create a new instance of {@code JavaTypeVisitor} with specified {@code parsingContext}.
     *
     * @param parsingContext the parsing context of the visitor
     */
    public JavaBaseVisitor(JavaParsingContext parsingContext) {
        this.application = JavaApplication.getInstance();
        this.parsingContext = parsingContext;

    }
}