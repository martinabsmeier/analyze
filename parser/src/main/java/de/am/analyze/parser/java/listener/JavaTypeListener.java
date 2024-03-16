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

import de.am.analyze.common.component.Component;
import de.am.analyze.parser.common.type.AbstractType;
import de.am.analyze.generated.parser.java.JavaParser.*;
import de.am.analyze.parser.java.JavaParsingContext;
import de.am.analyze.parser.java.visitor.JavaTypeVisitor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * {@code JavaTypeListener} adds the variables, parameters and constants and there types to a compilation unit.
 *
 * @author Martin Absmeier
 */
@Log4j2
public class JavaTypeListener extends JavaBaseListener {

    private final JavaTypeVisitor typeVisitor;

    /**
     * Creates a new instance of {@code JavaListenerBase} class.
     *
     * @param revisionId revisionId the unique id of the source code
     */
    public JavaTypeListener(String revisionId) {
        super(revisionId, JavaParsingContext.builder().revisionId(revisionId).build());
        typeVisitor = new JavaTypeVisitor(parsingContext);
    }

    // #################################################################################################################
    
    @Override
    public void enterTypeList(TypeListContext ctx) {
        List<TypeTypeContext> typeList = ctx.typeType();

        super.enterTypeList(ctx);
    }

    @Override
    public void enterTypeType(TypeTypeContext ctx) {
        Component currentComponent = parsingContext.getCurrentComponent();
        AbstractType type = ctx.accept(typeVisitor);

        System.out.println("OK");
    }

    /*
    @Override
    public void enterFieldDeclaration(FieldDeclarationContext ctx) {
        VariableDeclaratorsContext varDeclarators = ctx.variableDeclarators();
        if (nonNull(varDeclarators)) {
            varDeclarators.variableDeclarator().forEach(varDeclarator -> {
                Component field = createComponent(JAVA_FIELD, varDeclarator.variableDeclaratorId().identifier().getText());

                addAndClearCollectedModifiers(field);
                addToCurrentComponentIfNotContained(field);

                parsingContext.setCurrentComponent(field);
            });
        }
    }

    @Override
    public void exitFieldDeclaration(FieldDeclarationContext ctx) {
        setParentIfAvailable();
    }
     */
}