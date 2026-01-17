package dev.flowty.gl.test.shape;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.jfree.svg.SVGGraphics2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * A handy way to produce graphical test failures
 */
public class ImageDebug {

  private static final Logger LOG = LoggerFactory.getLogger(ShapeAssert.class);

  private interface DebugImage extends AutoCloseable, Supplier<Graphics2D> {

    @Override
    void close() throws IOException;
  }

  /**
   * Dumps a debug image into the target directory and opens it for the user
   *
   * @param width   Image width
   * @param height  Image height
   * @param content Image content
   */
  public static void debug(double width, double height, Consumer<Graphics2D> content) {
    StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
    debug(ste.getClassName() + "/" + ste.getMethodName(), width, height, content);
  }

  /**
   * Dumps a debug image into the target directory and opens it for the user
   *
   * @param name    A name for the file
   * @param width   Image width
   * @param height  Image height
   * @param content Image content
   */
  public static void debug(String name, double width, double height,
      Consumer<Graphics2D> content) {

    try (DebugImage di = new SvgDebugImage(name, width + 60, height + 60)) {
      Graphics2D g = di.get();

      AffineTransform at = new AffineTransform();
      at.translate(30, 30);
      g.setTransform(at);

      content.accept(g);
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  /**
   * Makes an attempt at centering text
   *
   * @param g      The graphics object
   * @param string The text
   * @param x      Where to draw
   * @param y      where to draw
   */
  public static void centered(Graphics2D g, String string, double x, double y) {
    float hrz = g.getFontMetrics().stringWidth(string) / 2.0f;
    float vrt = g.getFontMetrics().getAscent() / 2.0f;

    g.drawString(string, (float) (x - hrz), (float) (y + vrt));
  }

  /**
   * line-breaks and indents XML
   *
   * @param xml XML
   * @return diffable xml
   */
  @SuppressWarnings("resource")
  public static String pretty(String xml) {
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      XMLReader reader = spf.newSAXParser().getXMLReader();
      /*
       * For what are no doubt annoyingly good reasons, formatting an xml file
       * requires that we download the dtd. By default this happens every time, and
       * sometimes if we run the tests often enough that provokes a 429 from the host.
       * Hence this mess - we're caching the dtd locally
       */
      reader.setEntityResolver((publicId, systemId) -> {
        Path cached = Paths.get("target", ImageDebug.class.getSimpleName(),
            systemId.replaceAll("[^a-zA-Z0-9.]", "_"));
        if (!Files.exists(cached)) {
          Files.createDirectories(cached.getParent());
          try (InputStream is = new URI(systemId).toURL().openStream()) {
            Files.copy(is, cached);
          } catch (URISyntaxException e) {
            LOG.error("Failed to get {}", systemId, e);
            return null;
          }
        }
        return new InputSource(Files.newInputStream(cached));
      });

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      StreamResult result = new StreamResult(new StringWriter());
      Source source = new SAXSource(reader, new InputSource(new StringReader(xml)));
      transformer.transform(source, result);
      try (Writer w = result.getWriter()) {
        return w.toString().replace("\r", "");
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Opens a file in the appropriate application
   *
   * @param p The file
   */
  public static void browse(Path p) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop desk = Desktop.getDesktop();
        if (desk.isSupported(Action.BROWSE)) {
          desk.browse(p.toUri());
          return;
        }
      }
      // fall back to...
      new ProcessBuilder("xdg-open", p.toUri().toString()).start();
    } catch (Exception e) {
      LOG.warn("Failed to browse {}", p, e);
    }
  }

  private static class SvgDebugImage implements DebugImage {

    private final String name;
    private final SVGGraphics2D g;

    SvgDebugImage(String name, double width, double height) {
      this.name = name;
      g = new SVGGraphics2D(width, height);
    }

    @Override
    public Graphics2D get() {
      return g;
    }

    @Override
    public void close() throws IOException {
      Path path = Paths.get("target", ImageDebug.class.getSimpleName(), name + ".svg");
      Files.createDirectories(path.getParent());
      Files.writeString(path, g.getSVGDocument(), UTF_8);
      browse(path);
    }
  }

  /**
   * We're keeping this around as an option in case we find ourselves not quite trusting the svg
   * lib
   */
  @SuppressWarnings("unused")
  private static class PngDebugImage implements DebugImage {

    private final String name;
    private final BufferedImage bi;
    private final Graphics2D g;

    PngDebugImage(String name, double width, double height) {
      this.name = name;
      bi = new BufferedImage(
          (int) Math.round(width + 60), (int) Math.round(height + 60),
          BufferedImage.TYPE_INT_ARGB);
      g = bi.createGraphics();
    }

    @Override
    public Graphics2D get() {
      return g;
    }

    @Override
    public void close() throws IOException {
      Path path = Paths.get("target", ImageDebug.class.getSimpleName(), name + ".png");
      Files.createDirectories(path.getParent());
      ImageIO.write(bi, "PNG", path.toFile());
      browse(path);
    }
  }
}
