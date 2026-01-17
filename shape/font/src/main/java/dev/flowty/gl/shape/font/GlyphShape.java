package dev.flowty.gl.shape.font;

import dev.flowty.gl.shape.Shape;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.locationtech.jts.awt.ShapeReader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * Extracts shape geometry from font glyphs
 */
public class GlyphShape {

  private static final GeometryFactory GEO_FACT = new GeometryFactory();
  private static final FontRenderContext FRC = new FontRenderContext(null, false, true);

  /**
   * The source font
   */
  final Font font;
  /**
   * The line height
   */
  final float height;

  /**
   * @param font The font
   */
  public GlyphShape(Font font) {
    this.font = font;
    height = font.getLineMetrics("", FRC).getHeight();
  }

  /**
   * Extracts the tesselated geometry of a character glyph
   *
   * @param character The character
   * @return The shape of that character
   */
  public Glyph of(char character) {
    try {
      char[] chars = {character};
      GlyphVector gv = font.createGlyphVector(FRC, chars);
      List<LinearRing> rings = new ArrayList<>();
      for (int i = 0; i < gv.getNumGlyphs(); i++) {
        @SuppressWarnings("unchecked")
        List<Coordinate[]> loops = ShapeReader.toCoordinates(
            gv.getGlyphOutline(i).getPathIterator(
                AffineTransform.getScaleInstance(1, -1),
                font.getSize() / 400.0f));
        for (Coordinate[] loop : loops) {
          // remove repeated vertices - they mess up triangulation
          CoordinateList cl = new CoordinateList(loop, false);
          rings.add(GEO_FACT.createLinearRing(cl.toCoordinateArray()));
        }
      }
      MultiPolygon geometry = EvenOddPolygonBuilder.build(
          GEO_FACT, rings.toArray(LinearRing[]::new));
      Shape shape = Triangulator.triangulate(geometry);
      return new Glyph(shape.vertices, shape.triangles, gv.getGlyphMetrics(0));
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to triangulate '" + character + "'", e);
    }
  }

  /**
   * Computes a scaling factor to apply to glyphs from the supplied source that will make them the
   * same size as glyphs from this source
   *
   * @param other The source of the glyphs to scale
   * @return The scaling factor
   */
  public Vector2f scaleFrom(GlyphShape other) {
    float ourAdvance = font.createGlyphVector(FRC, " ").getGlyphMetrics(0).getAdvance();
    float theirAdvance = other.font.createGlyphVector(FRC, " ").getGlyphMetrics(0).getAdvance();

    float ourHeight = font.getLineMetrics("|", FRC).getHeight();
    float theirHeight = other.font.getLineMetrics("|", FRC).getHeight();

    return new Vector2f(ourAdvance / theirAdvance, ourHeight / theirHeight);
  }
}
