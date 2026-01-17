package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Shape;
import java.util.function.Consumer;

public class Cruciform {

  private Cruciform() {
    // no instances
  }

  /**
   * Sets the first 12 vertices in the unit square
   *
   * <pre>
   *   8─7
   *   │ │
   * a─9 6─5
   * │     │
   * b─0 3─4
   *   │ │
   *   1─2
   * </pre>
   *
   * @param width the width of the arms
   * @return a shape update operation that sets the first 12 vertices
   */
  public static Consumer<Shape> unit(float width) {

    float n = 0.5f - width / 2;
    float f = n + width;

    return shape -> {
      shape.vertex(0, v -> v.set(n, n, v.z));
      shape.vertex(1, v -> v.set(n, 0, v.z));
      shape.vertex(2, v -> v.set(f, 0, v.z));
      shape.vertex(3, v -> v.set(f, n, v.z));
      shape.vertex(4, v -> v.set(1, n, v.z));
      shape.vertex(5, v -> v.set(1, f, v.z));
      shape.vertex(6, v -> v.set(f, f, v.z));
      shape.vertex(7, v -> v.set(f, 1, v.z));
      shape.vertex(8, v -> v.set(n, 1, v.z));
      shape.vertex(9, v -> v.set(n, f, v.z));
      shape.vertex(10, v -> v.set(0, f, v.z));
      shape.vertex(11, v -> v.set(0, n, v.z));
    };
  }
}
