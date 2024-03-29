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

package com.io7m.cedarbridge.schema.compiled.internal;

import com.io7m.cedarbridge.schema.compiled.CBPackageType;
import com.io7m.cedarbridge.schema.compiled.CBProtocolDeclarationType;
import com.io7m.cedarbridge.schema.compiled.CBTypeDeclarationType;
import com.io7m.cedarbridge.schema.names.CBPackageNames;
import com.io7m.jaffirm.core.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A package declaration.
 */

public final class CBPackage implements CBPackageType
{
  private final String name;
  private final List<CBPackageType> imports;
  private final List<CBPackageType> importsRead;
  private final Map<String, CBTypeDeclarationType> types;
  private final Map<String, CBTypeDeclarationType> typesRead;
  private final Map<String, CBProtocolDeclarationType> protosRead;
  private final Map<String, CBProtocolDeclarationType> protos;

  /**
   * Construct a package.
   *
   * @param inName The name
   */

  public CBPackage(
    final String inName)
  {
    this.name = Objects.requireNonNull(inName, "name");
    this.imports = new ArrayList<>();
    this.types = new HashMap<>();
    this.protos = new HashMap<>();
    this.importsRead = Collections.unmodifiableList(this.imports);
    this.typesRead = Collections.unmodifiableMap(this.types);
    this.protosRead = Collections.unmodifiableMap(this.protos);

    Preconditions.checkPreconditionV(
      inName,
      CBPackageNames.INSTANCE.isValid(inName),
      "Package name must be valid"
    );
  }

  /**
   * Add an import to the package.
   *
   * @param imported The imported package
   */

  public void addImport(
    final CBPackageType imported)
  {
    this.imports.add(
      Objects.requireNonNull(imported, "imported")
    );
  }

  /**
   * Add a protocol to the package.
   *
   * @param protocol The protocol
   */

  public void addProtocol(
    final CBProtocolDeclaration protocol)
  {
    final var rName = protocol.name();
    Preconditions.checkPreconditionV(
      !this.protos.containsKey(rName),
      "A protocol named %s must not already exist!",
      rName
    );
    this.protos.put(rName, protocol);
    protocol.setOwner(this);
  }

  /**
   * Add a record to the package.
   *
   * @param record The record
   */

  public void addRecord(
    final CBTypeDeclarationRecord record)
  {
    final var rName = record.name();
    Preconditions.checkPreconditionV(
      !this.types.containsKey(rName),
      "A type named %s must not already exist!",
      rName
    );
    this.types.put(rName, record);
    record.setOwner(this);
  }

  /**
   * Add a variant to the package.
   *
   * @param variant The variant
   */

  public void addVariant(
    final CBTypeDeclarationVariant variant)
  {
    final var vName = variant.name();
    Preconditions.checkPreconditionV(
      !this.types.containsKey(vName),
      "A type named %s must not already exist!",
      vName
    );
    this.types.put(vName, variant);
    variant.setOwner(this);
  }

  /**
   * Add an external type to the package.
   *
   * @param external The external type
   */

  public void addExternal(
    final CBTypeDeclarationExternal external)
  {
    final var vName = external.name();
    Preconditions.checkPreconditionV(
      !this.types.containsKey(vName),
      "A type named %s must not already exist!",
      vName
    );
    this.types.put(vName, external);
    external.setOwner(this);
  }

  @Override
  public String name()
  {
    return this.name;
  }

  @Override
  public List<CBPackageType> imports()
  {
    return this.importsRead;
  }

  @Override
  public Map<String, CBTypeDeclarationType> types()
  {
    return this.typesRead;
  }

  @Override
  public Map<String, CBProtocolDeclarationType> protocols()
  {
    return this.protosRead;
  }
}
