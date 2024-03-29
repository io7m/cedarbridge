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

package com.io7m.cedarbridge.cmdline.internal;

import com.io7m.cedarbridge.bridgedoc.api.CBDocGenerators;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QCommandType;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType;

import java.util.List;
import java.util.Optional;

/**
 * The "list-documentation-generators" command.
 */

public final class CBCommandListDocGenerators implements QCommandType
{
  /**
   * Construct a command.
   */

  public CBCommandListDocGenerators()
  {

  }

  @Override
  public QCommandMetadata metadata()
  {
    return new QCommandMetadata(
      "list-documentation-generators",
      new QStringType.QConstant("List available documentation generators."),
      Optional.empty()
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of();
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
  {
    final var docGenerators = new CBDocGenerators();

    final var factories =
      docGenerators.availableGenerators();

    for (final var factory : factories) {
      final var description = factory.description();
      System.out.printf(
        "%-32s : %-12s : %s%n",
        description.id(),
        description.languageName(),
        description.description()
      );
    }

    return QCommandStatus.SUCCESS;
  }
}
