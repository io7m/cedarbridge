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

package com.io7m.cedarbridge.tests;

import com.io7m.cedarbridge.schema.compiled.CBPackageType;
import com.io7m.cedarbridge.schema.compiled.CBProtocolDeclarationType;
import com.io7m.cedarbridge.schema.compiled.CBTypeDeclarationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CBFakePackage implements CBPackageType
{
  public final List<CBPackageType> imports;
  public final Map<String, CBTypeDeclarationType> types;
  private final String name;
  private final HashMap<String, CBProtocolDeclarationType> protos;

  public CBFakePackage(
    final String inName)
  {
    this.name = Objects.requireNonNull(inName, "name");
    this.imports = new ArrayList<>();
    this.types = new HashMap<>();
    this.protos = new HashMap<>();
  }

  @Override
  public String name()
  {
    return this.name;
  }

  @Override
  public List<CBPackageType> imports()
  {
    return this.imports;
  }

  @Override
  public Map<String, CBTypeDeclarationType> types()
  {
    return this.types;
  }

  @Override
  public Map<String, CBProtocolDeclarationType> protocols()
  {
    return this.protos;
  }

  public void addType(
    final CBFakeRecord t)
  {
    t.setOwner(this);
    this.types.put(t.name(), t);
  }
}
