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

/**
 * {@code ComponentType} enumeration of the component types.
 *
 * @author Martin Absmeier
 */
public enum ComponentType {

    /**
     * The root element of a component tree.
     */
    ROOT,
    /**
     * The root element of an application component tree.
     */
    APP_ROOT,
    /**
     * The root element of a library component tree.
     */
    LIB_ROOT,
    /**
     * Is used for file based component trees. (e.g. ini and xml)
     */
    SOURCE_NAME,

    // #################################################################################################################
    // Java types

    /**
     * Programs are organized as sets of packages. Each package has its own set of names for types, which helps to prevent
     * name conflicts. A top level type is accessible outside the package that declares it only if the type is declared public.
     */
    JAVA_PACKAGE,
    /**
     * A single type import declaration imports a single named type, by mentioning its canonical name.<br>
     * <b>Example:</b> import java.util.Vector;
     */
    JAVA_IMPORT,
    /**
     * A type import on demand declaration imports all the accessible types of a named type or named package as needed,
     * by mentioning the canonical name of a type or package.<br>
     * <b>Example:</b> import java.util.*;
     */
    JAVA_IMPORT_ON_DEMAND,
    /**
     * A single static import declaration imports all accessible static members with a given simple name from a type.
     * This makes these static members available under their simple name in the class and interface declarations of the
     * compilation unit in which the single static import declaration appears.<br>
     * <b>Example:</b> import static java.util.Objects.nonNull;
     */
    JAVA_IMPORT_STATIC,
    /**
     * A static import on demand declaration allows all accessible static members of a named type to be imported as
     * needed.<br>
     * <b>Example:</b> import static java.util.Objects.*;
     */
    JAVA_IMPORT_STATIC_ON_DEMAND,
    /**
     * An annotation type declaration specifies a new annotation type, a special kind of interface type. To distinguish
     * an annotation type declaration from a normal interface declaration, the keyword interface is preceded by an at-sign (@).
     */
    JAVA_ANNOTATION,
    /**
     * Class declarations define new reference types and describe how they are implemented. A top level class is a class
     * that is not a nested class.
     */
    JAVA_CLASS,
    /**
     * An interface declaration introduces a new reference type whose members are classes, interfaces, constants, and methods.
     * This type has no instance variables, and typically declares one or more abstract methods; otherwise unrelated classes
     * can implement the interface by providing implementations for its abstract methods.
     * Interfaces may not be directly instantiated.
     * A nested interface is any interface whose declaration occurs within the body of another class or interface.
     */
    JAVA_INTERFACE,
    /**
     * An enum declaration specifies a new enum type, a special kind of class type.
     */
    JAVA_ENUM,
    /**
     * The body of an enum declaration may contain enum constants. An enum constant defines an instance of the enum type.
     */
    JAVA_ENUM_CONSTANT,
    /**
     * A constructor is used in the creation of an object that is an instance of a class.
     */
    JAVA_CONSTRUCTOR,
    /**
     * A method declares executable code that can be invoked, passing a fixed number of values as arguments.
     */
    JAVA_METHOD,
    /**
     * The formal parameters of a method or constructor, if any, are specified by a list of comma-separated parameter
     * specifiers. Each parameter specifier consists of a type (optionally preceded by the final modifier and/or one or
     * more annotations) and an identifier (optionally followed by brackets) that specifies the name of the parameter.
     * If a method or constructor has no formal parameters, only an empty pair of parentheses appears in the declaration
     * of the method or constructor.
     */
    JAVA_PARAMETER,
    /**
     * The variables of a class type (include interfaces) are introduced by field declarations.<br>
     * Each declarator in a <i>FieldDeclaration</i> declares one field. The Identifier in a declarator may be used in a
     * name to refer to the field. More than one field may be declared in a single <i>FieldDeclaration</i> by using more
     * than one declarator; the FieldModifiers and UnannType apply to all the declarators in the declaration.
     * Every field declaration in the body of an interface is implicitly public, static, and final. It is permitted to
     * redundantly specify any or all of these modifiers for such fields.
     */
    JAVA_FIELD,
    /**
     * When we specialize a generic type (like e.g. in {@code List<String>)} we add the specialization as child to the component.
     * This way also the specialized types will have a unique node in the component tree and we can avoid duplicates
     * We could use individual attributes with the string representation of the comma separated type list only but that
     * would break our design principle that every non-primitive type corresponds to exactly one node in the component tree
     */
    JAVA_SPECIALIZATION_GROUP,
    /**
     * Each type specialization (e.g. String and Long from {@code Map<String, Long>)} gets an entry in the component representing
     * the combination of all specializations.
     */
    JAVA_SPECIALIZATION,
    /**
     * A class or interface declaration that is generic defines a set of parameterized types.<br>
     * A parameterized type is a class or interface type of the form {@code C<T1,...,Tn>}, where C is the name of a generic
     * type and {@code <T1,...,Tn>} is a list of type arguments that denote a particular parameterization of the generic type.
     */
    JAVA_PARAMETERIZED_TYPE
}