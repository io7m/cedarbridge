/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.cedarbridge.codegen.javastatic.internal;

import com.io7m.cedarbridge.runtime.api.CBSerializableType;
import com.io7m.cedarbridge.schema.compiled.CBExternalType;
import com.io7m.cedarbridge.schema.compiled.CBRecordType;
import com.io7m.cedarbridge.schema.compiled.CBTypeExpressionType;
import com.io7m.cedarbridge.schema.compiled.CBTypeExpressionType.CBTypeExprNamedType;
import com.io7m.cedarbridge.schema.compiled.CBTypeParameterType;
import com.io7m.cedarbridge.schema.compiled.CBVariantType;
import com.io7m.jodist.ClassName;
import com.io7m.jodist.ParameterizedTypeName;
import com.io7m.jodist.TypeName;
import com.io7m.jodist.TypeVariableName;
import com.io7m.junreachable.UnreachableCodeException;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.io7m.cedarbridge.schema.compiled.CBTypeExpressionType.CBTypeExprApplicationType;
import static com.io7m.cedarbridge.schema.compiled.CBTypeExpressionType.CBTypeExprParameterType;

/**
 * Functions to generate code for type expressions.
 */

public final class CBCGJavaTypeExpressions
{
  private CBCGJavaTypeExpressions()
  {

  }

  /**
   * Evaluate the given type expression.
   *
   * @param type The type expression
   *
   * @return The name of the resulting type
   */

  public static TypeName evaluateTypeExpression(
    final CBTypeExpressionType type)
  {
    Objects.requireNonNull(type, "type");

    if (type instanceof CBTypeExprNamedType t) {
      return evaluateTypeExpressionNamed(t);
    }
    if (type instanceof CBTypeExprParameterType t) {
      return evaluateTypeExpressionParameter(t);
    }
    if (type instanceof CBTypeExprApplicationType t) {
      return evaluateTypeExpressionApplication(t);
    }
    throw new UnreachableCodeException();
  }

  private static TypeName evaluateTypeExpressionApplication(
    final CBTypeExprApplicationType type)
  {
    final var arguments =
      type.arguments()
        .stream()
        .map(CBCGJavaTypeExpressions::evaluateTypeExpression)
        .collect(Collectors.toCollection(LinkedList::new));

    final var argumentArray = new TypeName[arguments.size()];
    arguments.toArray(argumentArray);

    final var target =
      evaluateTypeExpressionNamed(type.target());

    if (target instanceof final ClassName name0) {
      return ParameterizedTypeName.get(name0, argumentArray);
    }

    throw new IllegalStateException(
      "First expression of type application is not a class");
  }

  private static TypeName evaluateTypeExpressionParameter(
    final CBTypeExprParameterType type)
  {
    return TypeVariableName.get(type.parameter().name());
  }

  /**
   * Evaluate the given type expression.
   *
   * @param type The type expression
   *
   * @return The name of the resulting type
   */

  public static TypeName evaluateTypeExpressionNamed(
    final CBTypeExprNamedType type)
  {
    final var decl = type.declaration();
    if (decl instanceof CBRecordType r) {
      return CBCGJavaTypeNames.dataClassNameOf(r);
    }
    if (decl instanceof CBVariantType v) {
      return CBCGJavaTypeNames.dataClassNameOf(v);
    }
    if (decl instanceof CBExternalType e) {
      return CBCGJavaTypeNames.externalTypeNameOf(e);
    }
    throw new UnreachableCodeException();
  }

  /**
   * Create type variables for the given list of parameters.
   *
   * @param typeParameters The type parameters
   *
   * @return The resulting type variables
   */

  public static List<TypeVariableName> createTypeVariables(
    final List<CBTypeParameterType> typeParameters)
  {
    return typeParameters
      .stream()
      .map(p -> TypeVariableName.get(p.name(), CBSerializableType.class))
      .collect(Collectors.toList());
  }
}
