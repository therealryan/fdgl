package dev.flowty.gl.doc;

import static java.util.stream.Collectors.joining;

import dev.flowty.gl.doc.PomData.DepData;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Supports validating that a project's Bill Of Materials pom is accurate
 */
public class BomGenerator {

  private PomData root;
  private PomData bom;

  /**
   * @param root The project root pom
   * @param bom  How to recognise the BOM
   */
  public BomGenerator(PomData root, Predicate<PomData> bom) {
    this.root = root;
    this.bom = root.traversal().filter(bom).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Failed to find bom module"));
  }

  /**
   * @return The current BOM's {@code dependencyManagement} section
   */
  public String actual() {
    return bom.dependencyManagement()
        .sorted(Comparator.comparing(DepData::coords))
        .map(dep -> String.format(""
                + "\t\t\t<dependency>\n"
                + "\t\t\t\t<groupId>%s</groupId>\n"
                + "\t\t\t\t<artifactId>%s</artifactId>\n"
                + "\t\t\t\t<version>%s</version>\n"
                + "\t\t\t</dependency>",
            dep.groupId()
                .replace(bom.groupId(), "${project.groupId}"),
            dep.artifactId(),
            dep.version()
                .replace(bom.version(), "${project.version}")))
        .collect(joining(
            "\n\n",
            ""
                + "\t<dependencyManagement>\n"
                + "\t\t<dependencies>\n",
            ""
                + "\n"
                + "\t\t</dependencies>\n"
                + "\t</dependencyManagement>"));
  }

  /**
   * @return The correct BOM {@code dependencyManagement} section
   */
  public String expected() {
    return root.traversal()
        .filter(m -> "jar".equals(m.packaging()))
        .sorted(Comparator.comparing(PomData::coords))
        .map(artifact -> String.format(""
                + "\t\t\t<dependency>\n"
                + "\t\t\t\t<groupId>%s</groupId>\n"
                + "\t\t\t\t<artifactId>%s</artifactId>\n"
                + "\t\t\t\t<version>%s</version>\n"
                + "\t\t\t</dependency>",
            artifact.groupId()
                .replace(bom.groupId(), "${project.groupId}"),
            artifact.artifactId(),
            artifact.version()
                .replace(bom.version(), "${project.version}")))
        .collect(joining(
            "\n\n",
            ""
                + "\t<dependencyManagement>\n"
                + "\t\t<dependencies>\n",
            ""
                + "\n"
                + "\t\t</dependencies>\n"
                + "\t</dependencyManagement>"));
  }
}
