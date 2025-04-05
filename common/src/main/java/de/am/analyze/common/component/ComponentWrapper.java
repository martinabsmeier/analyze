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

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * {@code ComponentWrapper} overwrites the method {@link Component#equals(Object)} and {@link Component#hashCode()} of
 * {@link Component} because it has to be compared with the {@link Component#getUniqueCoordinate()} and not with the name.
 *
 * @author Martin Absmeier
 */
public class ComponentWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = -740592238817223488L;

    /** The wrapped component */
    private final Component component;

    /**
     * Creates a new instance of {@link ComponentWrapper} class.
     *
     * @param component the wrapped component
     */
    @Builder
    public ComponentWrapper(Component component) {
        requireNonNull(component, "Parameter 'component' must not be NULL.");
        this.component = component;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (isNull(other) || getClass() != other.getClass()) {
            return false;
        }
        ComponentWrapper otherComponent = (ComponentWrapper) other;
        return Objects.equals(component.getUniqueCoordinate(), otherComponent.component.getUniqueCoordinate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(component.getUniqueCoordinate());
    }

    @Override
    public String toString() {
        return component.getUniqueCoordinate();
    }
}