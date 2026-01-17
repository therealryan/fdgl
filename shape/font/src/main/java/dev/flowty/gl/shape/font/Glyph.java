package dev.flowty.gl.shape.font;

import dev.flowty.gl.shape.Shape;
import java.awt.font.GlyphMetrics;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * The visual representation of a single character, along with some numbers about how it's laid out
 */
public class Glyph extends Shape {

  private final Vector3f advance;

  /**
   * @param vertices  shape vertices
   * @param triangles shape triangle indices
   * @param metrics   layout metrics
   */
  public Glyph(float[] vertices, int[] triangles, GlyphMetrics metrics) {
    super(vertices, triangles);
    advance = new Vector3f(metrics.getAdvanceX(), metrics.getAdvanceY(), 0);
  }

  /**
   * @return The displacement from the origin of this glyph to the next
   */
  public Vector3fc advance() {
    return advance;
  }

  @Override
  public Glyph transform(Matrix4f tr) {
    super.transform(tr);
    advance.mulDirection(tr, advance);
    return this;
  }
}
