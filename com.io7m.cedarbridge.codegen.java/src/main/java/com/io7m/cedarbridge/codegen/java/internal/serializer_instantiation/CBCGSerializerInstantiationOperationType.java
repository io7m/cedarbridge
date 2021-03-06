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

package com.io7m.cedarbridge.codegen.java.internal.serializer_instantiation;

import com.squareup.javapoet.CodeBlock;

/**
 * An operation performed by a serializer. Operations are ordered by
 * {@link #programOrder()}, with each associated code block being executed
 * in this order.
 */

public interface CBCGSerializerInstantiationOperationType
  extends Comparable<CBCGSerializerInstantiationOperationType>
{
  /**
   * @return The program order
   */

  int programOrder();

  /**
   * @return The code block to execute
   */

  CodeBlock serialize();

  @Override
  default int compareTo(
    final CBCGSerializerInstantiationOperationType other)
  {
    return Integer.compare(this.programOrder(), other.programOrder());
  }
}
