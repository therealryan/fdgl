package dev.flowty.gl.shape.font;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import dev.flowty.gl.shape.Shape;
import dev.flowty.gl.shape.font.Triangulator.TriangulationException;
import dev.flowty.gl.test.shape.ImageDebug;
import dev.flowty.gl.test.shape.ShapeAssert;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.joml.Matrix4f;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;

/**
 * Exercises extracting the geometry of font glyphs
 */
@SuppressWarnings("static-method")
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class GlyphShapeTest {

  private static final GlyphShape FONT;

  static {
    try (InputStream dejavu = GlyphShapeTest.class.getResourceAsStream(
        "/DejaVuSansMono.ttf")) {
      FONT = new GlyphShape(
          Font.createFont(Font.TRUETYPE_FONT, dejavu).deriveFont(800f));
    } catch (FontFormatException | IOException e) {
      throw new IllegalStateException("failed to load font", e);
    }
  }

  private static final Matrix4f SCALE = new Matrix4f().scale(1, -1, 1);

  /**
   * A simple single-element glyph
   */
  @Test
  void simple() {
    Shape glyph = FONT.of('f');
    ShapeAssert.assertGeometry(glyph.transform(SCALE));
  }

  /**
   * A glyph with two distinct elements
   */
  @Test
  void compound() {
    Shape glyph = FONT.of('!');
    ShapeAssert.assertGeometry(glyph.transform(SCALE));
  }

  /**
   * A glyph with a hole
   */
  @Test
  void hole() {
    Shape glyph = FONT.of('o');
    ShapeAssert.assertGeometry(glyph.transform(SCALE));
  }

  /**
   * A glyph with distinct elements <i>and</i> holes
   */
  @Test
  void complex() {
    Shape glyph = FONT.of('%');
    ShapeAssert.assertGeometry(glyph.transform(SCALE));
  }

  /**
   * A glyph that isn't supported by the font
   */
  @Test
  void disapproval() {
    Shape glyph = FONT.of('ಠ');
    ShapeAssert.assertGeometry(glyph.transform(SCALE));
  }

  /**
   * There are a <i>bunch</i> of useful shapes in fonts
   */
  @Test
  void hazard() {
    Shape glyph = FONT.of('☣');
    ShapeAssert.assertGeometry(glyph.transform(SCALE));
  }

  /**
   * Confirms that we can generate the alphabet
   *
   * @return test instances
   */
  @TestFactory
  Stream<DynamicNode> alphabet() {
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    alphabet += alphabet.toUpperCase();
    alphabet += "0123456789";
    alphabet += "¬`!\"£$%^&*()_+-=[]{};:'@#~,.<>/?'|\\ ";
    return alphabet.chars()
        .mapToObj(character -> dynamicTest(Character.getName(character), () -> {
          try {
            FONT.of((char) character);
          } catch (Exception e) {
            if (e.getCause() instanceof TriangulationException te) {
              te.geometry.apply(AffineTransformation.scaleInstance(500, 500));
              Envelope env = te.geometry.getEnvelopeInternal();
              ImageDebug.debug(Character.getName(character).replaceAll("[^a-zA-Z0-9]+", "_"),
                  env.getWidth(), env.getHeight(),
                  drawGeometry(te.geometry));
            }
            throw e;
          }
        }));
  }

  private static Consumer<Graphics2D> drawGeometry(Geometry geometry) {
    return g -> {
      Envelope env = geometry.getEnvelopeInternal();
      g.transform(AffineTransform.getTranslateInstance(-env.getMinX(), -env.getMinY()));

      g.setColor(new Color(0, 0, 0, 0.25f));
      for (int i = 0; i < geometry.getCoordinates().length; i++) {
        Coordinate coord = geometry.getCoordinates()[i];
        g.drawOval((int) Math.round(coord.getX() - 3),
            (int) Math.round(coord.getY() - 3),
            6, 6);
        ImageDebug.centered(g, String.valueOf(i), coord.getX(), coord.getY());
      }
    };
  }
}
