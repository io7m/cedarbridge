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

package com.io7m.cedarbridge.runtime.api;

import java.io.IOException;

/**
 * A float64 serializer.
 */

public final class CBFloat64Serializer extends CBAbstractSerializer<CBFloat64>
{
  /**
   * A float64 serializer.
   */

  public CBFloat64Serializer()
  {

  }

  @Override
  public void serialize(
    final CBSerializationContextType context,
    final CBFloat64 value)
    throws IOException
  {
    context.writeF64(value.value());
  }

  @Override
  public CBFloat64 deserialize(
    final CBSerializationContextType context)
    throws IOException
  {
    return CBFloat64.of(context.readF64());
  }
}
