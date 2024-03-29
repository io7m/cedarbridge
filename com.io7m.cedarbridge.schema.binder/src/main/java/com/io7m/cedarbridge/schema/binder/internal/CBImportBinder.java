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

package com.io7m.cedarbridge.schema.binder.internal;

import com.io7m.cedarbridge.schema.ast.CBASTImport;
import com.io7m.cedarbridge.schema.binder.api.CBBindFailedException;
import com.io7m.cedarbridge.schema.compiled.CBPackageType;
import com.io7m.cedarbridge.schema.loader.api.CBLoadFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import static com.io7m.cedarbridge.schema.names.CBUUIDs.uuid;

/**
 * Binding analysis for import declarations.
 */

public final class CBImportBinder implements CBElementBinderType<CBASTImport>
{
  private static final Optional<UUID> SPEC_SECTION =
    uuid("5fda7c00-0000-4674-8b8c-440fa936e81b");

  private static final Logger LOG =
    LoggerFactory.getLogger(CBImportBinder.class);

  /**
   * Binding analysis for import declarations.
   */

  public CBImportBinder()
  {

  }

  @Override
  public void bind(
    final CBBinderContextType context,
    final CBASTImport item)
    throws CBBindFailedException
  {
    final var loader = context.loader();
    final var longName = item.target().text();
    final var shortName = item.shortName().text();

    try {
      final var packageV =
        loader.load(context.currentPackage(), longName);

      context.registerPackage(
        SPEC_SECTION,
        item.lexical(),
        shortName,
        packageV
      );

      item.userData().put(CBPackageType.class, packageV);
    } catch (final CBLoadFailedException e) {
      LOG.debug("failed to load package {}: ", longName, e);
      throw context.failed(
        SPEC_SECTION,
        item.lexical(),
        "errorPackageUnavailable",
        longName
      );
    }
  }
}
