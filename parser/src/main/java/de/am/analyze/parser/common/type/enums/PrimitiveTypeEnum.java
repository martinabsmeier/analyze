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
package de.am.analyze.parser.common.type.enums;

import lombok.Data;

/**
 * {@code PrimitiveType} represents a primitive types.
 *
 * @author Martin Absmeier
 */
public enum PrimitiveTypeEnum {

    BOOLEAN("boolean"),
    DOUBLE("double"),
    FLOAT("float"),
    LONG("long"),
    INT("integer"),
    SHORT("short"),
    CHAR("character"),
    BYTE("byte");

    private final String name;

    PrimitiveTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}