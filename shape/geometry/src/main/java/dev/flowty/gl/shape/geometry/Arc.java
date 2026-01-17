package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Shape;
import java.util.function.Consumer;
import org.joml.Math;

public class Arc {

  /**
   * Places vertices on the circumference of a circle around the origin, evenly spaced in the
   * anticlockwise direction between the two angles
   *
   * <pre>
   * 3─2─1
   * │   │
   * 4---0
   * </pre>
   *
   * @param radius     circle radius
   * @param startAngle in radians
   * @param endAngle   in radians
   * @return
   */
  public static Consumer<Shape> fan(float radius, float startAngle, float endAngle) {
    return shape -> {
      // normalise the angles to simplify the lerp
      float sa = startAngle;
      float ea = endAngle;
      while (sa < 0) {
        sa += Math.PI_TIMES_2_f;
        ea += Math.PI_TIMES_2_f;
      }
      for (int i = 0; i < shape.vertices(); i++) {
        float angle = sa + i * (ea - sa) / (shape.vertices() - 1);
        shape.vertex(i, v -> v.set(
            radius * Math.cos(angle),
            radius * Math.sin(angle)
            , 0
        ));
      }
    };
  }

  /**
   * Places the first vertex at the origin, and the remaining vertices on the circumference of a
   * circle around the origin, evenly spaced in the anticlockwise direction between the two angles
   *
   * <pre>
   * 3─2─1
   * │   │
   * 4---0
   * </pre>
   *
   * @param radius     circle radius
   * @param startAngle in radians
   * @param endAngle   in radians
   * @return
   */
  public static Consumer<Shape> wheel(float radius, float startAngle, float endAngle) {
    return shape -> {
      // normalise the angles to simplify the lerp
      float sa = startAngle;
      float ea = endAngle;
      while (sa < 0) {
        sa += Math.PI_TIMES_2_f;
        ea += Math.PI_TIMES_2_f;
      }
      shape.vertex(0, v -> v.set(0, 0, 0));
      for (int i = 1; i < shape.vertices(); i++) {
        float angle = sa + (i - 1) * (ea - sa) / (shape.vertices() - 2);
        shape.vertex(i, v -> v.set(
            radius * Math.cos(angle),
            radius * Math.sin(angle)
            , 0
        ));
      }
    };
  }

  /**
   * Places vertices on a circular border around the origin
   * <pre>
   * 5---3
   * │   │
   * 4-2 │
   *   │ │
   *   0-1
   * </pre>
   *
   * @param radius
   * @param width
   * @param startAngle
   * @param endAngle
   * @return
   */
  public static Consumer<Shape> border(float radius, float width, float startAngle,
      float endAngle) {
    return shape -> {
      // normalise the angles to simplify the lerp
      float sa = startAngle;
      float ea = endAngle;
      while (sa < 0) {
        sa += Math.PI_TIMES_2_f;
        ea += Math.PI_TIMES_2_f;
      }
      for (int i = 0; i < shape.vertices() / 2; i++) {
        float angle = sa + i * (ea - sa) / ((shape.vertices() / 2) - 1);
        float hw = width / 2;
        float x = Math.cos(angle);
        float y = Math.sin(angle);

        shape.vertex(i * 2, v -> v.set(
            (radius - hw) * x,
            (radius - hw) * y
            , 0
        ));
        shape.vertex(i * 2 + 1, v -> v.set(
            (radius + hw) * x,
            (radius + hw) * y
            , 0
        ));
      }
    };
  }
}
