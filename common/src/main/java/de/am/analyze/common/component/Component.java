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

import de.am.analyze.common.component.type.ComponentAttributeType;
import de.am.analyze.common.component.type.ComponentType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.am.analyze.common.AnalyzeConstants.DEFAULT_DELIMITER;
import static de.am.analyze.common.component.type.ComponentType.APP_ROOT;
import static de.am.analyze.common.component.type.ComponentType.LIB_ROOT;
import static de.am.analyze.common.component.type.ComponentType.ROOT;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * {@code ComponentNode} represents a node in the component tree.
 *
 * @author Martin Absmeier
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Component implements Serializable {
    private static final long serialVersionUID = -2950087032826957721L;

    @EqualsAndHashCode.Exclude
    private Component parent;
    @EqualsAndHashCode.Exclude
    private List<Component> children = new ArrayList<>();
    private ComponentType type;
    private String value;
    private String checksum;
    @EqualsAndHashCode.Exclude
    private List<ComponentAttribute> attributes = new ArrayList<>();

    /**
     * Create a new instance specified by {@code type} and {@code value}.
     *
     * @param type  the {@link ComponentType} of the component
     * @param value the value of the component
     */
    @Builder
    public Component(ComponentType type, String value) {
        this.type = type;
        this.value = value;
    }

    // #################################################################################################################

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
     * @param componentValue the value of the children
     * @return the children or an empty list if no one matches the value
     */
    public List<Component> findChildrenByValue(String componentValue) {
        requireNonNull(componentValue, "Parameter 'componentValue' must not be NULL.");

        return children.stream()
            .filter(child -> componentValue.equals(child.getValue()))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all children of the {@code component} with the specified {@code componentType}.
     *
     * @param componentType the type of the children
     * @return list with all children matching the type or an empty list if no child matches
     */
    public List<Component> findChildrenByType(ComponentType componentType) {
        requireNonNull(componentType, "Parameter 'componentType' must not be NULL.");

        return getChildren().stream()
            .filter(child -> child.isType(componentType))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the child specified by {@code componentValue} from the children.
     *
     * @param componentValue the value of the child
     * @return the child of the component or NULL if no one is found
     */
    public Component findChildByValue(String componentValue) {
        requireNonNull(componentValue, "Parameter 'componentValue' must not be NULL.");

        return children.stream()
            .filter(child -> {
                String checksum = child.getChecksum();
                // Added: if we are not looking for something including the # sign we also only look at
                // the value and not the value concatenated with # and the checksum
                if (nonNull(checksum) && !checksum.isEmpty() && componentValue.contains("#")) {
                    return componentValue.equals(child.getValue()
                                                     .concat("#")
                                                     .concat(checksum));
                } else {
                    return componentValue.equals(child.getValue());
                }
            })
            .findFirst()
            .orElse(null);
    }

    /**
     * Retrieves the child specified by {@code component} by calling equals method.
     *
     * @param component the component
     * @return the child of the component or NULL if no one is found
     */
    public Component findChild(Component component) {
        requireNonNull(component, "Parameter 'component' must not be NULL.");

        return children.stream()
            .filter(child -> child.equals(component))
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks whether the {@link Component} knows a child specified by {@code component} or not.
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

    /**
     * Add an attribute specified by {@code attribute} to the {@link Component}.<br>
     *
     * @param componentAttribute the attribute to be added
     */
    public void addAttribute(ComponentAttribute componentAttribute) {
        requireNonNull(componentAttribute, "Parameter 'componentAttribute' must not be NULL.");

        attributes.add(componentAttribute);
    }

    /**
     * Retrieves all attributes of the {@code component} specified by {@code attributeType}.
     *
     * @param attributeType the type of the attributes
     * @return list with all attributes matching the type or an empty list if no one matches
     */
    public List<ComponentAttribute> findAttributesByType(ComponentAttributeType attributeType) {
        requireNonNull(attributeType, "Parameter 'attributeType' must not be NULL.");

        return getAttributes().stream()
            .filter(attribute -> attributeType.equals(attribute.getType()))
            .collect(Collectors.toList());
    }

    /**
     * Check if this {@link Component} has attributes.
     *
     * @return true if the component has attributes, false otherwise
     */
    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    /**
     * Checks if the {@code component} has an attribute specified by {@code attributeType} and {@code value}.
     *
     * @param attributeType the type of the attributes
     * @param value         the value of the attribute
     * @return true if the component has an attribute otherwise false
     */
    public boolean hasAttributeWithTypeAndValue(ComponentAttributeType attributeType, String value) {
        requireNonNull(attributeType, "Parameter 'attributeType' must not be NULL.");
        requireNonNull(value, "Parameter 'value' must not be NULL.");

        if (!hasAttributes()) {
            return false;
        }

        return attributes.contains(ComponentAttribute.builder()
                                       .type(attributeType)
                                       .value(value)
                                       .build());
    }

    // #################################################################################################################

    /**
     * Retrieves the first parent specified by {@code componentType}.
     *
     * @param componentType the type of the parent
     * @return the parent or NULL if no one is found
     */
    public Component findParentByType(ComponentType componentType) {
        requireNonNull(componentType, "Parameter 'componentType' must not be NULL.");

        return findParentByType(this, componentType);
    }

    /**
     * Return the parent of this {@link Component}.
     *
     * @return parent component or NULL if there is no parent
     */
    // @JsonIgnore
    public List<Component> getParents() {
        List<Component> parents = new ArrayList<>();

        if (hasParentAndParentIsNotRoot()) {
            Component parent = getParent();
            parents.add(parent);
            parents.addAll(parent.getParents());
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

    /**
     * Checks whether this {@link Component} is of type specified by {@code type}.
     *
     * @param type the type
     * @return true if this component is of the specified type, false otherwise
     */
    public boolean isType(ComponentType type) {
        requireNonNull(type, "Parameter 'type' must not be NULL.");

        return type.equals(getType());
    }

    /**
     * Get the unique coordinate of this {@link Component}.
     *
     * @return the unique coordinate
     */
    public String getUniqueCoordinate() {
        String componentName = getValue();

        if (hasParentAndParentIsNotRoot()) {
            if (nonNull(getChecksum()) && !getChecksum().isEmpty()) {
                componentName = componentName.concat("#").concat(getChecksum());
            }
            return getParent().getUniqueCoordinate()
                .concat(DEFAULT_DELIMITER)
                .concat(componentName);
        }

        return componentName;
    }

    @Override
    public String toString() {
        return getType().name()
            .concat(" -> ")
            .concat(getValue());
    }

    // #################################################################################################################
    private Component findParentByType(Component component, ComponentType type) {
        if (component.hasParentAndParentIsNotRoot()) {
            Component parent = component.getParent();
            if (parent.isType(type)) {
                return parent;
            }
            return findParentByType(parent, type);
        }
        return null;
    }
}