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

import com.io7m.cedarbridge.schema.compiled.CBExternalName;
import com.io7m.cedarbridge.schema.compiled.CBFieldType;
import com.io7m.cedarbridge.schema.compiled.CBPackageType;
import com.io7m.cedarbridge.schema.compiled.CBRecordType;
import com.io7m.cedarbridge.schema.compiled.CBTypeParameterType;
import com.io7m.cedarbridge.schema.names.CBTypeNames;
import com.io7m.jaffirm.core.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A record type declaration.
 */

public final class CBTypeDeclarationRecord implements CBRecordType
{
  private final List<CBFieldType> fieldsRead;
  private final List<CBFieldType> fields;
  private final String name;
  private final List<CBTypeParameterType> parametersRead;
  private final List<CBTypeParameterType> parameters;
  private CBPackageType owner;
  private Optional<CBExternalName> externalName;
  private List<String> documentation;

  /**
   * Construct a type declaration.
   *
   * @param inName The type name
   */

  public CBTypeDeclarationRecord(
    final String inName)
  {
    this.name =
      Objects.requireNonNull(inName, "name");

    this.fields =
      new ArrayList<>();
    this.parameters =
      new ArrayList<>();
    this.externalName =
      Optional.empty();
    this.documentation =
      List.of();

    Preconditions.checkPreconditionV(
      inName,
      CBTypeNames.INSTANCE.isValid(inName),
      "Type name must be valid"
    );

    this.fieldsRead =
      Collections.unmodifiableList(this.fields);
    this.parametersRead =
      Collections.unmodifiableList(this.parameters);
  }

  /**
   * Add a type parameter.
   *
   * @param parameter The parameter
   */

  public void addTypeParameter(
    final CBTypeParameter parameter)
  {
    Objects.requireNonNull(parameter, "parameter");

    final var pName = parameter.name();
    Preconditions.checkPreconditionV(
      this.parameters.stream()
        .filter(f -> Objects.equals(f.name(), pName))
        .findAny()
        .isEmpty(),
      "A parameter named %s cannot already exist!",
      pName
    );

    final int pIndex = parameter.index();
    Preconditions.checkPreconditionV(
      this.parameters.stream()
        .filter(f -> f.index() == pIndex)
        .findAny()
        .isEmpty(),
      "A parameter with index %s cannot already exist!",
      Integer.valueOf(pIndex)
    );

    this.parameters.add(parameter);
    parameter.setOwner(this);
  }

  /**
   * Add a field.
   *
   * @param field The field
   */

  public void addField(
    final CBField field)
  {
    Objects.requireNonNull(field, "field");

    final var fName = field.name();
    Preconditions.checkPreconditionV(
      this.fields.stream()
        .filter(f -> Objects.equals(f.name(), fName))
        .findAny()
        .isEmpty(),
      "A field named %s cannot already exist!",
      fName
    );

    this.fields.add(field);
    field.setOwner(this);
  }

  /**
   * Set the external name.
   *
   * @param newExternalPackage The external package
   * @param newExternalName    The external name
   */

  public void setExternalName(
    final String newExternalPackage,
    final String newExternalName)
  {
    this.externalName = Optional.of(
      new CBExternalName(newExternalPackage, newExternalName)
    );
  }

  @Override
  public List<CBFieldType> fields()
  {
    return this.fieldsRead;
  }

  @Override
  public CBPackageType owner()
  {
    return this.owner;
  }

  @Override
  public String name()
  {
    return this.name;
  }

  @Override
  public List<CBTypeParameterType> parameters()
  {
    return this.parametersRead;
  }

  @Override
  public Optional<CBExternalName> external()
  {
    return this.externalName;
  }

  @Override
  public List<String> documentation()
  {
    return this.documentation;
  }

  /**
   * Set the owner.
   *
   * @param newOwner The new owner
   */

  public void setOwner(
    final CBPackage newOwner)
  {
    this.owner = Objects.requireNonNull(newOwner, "cbPackage");
  }

  /**
   * Set the documentation.
   *
   * @param text The text
   */

  public void setDocumentation(
    final List<String> text)
  {
    this.documentation = Objects.requireNonNull(text, "text");
  }
}
