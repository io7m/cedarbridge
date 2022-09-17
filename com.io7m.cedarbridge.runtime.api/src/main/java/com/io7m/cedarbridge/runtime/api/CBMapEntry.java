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

package com.io7m.cedarbridge.runtime.api;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Objects;

/**
 * The type of map entries.
 *
 * @param <K>   The key type
 * @param <V>   The value type
 * @param key   The key
 * @param value The value
 */

public record CBMapEntry<K extends CBSerializableType, V extends CBSerializableType>(
  K key,
  V value)
  implements Formattable, CBSerializableType
{
  /**
   * The type of map entries.
   *
   * @param key   The key
   * @param value The value
   */

  public CBMapEntry
  {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(value, "value");
  }

  @Override
  public void formatTo(
    final Formatter formatter,
    final int flags,
    final int width,
    final int precision)
  {
    formatter.format("(%s %s)", this.key(), this.value());
  }
}