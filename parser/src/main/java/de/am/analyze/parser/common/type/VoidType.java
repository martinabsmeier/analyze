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

import lombok.EqualsAndHashCode;

import static de.am.analyze.parser.common.type.enums.BaseTypeEnum.VOID;

/**
 * {@code VoidType} is the return type of function that returns normally, but does not provide a result value to
 * its caller.
 *
 * @author Martin Absmeier
 */
@EqualsAndHashCode(callSuper = true)
public class VoidType extends BaseType {

    /**
     * Creates a new instance
     */
    public VoidType() {
        super(VOID);
    }

    @Override
    public String getUniqueIdentifier() {
        return getName();
    }

    @Override
    public boolean canUpcast(BaseType type) {
        // You can not cast void to anything
        return false;
    }

    @Override
    public String getName() {
        return getBaseType().getName();
    }
}