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
import de.am.analyze.parser.common.listener.ListenerBase;
import de.am.analyze.generated.parser.java.JavaParser;
import de.am.analyze.generated.parser.java.JavaParser.ClassDeclarationContext;
import de.am.analyze.generated.parser.java.JavaParser.ClassOrInterfaceModifierContext;
import de.am.analyze.generated.parser.java.JavaParser.CompilationUnitContext;
import de.am.analyze.generated.parser.java.JavaParser.EnumDeclarationContext;
import de.am.analyze.generated.parser.java.JavaParser.IdentifierContext;
import de.am.analyze.generated.parser.java.JavaParser.ImportDeclarationContext;
import de.am.analyze.generated.parser.java.JavaParser.PackageDeclarationContext;
import de.am.analyze.generated.parser.java.JavaParser.TypeListContext;
import de.am.analyze.generated.parser.java.JavaParserBaseListener;
import de.am.analyze.parser.java.JavaApplication;
import de.am.analyze.parser.java.JavaParsingContext;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.am.analyze.common.AnalyzeConstants.JAVA;
import static de.am.analyze.common.AnalyzeConstants.JAVA.DEFAULT_PACKAGE;
import static de.am.analyze.common.AnalyzeConstants.JAVA.MODIFIER_ABSTRACT;
import static de.am.analyze.common.AnalyzeConstants.JAVA.MODIFIER_DEFAULT;
import static de.am.analyze.common.AnalyzeConstants.JAVA.MODIFIER_FINAL;
import static de.am.analyze.common.AnalyzeConstants.JAVA.MODIFIER_NATIVE;
import static de.am.analyze.common.AnalyzeConstants.JAVA.MODIFIER_PRIVATE;
import static de.am.analyze.common.AnalyzeConstants.JAVA.MODIFIER_PROTECTED;
import static de.am.analyze.common.AnalyzeConstants.JAVA.MODIFIER_PUBLIC;
import static de.am.analyze.common.AnalyzeConstants.JAVA.MODIFIER_STATIC;
import static de.am.analyze.common.component.type.ComponentAttributeType.COLUMN;
import static de.am.analyze.common.component.type.ComponentAttributeType.JAVA_ANNOTATED;
import static de.am.analyze.common.component.type.ComponentAttributeType.JAVA_EXTENDS;
import static de.am.analyze.common.component.type.ComponentAttributeType.JAVA_MODIFIER;
import static de.am.analyze.common.component.type.ComponentAttributeType.LINE;
import static de.am.analyze.common.component.type.ComponentAttributeType.SOURCE_NAME;
import static de.am.analyze.common.component.type.ComponentType.JAVA_CLASS;
import static de.am.analyze.common.component.type.ComponentType.JAVA_CONSTRUCTOR;
import static de.am.analyze.common.component.type.ComponentType.JAVA_ENUM;
import static de.am.analyze.common.component.type.ComponentType.JAVA_INTERFACE;
import static de.am.analyze.common.component.type.ComponentType.JAVA_PACKAGE;
import static java.io.File.separator;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * {@code JavaBaseListener} is responsible for building the basic structure consisting of classes, interfaces
 * and enumerations.
 *
 * @author Martin Absmeier
 */
@Log4j2
public abstract class JavaBaseListener extends JavaParserBaseListener implements ListenerBase {
    protected JavaApplication application;
    protected JavaParsingContext parsingContext;
    protected String sourceName;
    private final List<String> collectedModifiers;

