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
 * {@code PrimitiveType} represents a primitive programming language independent type.
 *
 * @author Martin Absmeier
 */
public class PrimitiveType extends AbstractType {

    public PrimitiveType(Type type) {
        super(type);
    }

    @Override
    public String getCoordinate() {
        return "Primitive(".concat(getName()).concat(")");
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public boolean canUpcast(AbstractType type) {
        return false;
    }

    @Override
    public String getName() {
        return getType().getName();
    }
}