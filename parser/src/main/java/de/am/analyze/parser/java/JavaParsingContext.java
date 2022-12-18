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
package de.am.analyze.parser.java;

import de.am.analyze.common.component.Component;
import de.am.analyze.parser.common.ParsingContextBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import static de.am.analyze.common.component.type.ComponentType.JAVA_CLASS;
import static de.am.analyze.common.component.type.ComponentType.JAVA_ENUM;
import static de.am.analyze.common.component.type.ComponentType.JAVA_INTERFACE;
import static de.am.analyze.common.component.type.ComponentType.JAVA_PARAMETERIZED_TYPE;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * {@code JavaParsingContext} is responsible to track where we are and what do we know during the parsing.<br>
 * e.g. the structure of the application
 *
 * @author Martin Absmeier
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JavaParsingContext extends ParsingContextBase {

    /**
     * If the current compilation unit has a package then true otherwise false
     */
    @Accessors(fluent = true)
    private boolean hasPackage;

    /**
     * All imports of the current compilation unit / component
     */
    private List<Component> imports = new ArrayList<>();

    /**
     * Add the import component specified by {@code component} to internal list for later lookup.
     *
     * @param component the component
     */
    public void addImport(Component component) {
        requireNonNull(component, "Parameter 'component' must not be NULL.");
        if (!imports.contains(component)) {
            imports.add(component);
        }
    }

    /**
     * Retrieves the visible component specified by {@code value}.
     *
     * @param value the value of the component
     * @return the component or NULL if no one is found
     */
    public Component findVisibleComponentByValue(String value) {
        requireNonNull(value, "Parameter 'value' must not be NULL.");

        Component component = visibleComponents.stream()
            .filter(cmp -> value.equals(cmp.getValue()))
            .findFirst().orElse(null);

        if (nonNull(component)) {
            return component;
        }

        return componentsWithVisibleChildren.stream()
            .map(Component::getChildren)
            .flatMap(List::stream)
            .filter(cmp -> value.equals(cmp.getValue()))
            .findFirst().orElse(null);
    }

    /**
     * Resets the parsing context so that the next listener can run.<br>
     * The following variables are reinitialized:<br>
     * - component = ComponentNode.builder().type(ROOT).value("root").build();<br>
     * - currentComponent = component;<br>
     * - currentPackage = null;<br>
     * - currentFile = null;<br>
     * - imports.clear();<br>
     * - visibleComponents.clear();<br>
     * - componentsWithVisibleChildren.clear();<br>
     */
    @Override
    public void reset() {
        super.reset();  // Reset the state of the base class
        hasPackage = false;
        imports.clear();
        visibleComponents.clear();
        componentsWithVisibleChildren.clear();
    }

    // #################################################################################################################

    private boolean isClassOrInterfaceOrEnumOrTypeParameter(Component component) {
        return isClassOrInterfaceOrEnum(component) || isParameteriziedType(component);
    }

    private boolean isClassOrInterfaceOrEnum(Component component) {
        return isClass(component) || isInterface(component) || isEnum(component);
    }

    private boolean isClass(Component component) {
        return component.isType(JAVA_CLASS);
    }

    private boolean isInterface(Component component) {
        return component.isType(JAVA_INTERFACE);
    }

    private boolean isEnum(Component component) {
        return component.isType(JAVA_ENUM);
    }

    private boolean isParameteriziedType(Component component) {
        return component.isType(JAVA_PARAMETERIZED_TYPE);
    }
}