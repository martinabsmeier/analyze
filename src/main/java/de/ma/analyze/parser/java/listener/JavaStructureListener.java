/*
 * Copyright 2025 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ma.analyze.parser.java.listener;

import de.ma.analyze.common.AnalyzeConstants.JAVA;
import de.ma.analyze.common.component.Component;
import de.ma.analyze.common.component.type.ComponentAttributeType;
import de.ma.analyze.common.component.type.ComponentType;
import de.ma.analyze.parser.java.JavaParser;
import de.ma.analyze.parser.java.JavaParsingContext;

import java.util.List;

/**
 * {@code JavaStructureListener} is responsible for building the basic structure consisting of classes, interfaces and
 * enumerations. <br>
 * This listener is almost empty because the logic is in the xxx only a constructor is created.
 *
 * @author Martin Absmeier
 */
public class JavaStructureListener extends JavaListenerBase {
    /**
     * Creates a new instance of {@code JavaListenerBase} class.
     *
     * @param revisionId the unique id of the source code
     */
    public JavaStructureListener(String revisionId) {
        super(revisionId, JavaParsingContext.builder().revisionId(revisionId).build());
    }

    // #################################################################################################################
    // Methods

    @Override
    public void enterInterfaceMethodDeclaration(JavaParser.InterfaceMethodDeclarationContext ctx) {
        JavaParser.InterfaceCommonBodyDeclarationContext methodBody = ctx.interfaceCommonBodyDeclaration();

        Component interfaceMethod = createComponent(ComponentType.JAVA_METHOD, methodBody.identifier().getText());
        addSourcePositionToComponentIfNotContained(interfaceMethod, ctx);
        addInterfaceModifiers(interfaceMethod, ctx.interfaceMethodModifier());
        addToCurrentComponentIfNotContained(interfaceMethod);

        parsingContext.setCurrentComponent(interfaceMethod);
    }

    @Override
    public void exitInterfaceMethodDeclaration(JavaParser.InterfaceMethodDeclarationContext ctx) {
        setParentIfAvailable();
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        Component classMethod = createComponent(ComponentType.JAVA_METHOD, ctx.identifier().getText());

        addSourcePositionToComponentIfNotContained(classMethod, ctx);
        addToCurrentComponentIfNotContained(classMethod);

        parsingContext.setCurrentComponent(classMethod);
    }

    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Enum constants

    @Override
    public void enterEnumConstant(JavaParser.EnumConstantContext ctx) {
        Component enumConstant = createComponent(ComponentType.JAVA_ENUM_CONSTANT, ctx.identifier().getText());

        addSourcePositionToComponentIfNotContained(enumConstant, ctx);
        addToCurrentComponentIfNotContained(enumConstant);

        parsingContext.setCurrentComponent(enumConstant);
    }

    @Override
    public void exitEnumConstant(JavaParser.EnumConstantContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Fields and constants
    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        Component field = createComponent(ComponentType.JAVA_FIELD, ctx.variableDeclarators().getText());

        addSourcePositionToComponentIfNotContained(field, ctx);
        addToCurrentComponentIfNotContained(field);

        parsingContext.setCurrentComponent(field);
    }

    @Override
    public void exitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        setParentIfAvailable();
    }

    @Override
    public void enterConstantDeclarator(JavaParser.ConstantDeclaratorContext ctx) {
        Component constant = createComponent(ComponentType.JAVA_FIELD, ctx.getText());

        addSourcePositionToComponentIfNotContained(constant, ctx);
        addToCurrentComponentIfNotContained(constant);

        parsingContext.setCurrentComponent(constant);
    }

    @Override
    public void exitConstantDeclarator(JavaParser.ConstantDeclaratorContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################

    private void addInterfaceModifiers(Component component, List<JavaParser.InterfaceMethodModifierContext> interfaceMethodModifiers) {
        if (interfaceMethodModifiers.isEmpty()) {
            component.addAttribute(createAttribute(ComponentAttributeType.JAVA_MODIFIER, JAVA.MODIFIER_PUBLIC));
        } else {
            interfaceMethodModifiers.forEach(modifier -> addModifierToComponent(component, modifier.getText()));
        }
    }
}
