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

package com.io7m.cedarbridge.codegen.java.internal.protocols;

import com.io7m.cedarbridge.codegen.java.internal.CBCGJavaClassGeneratorType;
import com.io7m.cedarbridge.codegen.java.internal.CBCGJavaTypeNames;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorConfiguration;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorException;
import com.io7m.cedarbridge.runtime.api.CBProtocolSerializerFactoryType;
import com.io7m.cedarbridge.schema.compiled.CBProtocolVersionDeclarationType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.io7m.cedarbridge.codegen.java.internal.serializer_instantiation.CBCGSerializerInstantiations.generateInstantiationMethodForProtocol;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

public final class CBCGProtocolVersionedSerializerFactoryClassGenerator
  implements CBCGJavaClassGeneratorType<CBProtocolVersionDeclarationType>
{
  public CBCGProtocolVersionedSerializerFactoryClassGenerator()
  {

  }

  private static MethodSpec createConstructor(
    final CBProtocolVersionDeclarationType proto)
  {
    return MethodSpec.constructorBuilder()
      .addModifiers(PUBLIC)
      .build();
  }

  @Override
  public Path execute(
    final CBSPICodeGeneratorConfiguration configuration,
    final CBProtocolVersionDeclarationType proto)
    throws CBSPICodeGeneratorException
  {
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(proto, "proto");

    final var owner = proto.owner();
    final var pack = owner.owner();
    final var className =
      CBCGJavaTypeNames.protoSerializerFactoryClassNameOf(proto);

    final var parameterizedSuperinterface =
      ParameterizedTypeName.get(
        ClassName.get(CBProtocolSerializerFactoryType.class),
        CBCGJavaTypeNames.protoVersionedInterfaceNameOf(proto)
      );

    final var classBuilder = TypeSpec.classBuilder(className);
    classBuilder.addModifiers(PUBLIC, FINAL);
    classBuilder.addSuperinterface(parameterizedSuperinterface);
    classBuilder.addMethod(createConstructor(proto));
    classBuilder.addMethod(generateInstantiationMethodForProtocol(proto));
    classBuilder.addJavadoc(
      "Protocol {@code $L.$L}, version {@code $L}.",
      pack.name(),
      owner.name(),
      proto.version()
    );

    final var classDefinition = classBuilder.build();

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
