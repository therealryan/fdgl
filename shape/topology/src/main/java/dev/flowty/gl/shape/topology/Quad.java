package dev.flowty.gl.shape.topology;

import dev.flowty.gl.shape.Shape;

/**
 * Index order
 *
 * <pre>
 * 1─3
 * │╲│
 * 0─2
 * </pre>
 */
public class Quad extends Shape {

  /***/
  public Quad() {
    super(new float[12], new int[]{
        0, 1, 2,
        1, 3, 2,
    });
  }
}
