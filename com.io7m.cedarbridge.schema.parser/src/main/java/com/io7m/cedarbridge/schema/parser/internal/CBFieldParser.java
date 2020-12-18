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

package com.io7m.cedarbridge.schema.parser.internal;

import com.io7m.cedarbridge.schema.ast.CBASTField;
import com.io7m.cedarbridge.schema.parser.api.CBParseFailedException;
import com.io7m.jsx.SExpressionListType;
import com.io7m.jsx.SExpressionType;

import java.util.List;

public final class CBFieldParser implements CBElementParserType<CBASTField>
{
  public CBFieldParser()
  {

  }

  private static CBASTField parseField(
    final CBParseContextType context,
    final SExpressionListType expression)
    throws CBParseFailedException
  {
    final var expectingKind =
      "objectField";
    final var expectingShapes =
      List.of("(field <field-name> <type-expression>)");

    try (var subContext =
           context.openExpectingOneOf(expectingKind, expectingShapes)) {
      if (expression.size() != 3) {
        throw subContext.failed(expression, "errorFieldInvalid");
      }

      final var name =
        CBNames.parseFieldName(subContext, expression.get(1));
      final var expr =
        new CBTypeExpressionParser().parse(subContext, expression.get(2));

      return CBASTField.builder()
        .setLexical(expression.lexical())
        .setName(name)
        .setType(expr)
        .build();
    }
  }

  @Override
  public CBASTField parse(
    final CBParseContextType context,
    final SExpressionType expression)
    throws CBParseFailedException
  {
    return parseField(
      context,
      context.checkExpressionIs(expression, SExpressionListType.class)
    );
  }
}