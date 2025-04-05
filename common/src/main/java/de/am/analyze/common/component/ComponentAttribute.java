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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * {@code ComponentAttribute} maps additional attributes of a component.
 *
 * @author Martin Absmeier
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ComponentAttribute implements Serializable {
    @Serial
    private static final long serialVersionUID = 4082428288136185574L;

    /** The type of the component attribute */
    private ComponentAttributeType type;
    /** The value of the component attribute */
    private String value;

    /**
     * Creates a new instance of {@code ComponentAttribute}.
     *
     * @param type  the type of the component attribute
     * @param value the value of the component attribute
     */
    @Builder
    public ComponentAttribute(ComponentAttributeType type, String value) {
        requireNonNull(type, "Parameter 'type' must not be NULL.");
        requireNonNull(value, "Parameter 'value' must not be NULL.");
        this.type = type;
        this.value = value;
    }

    /**
     * Checks whether the attribute is of the specified type.
     *
     * @param type the type of the attribute
     * @return true if the attribute is of the specified type, false otherwise
     */
    public boolean isType(ComponentAttributeType type) {
        requireNonNull(type, "Parameter 'type' must not be NULL.");

        return type.equals(getType());
    }

    @Override
    public String toString() {
        return type.name().concat(" -> ").concat(value);
    }
}