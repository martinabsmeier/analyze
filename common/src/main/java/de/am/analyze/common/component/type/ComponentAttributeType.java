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
package de.am.analyze.common.component.type;

import de.am.analyze.common.component.Component;

/**
 * Enumeration of the attribute types of a {@link Component}.
 *
 * @author Martin Absmeier
 */
public enum ComponentAttributeType {

    // #################################################################################################################
    // Common attributes

    /**
     * The code snippet starts at this line.
     */
    LINE,
    /**
     * The code snippet starts at this column.
     */
    COLUMN,
    /**
     * The name of the source. e.g. the name of the file
     */
    SOURCE_NAME,

    // #################################################################################################################
    // Java attributes

    /**
     * The name of the package to which the component belongs.
     */
    JAVA_PACKAGE_NAME,
    /**
     * The component is annotated with the specified annotation.
     */
    JAVA_ANNOTATED,
    /**
     * The optional implements' clause in a class declaration lists the names of interfaces that are direct
     * superinterfaces of the class being declared.
     */
    JAVA_IMPLEMENTS,
    /**
     * The optional extends clause in a normal class or interface declaration specifies the direct superclass of the
     * current class or interface.
     */
    JAVA_EXTENDS,
    /**
     * A modifier defines the type of a variable or parameter, this can be also the return type.
     */
    JAVA_MODIFIER,
    /**
     * The type of declared field, parameter or parametrized type.
     */
    JAVA_TYPE,
    /**
     * Assignment contexts allow the value of an expression to be assigned to a variable; the type of the expression
     * must be converted to the type of the variable.
     */
    JAVA_ASSIGNMENT,
    /**
     * The return type of method.
     */
    JAVA_RETURN_TYPE,
    /**
     * Ellipsis (...) is a token unto itself. It is possible to put whitespace between it and the type, but this is
     * discouraged as a matter of style.
     */
    JAVA_ELLIPSIS,
    /**
     * A local class is a nested class that is not a member of any class and that has a name.<br>
     * A class defined in a local context. All local classes are inner classes
     */
    JAVA_LOCAL_CLASS,
    /**
     * Every graph has an entry point - the first node in the graph is a CALL_CONTEXT node. For linking we remember the
     * id of this node in the component
     */
    JAVA_CALL_CONTEXT_ID,
    /**
     * We keep both directions of inheritance relations in the tree - a class already contains its parents and now we
     * also keep the other direction, i.e. a parent class will list all of the classes that derive from it for easier
     * navigation also in view of a non-fully loaded component tree. This is used to resolve virtual calls only so we
     * do not apply it for e.g. type bounds
     */
    JAVA_EXTENDING_CHILD_TYPE,
    /**
     * Same for the implements relation
     */
    JAVA_IMPLEMENTING_CHILD_TYPE,
    /**
     * The formal signature of a method excluding its class. Used to find matching methods once we reached the class
     */
    JAVA_SIGNATURE,
    /**
     * Every graph has an exit point - the first node in the graph is a RETURN_CONTEXT node. For linking we remember
     * the id of this node in the component
     */
    JAVA_RETURN_CONTEXT_ID
}