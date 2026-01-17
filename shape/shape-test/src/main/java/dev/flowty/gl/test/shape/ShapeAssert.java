package dev.flowty.gl.test.shape;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.flowty.gl.shape.Bounds;
import dev.flowty.gl.shape.Shape;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jfree.svg.SVGGraphics2D;
import org.joml.Vector3f;

/**
 * Dumps shape geometry to an SVG file for assertion
 */
public class ShapeAssert {

  private static final Color text = new Color(0, 0, 0, 1f);
  private static final Color vertex = new Color(1, 0, 0, 0.25f);
  private static final Color edge = new Color(0, 0, 1, 0.25f);

  /**
   * Asserts on the geometry of a shape against a stored expectation. The image is a full breakdown
   * of vertex position, order, and triangle winding.
   *
   * @param shape The shape to assert
   */
  public static void assertGeometry(Shape shape) {
    Bounds bounds = shape.bounds(new Bounds());
    SVGGraphics2D g = new SVGGraphics2D(bounds.width() + 60, bounds.height() + 60);
    AffineTransform at = new AffineTransform();

    at.translate(
        30 - bounds.min.x,
        30 - bounds.min.y);

    g.setTransform(at);

    for (int i = 0; i < shape.vertices(); i++) {
      String label = String.valueOf(i);
      shape.vertex(i, v -> {
        g.setColor(vertex);
        g.drawOval(Math.round(v.x - 3), Math.round(v.y - 3), 6, 6);
        g.setColor(text);
        ImageDebug.centered(g, label, v.x, v.y);
      });
    }

    for (int i = 0; i < shape.triangles.length; i += 3) {
      Vector3f a = shape.vertex(shape.triangles[i], new Vector3f());
      Vector3f b = shape.vertex(shape.triangles[i + 1], new Vector3f());
      Vector3f c = shape.vertex(shape.triangles[i + 2], new Vector3f());
      Vector3f mid = new Vector3f().add(a).add(b).add(c).div(3);
      g.setColor(edge);
      g.drawLine(
          Math.round(a.x), Math.round(a.y),
          Math.round(b.x), Math.round(b.y));
      g.drawLine(
          Math.round(b.x), Math.round(b.y),
          Math.round(c.x), Math.round(c.y));
      g.drawLine(
          Math.round(c.x), Math.round(c.y),
          Math.round(a.x), Math.round(a.y));

      a.mul(2).add(mid).div(3);
      ImageDebug.centered(g, "a", a.x, a.y);
      b.mul(2).add(mid).div(3);
      ImageDebug.centered(g, "b", b.x, b.y);
      c.mul(2).add(mid).div(3);
      ImageDebug.centered(g, "c", c.x, c.y);
    }

    check(g);
  }

  /**
   * Asserts on the geometry of a shape against a stored expectation.
   *
   * @param shape The shape to assert
   */
  public static void assertShape(Shape shape) {
    Bounds bounds = shape.bounds(new Bounds());
    SVGGraphics2D g = new SVGGraphics2D(bounds.width() + 60, bounds.height() + 60);
    AffineTransform at = new AffineTransform();

    at.translate(
        30 - bounds.min.x,
        30 - bounds.min.y);

    g.setTransform(at);
    g.setColor(edge);
    for (int i = 0; i < shape.triangles.length; i += 3) {
      Vector3f a = shape.vertex(shape.triangles[i], new Vector3f());
      Vector3f b = shape.vertex(shape.triangles[i + 1], new Vector3f());
      Vector3f c = shape.vertex(shape.triangles[i + 2], new Vector3f());
      Path2D.Float triangle = new Path2D.Float(Path2D.WIND_EVEN_ODD, 3);
      triangle.moveTo(a.x, a.y);
      triangle.lineTo(b.x, b.y);
      triangle.lineTo(c.x, c.y);
      triangle.closePath();
      g.fill(triangle);
    }

    check(g);
  }

  private static void check(SVGGraphics2D g) {
    StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
    Path tail = Paths.get(
        ste.getClassName().replace('.', '/'),
        ste.getMethodName());
    Path path = Paths.get("src/test/resources").resolve(tail + ".svg");
    String expected = nothing(g.getWidth(), g.getHeight());

    try {
      if (Files.exists(path)) {
        expected = Files.readString(path, UTF_8);
      }

      String actual = ImageDebug.pretty(g.getSVGDocument());
      if (!actual.equals(expected)) {
        Files.createDirectories(path.getParent());
        Files.writeString(path, actual, UTF_8);

        compare(tail, expected, actual);
        assertEquals(expected, actual);
      }
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  private static String nothing(double width, double height) {
    SVGGraphics2D g = new SVGGraphics2D(width, height);
    g.drawString("Nothing yet!", 20, 20);
    return ImageDebug.pretty(g.getSVGDocument());
  }

  private static void compare(Path name, String expected, String actual) {
    String html = String.format(
        """
            <!DOCTYPE html>
            <html lang="en">
              <head>
                <title>%s</title>
                <link
                  rel="stylesheet" 
                  href="https://unpkg.com/image-compare-viewer/dist/image-compare-viewer.min.css"/>
                <script src="https://unpkg.com/image-compare-viewer/dist/image-compare-viewer.min.js"></script>
              </head>
              <body>
                <details>
                  <summary>Expected</summary>
                  <pre>%s</pre>
                </details>
                <details>
                  <summary>Actual</summary>
                  <pre>%s</pre>
                </details>
                <details open>
                  <summary>Diff</summary>
                  <div id="image-compare">
                    <img style="background-color:white" src='data:image/svg+xml;charset=utf-8,%s'/>
                    <img style="background-color:white" src='data:image/svg+xml;charset=utf-8,%s'/>
                  </div>
                </details>
                <script>
                  function encodePreElements() {
                      var pre = document.getElementsByTagName('pre');
                      for(var i = 0; i < pre.length; i++) {
                          var encoded = htmlEncode(pre[i].innerHTML);
                          pre[i].innerHTML = encoded;
                      }
                  };
            
                  function htmlEncode(value) {
                     var div = document.createElement('div');
                     var text = document.createTextNode(value);
                     div.appendChild(text);
                     return div.innerHTML;
                  }
            
                  encodePreElements();
            
                  const element = document.getElementById("image-compare");
                  const viewer = new ImageCompare(element).mount();
                </script>
              </body>
            </html>""",
        name,
        expected.substring(expected.indexOf("<svg")),
        actual.substring(expected.indexOf("<svg")),
        expected.substring(expected.indexOf("<svg")).replace("#", "%23"),
        actual.substring(expected.indexOf("<svg")).replace("#", "%23")
    );
    Path path = Paths.get("target/failure/").resolve(name + ".html");
    try {
      Files.createDirectories(path.getParent());
      Files.write(path, html.getBytes(UTF_8));
      ImageDebug.browse(path);
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }
}
