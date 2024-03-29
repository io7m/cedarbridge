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

package com.io7m.cedarbridge.tests.runtime.api;

import com.io7m.cedarbridge.runtime.api.CBIntegerSigned64;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned32;
import com.io7m.cedarbridge.runtime.api.CBNone;
import com.io7m.cedarbridge.runtime.api.CBOptionType;
import com.io7m.cedarbridge.runtime.api.CBSome;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CBOptionTest
{
  @Test
  public void testSome()
  {
    assertEquals(
      Optional.of(new CBIntegerUnsigned32(23L)),
      new CBSome<>(new CBIntegerUnsigned32(23L)).asOptional()
    );
  }

  @Test
  public void testNone()
  {
    assertEquals(
      Optional.empty(),
      new CBNone<>().asOptional()
    );
  }

  @Test
  public void testFormat()
  {
    assertEquals(
      "(CBSome 23)",
      String.format("%s", new CBSome<>(new CBIntegerUnsigned32(23L)))
    );
    assertEquals(
      "(CBNone)",
      String.format("%s", new CBNone<>())
    );
  }

  @Property
  public void toFromOptionIdentity(
    final @ForAll long value)
  {
    final var actual =
      CBOptionType.fromOptional(Optional.of(new CBIntegerSigned64(value)))
        .asOptional()
        .get()
        .value();

    assertEquals(Long.valueOf(value), actual);
  }

  @Test
  public void toFromOptionIdentity()
  {
    assertEquals(
      Optional.empty(),
      CBOptionType.fromOptional(Optional.empty()).asOptional()
    );
  }
}
