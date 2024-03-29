/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cedarbridge.schema.ast;

import com.io7m.jlexing.core.LexicalPosition;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * A protocol version declaration.
 *
 * @param userData        The user data
 * @param lexical         The lexical info
 * @param version         The version
 * @param typesAdded      The types added in this protocol version
 * @param typesRemoved    The types removed in this protocol version
 * @param typesRemovedAll All types from the previous version should be removed
 */

public record CBASTProtocolVersion(
  CBASTMutableUserData userData,
  LexicalPosition<URI> lexical,
  BigInteger version,
  List<CBASTTypeName> typesAdded,
  List<CBASTTypeName> typesRemoved,
  boolean typesRemovedAll)
  implements CBASTDeclarationType
{
  /**
   * A protocol version declaration.
   *
   * @param userData        The user data
   * @param lexical         The lexical info
   * @param version         The version
   * @param typesAdded      The types added in this protocol version
   * @param typesRemoved    The types removed in this protocol version
   * @param typesRemovedAll All types from the previous version should be
   *                        removed
   */

  public CBASTProtocolVersion
  {
    Objects.requireNonNull(userData, "userData");
    Objects.requireNonNull(lexical, "lexical");
    Objects.requireNonNull(version, "version");
    Objects.requireNonNull(typesAdded, "typesAdded");
    Objects.requireNonNull(typesRemoved, "typesRemoved");
  }
}
