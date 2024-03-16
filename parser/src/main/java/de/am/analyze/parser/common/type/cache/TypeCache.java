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
package de.am.analyze.parser.common.type.cache;

import de.am.analyze.parser.common.type.AbstractType;
import de.am.analyze.parser.common.type.PrimitiveType;
import lombok.Synchronized;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * {@code TypeCache} ensures that there is always only one instance of a {@link AbstractType}. Without this cache, each method
 * of a class that uses int parameters would have its own instance of a {@link PrimitiveType}.<br>
 * {@code TypeCache} checks whether the instance of a type is already known, if so then it is returned, otherwise a new
 * one is created and returned.
 *
 * @author Martin Absmeier
 */
public class TypeCache {
    // The singleton instance of the type cache
    private static TypeCache instance;

    /**
     * Cache of the types of a programming language.<br>
     * The key of the map is the unique identifier of the type.
     */
    private final Map<String, AbstractType> cache = new HashMap<>();

    /**
     * Return the singleton instance of {@code TypeCache}.
     *
     * @return the TypeCache instance
     */
    @Synchronized
    public static TypeCache getInstance() {
        if (isNull(instance)) {
            instance = new TypeCache();
        }
        return instance;
    }

    /**
     * Checks whether the type is already known if so, it is returned, otherwise it is added to the cache and then returned.
     *
     * @param type the type
     * @return the singleton type
     */
    @Synchronized
    public AbstractType getType(AbstractType type) {
        if (isNull(type)) {
            return null;
        }

        String key = type.getUniqueIdentifier();
        return cache.computeIfAbsent(key, value -> type);
    }

    /**
     * Retrieves the {@link AbstractType} specified by {@code uniqueTypeIdentifier} from the cache.
     *
     * @param uniqueIdentifier the unique identifier of the type
     * @return the base type or NULL if no base type is found
     */
    @Synchronized
    public AbstractType findBaseTypeByUniqueIdentifier(String uniqueIdentifier) {
        if (isNull(uniqueIdentifier) || uniqueIdentifier.isEmpty()) {
            return null;
        }

        return cache.get(uniqueIdentifier);
    }

    // #################################################################################################################
    private TypeCache() {
        // We want a singleton
    }
}