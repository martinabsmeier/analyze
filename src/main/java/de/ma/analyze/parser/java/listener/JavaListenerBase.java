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
import de.ma.analyze.common.component.ComponentAttribute;
import de.ma.analyze.common.component.type.ComponentAttributeType;
import de.ma.analyze.common.component.type.ComponentType;
import de.ma.analyze.parser.common.listener.ListenerBase;
import de.ma.analyze.parser.java.JavaApplication;
import de.ma.analyze.parser.java.JavaParser;
import de.ma.analyze.parser.java.JavaParserBaseListener;
import de.ma.analyze.parser.java.JavaParsingContext;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Objects.*;

/**
 * {@code JavaBaseListener} is responsible for building the basic structure consisting of classes, interfaces
 * and enumerations.
 *
 * @author Martin Absmeier
 */
@Log4j2
public abstract class JavaListenerBase extends JavaParserBaseListener implements ListenerBase {

    private static final Map<String, String> MODIFIER_MAP = Map.ofEntries(
            Map.entry("public", JAVA.MODIFIER_PUBLIC),
            Map.entry("protected", JAVA.MODIFIER_PROTECTED),
            Map.entry("private", JAVA.MODIFIER_PRIVATE),
            Map.entry("static", JAVA.MODIFIER_STATIC),
            Map.entry("final", JAVA.MODIFIER_FINAL),
            Map.entry("native", JAVA.MODIFIER_NATIVE),
            Map.entry("abstract", JAVA.MODIFIER_ABSTRACT),
            Map.entry("default", JAVA.MODIFIER_DEFAULT)
    );

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
    protected JavaListenerBase(String revisionId, JavaParsingContext parsingContext) {
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
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        int startIdx = sourceName.lastIndexOf(File.separator) + 1;
        int stopIdx = sourceName.length();
        parsingContext.setCurrentFile(createAttribute(ComponentAttributeType.SOURCE_NAME, sourceName.substring(startIdx, stopIdx)));
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
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        Component currentComponent = parsingContext.getCurrentComponent();

        List<JavaParser.IdentifierContext> nodes = ctx.qualifiedName().identifier();
        for (JavaParser.IdentifierContext node : nodes) {
            String packageName = node.getText();
            Component newPackage = createComponent(ComponentType.JAVA_PACKAGE, packageName);

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
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
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
    public void enterClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        collectedModifiers.add(ctx.getText());
    }

    // #################################################################################################################
    // Type Declaration Handling (Class, Interface, Enum)

    /**
     * Handles entry into type declarations (class, interface, enum).
     * Extracts common logic that applies to all type declarations.
     *
     * @param componentType the type of component being declared
     * @param identifier    the identifier context containing the component name
     * @param ctx           the parser rule context for source position
     * @param heritage      optional heritage (extends) list for interfaces
     */
    private void enterTypeDeclaration(ComponentType componentType, JavaParser.IdentifierContext identifier,
                                      ParserRuleContext ctx, List<String> heritage) {
        setDefaultPackageIfNecessary();

        Component newComponent = createComponent(componentType, identifier.getText());
        applyTypeDeclarationAttributes(newComponent, ctx);

        if (heritage != null && !heritage.isEmpty()) {
            heritage.forEach(extendName ->
                    newComponent.addAttribute(createAttribute(ComponentAttributeType.JAVA_EXTENDS, extendName)));
        }

        parsingContext.setCurrentComponent(newComponent);
    }

    /**
     * Applies common attributes to type declarations.
     *
     * @param component the component to configure
     * @param ctx       the parser context for source position
     */
    private void applyTypeDeclarationAttributes(Component component, ParserRuleContext ctx) {
        addCompilationUnitAttribute(component);
        addSourcePositionToComponentIfNotContained(component, ctx);
        addImportsToComponent(component);
        addAndClearCollectedModifiers(component);
        addToCurrentComponentIfNotContained(component);
    }

    // #################################################################################################################
    // Interface

    @Override
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        List<String> extendList = getInheritanceIfPresent(ctx.EXTENDS(), ctx.typeList());
        enterTypeDeclaration(ComponentType.JAVA_INTERFACE, ctx.identifier(), ctx, extendList);
    }

