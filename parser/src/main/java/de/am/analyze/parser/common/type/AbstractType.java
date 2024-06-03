/*
 * Copyright 2023 Martin Absmeier
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
package de.am.analyze.parser.common.type;

/**
 * {@code AbstractType} is the base class of all type representations.
 *
 * @author Martin Absmeier
 */
public abstract class AbstractType {

    private final Type type;

    protected AbstractType(Type type) {
        this.type = type;
    }

    // #################################################################################################################

    public abstract String getCoordinate();

    public abstract boolean isPrimitive();

    public abstract boolean canUpcast(AbstractType type);

    public abstract String getName();

    // #################################################################################################################

    /**
     * Get the base type.
     *
     * @return the base type
     */
    public Type getType() {
        return type;
    }

}