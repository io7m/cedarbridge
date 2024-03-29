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

package com.io7m.cedarbridge.schema.typer.internal;

import com.io7m.cedarbridge.schema.typer.api.CBTypeCheckFailedException;
import com.io7m.jlexing.core.LexicalPosition;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Contextual information for type checking.
 */

public interface CBTyperContextType extends AutoCloseable
{
  @Override
  void close()
    throws CBTypeCheckFailedException;

  /**
   * Something failed.
   *
   * @param specSection The quoted spec section
   * @param lexical     The lexical information
   * @param errorCode   The error code
   * @param arguments   The error arguments
   *
   * @return An exception describing the error
   */

  CBTypeCheckFailedException failed(
    Optional<UUID> specSection,
    LexicalPosition<URI> lexical,
    String errorCode,
    Object... arguments);
}
