package dev.flowty.gl.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link AbstractXml}
 */
@SuppressWarnings("static-method")
class HtmlTest {

  /**
   * Empty elements are valid
   */
  @Test
  void empty() {
    assertEquals("<!DOCTYPE html>\n"
        + "<html></html>", new Html().toString());

    assertEquals("<!DOCTYPE html>\n"
            + "<html><br></html>", new Html().br().toString(),
        "void elements are valid");
    assertEquals("<!DOCTYPE html>\n"
            + "<html><ul></ul></html>", new Html().ul().toString(),
        "non-void elements are start/end tagged");
  }

  /**
   * Content is not allowed in void elements
   */
  @Test
  void voidElements() {
    Html html = new Html();

    assertThrows(IllegalArgumentException.class, () -> html
        .elm("br", b -> b
            .txt("foo")));

    assertThrows(IllegalArgumentException.class, () -> html
        .elm("IMG", b -> b.span("bar")));
  }

  /**
   * Attributes can be added
   */
  @Test
  void attributes() {
    assertEquals("<!DOCTYPE html>\n"
            + "<html name=\"value\"></html>",
        new Html().atr("name", "value").toString());
  }

  /**
   * Element contents can be added
   */
  @Test
  void text() {
    assertEquals("<!DOCTYPE html>\n"
            + "<html>text</html>",
        format(new Html().txt("text")));
  }

  /**
   * Arbitrary content can be added
   */
  @Test
  void content() {
    assertEquals(""
            + "<!DOCTYPE html>\n"
            + "<html>text with &#60;i&#62;markup!&#60;/i&#62;\n"
            + "<br>text with \n"
            + "<i>markup!</i>\n"
            + "</html>",
        format(new Html()
            .txt("text with <i>markup!</i>")
            .br()
            .cnt("text with <i>markup!</i>")));
  }

  /**
   * Attributes can be set at any point, but text and child element order matters
   */
  @Test
  void interleaving() {
    assertEquals(""
            + "<!DOCTYPE html>\n"
            + "<html a=\"1\" b=\"2\">foo\n"
            + "<child>element</child>\n"
            + "bar</html>",
        format(new Html()
            .atr("b", "2")
            .txt("foo")
            .elm("child", "element")
            .atr("a", "1")
            .txt("bar")));
  }

  /**
   * Child elements can be added
   */
  @Test
  void child() {
    assertEquals(""
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<empty></empty>\n"
            + "<flat>text</flat>\n"
            + "<root>\n"
            + "<branch>\n"
            + "<leaf></leaf>\n"
            + "</branch>\n"
            + "</root>\n"
            + "</html>",
        format(new Html()
            .elm("empty")
            .elm("flat", "text")
            .elm("root", r -> r
                .elm("branch", b -> b
                    .elm("leaf")))));
  }

  /**
   * Sibling elements can be added in separate functions
   */
  @Test
  void sibling() {
    assertEquals(""
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<body>\n"
            + "<span>abc</span>\n"
            + "<span>def</span>\n"
            + "</body>\n"
            + "</html>",
        format(new Html().body(
            b -> b.span("abc"),
            b -> b.span("def"))));
  }

  /**
   * Optional elements can be defined in the call chain
   */
  @Test
  void optional() {
    assertEquals(""
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<span>exists!</span>\n"
            + "</html>",
        format(new Html()
            .optional(Html::span).of("exists!")
            .optional(Html::span).of(null)));
  }

  /**
   * Conditional elements can be defined in the call chain
   */
  @Test
  void conditional() {
    assertEquals(""
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<span>hello</span>\n"
            + "</html>",
        format(new Html()
            .conditional(h -> h.span("hello")).on(true)
            .conditional(h -> h.span("world!")).on(false)));
  }

  /**
   * Repeated elements can be defined in the call chain
   */
  @Test
  void repeated() {
    assertEquals(""
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<span>a</span>\n"
            + "<span>b</span>\n"
            + "<p>c</p>\n"
            + "<p>d</p>\n"
            + "ef</html>",
        format(new Html()
            .repeat(Html::span).over(Arrays.asList("a", "b"))
            .repeat(Html::p).over("c", "d")
            .repeat(Html::txt).over(Stream.of("e", "f"))));
  }

  /**
   * This is terrible, but it's apparently difficult to find a lib to pretty-print html without
   * changing the content
   */
  private static String format(Html html) {
    return html.toString()
        // newline before opening tags
        .replaceAll("(</[^>]*?>)", "$1\n")
        // newline after closing tags
        .replaceAll("(<[^/][^>]*?>)", "\n$1")
        // collapse contiguous newlines
        .replaceAll("\n+", "\n")
        .trim();
  }

}