package dev.flowty.gl.shape;

import org.joml.Vector3f;

/**
 * An axis-aligned bounding box
 */
public class Bounds {

  /**
   * The corner of the box closest to negative infinity on all axes
   */
  public final Vector3f min = new Vector3f(Float.NaN);
  /**
   * The corner of the box closest to positive infinity on all axes
   */
  public final Vector3f max = new Vector3f(Float.NaN);

  /**
   * Expands the bounds to include a point
   *
   * @param point The point
   * @return <code>this</code>
   */
  public Bounds include(Vector3f point) {
    if (Float.isNaN(min.x) && Float.isNaN(min.y) && Float.isNaN(min.z)) {
      min.set(point);
    }
    if (Float.isNaN(max.x) && Float.isNaN(max.y) && Float.isNaN(max.z)) {
      max.set(point);
    }

    min.x = Math.min(min.x, point.x);
    min.y = Math.min(min.y, point.y);
    min.z = Math.min(min.z, point.z);

    max.x = Math.max(max.x, point.x);
    max.y = Math.max(max.y, point.y);
    max.z = Math.max(max.z, point.z);

    return this;
  }

  /**
   * Expands the bounds to include another
   *
   * @param point The other bounds
   * @return <code>this</code>
   */
  public Bounds include(Bounds bounds) {
    return include(bounds.min).include(bounds.max);
  }

  /**
   * Resets the {@link Bounds} to encompass no space
   *
   * @return <code>this</code>
   */
  public Bounds clear() {
    min.set(Float.NaN);
    max.set(Float.NaN);
    return this;
  }

  /**
   * @return The extent of the box on the x axis
   */
  public float width() {
    return max.x - min.x;
  }

  /**
   * @return The extent of the box on the y axis
   */
  public float height() {
    return max.y - min.y;
  }

  /**
   * @return The extent of the box on the z axis
   */
  public float depth() {
    return max.z - min.z;
  }

  public Bounds translate(float x, float y, float z) {
    min.add(x, y, z);
    max.add(x, y, z);
    return this;
  }

  @Override
  public String toString() {
    return String.format("(%s,%s,%s) w%s h%s d%s",
        min.x(), min.y(), min.z(),
        width(), height(), depth());
  }
}
