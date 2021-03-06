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

package com.io7m.cedarbridge.codegen.java.internal.serializers;

import com.squareup.javapoet.CodeBlock;

import java.util.Objects;

import static com.io7m.cedarbridge.codegen.java.internal.CBCGJavaTypeNames.fieldAccessorName;

/**
 * An operation that calls a serialize method.
 */

public final class OpCallSerialize extends OpAbstract
{
  private final String fieldName;

  /**
   * An operation that calls a serialize method.
   *
   * @param inOrder     The program order
   * @param inFieldName The field name
   */

  public OpCallSerialize(
    final int inOrder,
    final String inFieldName)
  {
    super(inOrder);
    this.fieldName =
      Objects.requireNonNull(inFieldName, "fieldName");
  }

  @Override
  public CodeBlock serialize()
  {
    return CodeBlock.builder()
      .addStatement("context.begin($S)", this.fieldName)
      .addStatement(
        "this.$L.serialize(context,value.$L())",
        this.fieldName,
        fieldAccessorName(this.fieldName))
      .addStatement("context.end($S)", this.fieldName)
      .build();
  }
}
