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
package de.am.analyze.common.component.filter;

import de.am.analyze.common.component.ComponentAttribute;

/**
 * {@code ComponentAttributeFilter} represents a {@link ComponentAttribute} filter function that accepts one argument
 * and produces a boolean.
 *
 * @author Martin Absmeier
 */
@FunctionalInterface
public interface ComponentAttributeFilter {

    /**
     * Applies this function to the given attribute.
     *
     * @param attribute the attribute
     * @return true if attribute should pass the filter, false otherwise
     */
    boolean apply(ComponentAttribute attribute);
}