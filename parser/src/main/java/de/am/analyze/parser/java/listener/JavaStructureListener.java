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
import de.am.analyze.common.component.ComponentAttribute;
import de.am.analyze.common.component.type.ComponentAttributeType;
import de.am.analyze.common.component.type.ComponentType;
import de.am.analyze.parser.common.ListenerBase;
import de.am.analyze.parser.generated.java.JavaParser.ClassBodyContext;
import de.am.analyze.parser.generated.java.JavaParser.ClassDeclarationContext;
import de.am.analyze.parser.generated.java.JavaParser.ClassOrInterfaceModifierContext;
import de.am.analyze.parser.generated.java.JavaParser.CompilationUnitContext;
import de.am.analyze.parser.generated.java.JavaParser.ConstructorDeclarationContext;
import de.am.analyze.parser.generated.java.JavaParser.CreatorContext;
import de.am.analyze.parser.generated.java.JavaParser.EnumConstantContext;
import de.am.analyze.parser.generated.java.JavaParser.EnumDeclarationContext;
import de.am.analyze.parser.generated.java.JavaParser.FormalParameterListContext;
import de.am.analyze.parser.generated.java.JavaParser.IdentifierContext;
import de.am.analyze.parser.generated.java.JavaParser.ImportDeclarationContext;
import de.am.analyze.parser.generated.java.JavaParser.InterfaceDeclarationContext;
import de.am.analyze.parser.generated.java.JavaParser.PackageDeclarationContext;
import de.am.analyze.parser.generated.java.JavaParserBaseListener;
import de.am.analyze.parser.java.JavaApplication;
import de.am.analyze.parser.java.JavaParsingContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.am.analyze.common.AnalyzeConstants.JAVA;
import static de.am.analyze.common.component.type.ComponentAttributeType.JAVA_ANNOTATED;
import static de.am.analyze.common.component.type.ComponentAttributeType.JAVA_MODIFIER;
import static de.am.analyze.common.component.type.ComponentAttributeType.SOURCE_NAME;
import static de.am.analyze.common.component.type.ComponentAttributeType.START_COLUMN;
import static de.am.analyze.common.component.type.ComponentAttributeType.START_LINE;
import static de.am.analyze.common.component.type.ComponentAttributeType.STOP_COLUMN;
import static de.am.analyze.common.component.type.ComponentAttributeType.STOP_LINE;
import static de.am.analyze.common.component.type.ComponentType.JAVA_CLASS;
import static de.am.analyze.common.component.type.ComponentType.JAVA_CONSTRUCTOR;
import static de.am.analyze.common.component.type.ComponentType.JAVA_DEFAULT_CONSTRUCTOR;
import static de.am.analyze.common.component.type.ComponentType.JAVA_ENUM;
import static de.am.analyze.common.component.type.ComponentType.JAVA_ENUM_CONSTANT;
import static de.am.analyze.common.component.type.ComponentType.JAVA_INTERFACE;
import static de.am.analyze.common.component.type.ComponentType.JAVA_PACKAGE;
import static java.io.File.separator;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * {@code JavaStructureListener} is responsible for building the basic structure consisting of classes, interfaces, enumerations
 * and their methods.
 *
 * @author Martin Absmeier
 */
public class JavaStructureListener extends JavaParserBaseListener implements ListenerBase {

    private static final Logger LOGGER = LogManager.getLogger(JavaStructureListener.class);

    protected JavaApplication application;
    protected JavaParsingContext parsingContext;
    protected String sourceName;
    /**
     * Modifiers are now one level above classes or interfaces in the optimized grammar so we collect them
     * first and then add them to the correct recipient in the level below
     */
    private final List<String> collectedModifiers;

    /**
     * Creates a new instance of {@code JavaListenerBase} class.
     *
     * @param revisionId revisionId the unique id of the source code
     */
    public JavaStructureListener(String revisionId) {
        this.application = JavaApplication.getInstance();
        this.parsingContext = JavaParsingContext.builder().revisionId(revisionId).build();
        this.collectedModifiers = new ArrayList<>();
        initParsingContext();
    }

    // #################################################################################################################

