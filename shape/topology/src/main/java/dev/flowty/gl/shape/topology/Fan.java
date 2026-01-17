package dev.flowty.gl.shape.topology;

import dev.flowty.gl.shape.Shape;

/**
 * A triangle fan, with index order
 *
 * <pre>
 * 6─5─4
 *  ╲│╱│
 *   0─3
 *   │╲│
 *   1─2
 * </pre>
 */
public class Fan extends Shape {

  /**
   * @param tris The number of triangles in the fan
   */
  public Fan(int tris) {
    super(new float[(2 + tris) * 3], indices(tris));
  }

  private static int[] indices(int tris) {
    int[] indices = new int[3 * tris];

    for (int i = 0; i < tris; i++) {
      indices[3 * i + 0] = 0;
      indices[3 * i + 1] = i + 1;
      indices[3 * i + 2] = i + 2;
    }
    return indices;
  }
}
