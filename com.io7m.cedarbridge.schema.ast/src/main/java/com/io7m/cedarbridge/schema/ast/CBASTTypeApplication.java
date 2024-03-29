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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A type application.
 *
 * @param userData  The user data
 * @param lexical   The lexical info
 * @param target    The target type
 * @param arguments The type arguments
 */

public record CBASTTypeApplication(
  CBASTMutableUserData userData,
  LexicalPosition<URI> lexical,
  CBASTTypeNamed target,
  List<CBASTTypeExpressionType> arguments)
  implements CBASTTypeExpressionType
{
  /**
   * A type application.
   *
   * @param userData  The user data
   * @param lexical   The lexical info
   * @param target    The target type
   * @param arguments The type arguments
   */

  public CBASTTypeApplication
  {
    Objects.requireNonNull(userData, "userData");
    Objects.requireNonNull(lexical, "lexical");
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(arguments, "arguments");
  }

  @Override
  public void formatTo(
    final Formatter formatter,
    final int flags,
    final int width,
    final int precision)
  {
    try {
      final var out = formatter.out();
      out.append('[');
      this.target().formatTo(formatter, flags, width, precision);
      if (this.arguments().size() > 0) {
        out.append(' ');
        out.append(
          this.arguments()
            .stream()
            .map(x -> String.format("%s", x))
            .collect(Collectors.joining(" "))
        );
      }
      out.append(']');
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