    /**
     * A compilation unit consists of three parts, each of which is optional:<br>
     * <ul>
     *     <li>A package declaration, giving the fully qualified name of the package to which the compilation unit belongs.
     *         A compilation unit that has no package declaration is part of an unnamed package.</li>
     *     <li>A Import declarations that allow types from other packages and static members of types to be referred
     *         to using their simple names.</li>
     *     <li>Top level type declarations of class and interface and enum types.</li>
     * </ul>
     *
     * @param ctx the compilation unit context
     */
    @Override
    public void enterCompilationUnit(CompilationUnitContext ctx) {
        int startIdx = sourceName.lastIndexOf(separator) + 1;
        int stopIdx = sourceName.length();
        parsingContext.setCurrentFile(createAttribute(SOURCE_NAME, sourceName.substring(startIdx, stopIdx)));
    }

    /**
     * A package declaration in a compilation unit specifies the fully qualified name of the package to which the
     * compilation unit belongs.<br>
     * A compilation unit that has no package declaration is part of an unnamed package, in that case we use
     * <b>default</b> as package name.<br>
     * A package is observable if and only if either:
     * <ul>
     *     <li>A compilation unit containing a declaration of the package is observable.</li>
     *     <li>A subpackage of the package is observable.</li>
     * </ul>
     * The packages <b>java</b>, <b>java.lang</b>, and <b>java.io</b> are always observable.
     *
     * @param ctx the package declaration context
     */
    @Override
    public void enterPackageDeclaration(PackageDeclarationContext ctx) {
        Component currentComponent = parsingContext.getCurrentComponent();

        List<IdentifierContext> nodes = ctx.qualifiedName().identifier();
        for (IdentifierContext node : nodes) {
            String packageName = node.getText();
            Component newPackage = createComponent(JAVA_PACKAGE, packageName);

            currentComponent.addChild(newPackage);
            currentComponent = newPackage;
        }

        parsingContext.hasPackage(true);
        parsingContext.setCurrentComponent(currentComponent);

        String uniquePackageName = currentComponent.getUniqueCoordinate();
        Component component = application.findApplicationComponentByUniqueCoordinate(uniquePackageName);
        if (nonNull(component)) {
            parsingContext.addComponentWithVisibleChildren(component);
        }
    }

    /**
     * This rule covers all flavours of import statements including on demand (.*) and static imports.
     *
     * @param ctx the context
     */
    @Override
    public void enterImportDeclaration(ImportDeclarationContext ctx) {
        // We create a child component with a flavour of import type
        ComponentType importType = determineImportType(ctx);
        String importName = ctx.qualifiedName().getText();
        Component importComponent = createComponent(importType, importName);
        parsingContext.addImport(importComponent);

        Component component = application.findApplicationComponentByUniqueCoordinate(importName);
        if (isNull(component)) {
            component = application.findLibraryComponentByUniqueCoordinate(importName);
        }

        boolean isMultipleImport = nonNull(ctx.MUL());
        if (nonNull(component)) {
            if (isMultipleImport) {
                parsingContext.addComponentWithVisibleChildren(component);
            } else {
                parsingContext.addVisibleComponentIfNotContained(component);
            }
        } else {
            LOGGER.warn("Unknown import: {}", importName);
        }
    }

