package dev.flowty.gl.shape.font;

import dev.flowty.gl.shape.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * Convenience layer for {@link GlyphShape}. Provides glyph caching and basic text layout.
 */
public class ShapeFont {

  private final GlyphShape[] shapes;
  private final Vector2f[] scales;
  private final Map<Character, Glyph> glyphCache = new HashMap<>();
  private static final FontRenderContext FRC = new FontRenderContext(null, false, true);
  private float monoAdvance = -1;

  /**
   * @param shapes The sources of glyph geometry
   */
  public ShapeFont(GlyphShape... shapes) {
    this.shapes = shapes;
    scales = new Vector2f[shapes.length];
    Arrays.fill(scales, new Vector2f(1, 1));
  }

  /**
   * Update the font so that glyphs from different {@link GlyphShape}s will be forced to have a
   * uniform advance
   *
   * @return <code>this</code>
   */
  public ShapeFont forceMonospace() {
    monoAdvance = shapes[0].of(' ').advance().x();
    for (int i = 0; i < scales.length; i++) {
      scales[i] = shapes[0].scaleFrom(shapes[i]);
    }
    return this;
  }

  /**
   * @return The line height of the font
   */
  public float height() {
    return shapes[0].height;
  }

  /**
   * Generates a string of glyphs
   *
   * @param text The text to generate from
   * @param sink What to do with the glyphs
   */
  public void of(String text, Consumer<Glyph> sink) {
    for (int i = 0; i < text.length(); i++) {
      sink.accept(of(text.charAt(i)));
    }
  }

  /**
   * Generates a single glyph
   *
   * @param character The character
   * @return the geometry of that character's glyph
   */
  @SuppressWarnings("boxing")
  public Glyph of(char character) {
    return glyphCache.computeIfAbsent(character, c -> {
      for (int i = 0; i < shapes.length; i++) {
        if (shapes[i].font.canDisplay(c)) {
          Glyph g = shapes[i].of(c)
              .transform(new Matrix4f().scale(scales[i].x, scales[i].y, 1));
          // you would think that all the characters from a monospace font would have the
          // same width, but you'd be wrong. Some obscure characters do not match the
          // rest, hence we have to look for those and squash/stretch them to fit the
          // standard advance
          if (monoAdvance != -1 && g.advance().x() != monoAdvance) {
            g.transform(new Matrix4f().scale(monoAdvance / g.advance().x(), 1, 1));
          }
          return g;
        }
      }
      return shapes[0].of(c);
    });
  }

  /**
   * Generates a shape for a string of text. Handles newlines.
   *
   * @param text the text
   * @return the renderable geometry of the text
   */
  public Shape of(String text) {
    List<Shape> glyphs = new ArrayList<>();
    LineMetrics lm = shapes[0].font.getLineMetrics(text, FRC);
    Matrix4f m = new Matrix4f();
    int li = 0;
    for (String line : text.split("\n")) {
      m.translation(0, -li * lm.getHeight(), 0);
      of(line, glyph -> {
        glyphs.add(new Shape(glyph).transform(m));
        m.translate(glyph.advance());
      });
      li++;
    }

    return new Shape(glyphs.toArray(Shape[]::new));
  }
}
