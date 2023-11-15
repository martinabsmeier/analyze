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

import de.am.analyze.common.component.Component;
import de.am.analyze.parser.common.type.enums.BaseTypeEnum;

import java.util.Collections;
import java.util.List;

/**
 * {@code BaseType} is the base class of all type representations.
 *
 * @author Martin Absmeier
 */
public abstract class BaseType {

    private final BaseTypeEnum type;

    protected BaseType(BaseTypeEnum type) {
        this.type = type;
    }

    // #################################################################################################################

    /**
     * Convert SourceType structure into one normalized string representation.
     * TODO Add documentation
     *
     * @return the unique type identifier
     */
    public abstract String getUniqueIdentifier();

    /**
     * Checks if an upcast to type specified by {@code type} is possible.
     *
     * @param type the type to check
     * @return true if an upcast is possible false otherwise
     */
    public abstract boolean canUpcast(BaseType type);

    /**
     * Return the name of the type.
     *
     * @return the name of the type
     */
    public abstract String getName();

    // #################################################################################################################

    /**
     * Get the base type.
     *
     * @return the base type
     */
    public BaseTypeEnum getBaseType() {
        return type;
    }

    /**
     * For template parameters we may need complex logic to find out all class or interface types that the template
     * parameter implements. We may even see cases where a template parameter implements a different one, this requiring
     * recursive search. This method encapsulates this logic. For types that are not class, interface or generic types
     * an empty list is returned (implemented here)
     *
     * @return the list with the {@link BaseType}
     */
    public List<BaseType> findAllImplementedClassesOrInterfaces() {
        return Collections.emptyList();
    }

    /**
     * Return the related component.<br>
     * For determining the type during graph creation we need to be able to convert a base type into its contained
     * component in order to search onwards from there.
     *
     * @return the related component or NULL if no one is found
     */
    public Component getRelatedComponent() {
        return null;
    }
}