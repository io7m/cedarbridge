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

package com.io7m.cedarbridge.codegen.javastatic;

import com.io7m.cedarbridge.codegen.javastatic.internal.CBCGJava;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorConfiguration;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorDescription;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorFactoryType;
import com.io7m.cedarbridge.codegen.spi.CBSPICodeGeneratorType;

/**
 * A factory of Java code generators.
 */

public final class CBCGJavaSFactory implements CBSPICodeGeneratorFactoryType
{
  private static final CBSPICodeGeneratorDescription DESCRIPTION =
    new CBSPICodeGeneratorDescription(
      "com.io7m.cedarbridge.javastatic17",
      "Java 17+",
      "A generator that produces Java 17 sources."
    );

  /**
   * Construct a code generator factory.
   */

  public CBCGJavaSFactory()
  {

  }

  @Override
  public CBSPICodeGeneratorDescription description()
  {
    return DESCRIPTION;
  }

  @Override
  public CBSPICodeGeneratorType createGenerator(
    final CBSPICodeGeneratorConfiguration configuration)
  {
    return new CBCGJava(configuration);
  }
}
