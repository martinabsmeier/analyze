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

import lombok.Getter;

/**
 * {@code Type} is used to identify the characteristics of the base type.
 *
 * @author Martin Absmeier
 */
@Getter
public enum Type {

    BOOLEAN("boolean"),
    BYTE("byte"),
    SHORT("short"),
    INT("int"),
    LONG("long"),
    CHAR("char"),
    FLOAT("float"),
    DOUBLE("double");

    private final String name;

    Type(String name) {
        this.name = name;
    }
}