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

import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorConfiguration;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorException;
import com.io7m.cedarbridge.runtime.api.CBProtocolMessageType;
import com.io7m.cedarbridge.schema.compiled.CBProtocolDeclarationType;
import com.io7m.jodist.ClassName;
import com.io7m.jodist.JavaFile;
import com.io7m.jodist.TypeSpec;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.SEALED;

/**
 * A generator of Java interface types for protocol declarations.
 */

public final class CBCGProtocolInterfaceGenerator
  implements CBCGJavaClassGeneratorType<CBProtocolDeclarationType>
{
  /**
   * A generator of Java interface types for protocol declarations.
   */

  public CBCGProtocolInterfaceGenerator()
  {

  }

  @Override
  public Path execute(
    final CBSPICodeGeneratorConfiguration configuration,
    final String packageName,
    final CBProtocolDeclarationType proto)
    throws CBSPICodeGeneratorException
  {
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(packageName, "packageName");
    Objects.requireNonNull(proto, "proto");

    final var pack =
      proto.owner();
    final var className =
      CBCGJavaTypeNames.protoInterfaceNameOf(proto);

    final var classBuilder =
      TypeSpec.interfaceBuilder(className);
    classBuilder.addSuperinterface(CBProtocolMessageType.class);
    classBuilder.addModifiers(PUBLIC, SEALED);
    classBuilder.addJavadoc(
      "Protocol {@code $L.$L}",
      pack.name(),
      proto.name()
    );

    final var versions =
      proto.versions()
        .values()
        .stream()
        .map(CBCGJavaTypeNames::protoVersionedInterfaceNameOf)
        .sorted(Comparator.comparing(ClassName::canonicalName))
        .toList();

    classBuilder.addPermittedSubclasses(versions);

    final var classDefinition =
      classBuilder.build();

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