    @Override
    public void exitInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Class

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        enterTypeDeclaration(ComponentType.JAVA_CLASS, ctx.identifier(), ctx, null);
    }

    @Override
    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Enumeration

    @Override
    public void enterEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
        enterTypeDeclaration(ComponentType.JAVA_ENUM, ctx.identifier(), ctx, null);
    }

    @Override
    public void exitEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
        setParentIfAvailable();
    }

    // #################################################################################################################
    // Constructor

    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        Component newConstructor = createComponent(ComponentType.JAVA_CONSTRUCTOR, ctx.identifier().getText());

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

        component.addAttribute(createAttribute(ComponentAttributeType.LINE, line));
        component.addAttribute(createAttribute(ComponentAttributeType.COLUMN, column));
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

        List<ComponentAttribute> modifiers = component.findAttributesByType(ComponentAttributeType.JAVA_MODIFIER);
        if (modifiers.isEmpty()) {
            switch (component.getType()) {
                case JAVA_CLASS:
                    component.addAttribute(createAttribute(ComponentAttributeType.JAVA_MODIFIER, JAVA.MODIFIER_PROTECTED));
                    break;
                case JAVA_INTERFACE, JAVA_ENUM:
                    component.addAttribute(createAttribute(ComponentAttributeType.JAVA_MODIFIER, JAVA.MODIFIER_PUBLIC));
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
            component.addAttribute(createAttribute(ComponentAttributeType.JAVA_ANNOTATED, determineModifier(modifier)));
        } else {
            component.addAttribute(createAttribute(ComponentAttributeType.JAVA_MODIFIER, determineModifier(modifier)));
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
     * Determines and maps the modifier to one of the known constants.
     *
     * @param modifier the modifier to map
     * @return the mapped modifier, or "unknown(modifier)" if not recognized
     */
    private String determineModifier(String modifier) {
        if (isNull(modifier)) {
            return "unknown";
        }

        String trimmed = modifier.trim().toLowerCase();
        return MODIFIER_MAP.getOrDefault(trimmed, "unknown(" + modifier + ")");
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
    private ComponentType determineImportType(JavaParser.ImportDeclarationContext ctx) {
        boolean isStatic = nonNull(ctx.STATIC());
        boolean isMultipleImport = nonNull(ctx.MUL());

        if (isStatic) {
            return isMultipleImport ? ComponentType.JAVA_IMPORT_STATIC_ON_DEMAND : ComponentType.JAVA_IMPORT_STATIC;
        } else {
            return isMultipleImport ? ComponentType.JAVA_IMPORT_ON_DEMAND : ComponentType.JAVA_IMPORT;
        }
    }

    /**
     * Set the default package if component has no package.
     */
    private void setDefaultPackageIfNecessary() {
        if (!parsingContext.hasPackage()) {
            createAndSetDefaultPackage();
        }
    }

    /**
     * Creates and set the default package at {@link JavaStructureListener#parsingContext}.
     */
    private void createAndSetDefaultPackage() {
        Component defaultPackage = createComponent(ComponentType.JAVA_PACKAGE, JAVA.DEFAULT_PACKAGE);
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
    private List<String> getInheritanceIfPresent(TerminalNode extendsNode, List<JavaParser.TypeListContext> typeList) {
        List<String> inheritanceList = new ArrayList<>();

        if (nonNull(extendsNode) && !typeList.isEmpty()) {
            for (JavaParser.TypeListContext typeContext : typeList) {
                String[] extendsArray = typeContext.getText().split(",");
                inheritanceList.addAll(Arrays.asList(extendsArray));
            }
        }

        return inheritanceList;
    }

    private void addDefaultConstructorIfNecessary(Component component) {
        if (ComponentType.JAVA_CONSTRUCTOR.equals(component.getType())) {
            return;
        }

        if (hasNoConstructor(component)) {
            String constructorName = component.getValue();
            Component newConstructor = createComponent(ComponentType.JAVA_CONSTRUCTOR, constructorName);
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
        List<Component> constructors = component.findChildrenByType(ComponentType.JAVA_CONSTRUCTOR);
        return constructors.isEmpty() && component.isType(ComponentType.JAVA_CLASS);
    }
}
