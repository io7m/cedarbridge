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

package com.io7m.cedarbridge.tests.cmdline;

import com.io7m.cedarbridge.cmdline.CBMain;
import com.io7m.cedarbridge.tests.CBTestDirectories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CBCommandLineTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CBCommandLineTest.class);

  private ByteArrayOutputStream output;
  private PrintStream outputPrint;
  private Path directory;
  private Path directoryOutput;

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.directory =
      CBTestDirectories.createTempDirectory();
    this.directoryOutput =
      CBTestDirectories.createTempDirectory();

    this.output = new ByteArrayOutputStream();
    this.outputPrint = new PrintStream(this.output);
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);
  }

  @AfterEach
  public void tearDown()
    throws IOException
  {
    CBTestDirectories.deleteDirectory(this.directory);
    CBTestDirectories.deleteDirectory(this.directoryOutput);
  }

  void flush()
  {
    try {
      this.outputPrint.flush();
      this.output.flush();
    } catch (final IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  @Test
  public void testHelpCheck()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "help", "check"
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertTrue(text.contains("cedarbridge: usage: check"));
  }

  @Test
  public void testHelpListCodeGenerators()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "help", "list-code-generators"
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertTrue(text.contains("cedarbridge: usage: list-code-generators"));
  }

  @Test
  public void testHelpListDocGenerators()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "help", "list-documentation-generators"
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertTrue(text.contains("cedarbridge: usage: list-documentation-generators"));
  }

  @Test
  public void testHelpDocument()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "help", "document"
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertTrue(text.contains("cedarbridge: usage: document"));
  }

  @Test
  public void testVersion()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "version"
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertTrue(text.contains("com.io7m.cedarbridge"));
  }

  @Test
  public void testCheckNothing()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "check"
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertEquals("", text.trim());
  }

  @Test
  public void testCheckError0()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    final var file =
      CBTestDirectories.resourceOf(
        CBCommandLineTest.class,
        this.directory,
        "errorBindDuplicateField0.cbs"
      );

    final var code =
      CBMain.mainExitless(new String[]{
        "check",
        "--file",
        file.toString()
      });

    assertTrue(code != 0);

    this.flush();
    final var text = this.output.toString();
    assertTrue(text.contains("errorBindDuplicateField0.cbs"));
    LOG.debug("{}", text);
  }

  @Test
  public void testListCodeGenerators()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "list-code-generators"
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertTrue(text.contains("Java 17+"));
  }

  @Test
  public void testListDocGenerators()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "list-documentation-generators"
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertTrue(text.contains("xhtml"));
  }

  @Test
  public void testCompileNothing()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    CBMain.mainExitless(new String[]{
      "compile",
      "--language",
      "Java 17+",
      "--output-directory",
      this.directoryOutput.toString()
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
    assertEquals("", text.trim());
  }

  @Test
  public void testCompileSimple()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    final var file =
      CBTestDirectories.resourceOf(
        CBCommandLineTest.class,
        this.directory,
        "basicWithCore.cbs"
      );

    CBMain.mainExitless(new String[]{
      "compile",
      "--file",
      file.toString(),
      "--language",
      "Java 17+",
      "--output-directory",
      this.directoryOutput.toString()
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
  }

  @Test
  public void testDocumentSimple()
    throws IOException
  {
    System.setOut(this.outputPrint);
    System.setErr(this.outputPrint);

    final var file =
      CBTestDirectories.resourceOf(
        CBCommandLineTest.class,
        this.directory,
        "basicWithCore.cbs"
      );

    CBMain.mainExitless(new String[]{
      "document",
      "--file",
      file.toString(),
      "--language",
      "xhtml",
      "--output-directory",
      this.directoryOutput.toString()
    });

    this.flush();
    final var text = this.output.toString();
    LOG.debug("{}", text);
  }
}
