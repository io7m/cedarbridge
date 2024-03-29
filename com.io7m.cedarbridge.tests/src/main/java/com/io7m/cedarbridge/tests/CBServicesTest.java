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

import com.io7m.cedarbridge.exprsrc.api.CBExpressionSourceFactoryType;
import com.io7m.cedarbridge.schema.binder.api.CBBinderFactoryType;
import com.io7m.cedarbridge.schema.compiler.api.CBSchemaCompilerFactoryType;
import com.io7m.cedarbridge.schema.parser.api.CBParserFactoryType;
import com.io7m.cedarbridge.schema.typer.api.CBTypeCheckerFactoryType;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ServiceLoader;
import java.util.stream.Stream;

public final class CBServicesTest
{
  private static DynamicTest testOf(
    final Class<?> clazz)
  {
    return DynamicTest.dynamicTest(
      String.format("testService_%s", clazz.getName()),
      () -> {
        ServiceLoader.load(clazz)
          .findFirst()
          .orElseThrow();
      }
    );
  }

  @TestFactory
  public Stream<DynamicTest> testServices()
  {
    return Stream.of(
      CBParserFactoryType.class,
      CBTypeCheckerFactoryType.class,
      CBBinderFactoryType.class,
      CBExpressionSourceFactoryType.class,
      CBSchemaCompilerFactoryType.class
    ).map(CBServicesTest::testOf);
  }
}
