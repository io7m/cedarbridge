/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cedarbridge.bridgedoc.xhtml.internal;

import com.io7m.cedarbridge.bridgedoc.spi.CBSPIDocGeneratorConfiguration;
import com.io7m.cedarbridge.bridgedoc.spi.CBSPIDocGeneratorException;
import com.io7m.cedarbridge.bridgedoc.spi.CBSPIDocGeneratorResult;
import com.io7m.cedarbridge.bridgedoc.spi.CBSPIDocGeneratorType;
import com.io7m.cedarbridge.schema.compiled.CBExternalType;
import com.io7m.cedarbridge.schema.compiled.CBFieldType;
import com.io7m.cedarbridge.schema.compiled.CBPackageType;
import com.io7m.cedarbridge.schema.compiled.CBProtocolDeclarationType;
import com.io7m.cedarbridge.schema.compiled.CBProtocolVersionDeclarationType;
import com.io7m.cedarbridge.schema.compiled.CBRecordType;
import com.io7m.cedarbridge.schema.compiled.CBTypeDeclarationType;
import com.io7m.cedarbridge.schema.compiled.CBTypeExpressionApplication;
import com.io7m.cedarbridge.schema.compiled.CBTypeExpressionType;
import com.io7m.cedarbridge.schema.compiled.CBVariantCaseType;
import com.io7m.cedarbridge.schema.compiled.CBVariantType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.io7m.cedarbridge.schema.compiled.CBTypeExpressionType.CBTypeExprNamedType;
import static com.io7m.cedarbridge.schema.compiled.CBTypeExpressionType.CBTypeExprParameterType;
import static java.lang.Boolean.FALSE;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * An XHTML generator.
 */

public final class CBXGenerator implements CBSPIDocGeneratorType
{
  private static final String XHTML =
    "http://www.w3.org/1999/xhtml";

  private final CBSPIDocGeneratorConfiguration configuration;
  private Map<String, CBPackageType> packages;
  private Map<String, CBDocumentedPackage> documented;

  /**
   * An XHTML generator.
   *
   * @param inConfiguration The configuration
   */

