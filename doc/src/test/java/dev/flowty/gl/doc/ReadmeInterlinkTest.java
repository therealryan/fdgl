package dev.flowty.gl.doc;


import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class ReadmeInterlinkTest {

  /**
   * Spiders over the pom structure and regenerates the title sections of associated readme files.
   * If this test fails it's because that regeneration made a change. It should pass if you just run
   * it again, but do be sure to check the changes are desired before you commit them.
   *
   * @return Test cases to regenerate readme interlinks
   */
  @TestFactory
  DynamicContainer titles() {
    ReadmeInterlink ri = new ReadmeInterlink("https://github.com/therealryan/fdgl");
    return fromPom(new PomData(null, Paths.get("../pom.xml")), ri);
  }

  private DynamicContainer fromPom(PomData pom, ReadmeInterlink ri) {
    DynamicTest readme = DynamicTest.dynamicTest(
        "title",
        () -> Assertions.assertFalse(ri.regenerate(pom)));
    return DynamicContainer.dynamicContainer(
        pom.artifactId(),
        Stream.concat(
            Stream.of(readme),
            pom.modules().map(m -> fromPom(m, ri))));
  }
}