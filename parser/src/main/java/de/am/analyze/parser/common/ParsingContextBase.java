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
package de.am.analyze.parser.common;

import de.am.analyze.common.component.Component;
import de.am.analyze.common.component.ComponentAttribute;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.am.analyze.common.component.type.ComponentType.ROOT;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

/**
 * {@code ParsingContextBase} is base class of all parsing contexts and holds the common information during the parsing process.
 *
 * @author Martin Absmeier
 */
@Data
@NoArgsConstructor
public class ParsingContextBase {

    /**
     * Component tree of the current compilation unit
     */
    private Component component = Component.builder().type(ROOT).value(ROOT.name()).build();

    /**
     * Component that is currently being parsed / edited
     */
    private Component currentComponent = component;

    /**
     * File path attribute of the current compilation unit
     */
    private ComponentAttribute currentFile;

    /**
     * Unique id of the source code e.g git commit id, it is used to compare source code.
     */
    private String revisionId;

    /**
     * The currently observable components. (e.g. Classes, interfaces and enumerations etc.)
     */
    protected List<Component> visibleComponents = new ArrayList<>();

    /**
     * Component whose children are visible. (e.g. libraries).The following packages are always observable for java:
     * <b>java</b>, <b>java.lang</b>, and <b>java.io</b>.
     */
    protected List<Component> componentsWithVisibleChildren = new ArrayList<>();

    public List<Component> getVisibleComponentsForValue(String value) {
        return visibleComponents.stream()
            .filter(cmp -> value.equals(cmp.getValue()))
            .collect(Collectors.toList());
    }

    public List<Component> getComponentsWithVisibleChildrenForValue(String value) {
        return componentsWithVisibleChildren.stream()
            .map(Component::getChildren)
            .flatMap(List::stream)
            .filter(cmp -> value.equals(cmp.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        String currentFileTxt = isNull(currentFile) ? "" : currentFile.getValue();
        String currentComponentTxt = isNull(currentComponent) ? "" : currentComponent.getUniqueCoordinate();
        return format("FILE: {0} | CURR-CMP: {1}", currentFileTxt, currentComponentTxt);
    }
}