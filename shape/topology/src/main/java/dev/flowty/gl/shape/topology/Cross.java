package dev.flowty.gl.shape.topology;

import dev.flowty.gl.shape.Shape;

/**
 * Index order:
 * <pre>
 *   8─7
 *   │ │
 * a─9 6─5
 * │     │
 * b─0 3─4
 *   │ │
 *   1─2
 * </pre>
 */
public class Cross extends Shape {

  public Cross() {
    super(new float[12 * 3], new int[]{
        0, 1, 2,
        0, 2, 3,
        0, 3, 6,
        0, 6, 9,
        3, 4, 5,
        3, 5, 6,
        6, 7, 8,
        6, 8, 9,
        9, 10, 11,
        9, 11, 0
    });
  }

}
