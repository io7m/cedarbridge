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

package com.io7m.cedarbridge.errors;

import com.io7m.immutables.styles.ImmutablesStyleType;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalType;
import org.immutables.value.Value;

import java.net.URI;

/**
 * The type of errors.
 */

@ImmutablesStyleType
@Value.Immutable
public interface CBErrorType extends LexicalType<URI>
{
  @Override
  LexicalPosition<URI> lexical();

  /**
   * @return The error severity
   */

  Severity severity();

  /**
   * @return The exception associated with the error
   */

  Exception exception();

  /**
   * @return The error code
   */

  String errorCode();

  /**
   * @return The error message
   */

  String message();

  /**
   * The error severity types
   */

  enum Severity
  {
    /**
     * The error is a fatal error.
     */

    ERROR,

    /**
     * The error is a warning.
     */

    WARNING
  }
}
