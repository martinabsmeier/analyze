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

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * {@code ComponentWrapper} isThe standard equals and hash code of components only look locally at their name but not at the unique components.
 * This is needed because the equals method is already called when building up a tree so in this stage comparisons
 * would fail. For this reason there is an artificial class UniqueComponentWrapper that contains a previously
 * initialized component and adds equals and hash code based on the unique components
 *
 * @author Martin Absmeier
 */
@AllArgsConstructor
public class ComponentWrapper {
    private final Component component;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (Objects.isNull(other) || getClass() != other.getClass()) {
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