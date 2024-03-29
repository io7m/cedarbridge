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

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * A variant case declaration.
 *
 * @param documentations The documentation elements
 * @param fields         The case fields
 * @param lexical        The lexical info
 * @param name           The case name
 * @param userData       The user data
 */

public record CBASTTypeVariantCase(
  CBASTMutableUserData userData,
  LexicalPosition<URI> lexical,
  CBASTVariantCaseName name,
  List<CBASTDocumentation> documentations,
  List<CBASTField> fields)
  implements CBASTDeclarationType
{
  /**
   * A variant case declaration.
   *
   * @param documentations The documentation elements
   * @param fields         The case fields
   * @param lexical        The lexical info
   * @param name           The case name
   * @param userData       The user data
   */

  public CBASTTypeVariantCase
  {
    Objects.requireNonNull(documentations, "documentations");
    Objects.requireNonNull(fields, "fields");
    Objects.requireNonNull(lexical, "lexical");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(userData, "userData");
  }
}
