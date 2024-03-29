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
import com.io7m.cedarbridge.schema.compiled.CBProtocolVersionDeclarationType;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A protocol declaration.
 */

public final class CBProtocolDeclaration implements CBProtocolDeclarationType
{
  private final String name;
  private final Map<BigInteger, CBProtocolVersionDeclarationType> versions;
  private final Map<BigInteger, CBProtocolVersionDeclarationType> versionsRead;
  private CBPackage owner;
  private List<String> documentation;

  CBProtocolDeclaration(
    final String inName)
  {
    this.name = Objects.requireNonNull(inName, "inName");
    this.versions = new HashMap<>();
    this.versionsRead = Collections.unmodifiableMap(this.versions);
    this.documentation = List.of();
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
  public Map<BigInteger, CBProtocolVersionDeclarationType> versions()
  {
    return this.versionsRead;
  }

  @Override
  public List<String> documentation()
  {
    return this.documentation;
  }

  /**
   * Set the owner of the protocol.
   *
   * @param newOwner The owner package
   */

  public void setOwner(
    final CBPackage newOwner)
  {
    this.owner = Objects.requireNonNull(newOwner, "cbPackage");
  }

  /**
   * Add a protocol version.
   *
   * @param version The version
   */

  public void addVersion(
    final CBProtocolVersionDeclaration version)
  {
    Objects.requireNonNull(version, "version");
    this.versions.put(version.version(), version);
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
