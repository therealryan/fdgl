package dev.flowty.gl.doc;

import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Ensures that the BOM includes all project artifacts
 */
class BomGeneratorTest {

  /**
   * Fails on missing artifacts in the BOM
   */
  @Test
  void bom() {
    BomGenerator bg = new BomGenerator(
        new PomData(null, Paths.get("../pom.xml")),
        m -> "bom".equals(m.artifactId()));
    Assertions.assertEquals(bg.expected(), bg.actual());
  }
}