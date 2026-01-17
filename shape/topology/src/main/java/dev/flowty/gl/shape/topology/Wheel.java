package dev.flowty.gl.shape.topology;

import dev.flowty.gl.shape.Shape;

/**
 * A closed {@link Fan}, with index order
 *
 * <pre>
 * 6─5─4
 * │╲│╱│
 * 7─0─3
 * │╱│╲│
 * 8─1─2
 * </pre>
 */
public class Wheel extends Shape {

  /**
   * @param edges The number of edges on the wheel
   */
  public Wheel(int edges) {
    super(new float[(1 + edges) * 3], indices(edges));
  }

  private static int[] indices(int edges) {
    int[] indices = new int[3 * edges];

    for (int i = 0; i < edges; i++) {
      indices[3 * i + 0] = 0;
      indices[3 * i + 1] = i + 1;
      indices[3 * i + 2] = i + 2;
    }
    indices[indices.length - 1] = 1;

    return indices;
  }
}
