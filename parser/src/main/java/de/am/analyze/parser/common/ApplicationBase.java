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
package de.am.analyze.parser.common;

import de.am.analyze.common.component.Component;
import de.am.analyze.common.component.type.ComponentType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.am.analyze.common.AnalyzeConstants.*;
import static de.am.analyze.common.component.type.ComponentType.APP_ROOT;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * {@code ApplicationBase} is the programming language independent base class of an application.
 *
 * @author Martin Absmeier
 */
@Getter
public abstract class ApplicationBase {

    private final Component components = Component.builder().type(APP_ROOT).value(APP_ROOT.name()).build();
    private final List<Component> libraries = new ArrayList<>();
    private static final String PARAM_UNIQUE_COORDINATE_NOT_NULL = "Parameter 'uniqueCoordinate' must not be NULL.";

    // #################################################################################################################
    // Interface

    /**
     * Update the specified {@code target} component with the attributes of {@code source} component.<br>
     * The implementation what to update is programming language dependant.
     *
     * @param source the component where data is read from
     * @param target the component where data is written
     */
    public abstract void updateComponent(Component source, Component target);

    // #################################################################################################################

    /**
     * Return the language specific unique coordinate of the specified {@code component} as defined in the language
     * specification. The default implementation adds up all component names up the tree separated with a dot.
     *
     * @param component the component (e.g. source class or method, etc.)
     * @return the unique coordinate
     */
    public String getParamUniqueCoordinate(Component component) {
        requireNonNull(component, "Parameter 'component' must not be NULL.");

        String uniqueCoordinate = component.getValue();
        if (component.hasParentAndParentIsNotRoot()) {
            uniqueCoordinate = getParamUniqueCoordinate(component.getParent())
                    .concat(JAVA.DELIMITER)
                    .concat(uniqueCoordinate);
        }

        return uniqueCoordinate;
    }

    /**
     * Merges the specified {@code component} with this application.<br>
     * To put it more precisely, the component is sorted into the right place in the tree.
     *
     * @param component the component
     */
    public void mergeWithApplication(Component component) {
        requireNonNull(component, "Parameter 'component' must not be NULL.");

        mergeComponent(component, components);
    }

    /**
     * Add the specified {@code library} to the libraries.
     *
     * @param library the library
     */
    public void addLibrary(Component library) {
        requireNonNull(library, "Parameter 'library' must not be NULL.");

        if (!libraries.contains(library)) {
            libraries.add(library);
        }
    }

    public List<Component> findAllComponentsByType(ComponentType componentType) {
        return components.findComponentsByType(componentType);
    }

    /**
     * Retrieves the component specified by {@code uniqueCoordinate} from the application and libraries.<br>
     * A unique coordinate is a string that uniquely identifies a component (e.g. java.lang.Integer).
     *
     * @param uniqueCoordinate the unique coordinate of the component
     * @return the component or NULL if no one is found
     */
    public Component findComponentByUniqueCoordinate(String uniqueCoordinate) {
        requireNonNull(uniqueCoordinate, PARAM_UNIQUE_COORDINATE_NOT_NULL);

        Component component = findApplicationComponentByUniqueCoordinate(uniqueCoordinate);
        if (isNull(component)) {
            component = findLibraryComponentByUniqueCoordinate(uniqueCoordinate);
        }
        return component;
    }

    /**
     * Retrieves the component specified by {@code value} from the application.<br>
     * A coordinate is a string that uniquely identifies a component (e.g. java.lang.Integer).
     *
     * @param uniqueCoordinate the unique coordinate of the component
     * @return the component or NULL if no one is found
     */
    public Component findApplicationComponentByUniqueCoordinate(String uniqueCoordinate) {
        requireNonNull(uniqueCoordinate, PARAM_UNIQUE_COORDINATE_NOT_NULL);

        return findComponentByUniqueCoordinate(components, uniqueCoordinate);
    }

    /**
     * Retrieves the component specified by {@code uniqueCoordinate} from the libraries of the application.<br>
     * A coordinate is a string that uniquely identifies a component (e.g. java.lang.Integer).
     *
     * @param uniqueCoordinate the unique coordinate of the component
     * @return the component or NULL if no one is found
     */
    public Component findLibraryComponentByUniqueCoordinate(String uniqueCoordinate) {
        requireNonNull(uniqueCoordinate, PARAM_UNIQUE_COORDINATE_NOT_NULL);

        return libraries.stream()
                .map(library -> findComponentByUniqueCoordinate(library, uniqueCoordinate))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    // #################################################################################################################

    /**
     * Add the children and attributes of the specified {@code component} into the pointer.
     *
     * @param component the component
     * @param pointer   the pointer to add the children and attributes
     */
    private void mergeComponent(Component component, Component pointer) {
        updateComponent(component, pointer);

        List<Component> children = component.getChildren();
        children.forEach(child -> {
            Component newPointer = pointer.findChildByComponent(child);
            if (isNull(newPointer)) {
                pointer.addChild(child);
            } else {
                // There can be more than one method or constructor with the same value let's add them
                mergeComponent(child, newPointer);
            }
        });
    }

    /**
     * Retrieves child component of {@code component} specified by {@code coordinate}.
     *
     * @param component  the component
     * @param coordinate the  coordinate of the component
     * @return the component or [NULL] if no one is found
     */
    private Component findComponentByUniqueCoordinate(Component component, String coordinate) {
        if (coordinate.contains(JAVA.DELIMITER)) {
            String[] values = coordinate.split(JAVA.DELIMITER_REGEX);
            for (String value : values) {
                component = component.findChildByCoordinate(value);
                if (isNull(component)) {
                    return null;
                }
            }
        } else {
            component = component.findChildByCoordinate(coordinate);
        }
        return component;
    }
}