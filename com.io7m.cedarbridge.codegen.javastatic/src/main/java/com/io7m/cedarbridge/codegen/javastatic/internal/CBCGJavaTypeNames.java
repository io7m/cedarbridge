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

import com.io7m.cedarbridge.schema.compiled.CBExternalName;
import com.io7m.cedarbridge.schema.compiled.CBExternalType;
import com.io7m.cedarbridge.schema.compiled.CBProtocolDeclarationType;
import com.io7m.cedarbridge.schema.compiled.CBProtocolVersionDeclarationType;
import com.io7m.cedarbridge.schema.compiled.CBTypeDeclarationType;
import com.io7m.cedarbridge.schema.compiled.CBTypeParameterType;
import com.io7m.cedarbridge.schema.compiled.CBVariantCaseType;
import com.io7m.jodist.ClassName;
import com.io7m.jodist.ParameterizedTypeName;
import com.io7m.jodist.TypeName;
import com.io7m.jodist.TypeVariableName;

/**
 * Functions over Java type names.
 */

public final class CBCGJavaTypeNames
{
  private CBCGJavaTypeNames()
  {

  }

  /**
   * Generate the Java class name for the given protocol.
   *
   * @param proto The protocol
   *
   * @return The Java interface name
   */

  public static ClassName protoNameOf(
    final CBProtocolDeclarationType proto)
  {
    final var ownerPack = proto.owner();

    return ClassName.get(
      ownerPack.name(),
      String.format("Protocol%s", proto.name())
    );
  }

  /**
   * Generate the Java interface name for the given protocol.
   *
   * @param proto The protocol
   *
   * @return The Java interface name
   */

  public static ClassName protoInterfaceNameOf(
    final CBProtocolDeclarationType proto)
  {
    final var ownerPack = proto.owner();

    return ClassName.get(
      ownerPack.name(),
      String.format("Protocol%sType", proto.name())
    );
  }

  /**
   * Generate the Java interface name for the given protocol version.
   *
   * @param proto The protocol version
   *
   * @return The Java interface name
   */

  public static ClassName protoVersionedInterfaceNameOf(
    final CBProtocolVersionDeclarationType proto)
  {
    final var ownerProto = proto.owner();
    final var ownerPack = ownerProto.owner();

    return ClassName.get(
      ownerPack.name(),
      String.format(
        "Protocol%sv%sType",
        ownerProto.name(),
        proto.version()
      )
    );
  }

  /**
   * Generate the Java data class name for the given type.
   *
   * @param type The type
   *
   * @return The Java data class name
   */

  public static ClassName dataClassNameOf(
    final CBTypeDeclarationType type)
  {
    final var externalName = externalNameOf(type);
    return ClassName.get(
      externalName.externalPackage(),
      externalName.externalName()
    );
  }

  /**
   * Generate the Java type name (including generic parameters) for the given
   * type.
   *
   * @param type The type
   *
   * @return The Java type name
   */

  public static TypeName dataTypeNameOf(
    final CBTypeDeclarationType type)
  {
    final var typeParameters =
      type.parameters();
    final var externalName =
      externalNameOf(type);
    final var className =
      ClassName.get(
        externalName.externalPackage(),
        externalName.externalName()
      );

    if (typeParameters.isEmpty()) {
      return className;
    }

    final var typeNames =
      typeParameters.stream()
        .map(p -> TypeVariableName.get(p.name()))
        .toList();

    final var typeArray = new TypeName[typeParameters.size()];
    typeNames.toArray(typeArray);
    return ParameterizedTypeName.get(className, typeArray);
  }

  /**
   * Generate the Java type name (including generic parameters) for the given
   * case.
   *
   * @param caseV The case
   *
   * @return The Java type name
   */

  public static TypeName dataTypeNameOfCase(
    final CBVariantCaseType caseV)
  {
    final var type =
      caseV.owner();
    final var typeParameters =
      type.parameters();
    final var externalName =
      externalNameOf(type);

    final var className =
      ClassName.get(
        externalName.externalPackage(),
        externalName.externalName(),
        caseV.name()
      );

    if (typeParameters.isEmpty()) {
      return className;
    }

    final var typeNames =
      typeParameters.stream()
        .map(p -> TypeVariableName.get(p.name()))
        .toList();

    final var typeArray = new TypeName[typeParameters.size()];
    typeNames.toArray(typeArray);
    return ParameterizedTypeName.get(className, typeArray);
  }

  /**
   * Generate the Java data class name for the given case.
   *
   * @param caseV The case
   *
   * @return The Java data class name
   */

  public static ClassName dataClassNameOfCase(
    final CBVariantCaseType caseV)
  {
    final var type =
      caseV.owner();
    final var externalName =
      externalNameOf(type);
    final var typeParameters =
      type.parameters();

    return ClassName.get(
      externalName.externalPackage(),
      externalName.externalName(),
      caseV.name()
    );
  }

  /**
   * Generate the external name for the given type.
   *
   * @param type The type
   *
   * @return The external name
   */

  public static CBExternalName externalNameOf(
    final CBTypeDeclarationType type)
  {
    if (type instanceof CBExternalType) {
      return type.external().get();
    }

    return type.external().orElse(
      new CBExternalName(
        type.owner().name(),
        type.name()
      )
    );
  }

  /**
   * Generate the external type name for the given type.
   *
   * @param decl The type
   *
   * @return The external type name
   */

  public static TypeName externalTypeNameOf(
    final CBExternalType decl)
  {
    final var externalName = externalNameOf(decl);
    return ClassName.get(
      externalName.externalPackage(),
      externalName.externalName()
    );
  }

  private static String toTitleCase(
    final String input)
  {
    final var text =
      input.toLowerCase();
    final var initial =
      text.charAt(0);
    final var cString =
      String.format("%s", Character.valueOf(initial)).toUpperCase();
    return String.format("%s%s", cString, input.substring(1));
  }

  /**
   * Generate the field accessor name for the given field name.
   *
   * @param name The field name
   *
   * @return The field accessor name
   */

  public static String fieldAccessorName(
    final String name)
  {
    return String.format("field%s", toTitleCase(name));
  }

  /**
   * Generate the name used for the serialization function for a given type
   * parameter.
   *
   * @param parameter The parameter
   *
   * @return The name
   */

  public static String typeParameterSerializeMethodName(
    final CBTypeParameterType parameter)
  {
    return "$serialize%s".formatted(parameter.name());
  }

  /**
   * Generate the name used for the deserialization function for a given type
   * parameter.
   *
   * @param parameter The parameter
   *
   * @return The name
   */

  public static String typeParameterDeserializeMethodName(
    final CBTypeParameterType parameter)
  {
    return "$deserialize%s".formatted(parameter.name());
  }

  /**
   * Generate the name used for the serializer for a versioned protocol.
   *
   * @param proto The protocol
   *
   * @return The name
   */

  public static ClassName protoVersionedSerializerNameOf(
    final CBProtocolVersionDeclarationType proto)
  {
    final var ownerProto = proto.owner();
    final var ownerPack = ownerProto.owner();

    return ClassName.get(
      ownerPack.name(),
      String.format(
        "Protocol%sv%sSerializer",
        ownerProto.name(),
        proto.version()
      )
    );
  }
}
