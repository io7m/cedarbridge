/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.cedarbridge.codegen.java.internal.serializer_factories;

import com.io7m.cedarbridge.codegen.java.internal.CBCGJavaClassGeneratorType;
import com.io7m.cedarbridge.codegen.java.internal.serializer_instantiation.CBCGSerializerInstantiations;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorConfiguration;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorException;
import com.io7m.cedarbridge.runtime.api.CBAbstractSerializerFactory;
import com.io7m.cedarbridge.runtime.api.CBSerializerFactoryType;
import com.io7m.cedarbridge.schema.compiled.CBTypeDeclarationType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.io7m.cedarbridge.codegen.java.internal.type_expressions.CBCGJavaTypeExpressions.createTypeVariables;
import static com.io7m.cedarbridge.codegen.java.internal.CBCGJavaTypeNames.dataTypeNameOf;
import static com.io7m.cedarbridge.codegen.java.internal.CBCGJavaTypeNames.serializerFactoryClassNameOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

public final class CBCGJavaSerializerFactoryGenerator
  implements CBCGJavaClassGeneratorType
{
  public CBCGJavaSerializerFactoryGenerator()
  {

  }

  private static MethodSpec createConstructor(
    final CBTypeDeclarationType type)
  {
    return MethodSpec.constructorBuilder()
      .addModifiers(PUBLIC)
      .addStatement("super($S,$S)", type.owner().name(), type.name())
      .build();
  }

  @Override
  public Path execute(
    final CBSPICodeGeneratorConfiguration configuration,
    final CBTypeDeclarationType type)
    throws CBSPICodeGeneratorException
  {
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(type, "type");

    final var pack = type.owner();

    final var dataTypeName =
      dataTypeNameOf(type);
    final var serializerClassName =
      serializerFactoryClassNameOf(type);

    final var superinterfaceName =
      ClassName.get(CBSerializerFactoryType.class);
    final var parameterizedSuperinterfaceName =
      ParameterizedTypeName.get(superinterfaceName, dataTypeName);

    final var superclassName =
      ClassName.get(CBAbstractSerializerFactory.class);
    final var parameterizedSuperclassName =
      ParameterizedTypeName.get(superclassName, dataTypeName);

    final var constructor =
      createConstructor(type);

    final var classDefinition =
      TypeSpec.classBuilder(serializerClassName)
        .addModifiers(FINAL, PUBLIC)
        .addSuperinterface(parameterizedSuperinterfaceName)
        .superclass(parameterizedSuperclassName)
        .addTypeVariables(createTypeVariables(type.parameters()))
        .addMethod(constructor)
        .addMethod(CBCGSerializerInstantiations.generateInstantiationMethod(type))
        .build();

    final var javaFile =
      JavaFile.builder(pack.name(), classDefinition)
        .build();

    try {
      return javaFile.writeToPath(configuration.outputDirectory(), UTF_8);
    } catch (final IOException e) {
      throw new CBSPICodeGeneratorException(e);
    }
  }
}