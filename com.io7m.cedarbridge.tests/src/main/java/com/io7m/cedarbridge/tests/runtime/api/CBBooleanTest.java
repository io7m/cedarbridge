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

import com.io7m.cedarbridge.runtime.api.CBBooleanType;
import com.io7m.cedarbridge.runtime.api.CBFalse;
import com.io7m.cedarbridge.runtime.api.CBTrue;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CBBooleanTest
{
  @Test
  public void testTrue()
  {
    assertEquals(
      Boolean.TRUE,
      Boolean.valueOf(new CBTrue().asBoolean())
    );
  }

  @Test
  public void testFalse()
  {
    assertEquals(
      Boolean.FALSE,
      Boolean.valueOf(new CBFalse().asBoolean())
    );
  }

  @Test
  public void testFormat()
  {
    assertEquals(
      "(CBTrue)",
      String.format("%s", new CBTrue())
    );
    assertEquals(
      "(CBFalse)",
      String.format("%s", new CBFalse())
    );
  }

  @Property
  public void testIdentity(
    final @ForAll boolean x)
  {
    assertEquals(
      Boolean.valueOf(x),
      Boolean.valueOf(CBBooleanType.fromBoolean(x).asBoolean())
    );
  }
}
