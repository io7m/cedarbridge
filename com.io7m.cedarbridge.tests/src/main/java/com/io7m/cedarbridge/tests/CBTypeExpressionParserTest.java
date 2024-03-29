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

package com.io7m.cedarbridge.tests;

import com.io7m.cedarbridge.errors.CBError;
import com.io7m.cedarbridge.schema.ast.CBASTPackageName;
import com.io7m.cedarbridge.schema.ast.CBASTTypeApplication;
import com.io7m.cedarbridge.schema.ast.CBASTTypeExpressionType;
import com.io7m.cedarbridge.schema.ast.CBASTTypeNamed;
import com.io7m.cedarbridge.schema.parser.api.CBParseFailedException;
import com.io7m.cedarbridge.schema.parser.internal.CBParseContext;
import com.io7m.cedarbridge.schema.parser.internal.CBParserStrings;
import com.io7m.cedarbridge.schema.parser.internal.CBTypeExpressionParser;
import com.io7m.cedarbridge.strings.api.CBStringsType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CBTypeExpressionParserTest extends CBElementParserContract
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CBTypeExpressionParserTest.class);

  private CBStringsType strings;
  private ArrayList<CBError> errors;

  private CBError takeError()
  {
    return this.errors.remove(0);
  }

  private void addError(
    final CBError error)
  {
    LOG.error("{}", error.message());
    this.errors.add(error);
  }

  private CBASTTypeExpressionType parse(
    final String text)
    throws CBParseFailedException
  {
    final var parser = new CBTypeExpressionParser();
    final var expr = this.expression(text);
    final var context = new CBParseContext(
      this.strings,
      this.source,
      this::addError);
    return parser.parse(context.current(), expr);
  }

  @BeforeEach
  public void setup()
  {
    this.errors = new ArrayList<>();
    this.strings = CBParserStrings.create();
  }

  @Test
  public void testQuotedBad()
    throws Exception
  {
    final var ex = assertThrows(CBParseFailedException.class, () -> {
      this.parse("\"hello\"");
    });
    LOG.debug("", ex);
    assertEquals("errorUnexpectedExpressionForm", this.takeError().errorCode());
    assertEquals(0, this.errors.size());
  }

  @Test
  public void testUnqualifiedName()
    throws CBParseFailedException
  {
    final var name = (CBASTTypeNamed) this.parse("T");
    assertEquals(Optional.empty(), name.packageName());
    assertEquals("T", name.name().text());
  }

  @Test
  public void testUnqualifiedNameBad0()
    throws Exception
  {
    final var ex = assertThrows(CBParseFailedException.class, () -> {
      this.parse("x");
    });
    LOG.debug("", ex);
    assertEquals("errorTypePathInvalid", this.takeError().errorCode());
    assertEquals(0, this.errors.size());
  }

  @Test
  public void testQualifiedName()
    throws Exception
  {
    final var name = (CBASTTypeNamed) this.parse("com.io7m.cedarbridge:T");

    assertEquals(
      "com.io7m.cedarbridge",
      name.packageName().map(CBASTPackageName::text).orElseThrow());
    assertEquals("T", name.name().text());
  }

  @Test
  public void testQualifiedNameBad0()
    throws Exception
  {
    final var ex = assertThrows(CBParseFailedException.class, () -> {
      this.parse("com.io7m.cedarbridge:");
    });
    LOG.debug("", ex);
    assertEquals("errorTypePathInvalid", this.takeError().errorCode());
    assertEquals(0, this.errors.size());
  }

  @Test
  public void testQualifiedNameBad1()
    throws Exception
  {
    final var ex = assertThrows(CBParseFailedException.class, () -> {
      this.parse("com.io7m.cedarbridge:::2");
    });
    LOG.debug("", ex);
    assertEquals("errorTypePathInvalid", this.takeError().errorCode());
    assertEquals(0, this.errors.size());
  }

  @Test
  public void testApplication0()
    throws Exception
  {
    final var app =
      (CBASTTypeApplication) this.parse("(A B)");

    final var name0 =
      (CBASTTypeNamed) app.target();
    final var name1 =
      (CBASTTypeNamed) app.arguments().get(0);

    assertEquals("A", name0.name().text());
    assertEquals("B", name1.name().text());
  }

  @Test
  public void testApplication1()
    throws Exception
  {
    final var app =
      (CBASTTypeApplication) this.parse("(A)");

    final var name0 =
      (CBASTTypeNamed) app.target();

    assertEquals("A", name0.name().text());
    assertEquals(0, app.arguments().size());
  }

  @Test
  public void testApplicationBad0()
    throws Exception
  {
    final var ex = assertThrows(CBParseFailedException.class, () -> {
      this.parse("[]");
    });
    LOG.debug("", ex);
    assertEquals("errorEmptyTypeApplication", this.takeError().errorCode());
    assertEquals(0, this.errors.size());
  }

  @Test
  public void testApplicationBad1()
    throws Exception
  {
    final var ex = assertThrows(CBParseFailedException.class, () -> {
      this.parse("([A B] [C D])");
    });
    LOG.debug("", ex);
    assertEquals("errorUnexpectedExpressionForm", this.takeError().errorCode());
    assertEquals(0, this.errors.size());
  }
}
