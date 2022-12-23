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
import de.am.analyze.parser.generated.java.JavaParser.CompilationUnitContext;
import de.am.analyze.parser.generated.java.JavaParser.IdentifierContext;
import de.am.analyze.parser.generated.java.JavaParser.PackageDeclarationContext;
import de.am.analyze.parser.generated.java.JavaParserBaseListener;
import de.am.analyze.parser.java.JavaApplication;
import de.am.analyze.parser.java.JavaParsingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static de.am.analyze.common.component.type.ComponentType.JAVA_PACKAGE;
import static java.io.File.separator;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class JavaDeclarationListener extends JavaParserBaseListener implements ListenerBase {

    private static final Logger LOGGER = LogManager.getLogger(JavaDeclarationListener.class);

    protected JavaApplication application;
    protected JavaParsingContext parsingContext;
    protected String sourceName;

    /**
     * Creates a new instance of {@code JavaListenerBase} class.
     *
     * @param revisionId revisionId the unique id of the source code
     */
    public JavaDeclarationListener(String revisionId) {
        this.application = JavaApplication.getInstance();
        this.parsingContext = JavaParsingContext.builder().revisionId(revisionId).build();
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

    // #################################################################################################################
    @Override
    public Component getResult() {
        return null;
    }

    @Override
    public void setSourceName(String sourceName) {

    }

    @Override
    public void reset() {

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

    // #################################################################################################################

    private void initParsingContext() {
        // We can always start looking at components from the top - this is the case e.g. when writing fully qualified
        // class names. We do this for both the application and the library
        parsingContext.addComponentWithVisibleChildren(application.getComponents());
        application.getLibraries().forEach(library -> parsingContext.addComponentWithVisibleChildren(library));
    }

    private void initParsingContextWithPackage(String uniqueCoordinate) {
        Component component = application.findLibraryComponentByUniqueCoordinate(uniqueCoordinate);
        if (isNull(component)) {
            component = application.findApplicationComponentByUniqueCoordinate(uniqueCoordinate);
        }
        if (isNull(component)) {
            LOGGER.warn("Can not find library: {}", uniqueCoordinate);
        } else {
            parsingContext.addComponentWithVisibleChildren(component);
        }
    }
}