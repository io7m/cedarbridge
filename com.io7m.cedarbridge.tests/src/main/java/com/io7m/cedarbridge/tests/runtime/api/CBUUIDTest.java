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

import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.jbssio.vanilla.BSSReaders;
import com.io7m.jbssio.vanilla.BSSWriters;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.io7m.cedarbridge.runtime.bssio.CBSerializationContextBSSIO.createFromByteArray;
import static com.io7m.cedarbridge.runtime.bssio.CBSerializationContextBSSIO.createFromOutputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CBUUIDTest
{
  private static final BSSReaders READERS = new BSSReaders();
  private static final BSSWriters WRITERS = new BSSWriters();

  @Test
  public void testCompare()
  {
    final var bigger =
      new CBUUID(UUID.fromString("20579b1b-92b0-48ac-b6f1-9e70c29e4b71"));
    final var smaller =
      new CBUUID(UUID.fromString("021b3e32-19f1-42a9-9ef3-3bf8321b4b00"));

    assertEquals(0, bigger.compareTo(bigger));
    assertTrue(bigger.compareTo(smaller) > 0);
    assertTrue(smaller.compareTo(bigger) < 0);
  }

  @Test
  public void testFormat()
  {
    final var bigger =
      new CBUUID(UUID.fromString("20579b1b-92b0-48ac-b6f1-9e70c29e4b71"));

    assertEquals(
      "20579b1b-92b0-48ac-b6f1-9e70c29e4b71",
      String.format("%s", bigger)
    );
  }

  @Provide
  public static Arbitrary<UUID> uris()
  {
    return Combinators.combine(Arbitraries.longs(), Arbitraries.longs())
      .as(UUID::new);
  }

  @Property
  public void testUUID(
    final @ForAll("uris") UUID x)
    throws Exception
  {
    final var bao = new ByteArrayOutputStream();
    final var ctxOut = createFromOutputStream(WRITERS, bao);

    final var x0 = new CBUUID(x);
    CBUUID.serialize(ctxOut, x0);

    final var ctxIn = createFromByteArray(READERS, bao.toByteArray());
    final var x1 = CBUUID.deserialize(ctxIn);

    assertEquals(x0, x1);
    assertEquals(String.format("%s", x0), String.format("%s", x1));
    assertEquals(0, x0.compareTo(x1));
  }
}
