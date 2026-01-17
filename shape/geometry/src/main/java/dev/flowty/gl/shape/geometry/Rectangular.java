package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Shape;
import java.util.function.Consumer;

/**
 * Utility methods for constructing rectangular geometries
 */
public class Rectangular {

  private Rectangular() {
    // no instances
  }

  /**
   * Sets the first 4 vertices:
   *
   * <pre>
   * 1─3
   * │ │
   * 0─2
   * </pre>
   *
   * @param minx The rectangle edge closest to negative infinity on the x-axis
   * @param miny The rectangle edge closest to negative infinity on the y-axis
   * @param maxx The rectangle edge closest to positive infinity on the x-axis
   * @param maxy The rectangle edge closest to positive infinity on the y-axis
   * @return a shape update operation that sets the first 4 vertices
   */
  public static Consumer<Shape> quad(float minx, float miny, float maxx, float maxy) {
    return shape -> {
      shape.vertex(0, v -> v.set(minx, miny, v.z));
      shape.vertex(1, v -> v.set(minx, maxy, v.z));
      shape.vertex(2, v -> v.set(maxx, miny, v.z));
      shape.vertex(3, v -> v.set(maxx, maxy, v.z));
    };
  }

  /**
   * Sets the first 8 vertices:
   *
   * <pre>
   * 6───4
   * │7─5│
   * ││ ││
   * │1─3│
   * 0───2
   * </pre>
   *
   * @param minx  The border edge closest to negative infinity on the x-axis
   * @param miny  The border edge closest to negative infinity on the y-axis
   * @param maxx  The border edge closest to positive infinity on the x-axis
   * @param maxy  The border edge closest to positive infinity on the y-axis
   * @param width The width of the border
   * @return a shape update operation that sets the first 8 vertices
   */
  public static Consumer<Shape> border(float minx, float miny, float maxx, float maxy,
      float width) {
    float hw = width / 2;
    return shape -> {
      shape.vertex(0, v -> v.set(minx - hw, miny - hw, v.z));
      shape.vertex(1, v -> v.set(minx + hw, miny + hw, v.z));
      shape.vertex(2, v -> v.set(maxx + hw, miny - hw, v.z));
      shape.vertex(3, v -> v.set(maxx - hw, miny + hw, v.z));
      shape.vertex(4, v -> v.set(maxx + hw, maxy + hw, v.z));
      shape.vertex(5, v -> v.set(maxx - hw, maxy - hw, v.z));
      shape.vertex(6, v -> v.set(minx - hw, maxy + hw, v.z));
      shape.vertex(7, v -> v.set(minx + hw, maxy - hw, v.z));
    };
  }

  /**
   * As with {@link #border(float, float, float, float, float)}, but the border lies entirely
   * <i>inside</i> the quad
   *
   * @param minx  The border edge closest to negative infinity on the x-axis
   * @param miny  The border edge closest to negative infinity on the y-axis
   * @param maxx  The border edge closest to positive infinity on the x-axis
   * @param maxy  The border edge closest to positive infinity on the y-axis
   * @param width The width of the border
   * @return a shape update operation that sets the first 8 vertices
   */
  public static Consumer<Shape> innerborder(float minx, float miny, float maxx, float maxy,
      float width) {
    float hw = width / 2;
    return border(
        minx + hw, miny + hw,
        maxx - hw, maxy - hw,
        width);
  }

  /**
   * As with {@link #border(float, float, float, float, float)}, but the border lies entirely
   * <i>outside</i> the quad
   *
   * @param minx  The border edge closest to negative infinity on the x-axis
   * @param miny  The border edge closest to negative infinity on the y-axis
   * @param maxx  The border edge closest to positive infinity on the x-axis
   * @param maxy  The border edge closest to positive infinity on the y-axis
   * @param width The width of the border
   * @return a shape update operation that sets the first 8 vertices
   */
  public static Consumer<Shape> outerborder(float minx, float miny, float maxx, float maxy,
      float width) {
    float hw = width / 2;
    return border(
        minx - hw, miny - hw,
        maxx + hw, maxy + hw,
        width);
  }
}
