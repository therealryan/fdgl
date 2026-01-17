package dev.flowty.gl.shape.topology;

import dev.flowty.gl.shape.Shape;

/**
 * Index order
 *
 * <pre>
 * 1─3─5─7...
 * │╲│╲│╲│...
 * 0─2─4─6...
 * </pre>
 */
public class Strip extends Shape {

  /**
   * @param quads the number of quads in the strip
   */
  public Strip(int quads) {
    super(new float[(2 + 2 * quads) * 3], indices(quads));
  }

  private static int[] indices(int quads) {
    int[] indices = new int[2 * 3 * quads];

    for (int i = 0; i < quads; i++) {
      indices[i * 6 + 0] = i * 2 + 0;
      indices[i * 6 + 1] = i * 2 + 1;
      indices[i * 6 + 2] = i * 2 + 2;

      indices[i * 6 + 3] = i * 2 + 1;
      indices[i * 6 + 4] = i * 2 + 3;
      indices[i * 6 + 5] = i * 2 + 2;
    }
    return indices;
  }
}
