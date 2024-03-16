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

import de.am.analyze.parser.common.type.enums.BaseTypeEnum;
import de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static de.am.analyze.parser.common.type.enums.BaseTypeEnum.PRIMITIVE;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

/**
 * {@code PrimitiveType} represents a primitive programming language independent type.
 *
 * @author Martin Absmeier
 */
@EqualsAndHashCode(callSuper = true)
public class PrimitiveType extends AbstractType {

    @Getter
    private final PrimitiveTypeEnum type;

    /**
     * Creates a new instance
     *
     * @param type the primitive type
     */
    public PrimitiveType(PrimitiveTypeEnum type) {
        super(PRIMITIVE);
        this.type = type;
    }

    @Override
    public String getUniqueIdentifier() {
        return getName();
    }

    @Override
    public boolean canUpcast(AbstractType type) {
        if (isNull(type)) {
            return false;
        }

        BaseTypeEnum baseType = type.getBaseType();
        if (!PRIMITIVE.equals(baseType)) {
            return false;
        }

        PrimitiveType other = (PrimitiveType) type;
        PrimitiveTypeEnum otherType = other.getType();
        switch (getType()) {
            case BOOLEAN:
                // Booleans can only be cast to Booleans.
                return PrimitiveTypeEnum.BOOLEAN.equals(otherType);

            case CHAR:
                switch (otherType) {
                    case BYTE:
                    case SHORT:
                    case INT:
                    case LONG:
                    case FLOAT:
                    case DOUBLE:
                        return true;
                    default:
                        return false;
                }

            case SHORT:
                switch (otherType) {
                    case SHORT:
                    case INT:
                    case LONG:
                    case FLOAT:
                    case DOUBLE:
                        return true;
                    default:
                        return false;
                }

            case INT:
                switch (otherType) {
                    case INT:
                    case LONG:
                    case FLOAT:
                    case DOUBLE:
                        return true;
                    default:
                        return false;
                }

            case LONG:
                switch (otherType) {
                    case LONG:
                    case FLOAT:
                    case DOUBLE:
                        return true;
                    default:
                        return false;
                }

            case FLOAT:
                switch (otherType) {
                    case FLOAT:
                    case DOUBLE:
                        return true;
                    default:
                        return false;
                }

            case DOUBLE:
                return PrimitiveTypeEnum.DOUBLE.equals(otherType);

            default:
                return false;
        }
    }

    @Override
    public String getName() {
        return format(getBaseType().getName(), type.getName());
    }
}