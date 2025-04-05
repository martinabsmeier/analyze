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
package de.am.analyze.common.component;

import de.am.analyze.common.AnalyzeConstants.JAVA;
import de.am.analyze.common.component.type.ComponentAttributeType;
import de.am.analyze.common.component.type.ComponentType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.am.analyze.common.component.type.ComponentAttributeType.JAVA_RETURN_TYPE;
import static de.am.analyze.common.component.type.ComponentType.*;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * {@code Component} represents a node in the component tree.
 *
 * @author Martin Absmeier
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Component implements Serializable {
    @Serial
    private static final long serialVersionUID = -2950087032826957721L;

    private static final String PARAM_VALUE_NOT_NULL = "Parameter 'value' must not be NULL.";
    private static final String PARAM_TYPE_NOT_NULL = "Parameter 'type' must not be NULL.";

    /**
     * The parent of this component
     */
    @EqualsAndHashCode.Exclude
    private Component parent;

    /**
     * The type of this component
     */
    private ComponentType type;

    /**
     * The value of this component
     */
    private String value;

    /**
     * The checksum of this component
     */
    private String checksum;

    /**
     * The children of this component
     */
    @EqualsAndHashCode.Exclude
    private List<Component> children;

    /**
     * The attributes of this component
     */
    @EqualsAndHashCode.Exclude
    private List<ComponentAttribute> attributes;

    /**
     * Create a new instance specified by {@code type} and {@code value}.
     *
     * @param type  the {@link ComponentType} of the component
     * @param value the value of the component
     */
    @Builder
    public Component(ComponentType type, String value) {
        requireNonNull(type, PARAM_TYPE_NOT_NULL);
        requireNonNull(value, PARAM_VALUE_NOT_NULL);
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    // #################################################################################################################
    // Methods for children

    /**
     * Add a child specified by {@code child} to the {@link Component}.
     *
     * @param child the child to be added
     */
    public void addChild(Component child) {
        requireNonNull(child, "Parameter 'child' must not be NULL.");

        child.setParent(this);
        children.add(child);
    }

    /**
     * Retrieves all children of a {@link Component} specified by {@code value}.
     *
     * @param value the value of the children
     * @return the children or an empty list if no one matches the value
     */
    public List<Component> findChildrenByValue(String value) {
        requireNonNull(value, PARAM_VALUE_NOT_NULL);

        return children.stream()
                .filter(child -> value.equals(child.getValue()))
                .toList();
    }

    /**
     * Retrieves the child specified by {@code coordinate} from this component.
     *
     * @param coordinate the coordinate of the child
     * @return the child of the component or NULL if no one is found
     */
    public Component findChildByCoordinate(String coordinate) {
        requireNonNull(coordinate, "Parameter 'coordinate' must not be NULL.");

        return children.stream()
                .filter(child -> {
                    // Added: if we are not looking for something including the # sign we also only look at
                    // the value and not the value concatenated with # and the checksum
                    if (hasChecksum() && coordinate.contains("#")) {
                        return coordinate.equals(child.getValue()
                                .concat("#")
                                .concat(child.getChecksum()));
                    } else {
                        return coordinate.equals(child.getValue());
                    }
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all children of the {@code component} with the specified {@code type}.
     *
     * @param type the type of the children
     * @return list with all children matching the type or an empty list if no child matches
     */
    public List<Component> findChildrenByType(ComponentType type) {
        requireNonNull(type, PARAM_TYPE_NOT_NULL);

        return getChildren().stream()
                .filter(child -> child.isType(type))
                .toList();
    }

    /**
     * Retrieves the first child specified by {@code component} by calling equals method.
     *
     * @param component the component
     * @return the first child of the component or NULL if no one is found
     */
    public Component findChildByComponent(Component component) {
        requireNonNull(component, "Parameter 'component' must not be NULL.");

        return children.stream()
                .filter(child -> child.equals(component))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks whether this {@link Component} knows a child specified by {@code component} or not.
     *
     * @param component the child component
     * @return true if the component does not know the child, otherwise false
     */
    public boolean childrenNotContains(Component component) {
        return !children.contains(component);
    }

    /**
     * Checks if this component has children.
     *
     * @return true if this component has children false otherwise
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    // #################################################################################################################
    // Methods for attributes

    /**
     * Add an attribute specified by {@code attribute} to the {@link Component}.<br>
     *
     * @param attribute the attribute to be added
     */
    public void addAttribute(ComponentAttribute attribute) {
        requireNonNull(attribute, "Parameter 'attribute' must not be NULL.");

        attributes.add(attribute);
    }

    /**
     * Retrieves all attributes of the {@code component} specified by {@code type}.
     *
     * @param type the type of the attributes
     * @return list with all attributes matching the type or an empty list if no one matches
     */
    public List<ComponentAttribute> findAttributesByType(ComponentAttributeType type) {
        requireNonNull(type, PARAM_TYPE_NOT_NULL);

        return getAttributes().stream()
                .filter(attribute -> type.equals(attribute.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all attributes of the {@code component} specified by {@code type} and {@code value}.
     *
     * @param type  the type of the attributes
     * @param value the value of the attribute
     * @return all attributes matching the type and value
     */
    public List<ComponentAttribute> findAttributesByTypeAndValue(ComponentAttributeType type, String value) {
        requireNonNull(type, PARAM_TYPE_NOT_NULL);
        requireNonNull(value, PARAM_VALUE_NOT_NULL);

        return findAttributesByType(type).stream()
                .filter(attribute -> value.equals(attribute.getValue()))
                .toList();
    }

    /**
     * Check if this {@link Component} has attributes.
     *
     * @return true if the component has attributes, false otherwise
     */
    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    // #################################################################################################################

    /**
     * Retrieves the first parent specified by {@code type}.
     *
     * @param type the type of the parent
     * @return the parent or NULL if no one is found
     */
    public Component findFirstParentByType(ComponentType type) {
        requireNonNull(type, PARAM_TYPE_NOT_NULL);

        return findParentByType(this, type);
    }

    /**
     * Return the parent of this {@link Component}.
     *
     * @return all parents of the component or NULL if there is no parent
     */
    // @JsonIgnore
    public List<Component> getParents() {
        List<Component> parents = new ArrayList<>();

        if (hasParentAndParentIsNotRoot()) {
            Component parentOfThis = getParent();
            parents.add(parentOfThis);
            parents.addAll(parentOfThis.getParents());
        }

        return parents;
    }

    /**
     * Checks if the component has a parent and the parent is not of type {@link ComponentType#ROOT}.
     *
     * @return true if the component has a parent and is not root false otherwise
     */
    public boolean hasParent() {
        return nonNull(getParent());
    }

    /**
     * Checks if the component has a parent and the parent is not of type {@link ComponentType#ROOT}, {@link ComponentType#APP_ROOT}
     * or {@link ComponentType#LIB_ROOT}.
     *
     * @return true if the component has a parent and is not root false otherwise
     */
    public boolean hasParentAndParentIsNotRoot() {
        return hasParent() && !getParent().isType(ROOT) && !getParent().isType(APP_ROOT) && !getParent().isType(LIB_ROOT);
    }

    // #################################################################################################################
    // Default implementation

    /**
     * Checks whether this {@link Component} is of type specified by {@code type}.
     *
     * @param type the type of the component
     * @return true if this component is of the specified type, false otherwise
     */
    public boolean isType(ComponentType type) {
        requireNonNull(type, PARAM_TYPE_NOT_NULL);

        return type.equals(this.type);
    }

    /**
     * Checks whether the {@code checksum} of this component is not NULL or empty.
     *
     * @return true if checksum is not NULL or empty, false otherwise
     */
    public boolean hasChecksum() {
        return nonNull(checksum) && !checksum.isEmpty();
    }

    /**
     * Get the coordinate of this {@link Component}.
     *
     * @return the coordinate of this component
     */
    public String getUniqueCoordinate() {
        String coordinate = getValue();

        if (hasParentAndParentIsNotRoot()) {
            return getParent().getUniqueCoordinate()
                    .concat(JAVA.DELIMITER)
                    .concat(coordinate);
        }

        return coordinate;
    }

    /**
     * Starting from this component, the children are searched recursively and if they are of {@code type}, they are
     * added to the result list.
     *
     * @param type the type of the components
     * @return all component of the searched type
     */
    public List<Component> findComponentsByType(ComponentType type) {
        List<Component> components = new ArrayList<>();

        if (isType(type)) {
            components.add(this);
        }

        if (hasChildren()) {
            for (Component child : children) {
                components.addAll(child.findComponentsByType(type));
            }
        }

        return components;
    }

    @Override
    public String toString() {
        return getType().name().concat(" -> ").concat(getValue());
    }

    // #################################################################################################################

    private Component findParentByType(Component component, ComponentType type) {
        if (component.hasParentAndParentIsNotRoot()) {
            Component parentOfComponent = component.getParent();
            if (parentOfComponent.isType(type)) {
                return parentOfComponent;
            }
            return findParentByType(parentOfComponent, type);
        }
        return null;
    }

    private boolean isMethod() {
        return JAVA_METHOD.equals(getType());
    }

    private boolean hasReturnType() {
        return nonNull(getReturnType());
    }

    private ComponentAttribute getReturnType() {
        return getAttributes().stream()
                .filter(att -> JAVA_RETURN_TYPE.equals(att.getType()))
                .findFirst()
                .orElse(null);
    }
}