    // #################################################################################################################
    // Collect modifiers to add them later to classes, interfaces and fields
    @Override
    public void enterClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
        collectedModifiers.add(ctx.getText());
    }

    // #################################################################################################################
    // Interfaces

    /**
     * An interface declaration specifies a new named reference type. There are two kinds of interface declarations -
     * <i>normal interface declarations and annotation type declarations.</i><br>
     * The Identifier in an interface declaration specifies the name of the interface.
     *
     * @param ctx the context
     */
    @Override
    public void enterInterfaceDeclaration(InterfaceDeclarationContext ctx) {
        // If we have no package add the default package
        if (!parsingContext.hasPackage()) {
            createAndSetDefaultPackage();
        }

        Component newInterface = createComponent(JAVA_INTERFACE, ctx.identifier().getText());
        addCompilationUnitAttribute(newInterface);
        addSourcePositionToComponentIfNotContained(newInterface, ctx);
        addImportsToComponent(newInterface);
        addAndClearCollectedModifiers(newInterface, true);
        addToCurrentComponentIfNotContained(newInterface);

        parsingContext.setCurrentComponent(newInterface);
    }

    @Override
    public void exitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Classes
    @Override
    public void enterClassDeclaration(ClassDeclarationContext ctx) {
        if (!parsingContext.hasPackage()) {
            createAndSetDefaultPackage();
        }

        Component newClass = createComponent(JAVA_CLASS, ctx.identifier().getText());

        addCompilationUnitAttribute(newClass);
        addSourcePositionToComponentIfNotContained(newClass, ctx);
        addImportsToComponent(newClass);
        // In order to be able to get qualified names for the parameterized types below we need to add the child to the
        // parent before processing the type parameters. Otherwise, we only get "T" or "List.T" instead of "java.lang.List.T"
        addToCurrentComponentIfNotContained(newClass);

        // Modifiers are collected one level above
        addAndClearCollectedModifiers(newClass, false);

        parsingContext.setCurrentComponent(newClass);
    }

    @Override
    public void exitClassDeclaration(ClassDeclarationContext ctx) {
        setParentIfAvailable();
    }

    @Override
    public void enterConstructorDeclaration(ConstructorDeclarationContext ctx) {
        Component newConstructor;

        FormalParameterListContext parameterList = ctx.formalParameters().formalParameterList();
        if (isNull(parameterList) || parameterList.isEmpty()) {
            newConstructor = createComponent(JAVA_DEFAULT_CONSTRUCTOR, ctx.identifier().getText());
        } else {
            newConstructor = createComponent(JAVA_CONSTRUCTOR, ctx.identifier().getText());
        }

        addCompilationUnitAttribute(newConstructor);
        addSourcePositionToComponentIfNotContained(newConstructor, ctx);
        addToCurrentComponentIfNotContained(newConstructor);
        addAndClearCollectedModifiers(newConstructor, false);

        parsingContext.setCurrentComponent(newConstructor);
    }

    @Override
    public void exitConstructorDeclaration(ConstructorDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // Add default constructor
    @Override
    public void exitClassBody(ClassBodyContext ctx) {
        Component currentComponent = parsingContext.getCurrentComponent();
        addDefaultConstructorIfNecessary(currentComponent);
    }

    // #################################################################################################################
    // Inner classes
    @Override
    public void enterCreator(CreatorContext ctx) {
        super.enterCreator(ctx);
        /*
        if (nonNull(ctx.classCreatorRest())) {
            Component newClass = createComponentForClassInstanceCreationExpression(ctx);

            String className = ctx.createdName().getText();
            newClass.addAttribute(createAttribute(ComponentType.JAVA_LOCAL_CLASS, className));

            addToCurrentComponentIfNotContained(newClass);
            parsingContext.setCurrentComponent(newClass);

            addToCurrentComponentIfNotContained(createInstanceInitializerMethod());
            addToCurrentComponentIfNotContained(createStaticInitializerMethod());
        }
        // We cannot create a new inner class with the array construct so skipping this here
         */
    }

    @Override
    public void exitCreator(CreatorContext ctx) {
        super.exitCreator(ctx);
        /*
        if (nonNull(ctx.classCreatorRest())) {
            setParentIfAvailable();
        }
        */
    }

    // #################################################################################################################
    // Enumerations
    @Override
    public void enterEnumDeclaration(EnumDeclarationContext ctx) {
        Component newEnum = createComponent(JAVA_ENUM, ctx.identifier().getText());

        addCompilationUnitAttribute(newEnum);
        addSourcePositionToComponentIfNotContained(newEnum, ctx);
        addImportsToComponent(newEnum);
        addAndClearCollectedModifiers(newEnum, false);
        addToCurrentComponentIfNotContained(newEnum);

        parsingContext.setCurrentComponent(newEnum);
    }

    @Override
    public void exitEnumDeclaration(EnumDeclarationContext ctx) {
        setParentIfAvailable();
    }

    @Override
    public void enterEnumConstant(EnumConstantContext ctx) {
        Component enumConstant = createComponent(JAVA_ENUM_CONSTANT, ctx.identifier().getText());

        addToCurrentComponentIfNotContained(enumConstant);
        addSourcePositionToComponentIfNotContained(enumConstant, ctx);

        parsingContext.setCurrentComponent(enumConstant);
    }

    @Override
    public void exitEnumConstant(EnumConstantContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Public methods
    @Override
    public Component getResult() {
        return parsingContext.getComponent();
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public void reset() {
        parsingContext.reset();
        initParsingContext();
        collectedModifiers.clear();
    }

    // #################################################################################################################

    /**
     * Creates a {@link Component} specified by {@code type} and {@code value}.
     *
     * @param type  the type of the component
     * @param value the value of the component
     * @return the {@code ComponentNode}
     */
    protected Component createComponent(ComponentType type, String value) {
        return Component.builder().type(type).value(value).build();
    }

    /**
     * Creates a {@link ComponentAttribute} specified by {@code type} and {@code value}.
     *
     * @param type  the type
     * @param value the value
     * @return the {@code ComponentAttribute}
     */
    protected ComponentAttribute createAttribute(ComponentAttributeType type, String value) {
        return ComponentAttribute.builder().type(type).value(value).build();
    }

    /**
     * Add the compilation unit attribute.<br>
     * The file to which the component belongs.
     *
     * @param component the component
     */
    protected void addCompilationUnitAttribute(Component component) {
        component.addAttribute(parsingContext.getCurrentFile());
    }

    /**
     * Add the source code position to the specified {@code component} as {@link ComponentAttribute} if not contained.
     *
     * @param component the component
     * @param ctx       the context
     */
    protected void addSourcePositionToComponentIfNotContained(Component component, ParserRuleContext ctx) {
        Token start = ctx.getStart();
        String startLine = String.valueOf(start.getLine());
        String startColumn = String.valueOf(start.getCharPositionInLine());
        Token stop = ctx.getStop();
        String stopLine = String.valueOf(stop.getLine());
        String stopColumn = String.valueOf(stop.getCharPositionInLine());

        List<ComponentAttribute> attributes = Arrays.asList(
            createAttribute(START_LINE, startLine),
            createAttribute(START_COLUMN, startColumn),
            createAttribute(STOP_LINE, stopLine),
            createAttribute(STOP_COLUMN, stopColumn)
        );

        attributes.forEach(attribute -> {
            if (!component.getAttributes().contains(attribute)) {
                component.addAttribute(attribute);
            }
        });
    }

    /**
     * Add the imports to the specified {@code component} as children.
     *
     * @param component the component
     */
    protected void addImportsToComponent(Component component) {
        parsingContext.getImports().forEach(component::addChild);
    }

    /**
     * Add the collected modifiers to the specified {@code component}.
     *
     * @param component               the component to add the modifiers
     * @param addPublicIfNotSpecified true to add a "public" modifier if no one was specified, false otherwise
     */
    protected void addAndClearCollectedModifiers(Component component, boolean addPublicIfNotSpecified) {
        boolean needToAddPublicModifier = true;

        for (String modifier : collectedModifiers) {
            addModifierToComponent(component, modifier);

            if (isPublicOrProtectedOrPrivate(modifier)) {
                needToAddPublicModifier = false;
            }
        }
        collectedModifiers.clear();

        if (addPublicIfNotSpecified && needToAddPublicModifier) {
            addModifierToComponent(component, JAVA.MODIFIER_PUBLIC);
        }
    }

    /**
     * Add a {@link ComponentAttribute} of type {@link ComponentAttributeType#JAVA_ANNOTATED} or {@link ComponentAttributeType#JAVA_MODIFIER}
     * to the component specified by {@code modifier}.<br>
     * If the {@code modifier} string contains an @ an {@link ComponentAttributeType#JAVA_ANNOTATED} attribute is added.
     *
     * @param component the component
     * @param modifier  the modifier
     */
    protected void addModifierToComponent(Component component, String modifier) {
        if (modifier.contains("@")) {
            component.addAttribute(createAttribute(JAVA_ANNOTATED, modifier));
        } else {
            component.addAttribute(createAttribute(JAVA_MODIFIER, modifier));
        }
    }

    /**
     * If the current component has a parent, this will be set as the new current component.
     */
    protected void setParentIfAvailable() {
        Component currentComponent = parsingContext.getCurrentComponent();
        if (nonNull(currentComponent) && currentComponent.hasParent()) {
            parsingContext.setCurrentComponent(currentComponent.getParent());
        }
    }

    /**
     * Add the component specified by {@code component} to the current component of the {@link JavaParsingContext} if
     * not contained.
     *
     * @param component the component to be added
     */
    protected void addToCurrentComponentIfNotContained(Component component) {
        Component currentComponent = parsingContext.getCurrentComponent();
        if (nonNull(currentComponent) && currentComponent.childrenNotContains(component)) {
            currentComponent.addChild(component);
        }
    }

    // #################################################################################################################

    private void initParsingContext() {
        initParsingContextWithPackage(JAVA.LANG_PACKAGE);
        initParsingContextWithPackage(JAVA.IO_PACKAGE);

        // We can always start looking at components from the top - this is the case e.g. when writing fully qualified
        // class names. We do this for both the application and the library
        parsingContext.addComponentWithVisibleChildren(application.getComponents());
        application.getLibraries().forEach(library -> parsingContext.addComponentWithVisibleChildren(library));
    }

    private void initParsingContextWithPackage(String packageName) {
        Component pckgCmp = application.findLibraryComponentByUniqueCoordinate(packageName);
        if (isNull(pckgCmp)) {
            pckgCmp = application.findApplicationComponentByUniqueCoordinate(packageName);
        }
        if (isNull(pckgCmp)) {
            LOGGER.warn("Can not find package: {}", packageName);
        } else {
            parsingContext.addComponentWithVisibleChildren(pckgCmp);
        }
    }

    private ComponentType determineImportType(ImportDeclarationContext ctx) {
        boolean isStatic = nonNull(ctx.STATIC());
        boolean isMultipleImport = nonNull(ctx.MUL());

        if (isStatic) {
            return isMultipleImport ? ComponentType.JAVA_IMPORT_STATIC_ON_DEMAND : ComponentType.JAVA_IMPORT_STATIC;
        } else {
            return isMultipleImport ? ComponentType.JAVA_IMPORT_ON_DEMAND : ComponentType.JAVA_IMPORT;
        }
    }

    private void createAndSetDefaultPackage() {
        Component newPackage = createComponent(JAVA_PACKAGE, JAVA.DEFAULT_PACKAGE);
        addCompilationUnitAttribute(newPackage);
        Component currentComponent = parsingContext.getCurrentComponent();
        currentComponent.addChild(newPackage);
        parsingContext.setCurrentComponent(newPackage);
        parsingContext.hasPackage(true);
    }

    private boolean isPublicOrProtectedOrPrivate(String modifier) {
        return JAVA.MODIFIER_PUBLIC.equals(modifier) || JAVA.MODIFIER_PROTECTED.equals(modifier) || JAVA.MODIFIER_PRIVATE.equals(modifier);
    }

    private void addDefaultConstructorIfNecessary(Component component) {
        ComponentType componentType = component.getType();
        if (JAVA_CONSTRUCTOR.equals(componentType) || JAVA_DEFAULT_CONSTRUCTOR.equals(componentType)) {
            return;
        }

        if (hasNoConstructor(component)) {
            String constructorName = component.getValue();
            Component newConstructor = createComponent(JAVA_DEFAULT_CONSTRUCTOR, constructorName);
            newConstructor.setChecksum(calculateChecksum("public".concat(constructorName).concat("(){}")));
            addModifierToComponent(newConstructor, JAVA.MODIFIER_PUBLIC);

            addToCurrentComponentIfNotContained(newConstructor);
        }
    }

    private boolean hasNoConstructor(Component component) {
        List<Component> constructors = component.findChildrenByType(JAVA_CONSTRUCTOR);
        constructors.addAll(component.findChildrenByType(JAVA_DEFAULT_CONSTRUCTOR));
        return constructors.isEmpty() && component.isType(JAVA_CLASS);
    }
}