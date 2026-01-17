package dev.flowty.gl.util;

public class Angle {

  private static final float PI_f = (float) java.lang.Math.PI;
  public static final float PI_TIMES_2_f = PI_f * 2.0f;

  public static float delta(float a, float b) {
    float d = a - b;
    if (d > PI_f) {
      d -= PI_TIMES_2_f;
    }
    if (d < -PI_f) {
      d += PI_TIMES_2_f;
    }
    return d;
  }

  public static float diff(float a, float b) {
    return Math.abs(delta(a, b));
  }
}
