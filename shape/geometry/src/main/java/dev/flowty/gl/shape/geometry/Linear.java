package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Shape;
import java.util.function.Consumer;
import org.joml.Vector2f;

/**
 * Utility methods for constructing linear geometries
 */
public class Linear {

  private Linear() {
    // no instances
  }

  /**
   * Sets as many vertices of the shape as required to follow a connected sequence of line
   * segments:
   *
   * <pre>
   * 1─3─5─7...
   * a b c d...
   * 0─2─4─6...
   * </pre>
   *
   * @param width    Line width
   * @param vertices The <code>x,y</code> pairs of the line segments
   * @return a shape update operation that sets two vertices for every input point
   */
  public static Consumer<Shape> strip(float width, float... vertices) {
    return partialStrip(width, vertices.length / 2, vertices);
  }

  /**
   * Sets as many vertices of the shape as required to follow a connected sequence of line
   * segments:
   *
   * <pre>
   * 1─3─5─7...
   * a b c d...
   * 0─2─4─6...
   * </pre>
   *
   * @param width    Line width
   * @param count    The number of vertices to read from the array
   * @param vertices The <code>x,y</code> pairs of the line segments
   * @return a shape update operation that sets two vertices for every input point
   */
  public static Consumer<Shape> partialStrip(float width, int count, float... vertices) {
    Vector2f a = new Vector2f();
    Vector2f b = new Vector2f();
    Vector2f c = new Vector2f();
    Vector2f ab = new Vector2f();
    Vector2f bc = new Vector2f();
    return shape -> {
      a.set(vertices[0], vertices[1]);
      b.set(vertices[2], vertices[3]);
      ab.set(b).sub(a).perpendicular().normalize(width / 2);
      shape.vertex(0, v -> v.set(a.x + ab.x, a.y + ab.y, 0));
      shape.vertex(1, v -> v.set(a.x - ab.x, a.y - ab.y, 0));

      for (int i = 2; i < 2 * count - 2; i += 2) {
        a.set(vertices[i - 2], vertices[i - 1]);
        b.set(vertices[i + 0], vertices[i + 1]);
        c.set(vertices[i + 2], vertices[i + 3]);

        ab.set(b).sub(a).perpendicular().normalize();
        bc.set(c).sub(b).perpendicular().normalize();
        ab.add(bc).normalize(width / 2);
        shape.vertex(i + 0, v -> v.set(b.x + ab.x, b.y + ab.y, 0));
        shape.vertex(i + 1, v -> v.set(b.x - ab.x, b.y - ab.y, 0));
      }
      b.set(vertices[2 * count - 4], vertices[2 * count - 3]);
      c.set(vertices[2 * count - 2], vertices[2 * count - 1]);
      bc.set(c).sub(b).perpendicular().normalize(width / 2);
      shape.vertex(shape.vertices() - 2, v -> v.set(c.x + bc.x, c.y + bc.y, 0));
      shape.vertex(shape.vertices() - 1, v -> v.set(c.x - bc.x, c.y - bc.y, 0));
    };
  }

  /**
   * Sets as many vertices of the shape as required to follow a connected loop of line segments
   *
   * @param width    Line width
   * @param vertices The <code>x,y</code> pairs of the loop segments
   * @return a shape update operation that sets two vertices for every input point
   */
  public static Consumer<Shape> loop(float width, float... vertices) {
    return shape -> {
      Vector2f a = new Vector2f();
      Vector2f b = new Vector2f();
      Vector2f c = new Vector2f();
      Vector2f ab = new Vector2f();
      Vector2f bc = new Vector2f();

      for (int i = 0; i < vertices.length; i += 2) {
        a.set(get(vertices, i - 2), get(vertices, i - 1));
        b.set(get(vertices, i + 0), get(vertices, i + 1));
        c.set(get(vertices, i + 2), get(vertices, i + 3));

        ab.set(b).sub(a).perpendicular().normalize();
        bc.set(c).sub(b).perpendicular().normalize();
        ab.add(bc).normalize(width / 2);
        shape.vertex(i + 0, v -> v.set(b.x + ab.x, b.y + ab.y, 0));
        shape.vertex(i + 1, v -> v.set(b.x - ab.x, b.y - ab.y, 0));
      }
    };
  }

  /**
   * Array access with wrap-around indexing
   *
   * @param array The array
   * @param index The index, which might be negative or greater than the array length
   * @return The wrap-around indexed array value
   */
  private static float get(float[] array, int index) {
    int i = index;
    if (i < 0) {
      i += array.length * (1 + index / array.length);
    }
    i %= array.length;
    return array[i];
  }
}
