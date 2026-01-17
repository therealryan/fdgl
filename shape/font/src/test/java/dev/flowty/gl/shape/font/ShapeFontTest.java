package dev.flowty.gl.shape.font;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.flowty.gl.test.shape.ShapeAssert;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises {@link ShapeFont}
 */
@SuppressWarnings("static-method")
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class ShapeFontTest {

  private static final ShapeFont FONT;

  static {
    try (
        InputStream krypton = GlyphShapeTest.class.getResourceAsStream(
            "/MonaspaceKrypton-Regular.otf");
        InputStream dejavu = GlyphShapeTest.class.getResourceAsStream(
            "/DejaVuSansMono.ttf")) {
      FONT = new ShapeFont(
          new GlyphShape(Font.createFont(Font.TRUETYPE_FONT,
                  krypton)
              .deriveFont(100f)),
          new GlyphShape(Font.createFont(Font.TRUETYPE_FONT,
                  dejavu)
              .deriveFont(100f)),
          new GlyphShape(new Font(Font.MONOSPACED, Font.PLAIN, 100)))
          .forceMonospace();
    } catch (FontFormatException | IOException e) {
      throw new IllegalStateException("failed to load font", e);
    }
  }

  private static final Matrix4f SCALE = new Matrix4f().scale(1, -1, 1);

  /**
   * Exercises constructing a single shape from a string
   */
  @Test
  void text() {
    ShapeAssert.assertShape(FONT.of(
            "Hello world!")
        .transform(SCALE));
  }

  /**
   * Forcing glyphs to be monospace. By default the hexagram character is wider than others in the
   * same allegedly-monospace font
   */
  @Test
  void hexagram() {
    ShapeAssert.assertShape(FONT.of(
            "| |\n|䷷|")
        .transform(SCALE));
  }

  /**
   * line breaks are supported
   */
  @Test
  void multiline() {
    ShapeAssert.assertShape(FONT.of("This is\ntext on\nmultiple\nlines!")
        .transform(SCALE));
  }

  /**
   * Proportional fonts are laid out correctly, but kerning isn't there yet
   */
  @Test
  void proportional() {
    ShapeFont sans = new ShapeFont(new GlyphShape(Font.decode("SansSerif-100")));
    ShapeAssert.assertShape(sans.of("proportional text\nMIwl AWA")
        .transform(SCALE));
  }

  /**
   * Testing (apparently) exotic characters
   */
  @Test
  void box() {
    ShapeAssert.assertShape(FONT.of("""
            ╔══════════════╗
            ║/trace/config/║
            ╟──────┼──────┼╢
            ║┌─────┴──────┘║
            ║├ launch      ║
            ║├ display/    ║
            ║└ exit        ║
            ╙──────────────╜
            """)
        .transform(SCALE));
  }

  /**
   * Illustrates how we can get kerning information. This is of no use right now as we're using a
   * monospace font, but maybe someday...
   */
  @Test
  void kerning() {
    Font font = Font.decode("SansSerif-100");
    FontRenderContext frc = new FontRenderContext(null, false, true);
    char[] text = "AVAVAVA".toCharArray();

    // by default layoutGlyphVector and createGlyphVector have the same behaviour
    assertEquals(435.4964904785156,
        font.layoutGlyphVector(frc, text, 0, text.length, Font.LAYOUT_LEFT_TO_RIGHT)
            .getVisualBounds().getWidth());
    assertEquals(435.4964904785156,
        font.createGlyphVector(frc, text)
            .getVisualBounds().getWidth());

    // but if we turn kerning on...
    font = font.deriveFont(Map.of(TextAttribute.KERNING, TextAttribute.KERNING_ON));

    // layoutGlyphVector squashes the As and Vs together a bit
    assertEquals(411.4964904785156,
        font.layoutGlyphVector(frc, text, 0, text.length, Font.LAYOUT_LEFT_TO_RIGHT)
            .getVisualBounds().getWidth());
    assertEquals(435.4964904785156,
        font.createGlyphVector(frc, text)
            .getVisualBounds().getWidth());
  }
}
