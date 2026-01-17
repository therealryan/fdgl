package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Shape;
import java.util.function.Consumer;
import org.joml.Math;

/**
 * Utility methods for constructing circular geometries
 */
public class Circular {

  private Circular() {
    // no instances
  }

  /**
   * Places vertices on the circumference of a circle around the origin:
   *
   * <pre>
   * 3─2─1
   * │   │
   * 4   0
   * │   │
   * 5─6─7
   * </pre>
   *
   * @param radius The circle radius
   * @return a shape update operation that sets all vertices
   */
  public static Consumer<Shape> fan(float radius) {
    return shape -> {
      for (int i = 0; i < shape.vertices(); i++) {
        float angle = i * Math.PI_TIMES_2_f / shape.vertices();
        shape.vertex(i, v -> v.set(
            radius * Math.cos(angle),
            radius * Math.sin(angle),
            0));
      }
    };
  }

  /**
   * Places the first vertex on the origin, then the rest on the circumference of a circle around
   * it:
   *
   * <pre>
   * 4─3─2
   * │   │
   * 5 0 1
   * │   │
   * 6─7─8
   * </pre>
   *
   * @param radius The circle radius
   * @return a shape update operation that sets all vertices
   */
  public static Consumer<Shape> wheel(float radius) {
    return shape -> {
      shape.vertex(0, v -> v.set(0, 0, 0));
      for (int i = 1; i < shape.vertices(); i++) {
        float angle = (i - 1) * Math.PI_TIMES_2_f / (shape.vertices() - 1);
        shape.vertex(i, v -> v.set(
            radius * Math.cos(angle),
            radius * Math.sin(angle),
            0));
      }
    };
  }

  /**
   * Places all vertices in a double circle around the origin:
   *
   * <pre>
   * ╭──2──╮
   * │ ╭3╮ │
   * 4 5 1 0
   * │ ╰7╯ │
   * ╰──6──╯
   * </pre>
   *
   * @param radius The circle radius
   * @param width  The circle width
   * @return a shape update operation that sets all vertices
   */
  public static Consumer<Shape> border(float radius, float width) {
    return shape -> {
      float hw = width / 2;
      float radStep = Math.PI_TIMES_2_f / shape.vertices();
      for (int i = 0; i < shape.vertices(); i += 2) {
        int idx = i;
        shape.vertex(i, v -> v.set(
            (radius + hw) * Math.cos(idx * radStep),
            (radius + hw) * Math.sin(idx * radStep),
            0));
        shape.vertex(i + 1, v -> v.set(
            (radius - hw) * Math.cos(idx * radStep),
            (radius - hw) * Math.sin(idx * radStep),
            0));
      }
    };
  }

  /**
   * As with {@link #border(float, float)}, but the border lies entirely
   * <i>inside</i> the radius
   *
   * @param radius The circle radius
   * @param width  The circle width
   * @return a shape update operation that sets all vertices
   */
  public static Consumer<Shape> innerBorder(float radius, float width) {
    return border(radius - width / 2, width);
  }

  /**
   * As with {@link #border(float, float)}, but the border lies entirely
   * <i>outside</i> the radius
   *
   * @param radius The circle radius
   * @param width  The circle width
   * @return a shape update operation that sets all vertices
   */
  public static Consumer<Shape> outerBorder(float radius, float width) {
    return border(radius + width / 2, width);
  }
}
