package dev.flowty.gl.shape.topology;

import dev.flowty.gl.shape.Shape;

/**
 * Same as a {@link Strip}, but with two extra triangles to join the end of the strip to the start
 */
public class Loop extends Shape {

  /**
   * @param quads The number of quads in the loop
   */
  public Loop(int quads) {
    super(new float[(2 + 2 * (quads - 1)) * 3], indices(quads - 1));
  }

  private static int[] indices(int quads) {
    int[] indices = new int[2 * 3 * (quads + 1)];

    for (int i = 0; i < quads; i++) {
      indices[i * 6 + 0] = i * 2 + 0;
      indices[i * 6 + 1] = i * 2 + 1;
      indices[i * 6 + 2] = i * 2 + 2;

      indices[i * 6 + 3] = i * 2 + 1;
      indices[i * 6 + 4] = i * 2 + 3;
      indices[i * 6 + 5] = i * 2 + 2;
    }

    indices[quads * 6 + 0] = quads * 2 + 0;
    indices[quads * 6 + 1] = quads * 2 + 1;
    indices[quads * 6 + 2] = 0 * 2 + 0;

    indices[quads * 6 + 3] = quads * 2 + 1;
    indices[quads * 6 + 4] = 0 * 2 + 1;
    indices[quads * 6 + 5] = 0 * 2 + 0;

    return indices;
  }

}
