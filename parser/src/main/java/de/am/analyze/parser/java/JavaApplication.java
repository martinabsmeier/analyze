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
package de.am.analyze.parser.java;

import de.am.analyze.common.component.Component;
import de.am.analyze.parser.common.ApplicationBase;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * {@code JavaApplication} contains all data collected from the listeners.<br>
 * The graph one is generated / completed from the data collected by this java application.
 *
 * @author Martin Absmeier
 */

public class JavaApplication extends ApplicationBase {
    private static JavaApplication instance;

    /**
     * Creates a new instance of {@code JavaApplication} if necessary and returns it.
     *
     * @return the singleton instance
     */
    public static synchronized JavaApplication getInstance() {
        if (isNull(instance)) {
            instance = new JavaApplication();
        }
        return instance;
    }

    /**
     * Update the specified {@code target} component with the attributes of {@code source} component.
     *
     * @param source the component where data is read from
     * @param target the component where data is written
     */
    @Override
    public void updateComponent(Component source, Component target) {
        requireNonNull(source, "Parameter 'source' must not be NULL.");
        requireNonNull(target, "Parameter 'target' must not be NULL.");

        if (source.equals(target) && source.hasAttributes()) {
            source.getAttributes().forEach(attribute -> {
                if (!target.getAttributes().contains(attribute)) {
                    target.addAttribute(attribute);
                }
            });
        }
    }

    // #################################################################################################################
    private JavaApplication() {
    }
}