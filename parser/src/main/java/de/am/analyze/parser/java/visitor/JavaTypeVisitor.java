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
package de.am.analyze.parser.java.visitor;

import de.am.analyze.parser.common.type.ArrayType;
import de.am.analyze.parser.common.type.BaseType;
import de.am.analyze.parser.common.type.PrimitiveType;
import de.am.analyze.parser.common.type.VoidType;
import de.am.analyze.parser.common.type.cache.TypeCache;
import de.am.analyze.parser.common.type.enums.PrimitiveTypeEnum;
import de.am.analyze.generated.parser.java.JavaParser.PrimitiveTypeContext;
import de.am.analyze.generated.parser.java.JavaParser.TypeTypeContext;
import de.am.analyze.generated.parser.java.JavaParser.TypeTypeOrVoidContext;
import de.am.analyze.parser.java.JavaParsingContext;

import static java.util.Objects.nonNull;

/**
 * {@code JavaTypeVisitor} is responsible for determining the types e.g. {@link BaseType}.
 *
 * @author Ahmed Mian Syed
 */
public class JavaTypeVisitor extends JavaBaseVisitor<BaseType> {

    private final TypeCache typeCache;

    /**
     * Create a new instance of {@code JavaTypeVisitor} with specified {@code parsingContext}.
     *
     * @param parsingContext the parsing context of the visitor
     */
    public JavaTypeVisitor(JavaParsingContext parsingContext) {
        super(parsingContext);
        this.typeCache = TypeCache.getInstance();
    }

    // #################################################################################################################

    @Override
    public BaseType visitTypeTypeOrVoid(TypeTypeOrVoidContext ctx) {
        if (ctx.VOID() != null) {
            return typeCache.getType(new VoidType());
        }
        return ctx.typeType().accept(this);
    }

    @Override
    public BaseType visitTypeType(TypeTypeContext ctx) {
        BaseType baseType = null;

        if (nonNull(ctx.classOrInterfaceType())) {
            baseType = ctx.classOrInterfaceType().accept(this);
        } else if (nonNull(ctx.primitiveType())) {
            baseType = ctx.primitiveType().accept(this);
        }

        // Array dimensions are also available here in the count of left respectively right brackets
        if (!ctx.LBRACK().isEmpty()) {
            baseType = new ArrayType(baseType, ctx.LBRACK().size());
        }

        return baseType;
    }

    @Override
    public BaseType visitPrimitiveType(PrimitiveTypeContext ctx) {
        BaseType typeCandidate = null;

        if (nonNull(ctx.BOOLEAN())) {
            typeCandidate = new PrimitiveType(PrimitiveTypeEnum.BOOLEAN);
        } else if (nonNull(ctx.CHAR())) {
            typeCandidate = new PrimitiveType(PrimitiveTypeEnum.CHAR);
        } else if (nonNull(ctx.BYTE())) {
            typeCandidate = new PrimitiveType(PrimitiveTypeEnum.BYTE);
        } else if (nonNull(ctx.DOUBLE())) {
            typeCandidate = new PrimitiveType(PrimitiveTypeEnum.DOUBLE);
        } else if (nonNull(ctx.FLOAT())) {
            typeCandidate = new PrimitiveType(PrimitiveTypeEnum.FLOAT);
        } else if (nonNull(ctx.INT())) {
            typeCandidate = new PrimitiveType(PrimitiveTypeEnum.INT);
        } else if (nonNull(ctx.LONG())) {
            typeCandidate = new PrimitiveType(PrimitiveTypeEnum.LONG);
        } else if (nonNull(ctx.SHORT())) {
            typeCandidate = new PrimitiveType(PrimitiveTypeEnum.SHORT);
        }

        return typeCache.getType(typeCandidate);
    }
}