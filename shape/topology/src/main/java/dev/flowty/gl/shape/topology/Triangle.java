package dev.flowty.gl.shape.topology;

import dev.flowty.gl.shape.Shape;

/**
 * A single triangle, index order:
 *
 * <pre>
 * 1
 * │╲
 * 0─2
 * </pre>
 */
public class Triangle extends Shape {

  /***/
  public Triangle() {
    super(
        new float[9],
        new int[]{
            0, 1, 2,
        });
  }
}
