package dev.flowty.gl.sound.generation.var;

import dev.flowty.gl.sound.generation.Variable;

/**
 * Multiplies two variables together
 */
public class Multiplication implements Variable {

  /**
   *
   */
  public Variable e;

  /**
   *
   */
  public Variable f;

  /**
   * @param e The first variable
   * @param f The second variable
   */
  public Multiplication(Variable e, Variable f) {
    this.e = e;
    this.f = f;
  }

  @Override
  public float getValue(float time) {
    return e.getValue(time) * f.getValue(time);
  }

}
