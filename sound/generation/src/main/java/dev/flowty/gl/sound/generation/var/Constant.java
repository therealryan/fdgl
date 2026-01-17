package dev.flowty.gl.sound.generation.var;

import dev.flowty.gl.sound.generation.Variable;

/**
 * A non-changing variable
 */
public class Constant implements Variable {

  /**
   * The value returned by this envelope for any time
   */
  public final float value;

  /**
   * @param value the value
   */
  public Constant(float value) {
    this.value = value;
  }

  @Override
  public float getValue(float time) {
    return value;
  }

}
