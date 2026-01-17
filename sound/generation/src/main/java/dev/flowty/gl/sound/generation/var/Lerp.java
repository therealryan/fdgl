package dev.flowty.gl.sound.generation.var;

import dev.flowty.gl.sound.generation.Variable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Linearly interpolates a sequence of (time, value) pairs. Querying for times less than the
 * minimum-time point will return the minimum-time value, likewise for maximum
 */
public class Lerp implements Variable {

  /**
   *
   */
  public ArrayList<float[]> points = new ArrayList<>();

  /**
   * Set it true if you've been monkeying with the points
   */
  public boolean orderDirty = true;

  private static final Comparator<float[]> c = (o1, o2) -> {
    if (o1[0] < o2[0]) {
      return -1;
    } else if (o1[0] > o2[0]) {
      return 1;
    }
    return 0;
  };

  /**
   * @param points x, y value points
   */
  public Lerp(float... points) {
    for (int i = 0; i < points.length; i += 2) {
      this.points.add(new float[]{points[i], points[i + 1]});
    }
  }

  /**
   * Deep copy constructor
   *
   * @param t to copy
   */
  public Lerp(Lerp t) {
    for (float[] p : t.points) {
      addPoint(p[0], p[1]);
    }
    orderDirty = true;
  }

  @Override
  public float getValue(float time) {
    enforceOrder();

    if (points.size() == 0) {
      return 0;
    }

    float[] temp = {time};
    int index = Collections.binarySearch(points, temp, c);
    if (index >= 0) {
      return points.get(index)[1];
    }

    index++;
    index = -index;

    if (index == 0) {
      return points.get(0)[1];
    } else if (index >= points.size()) {
      return points.get(points.size() - 1)[1];
    } else { // general case
      float[] low = points.get(index - 1);
      float[] high = points.get(index);
      float lt = low[0];
      float ht = high[0];
      float lv = low[1];
      float hv = high[1];

      float dt = ht - lt;
      float dv = hv - lv;

      float result = lv + dv * (time - lt) / dt;

      return result;
    }
  }

  private void enforceOrder() {
    if (orderDirty) {
      Collections.sort(points, c);
    }
  }

  /**
   * Adds a point to this envelope. If this point coincides with an existing point, that point is
   * replaced
   *
   * @param time  The time
   * @param value The value
   */
  public void addPoint(float time, float value) {
    points.add(new float[]{time, value});
    orderDirty = true;
  }

  /**
   * Clears the current terrain
   */
  public void clear() {
    points.clear();
  }

  /**
   * Reads the values of this terrain from a stream
   *
   * @param dis data source
   * @throws IOException if something goes wrong
   */
  public void read(DataInputStream dis) throws IOException {
    clear();

    int l = dis.readInt();

    for (int i = 0; i < l; i++) {
      addPoint(dis.readFloat(), dis.readFloat());
    }

    orderDirty = true;
  }

  /**
   * Writes this {@link Lerp} to a buffer
   *
   * @param dos data sink
   * @throws IOException on failure
   */
  public void write(DataOutputStream dos) throws IOException {
    dos.writeInt(points.size());

    for (float[] p : points) {
      dos.writeFloat(p[0]);
      dos.writeFloat(p[1]);
    }
  }

  /**
   * Removes a point from the terrain
   *
   * @param x The time
   * @param y The value
   * @return <code>true</code> if a point was removed
   */
  public boolean removePoint(float x, float y) {
    for (int i = 0; i < points.size(); i++) {
      if (points.get(i)[0] == x && points.get(i)[1] == y) {
        points.remove(i);
        return true;
      }
    }
    return false;
  }

  /**
   * Alters the points in this terrain slightly, while preserving their order
   *
   * @param rng  source of randomness
   * @param xMag the maximum magnitude of the mutation in the x-axis
   * @param yMag the maximum magnitude of the mutation in the y-axis
   */
  @SuppressWarnings("unused")
  public void mutate(Random rng, float xMag, float yMag) {
    for (float[] point : points) {
      int i = rng.nextInt(points.size());

      float px = i < 1 ? -Float.MAX_VALUE : points.get(i - 1)[0];
      float[] p = points.get(i);
      float nx = i == points.size() - 1 ? Float.MAX_VALUE : points.get(i + 1)[0];

      float mx = (2 * rng.nextFloat() - 1) * xMag;

      mx = Math.max(px - p[0], mx);
      mx = Math.min(nx - p[0], mx);

      float my = (2 * rng.nextFloat() - 1) * yMag;

      p[0] += mx;
      p[1] += my;
    }
  }

  /**
   * Ensures that all points lie within the specified range
   *
   * @param minX minimum time
   * @param minY minimum value
   * @param maxX maximum time
   * @param maxY maximum value
   */
  public void enforceBounds(float minX, float minY, float maxX, float maxY) {
    for (float[] point : points) {
      float[] p = point;

      if (p[0] < minX) {
        p[0] = minX;
      } else if (p[0] > maxX) {
        p[0] = maxX;
      }

      if (p[1] < minY) {
        p[1] = minY;
      } else if (p[1] > maxY) {
        p[1] = maxY;
      }
    }
  }

  /**
   * Sets this terrain to have a number of random points within specified bounds
   *
   * @param rng  Source of randmoness
   * @param p    The number of points
   * @param minX minimum time
   * @param minY minimum value
   * @param maxX maximum time
   * @param maxY maximum value
   */
  public void randomise(Random rng, int p, float minX, float minY, float maxX,
      float maxY) {
    clear();

    float dx = maxX - minX;
    float dy = maxY - minY;

    for (int i = 0; i < p; i++) {
      addPoint(minX + rng.nextFloat() * dx, minY + rng.nextFloat() * dy);
    }
  }
}
