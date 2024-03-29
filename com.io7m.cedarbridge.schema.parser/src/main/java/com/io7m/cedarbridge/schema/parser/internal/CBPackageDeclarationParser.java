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

package com.io7m.cedarbridge.schema.parser.internal;

import com.io7m.cedarbridge.schema.ast.CBASTMutableUserData;
import com.io7m.cedarbridge.schema.ast.CBASTNames;
import com.io7m.cedarbridge.schema.ast.CBASTPackageDeclaration;
import com.io7m.cedarbridge.schema.ast.CBASTPackageName;
import com.io7m.cedarbridge.schema.parser.api.CBParseFailedException;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SSymbol;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.cedarbridge.schema.names.CBUUIDs.uuid;
import static com.io7m.cedarbridge.schema.parser.api.CBParseFailedException.Fatal.IS_NOT_FATAL;

/**
 * A parser for package declarations.
 */

public final class CBPackageDeclarationParser
  implements CBElementParserType<CBASTPackageDeclaration>
{
  private static final String EXPECTING_KIND =
    "objectPackage";

  private static final List<String> EXPECTING_SHAPES =
    List.of("(package <package-name>)");

  private static final Optional<UUID> SPEC_SECTION =
    uuid("af42a8a2-c98d-4b5d-92ca-46406a5ddbbe");

  /**
   * A parser for package declarations.
   */

  public CBPackageDeclarationParser()
  {

  }

  private static CBASTPackageDeclaration parsePackage(
    final CBParseContextType context,
    final SList list)
    throws CBParseFailedException
  {
    if (list.size() != 2) {
      throw context.failed(
        list,
        IS_NOT_FATAL,
        SPEC_SECTION,
        "errorPackageInvalid"
      );
    }

    context.checkExpressionIsKeyword(
      list.get(0),
      SPEC_SECTION,
      "package",
      "errorPackageKeyword"
    );

    final var packName =
      context.checkExpressionIs(
        list.get(1),
        SPEC_SECTION,
        SSymbol.class
      );

    return new CBASTPackageDeclaration(
      new CBASTMutableUserData(),
      list.lexical(),
      parsePackageName(context, packName)
    );
  }

  private static CBASTPackageName parsePackageName(
    final CBParseContextType context,
    final SSymbol packName)
    throws CBParseFailedException
  {
    try (var subContext =
           context.openExpectingOneOf(
             "objectPackageName", List.of("<package-name>"))) {
      try {
        return CBASTNames.packageName(packName, packName.text());
      } catch (final IllegalArgumentException e) {
        throw subContext.failed(
          packName,
          IS_NOT_FATAL,
          SPEC_SECTION,
          "errorPackageNameInvalid",
          e);
      }
    }
  }

  @Override
  public CBASTPackageDeclaration parse(
    final CBParseContextType context,
    final SExpressionType expression)
    throws CBParseFailedException
  {
    try (var subContext =
           context.openExpectingOneOf(EXPECTING_KIND, EXPECTING_SHAPES)) {
      return parsePackage(
        subContext,
        subContext.checkExpressionIs(
          expression,
          SPEC_SECTION,
          SList.class)
      );
    }
  }
}