  public CBXGenerator(
    final CBSPIDocGeneratorConfiguration inConfiguration)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
  }

  @Override
  public CBSPIDocGeneratorResult execute(
    final CBPackageType pack)
    throws CBSPIDocGeneratorException
  {
    try {
      final var documentBuilders =
        DocumentBuilderFactory.newDefaultInstance();
      final var documentBuilder =
        documentBuilders.newDocumentBuilder();

      this.packages =
        this.collectPackages(pack)
          .distinct()
          .collect(Collectors.toMap(CBPackageType::name, Function.identity()));

      this.documented =
        this.packages.values()
          .stream()
          .map(p -> Map.entry(
            p.name(),
            this.createDocumentedPackage(p, documentBuilder)))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      for (final var p : this.packages.values()) {
        this.processPackage(p);
      }

      final var registry =
        DOMImplementationRegistry.newInstance();
      final var lsImplementation =
        (DOMImplementationLS) registry.getDOMImplementation("LS");

      final var files = new ArrayList<Path>();
      for (final var d : this.documented.values()) {
        files.add(this.serializePackage(d, lsImplementation));
      }

      files.add(this.writeCSS("cedarbridge-document.css"));
      files.add(this.writeCSS("cedarbridge-reset.css"));

      return new CBSPIDocGeneratorResult(List.copyOf(files));
    } catch (final Exception e) {
      throw new CBSPIDocGeneratorException(e);
    }
  }

  private Path writeCSS(
    final String name)
    throws IOException
  {
    final var path =
      "/com/io7m/cedarbridge/bridgedoc/xhtml/%s".formatted(name);
    final var outputFile =
      this.configuration.outputDirectory()
        .resolve(name);

    try (var output =
           Files.newOutputStream(outputFile, CREATE, TRUNCATE_EXISTING)) {
      try (var input = CBXGenerator.class.getResourceAsStream(path)) {
        input.transferTo(output);
        output.flush();
      }
    }
    return outputFile;
  }

  private Path serializePackage(
    final CBDocumentedPackage documentedPackage,
    final DOMImplementationLS lsImplementation)
    throws Exception
  {
    final var fileOutput =
      this.configuration.outputDirectory()
        .resolve(documentedPackage.name() + ".xhtml");

    try (var fileWriter = Files.newBufferedWriter(fileOutput)) {
      final var output = lsImplementation.createLSOutput();
      output.setEncoding(StandardCharsets.UTF_8.name());
      output.setCharacterStream(fileWriter);

      final var domWriter = lsImplementation.createLSSerializer();
      final var domConfig = domWriter.getDomConfig();
      domConfig.setParameter("xml-declaration", FALSE);

      fileWriter.append(xmlDirective());
      fileWriter.append("\n");
      fileWriter.append(xmlDoctype());
      fileWriter.append("\n");
      domWriter.write(documentedPackage.document, output);
      fileWriter.append("\n");
      fileWriter.flush();
    }

    return fileOutput;
  }

  private static String xmlDoctype()
  {
    return """
      <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
            """.strip();
  }

  private static String xmlDirective()
  {
    return """  
      <?xml version="1.0" encoding="UTF-8"?>
      """.strip();
  }

  private CBDocumentedPackage createDocumentedPackage(
    final CBPackageType pack,
    final DocumentBuilder documentBuilder)
  {
    final var document =
      documentBuilder.newDocument();

    final var root =
      document.createElementNS(XHTML, "html");
    root.setAttribute("xml:lang", "en");
    document.appendChild(root);

    final var head = document.createElementNS(XHTML, "head");
    root.appendChild(head);

    final var meta = document.createElementNS(XHTML, "meta");
    meta.setAttribute("http-equiv", "content-type");
    meta.setAttribute("content", "application/xhtml+xml; charset=utf-8");
    head.appendChild(meta);

    {
      final var style = document.createElementNS(XHTML, "link");
      style.setAttribute("rel", "stylesheet");
      style.setAttribute("type", "text/css");
      style.setAttribute("href", "cedarbridge-reset.css");
      head.appendChild(style);
    }

    {
      final var style = document.createElementNS(XHTML, "link");
      style.setAttribute("rel", "stylesheet");
      style.setAttribute("type", "text/css");
      style.setAttribute("href", "cedarbridge-document.css");
      head.appendChild(style);
    }

    this.configuration.customStyle().ifPresent(styleName -> {
      final var style = document.createElementNS(XHTML, "link");
      style.setAttribute("rel", "stylesheet");
      style.setAttribute("type", "text/css");
      style.setAttribute("href", "%s.css".formatted(styleName));
      head.appendChild(style);
    });

    {
      final var title = document.createElementNS(XHTML, "title");
      title.setTextContent(pack.name());
      head.appendChild(title);
    }

    final var body = document.createElementNS(XHTML, "body");
    root.appendChild(body);

    final var bodyMain = document.createElementNS(XHTML, "div");
    bodyMain.setAttribute("class", "cbPackage");
    bodyMain.setAttribute("id", "main");
    body.appendChild(bodyMain);

    return new CBDocumentedPackage(
      document,
      bodyMain,
      pack.name(),
      new HashMap<>(),
      new HashMap<>()
    );
  }

  private void processPackage(
    final CBPackageType p)
  {
    final var documentedPackage =
      this.documented.get(p.name());

    final var doc =
      documentedPackage.document;

    {
      final var title = doc.createElementNS(XHTML, "h1");
      final var a = doc.createElementNS(XHTML, "a");
      a.setAttribute("href", "#main");
      a.setTextContent(p.name());
      title.appendChild(a);
      documentedPackage.bodyMain.appendChild(title);
    }

    this.processPackageTypes(p, documentedPackage, doc);
    processPackageProtocols(p, documentedPackage, doc);
  }

  private static void processPackageProtocols(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPackage,
    final Document doc)
  {
    final var pProtocols =
      pack.protocols()
        .values()
        .stream()
        .sorted(Comparator.comparing(CBProtocolDeclarationType::name))
        .toList();

    if (!pProtocols.isEmpty()) {
      final var cc = doc.createElementNS(XHTML, "div");
      cc.setAttribute("class", "cbProtocolList");
      cc.setAttribute("id", "protocols");

      {
        final var title = doc.createElementNS(XHTML, "h2");
        final var a = doc.createElementNS(XHTML, "a");
        a.setAttribute("href", "#protocols");
        a.setTextContent("Protocols");
        title.appendChild(a);
        cc.appendChild(title);
      }

      for (final var t : pProtocols) {
        processProtocol(pack, documentedPackage, t);
      }

      final var protocols =
        documentedPackage.protocols.values()
          .stream()
          .sorted(Comparator.comparing(o -> o.protocol.name()))
          .toList();

      {
        final var ul = doc.createElementNS(XHTML, "ul");
        for (final var p : protocols) {
          final var li =
            doc.createElementNS(XHTML, "li");
          final var a =
            doc.createElementNS(XHTML, "a");

          a.setAttribute("href", anchor(p.protocol()));
          a.setTextContent(p.protocol().name());

          li.appendChild(a);
          ul.appendChild(li);
        }
        cc.appendChild(ul);
      }

      documentedPackage.bodyMain.appendChild(cc);
      for (final var t : protocols) {
        documentedPackage.bodyMain.appendChild(t.element);
      }
    }
  }

  private static void processProtocol(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBProtocolDeclarationType protocol)
  {
    final var doc =
      documentedPack.document;
    final var holder =
      doc.createElementNS(XHTML, "div");
    holder.setAttribute("class", "cbProtocol");

    final var docProto = new CBDocumentedProtocol(protocol, holder);
    holder.setAttribute("id", id(protocol));

    final var header =
      doc.createElementNS(XHTML, "h3");
    final var anchor =
      doc.createElementNS(XHTML, "a");

    anchor.setAttribute("href", anchor(protocol));
    anchor.setTextContent(protocol.name());
    header.appendChild(anchor);
    holder.appendChild(header);

    {
      final var id =
        documentedPack.document.createElementNS(XHTML, "p");
      id.appendChild(
        documentedPack.document.createTextNode("Protocol identifier: "));

      final var uuid =
        documentedPack.document.createElementNS(XHTML, "tt");
      uuid.setTextContent(protocol.id().toString());
      id.appendChild(uuid);

      holder.appendChild(id);
    }

    for (final var docText : protocol.documentation()) {
      final var p =
        documentedPack.document.createElementNS(XHTML, "p");
      p.setTextContent(docText);
      holder.appendChild(p);
    }

    final var versions =
      protocol.versions()
        .values()
        .stream()
        .sorted(Comparator.comparing(CBProtocolVersionDeclarationType::version))
        .toList();

    for (final var version : versions) {
      holder.appendChild(processProtocolVersion(doc, version));
    }

    documentedPack.protocols.put(protocol.name(), docProto);
  }

  private static Element processProtocolVersion(
    final Document doc,
    final CBProtocolVersionDeclarationType version)
  {
    final var cc = doc.createElementNS(XHTML, "div");
    cc.setAttribute("class", "cbProtocolVersion");

    final var title = doc.createElementNS(XHTML, "h4");
    title.setTextContent("Version %s Messages".formatted(version.version()));
    cc.appendChild(title);

    final var table = doc.createElementNS(XHTML, "table");
    table.setAttribute("class", "cbProtocolTable");
    cc.appendChild(table);

    {
      final var thead =
        doc.createElementNS(XHTML, "thead");
      table.appendChild(thead);

      final var tr =
        doc.createElementNS(XHTML, "tr");
      final var tname =
        doc.createElementNS(XHTML, "th");
      final var tdesc =
        doc.createElementNS(XHTML, "th");

      tname.setTextContent("Name");
      tdesc.setTextContent("Description");

      tr.appendChild(tname);
      tr.appendChild(tdesc);
      thead.appendChild(tr);
    }

    {
      final var tbody =
        doc.createElementNS(XHTML, "tbody");
      table.appendChild(tbody);

      for (final var type : version.typesInOrder()) {
        final var tr =
          doc.createElementNS(XHTML, "tr");
        tbody.appendChild(tr);

        final var tname =
          doc.createElementNS(XHTML, "td");
        final var tdesc =
          doc.createElementNS(XHTML, "td");

        tr.appendChild(tname);
        tr.appendChild(tdesc);

        final var a =
          doc.createElementNS(XHTML, "a");
        final var typeDecl = type.declaration();
        a.setAttribute("href", anchor(typeDecl));
        a.setTextContent(typeDecl.name());
        tname.appendChild(a);

        final var pDoc = typeDecl.documentation();
        if (!pDoc.isEmpty()) {
          tdesc.appendChild(processDocumentationInline(doc, pDoc));
        } else {
          tdesc.appendChild(doc.createTextNode("No description provided."));
        }
      }
    }

    return cc;
  }

  private void processPackageTypes(
    final CBPackageType p,
    final CBDocumentedPackage documentedPackage,
    final Document doc)
  {
    final var pTypes =
      p.types()
        .values()
        .stream()
        .sorted(Comparator.comparing(CBTypeDeclarationType::name))
        .toList();

    if (!pTypes.isEmpty()) {
      final var cc = doc.createElementNS(XHTML, "div");
      cc.setAttribute("class", "cbTypeList");
      cc.setAttribute("id", "types");

      {
        final var title = doc.createElementNS(XHTML, "h2");
        final var a = doc.createElementNS(XHTML, "a");
        a.setAttribute("href", "#types");
        a.setTextContent("Types");
        title.appendChild(a);
        cc.appendChild(title);
      }

      for (final var t : pTypes) {
        this.processType(p, documentedPackage, t);
      }

      final var types =
        documentedPackage.types.values()
          .stream()
          .sorted(Comparator.comparing(o -> o.type.name()))
          .toList();

      {
        final var ul = doc.createElementNS(XHTML, "ul");
        for (final var t : types) {
          final var li =
            doc.createElementNS(XHTML, "li");
          final var a =
            doc.createElementNS(XHTML, "a");

          a.setAttribute("href", anchor(t.type()));
          a.setTextContent(t.type().name());

          li.appendChild(a);
          ul.appendChild(li);
        }
        cc.appendChild(ul);
      }

      documentedPackage.bodyMain.appendChild(cc);
      for (final var t : types) {
        documentedPackage.bodyMain.appendChild(t.element);
      }
    }
  }

  private void processType(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBTypeDeclarationType t)
  {
    final var holder =
      documentedPack.document.createElementNS(XHTML, "div");
    holder.setAttribute("class", "cbType");

    final var docType = new CBDocumentedType(t, holder);
    holder.setAttribute("id", id(t));

    final var header =
      documentedPack.document.createElementNS(XHTML, "h3");
    final var anchor =
      documentedPack.document.createElementNS(XHTML, "a");

    anchor.setAttribute("href", anchor(t));
    anchor.setTextContent(t.name());

    header.appendChild(anchor);
    holder.appendChild(header);

    documentedPack.types.put(t.name(), docType);

    for (final var doc : t.documentation()) {
      final var p =
        documentedPack.document.createElementNS(XHTML, "p");
      p.setTextContent(doc);
      holder.appendChild(p);
    }

    if (t instanceof CBRecordType rec) {
      holder.appendChild(
        processTypeRecord(pack, documentedPack, rec, docType)
      );
    } else if (t instanceof CBVariantType var) {
      holder.appendChild(
        this.processTypeVariant(pack, documentedPack, var, docType)
      );
    } else if (t instanceof CBExternalType ext) {
      holder.appendChild(
        processTypeExternal(pack, documentedPack, ext, docType)
      );
    } else {
      throw new IllegalStateException();
    }
  }

  private static Node processTypeExternal(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBExternalType ext,
    final CBDocumentedType documented)
  {
    final var d = documentedPack.document;
    final var e = d.createElementNS(XHTML, "div");

    e.appendChild(renderTypeExternal(pack, documentedPack, ext, documented));

    processTypeParameters(documentedPack, ext, documented)
      .ifPresent(e::appendChild);

    return e;
  }

  private static Optional<Element> processTypeParameters(
    final CBDocumentedPackage documentedPack,
    final CBTypeDeclarationType t,
    final CBDocumentedType documentedType)
  {
    final var parameters = t.parameters();
    if (!parameters.isEmpty()) {
      final var doc = documentedPack.document;
      final var table =
        doc.createElementNS(XHTML, "table");

      table.setAttribute("class", "cbParameterTable");

      {
        final var thead =
          doc.createElementNS(XHTML, "thead");
        table.appendChild(thead);

        final var tr =
          doc.createElementNS(XHTML, "tr");
        final var tname =
          doc.createElementNS(XHTML, "th");
        final var tdesc =
          doc.createElementNS(XHTML, "th");

        tname.setTextContent("Name");
        tdesc.setTextContent("Description");

        tr.appendChild(tname);
        tr.appendChild(tdesc);
        thead.appendChild(tr);
      }

      {
        final var tbody =
          doc.createElementNS(XHTML, "tbody");
        table.appendChild(tbody);

        for (final var param : parameters) {
          final var tr =
            doc.createElementNS(XHTML, "tr");
          tbody.appendChild(tr);

          final var tname =
            doc.createElementNS(XHTML, "td");
          final var tdesc =
            doc.createElementNS(XHTML, "td");

          tr.appendChild(tname);
          tr.appendChild(tdesc);

          final var a =
            doc.createElementNS(XHTML, "a");
          a.setAttribute("id", idPlus(t, param.name()));
          a.setAttribute("href", anchorPlus(t, param.name()));
          a.setTextContent(param.name());
          tname.appendChild(a);

          final var pDoc = param.documentation();
          if (!pDoc.isEmpty()) {
            tdesc.appendChild(processDocumentationInline(doc, pDoc));
          } else {
            tdesc.appendChild(doc.createTextNode("No description provided."));
          }
        }
      }

      final var pHolder =
        doc.createElementNS(XHTML, "div");
      final var pTitle =
        doc.createElementNS(XHTML, "h4");

      pTitle.setTextContent("Parameters");
      pHolder.appendChild(pTitle);
      pHolder.appendChild(table);
      return Optional.of(pHolder);
    }
    return Optional.empty();
  }

  private static Element processDocumentationInline(
    final Document document,
    final List<String> documentation)
  {
    final var e = document.createElementNS(XHTML, "span");
    e.setTextContent(String.join(" ", documentation));
    return e;
  }

  private Element processTypeVariant(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBVariantType var,
    final CBDocumentedType documentedType)
  {
    final var d = documentedPack.document;
    final var e = d.createElementNS(XHTML, "div");

    e.appendChild(renderTypeVariant(pack, documentedPack, var, documentedType));

    processTypeParameters(documentedPack, var, documentedType)
      .ifPresent(e::appendChild);

    final var cases = var.cases();
    if (!cases.isEmpty()) {
      final var casesTitle =
        d.createElementNS(XHTML, "h4");
      casesTitle.setTextContent("Cases");
      e.appendChild(casesTitle);

      for (final var caseV : cases) {
        final var cc =
          d.createElementNS(XHTML, "div");
        final var caseTitle =
          d.createElementNS(XHTML, "h5");
        final var nc =
          d.createElementNS(XHTML, "span");
        nc.setTextContent(caseV.name());

        caseTitle.appendChild(d.createTextNode("Case "));
        caseTitle.appendChild(nc);
        cc.appendChild(caseTitle);

        final var fields = caseV.fields();
        if (fields.isEmpty()) {
          final var name = d.createElementNS(XHTML, "span");
          name.setTextContent(caseV.name());
          name.setAttribute("class", "cbCaseName");

          final var p = d.createElementNS(XHTML, "p");
          p.appendChild(d.createTextNode("The case "));
          p.appendChild(name);
          p.appendChild(d.createTextNode(" has no fields."));
          cc.appendChild(p);
        } else {
          cc.appendChild(processFields(pack, documentedPack, var, fields));
        }

        e.appendChild(cc);
      }
    }

    return e;
  }

  private static Element processTypeRecord(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBRecordType rec,
    final CBDocumentedType documentedType)
  {
    final var e =
      documentedPack.document.createElementNS(XHTML, "div");

    e.appendChild(renderTypeRecord(pack, documentedPack, rec, documentedType));

    processTypeParameters(documentedPack, rec, documentedType)
      .ifPresent(e::appendChild);

    final var fieldsTitle =
      documentedPack.document.createElementNS(XHTML, "h4");
    fieldsTitle.setTextContent("Fields");
    e.appendChild(fieldsTitle);

    final var fields = rec.fields();
    e.appendChild(processFields(pack, documentedPack, rec, fields));
    return e;
  }

  private static Element processFields(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBTypeDeclarationType type,
    final List<CBFieldType> fields)
  {
    final var d = documentedPack.document;
    if (fields.isEmpty()) {
      final var p = d.createElementNS(XHTML, "p");
      p.appendChild(d.createTextNode("The record type has no fields."));
      return p;
    }

    final var table =
      d.createElementNS(XHTML, "table");

    table.setAttribute("class", "cbFieldsTable");

    {
      final var thead =
        d.createElementNS(XHTML, "thead");
      table.appendChild(thead);

      final var tr =
        d.createElementNS(XHTML, "tr");
      final var tname =
        d.createElementNS(XHTML, "th");
      final var ttype =
        d.createElementNS(XHTML, "th");
      final var tdesc =
        d.createElementNS(XHTML, "th");

      tname.setTextContent("Name");
      ttype.setTextContent("Type");
      tdesc.setTextContent("Description");

      tr.appendChild(tname);
      tr.appendChild(ttype);
      tr.appendChild(tdesc);
      thead.appendChild(tr);
    }

    {
      final var tbody =
        d.createElementNS(XHTML, "tbody");
      table.appendChild(tbody);

      for (final var field : fields) {
        final var tr =
          d.createElementNS(XHTML, "tr");
        final var tname =
          d.createElementNS(XHTML, "td");
        final var ttype =
          d.createElementNS(XHTML, "td");
        final var tdesc =
          d.createElementNS(XHTML, "td");

        final var anchorName =
          d.createElementNS(XHTML, "a");
        anchorName.setTextContent(field.name());

        final var owner = field.fieldOwner();
        if (owner instanceof CBRecordType rec) {
          anchorName.setAttribute("id", idPlus(type, field.name()));
          anchorName.setAttribute("href", anchorPlus(type, field.name()));
        } else if (owner instanceof CBVariantCaseType vcase) {
          anchorName.setAttribute(
            "id",
            idPlus(type, "%s_%s".formatted(vcase.name(), field.name())));
          anchorName.setAttribute(
            "href",
            anchorPlus(type, "%s_%s".formatted(vcase.name(), field.name())));
        }

        tname.appendChild(anchorName);
        ttype.appendChild(
          processTypeExpression(d, pack, field.type())
        );
        tdesc.appendChild(
          processDocumentationInline(
            d,
            field.documentation())
        );

        tr.appendChild(tname);
        tr.appendChild(ttype);
        tr.appendChild(tdesc);
        tbody.appendChild(tr);
      }
    }
    return table;
  }

  private static Element renderTypeRecord(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBRecordType rec,
    final CBDocumentedType documentedType)
  {
    final var d = documentedPack.document;
    final var e = d.createElementNS(XHTML, "div");
    e.setAttribute("class", "cbTypeOverview");

    final var et = d.createElementNS(XHTML, "pre");
    et.appendChild(d.createTextNode("["));

    {
      final var kw =
        createKeyword(d, "record");

      final var a = d.createElementNS(XHTML, "a");
      a.setAttribute("href", anchor(rec));
      a.setTextContent(rec.name());

      et.appendChild(kw);
      et.appendChild(d.createTextNode(" "));
      et.appendChild(a);
    }

    if (!rec.parameters().isEmpty()) {
      et.appendChild(d.createTextNode("\n"));

      for (final var parameter : rec.parameters()) {
        final var a = d.createElementNS(XHTML, "a");
        a.setAttribute("href", anchorPlus(rec, parameter.name()));
        a.setTextContent(parameter.name());

        final var kw =
          createKeyword(d, "parameter");

        et.appendChild(d.createTextNode("  ["));
        et.appendChild(kw);
        et.appendChild(d.createTextNode(" "));
        et.appendChild(a);
        et.appendChild(d.createTextNode("]\n"));
      }
    }

    if (!rec.fields().isEmpty()) {
      et.appendChild(d.createTextNode("\n"));

      for (final var field : rec.fields()) {
        final var a = d.createElementNS(XHTML, "a");
        a.setAttribute("href", anchorPlus(rec, field.name()));
        a.setTextContent(field.name());

        final var kw = createKeyword(d, "field");

        et.appendChild(d.createTextNode("  ["));
        et.appendChild(kw);
        et.appendChild(d.createTextNode(" "));
        et.appendChild(a);
        et.appendChild(d.createTextNode(" "));
        et.appendChild(processTypeExpression(d, pack, field.type()));
        et.appendChild(d.createTextNode("]\n"));
      }
    }

    et.appendChild(d.createTextNode("]\n"));
    e.appendChild(et);
    return e;
  }

  private static Element renderTypeExternal(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBExternalType ext,
    final CBDocumentedType documented)
  {
    final var d = documentedPack.document;
    final var e = d.createElementNS(XHTML, "div");
    e.setAttribute("class", "cbTypeOverview");

    {
      final var e0 =
        d.createElementNS(XHTML, "pre");
      final var text =
        new StringBuilder(128);

      text.append("[external ");
      text.append(ext.name());

      if (!ext.parameters().isEmpty()) {
        text.append("\n");
        for (final var parameter : ext.parameters()) {
          text.append("  ");
          text.append("[parameter ");
          text.append(parameter.name());
          text.append("]\n");
        }
      }

      text.append(']');
      e0.setTextContent(text.toString());
      e.appendChild(e0);
    }

    return e;
  }

  private static Element renderTypeVariant(
    final CBPackageType pack,
    final CBDocumentedPackage documentedPack,
    final CBVariantType var,
    final CBDocumentedType documentedType)
  {
    final var d = documentedPack.document;
    final var e = d.createElementNS(XHTML, "div");
    e.setAttribute("class", "cbTypeOverview");

    final var et = d.createElementNS(XHTML, "pre");
    et.appendChild(d.createTextNode("["));

    {
      final var kw =
        createKeyword(d, "variant");

      final var a = d.createElementNS(XHTML, "a");
      a.setAttribute("href", anchor(var));
      a.setTextContent(var.name());

      et.appendChild(kw);
      et.appendChild(d.createTextNode(" "));
      et.appendChild(a);
    }

    if (!var.parameters().isEmpty()) {
      et.appendChild(d.createTextNode("\n"));

      for (final var parameter : var.parameters()) {
        final var a = d.createElementNS(XHTML, "a");
        a.setAttribute("href", anchorPlus(var, parameter.name()));
        a.setTextContent(parameter.name());

        final var kw =
          createKeyword(d, "parameter");

        et.appendChild(d.createTextNode("  ["));
        et.appendChild(kw);
        et.appendChild(d.createTextNode(" "));
        et.appendChild(a);
        et.appendChild(d.createTextNode("]"));
      }
    }

    if (!var.cases().isEmpty()) {
      et.appendChild(d.createTextNode("\n"));

      for (final var caseV : var.cases()) {
        final var kw = createKeyword(d, "case");

        et.appendChild(d.createTextNode("  ["));
        et.appendChild(kw);
        et.appendChild(d.createTextNode(" "));
        et.appendChild(d.createTextNode(caseV.name()));

        if (!caseV.fields().isEmpty()) {
          et.appendChild(d.createTextNode("\n"));

          for (final var field : caseV.fields()) {
            final var fa = 
              createFieldInCaseAnchor(d, var, caseV, field);
            final var fieldKw =
              createKeyword(d, "field");

            et.appendChild(d.createTextNode("    ["));
            et.appendChild(fieldKw);
            et.appendChild(d.createTextNode(" "));
            et.appendChild(fa);
            et.appendChild(d.createTextNode(" "));
            et.appendChild(processTypeExpression(d, pack, field.type()));
            et.appendChild(d.createTextNode("]\n"));
          }
          et.appendChild(d.createTextNode("  ]\n"));
        } else {
          et.appendChild(d.createTextNode("]\n"));
        }
      }
    }

    et.appendChild(d.createTextNode("]\n"));
    e.appendChild(et);
    return e;
  }

  private static Element createKeyword(
    final Document d,
    final String name)
  {
    final var kw = d.createElementNS(XHTML, "span");
    kw.setAttribute("class", "cbKeyword");
    kw.setTextContent(name);
    return kw;
  }

  private static Element createFieldInCaseAnchor(
    final Document d,
    final CBVariantType var,
    final CBVariantCaseType caseV,
    final CBFieldType field)
  {
    final var fa = d.createElementNS(XHTML, "a");
    fa.setAttribute(
      "href",
      anchorPlus(var, "%s_%s".formatted(caseV.name(), field.name())));
    fa.setTextContent(field.name());
    return fa;
  }

  private static Element processTypeExpression(
    final Document d,
    final CBPackageType currentPackage,
    final CBTypeExpressionType type)
  {
    if (type instanceof CBTypeExpressionApplication app) {
      final var e = d.createElementNS(XHTML, "span");
      e.appendChild(d.createTextNode("["));
      e.appendChild(processTypeExpression(d, currentPackage, app.target()));
      for (final var ex : app.arguments()) {
        e.appendChild(processTypeExpression(d, currentPackage, ex));
      }
      e.appendChild(d.createTextNode("]"));
      return e;
    }

    if (type instanceof CBTypeExprParameterType exprParam) {
      final var param = exprParam.parameter();
      final var e =
        d.createElementNS(XHTML, "a");
      e.setAttribute("href", anchorPlus(param.owner(), param.name()));
      e.setTextContent(param.name());
      return e;
    }

    if (type instanceof CBTypeExprNamedType named) {
      final var e = d.createElementNS(XHTML, "a");
      final var targetType = named.declaration();
      final var targetOwner = targetType.owner();
      if (Objects.equals(targetOwner, currentPackage)) {
        e.setAttribute("href", anchor(targetType));
      } else {
        final var file = targetOwner.name() + ".xhtml";
        e.setAttribute("href", file + anchor(targetType));
      }

      e.setTextContent(named.declaration().name());
      return e;
    }

    throw new IllegalStateException();
  }

  private Stream<CBPackageType> collectPackages(
    final CBPackageType p)
  {
    final var ip =
      p.imports()
        .stream()
        .flatMap(this::collectPackages);

    return Stream.concat(Stream.of(p), ip);
  }

  private record CBDocumentedType(
    CBTypeDeclarationType type,
    Element element)
  {

  }

  private record CBDocumentedProtocol(
    CBProtocolDeclarationType protocol,
    Element element)
  {

  }

  private record CBDocumentedPackage(
    Document document,
    Element bodyMain,
    String name,
    Map<String, CBDocumentedProtocol> protocols,
    Map<String, CBDocumentedType> types)
  {

  }

  private static String id(
    final CBTypeDeclarationType type)
  {
    return String.format("id_%s", type.id());
  }

  private static String anchor(
    final CBTypeDeclarationType type)
  {
    return String.format("#%s", id(type));
  }

  private static String idPlus(
    final CBTypeDeclarationType type,
    final String name)
  {
    return String.format("id_%s_%s", type.id(), name);
  }

  private static String anchorPlus(
    final CBTypeDeclarationType type,
    final String name)
  {
    return String.format("#%s", idPlus(type, name));
  }

  private static String id(
    final CBProtocolDeclarationType type)
  {
    return String.format("id_%s", type.id());
  }

  private static String anchor(
    final CBProtocolDeclarationType type)
  {
    return String.format("#%s", id(type));
  }

  private static String idPlus(
    final CBProtocolDeclarationType type,
    final String name)
  {
    return String.format("id_%s_%s", type.id(), name);
  }

  private static String anchorPlus(
    final CBProtocolDeclarationType type,
    final String name)
  {
    return String.format("#%s", idPlus(type, name));
  }
}
