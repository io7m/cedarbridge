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

package com.io7m.cedarbridge.schema.ast;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.util.List;

/**
 * A package.
 */

@ImmutablesStyleType
@Value.Immutable
public interface CBASTPackageType
{
  /**
   * @return The user data associated with the package
   */

  @Value.Default
  default CBASTMutableUserData userData()
  {
    return new CBASTMutableUserData();
  }

  /**
   * @return The name of the package
   */

  CBASTPackageName name();

  /**
   * @return The package language
   */

  CBASTLanguage language();

  /**
   * @return The packages imported by this package
   */

  List<CBASTImport> imports();

  /**
   * @return The type declarations in the package
   */

  List<CBASTTypeDeclarationType> types();

  /**
   * @return The protocol declarations in the package
   */

  List<CBASTProtocolDeclaration> protocols();
}