    /**
     * Creates a new instance of {@code JavaListenerBase} class.
     *
     * @param revisionId     the unique id of the source code
     * @param parsingContext the parsing context
     */
    protected JavaBaseListener(String revisionId, JavaParsingContext parsingContext) {
        requireNonNull(revisionId, "Parameter 'revisionId' must not be NULL.");
        requireNonNull(parsingContext, "Parameter 'parsingContext' must not be NULL.");

        this.application = JavaApplication.getInstance();
        this.parsingContext = parsingContext;
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

        String uniquePackageName = currentComponent.getCoordinate();
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
            log.debug("Unknown import: {}", importName);
        }
    }

    // #################################################################################################################
    // Collect modifiers to add them later to classes, interfaces and fields
    @Override
    public void enterClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) {
        collectedModifiers.add(ctx.getText());
    }

    // #################################################################################################################
    // Interface

    @Override
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        // If we have no package add the default package
        if (!parsingContext.hasPackage()) {
            createAndSetDefaultPackage();
        }

        Component newInterface = createComponent(JAVA_INTERFACE, ctx.identifier().getText());
        addCompilationUnitAttribute(newInterface);
        addSourcePositionToComponentIfNotContained(newInterface, ctx);
        addImportsToComponent(newInterface);
        addAndClearCollectedModifiers(newInterface);
        addToCurrentComponentIfNotContained(newInterface);
        List<String> extendList = getInheritanceIfPresent(ctx.EXTENDS(), ctx.typeList());
        extendList.forEach(extendName -> newInterface.addAttribute(createAttribute(JAVA_EXTENDS, extendName)));

        parsingContext.setCurrentComponent(newInterface);
    }

    @Override
    public void exitInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Class
    @Override
    public void enterClassDeclaration(ClassDeclarationContext ctx) {
        if (!parsingContext.hasPackage()) {
            createAndSetDefaultPackage();
        }

        Component newClass = createComponent(JAVA_CLASS, ctx.identifier().getText());

        addCompilationUnitAttribute(newClass);
        addSourcePositionToComponentIfNotContained(newClass, ctx);
        addImportsToComponent(newClass);
        addAndClearCollectedModifiers(newClass);
        addToCurrentComponentIfNotContained(newClass);

        parsingContext.setCurrentComponent(newClass);
    }

    @Override
    public void exitClassDeclaration(ClassDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Enumerations
    @Override
    public void enterEnumDeclaration(EnumDeclarationContext ctx) {
        if (!parsingContext.hasPackage()) {
            createAndSetDefaultPackage();
        }

        Component newEnum = createComponent(JAVA_ENUM, ctx.identifier().getText());

        addCompilationUnitAttribute(newEnum);
        addSourcePositionToComponentIfNotContained(newEnum, ctx);
        addImportsToComponent(newEnum);
        addAndClearCollectedModifiers(newEnum);
        addToCurrentComponentIfNotContained(newEnum);

        parsingContext.setCurrentComponent(newEnum);
    }

    @Override
    public void exitEnumDeclaration(EnumDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Constructor

    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        Component newConstructor = createComponent(JAVA_CONSTRUCTOR, ctx.identifier().getText());

        addCompilationUnitAttribute(newConstructor);
        addSourcePositionToComponentIfNotContained(newConstructor, ctx);
        addToCurrentComponentIfNotContained(newConstructor);
        addAndClearCollectedModifiers(newConstructor);

        parsingContext.setCurrentComponent(newConstructor);
    }

    @Override
    public void exitConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Default constructor

    @Override
    public void exitClassBody(JavaParser.ClassBodyContext ctx) {
        Component currentComponent = parsingContext.getCurrentComponent();
        // Add default constructor
        addDefaultConstructorIfNecessary(currentComponent);
    }

    // #################################################################################################################
    // Cleanup

    @Override
    public void exitMemberDeclaration(JavaParser.MemberDeclarationContext ctx) {
        // Class variables also have modifiers these are deleted here because they will only be processed later.
        collectedModifiers.clear();
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
        String line = String.valueOf(start.getLine());
        String column = String.valueOf(start.getCharPositionInLine());

        component.addAttribute(createAttribute(LINE, line));
        component.addAttribute(createAttribute(COLUMN, column));
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
     * @param component the component to add the modifiers
     */
    protected void addAndClearCollectedModifiers(Component component) {
        collectedModifiers.forEach(modifier -> addModifierToComponent(component, modifier));

        List<ComponentAttribute> modifiers = component.findAttributesByType(JAVA_MODIFIER);
        if (modifiers.isEmpty()) {
            switch (component.getType()) {
                case JAVA_CLASS:
                    component.addAttribute(createAttribute(JAVA_MODIFIER, MODIFIER_PROTECTED));
                    break;
                case JAVA_INTERFACE, JAVA_ENUM:
                    component.addAttribute(createAttribute(JAVA_MODIFIER, MODIFIER_PUBLIC));
                    break;
                default:
                    // Nothing to do
                    break;
            }
        }

        collectedModifiers.clear();
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
            component.addAttribute(createAttribute(JAVA_ANNOTATED, determineModifier(modifier)));
        } else {
            component.addAttribute(createAttribute(JAVA_MODIFIER, determineModifier(modifier)));
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

    /**
     * Determines and maps the modifier to one of the constants.<br/>
     * - {@link JAVA#MODIFIER_PUBLIC}<br/>
     * - {@link JAVA#MODIFIER_PROTECTED}<br/>
     * - {@link JAVA#MODIFIER_PRIVATE}<br/>
     * - {@link JAVA#MODIFIER_STATIC}<br/>
     * - {@link JAVA#MODIFIER_FINAL}<br/>
     * - {@link JAVA#MODIFIER_NATIVE}<br/>
     * - {@link JAVA#MODIFIER_DEFAULT}
     *
     * @param modifier the modifier to map
     * @return the mapped modifier
     */
    private String determineModifier(String modifier) {
        if (nonNull(modifier)) {
            modifier = modifier.trim();
        }

        String result = "unknown";
        if (MODIFIER_PUBLIC.equalsIgnoreCase(modifier)) {
            result = MODIFIER_PUBLIC;
        } else if (MODIFIER_ABSTRACT.equalsIgnoreCase(modifier)) {
            result = MODIFIER_ABSTRACT;
        } else if (MODIFIER_PROTECTED.equalsIgnoreCase(modifier)) {
            result = MODIFIER_PROTECTED;
        } else if (MODIFIER_PRIVATE.equalsIgnoreCase(modifier)) {
            result = MODIFIER_PRIVATE;
        } else if (MODIFIER_STATIC.equalsIgnoreCase(modifier)) {
            result = MODIFIER_STATIC;
        } else if (MODIFIER_FINAL.equalsIgnoreCase(modifier)) {
            result = MODIFIER_FINAL;
        } else if (MODIFIER_NATIVE.equalsIgnoreCase(modifier)) {
            result = MODIFIER_NATIVE;
        } else if (MODIFIER_DEFAULT.equalsIgnoreCase(modifier)) {
            result = MODIFIER_DEFAULT;
        } else {
            result = result.concat("(").concat(modifier).concat(")");
        }

        return result;
    }

    /**
     * Initializes the parsing context with the java packages that are always visible.
     */
    private void initParsingContext() {
        addPackageToParsingContext(JAVA.LANG_PACKAGE);
        addPackageToParsingContext(JAVA.IO_PACKAGE);

        // We can always start looking at components from the top - this is the case e.g. when writing fully qualified
        // class names. We do this for both the application and the library
        parsingContext.addComponentWithVisibleChildren(application.getComponents());
        application.getLibraries().forEach(library -> parsingContext.addComponentWithVisibleChildren(library));
    }

    private void addPackageToParsingContext(String packageName) {
        Component pckgCmp = application.findLibraryComponentByUniqueCoordinate(packageName);
        if (isNull(pckgCmp)) {
            pckgCmp = application.findApplicationComponentByUniqueCoordinate(packageName);
        }
        if (isNull(pckgCmp)) {
            log.debug("Can not find package: {}", packageName);
        } else {
            parsingContext.addComponentWithVisibleChildren(pckgCmp);
        }
    }

    /**
     * Determine which import type it is. <br/>
     * - {@link ComponentType#JAVA_IMPORT_STATIC_ON_DEMAND} or <br/>
     * - {@link ComponentType#JAVA_IMPORT_STATIC} or <br/>
     * - {@link ComponentType#JAVA_IMPORT_ON_DEMAND} or <br/>
     * - {@link ComponentType#JAVA_IMPORT})
     *
     * @param ctx the context of the import declaration
     * @return the import type
     */
    private ComponentType determineImportType(ImportDeclarationContext ctx) {
        boolean isStatic = nonNull(ctx.STATIC());
        boolean isMultipleImport = nonNull(ctx.MUL());

        if (isStatic) {
            return isMultipleImport ? ComponentType.JAVA_IMPORT_STATIC_ON_DEMAND : ComponentType.JAVA_IMPORT_STATIC;
        } else {
            return isMultipleImport ? ComponentType.JAVA_IMPORT_ON_DEMAND : ComponentType.JAVA_IMPORT;
        }
    }

    /**
     * Creates and set the default package.
     */
    private void createAndSetDefaultPackage() {
        Component defaultPackage = createComponent(JAVA_PACKAGE, DEFAULT_PACKAGE);
        addCompilationUnitAttribute(defaultPackage);
        Component currentComponent = parsingContext.getCurrentComponent();
        currentComponent.addChild(defaultPackage);
        parsingContext.setCurrentComponent(defaultPackage);
        parsingContext.hasPackage(true);
    }

    /**
     * Returns all interfaces or the class is inherited from if inheritance is present.
     *
     * @param extendsNode the inheritance node
     * @param typeList    the list of data types
     * @return all interfaces or the class is inherited from
     */
    private List<String> getInheritanceIfPresent(TerminalNode extendsNode, List<TypeListContext> typeList) {
        List<String> inheritanceList = new ArrayList<>();

        if (nonNull(extendsNode) && !typeList.isEmpty()) {
            for (TypeListContext typeContext : typeList) {
                String[] extendsArray = typeContext.getText().split(",");
                inheritanceList.addAll(Arrays.asList(extendsArray));
            }
        }

        return inheritanceList;
    }

    private void addDefaultConstructorIfNecessary(Component component) {
        if (JAVA_CONSTRUCTOR.equals(component.getType())) {
            return;
        }

        if (hasNoConstructor(component)) {
            String constructorName = component.getValue();
            Component newConstructor = createComponent(JAVA_CONSTRUCTOR, constructorName);
            String constructorSrc = JAVA.MODIFIER_PUBLIC.concat(" ").concat(constructorName).concat("() { }");
            newConstructor.setChecksum(calculateChecksum(constructorSrc));

            addModifierToComponent(newConstructor, JAVA.MODIFIER_PUBLIC);
            addToCurrentComponentIfNotContained(newConstructor);
        }
    }

    /**
     * Checks if the component has no constructor.
     *
     * @param component the component to be checked
     * @return true if the component has no constructor, false otherwise
     */
    private boolean hasNoConstructor(Component component) {
        List<Component> constructors = component.findChildrenByType(JAVA_CONSTRUCTOR);
        return constructors.isEmpty() && component.isType(JAVA_CLASS);
    }
}