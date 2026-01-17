package dev.flowty.gl.shader.flat;

import dev.flowty.gl.shape.Shape;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A {@link Shape} with per-vertex colour information
 */
public class ColouredShape extends Shape {

  /**
   * per-vertex packed RGBA colours
   */
  public final int[] colours;

  /**
   * @param geometry The shape geometry
   * @param colour   The colour for all vertices
   */
  public ColouredShape(Shape geometry, int colour) {
    super(geometry.vertices, geometry.triangles);
    colours = new int[geometry.vertices()];
    Arrays.fill(colours, colour);
  }

  /**
   * Combines shapes into one geometry
   *
   * @param constituents The shapes to combine
   */
  public ColouredShape(ColouredShape... constituents) {
    super(constituents);
    colours = new int[vertices()];
    int ci = 0;
    for (ColouredShape c : constituents) {
      System.arraycopy(c.colours, 0, colours, ci, c.colours.length);
      ci += c.colours.length;
    }
  }

  /**
   * Updates a vertex colour
   *
   * @param idx    The index of the vertex
   * @param update How to alter the colour
   * @return <code>this</code>
   */
  public ColouredShape colour(int idx, IntUnaryOperator update) {
    colours[idx] = update.applyAsInt(colours[idx]);
    return this;
  }

  @Override // for the return type
  public ColouredShape vertex(int idx, Consumer<Vector3f> update) {
    super.vertex(idx, update);
    return this;
  }

  @Override
  public ColouredShape transform(Matrix4f tr) {
    super.transform(tr);
    return this;
  }
}
