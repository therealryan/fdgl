package dev.flowty.gl.shader.uniform;

import static org.lwjgl.opengl.GL20.glUniform1f;

import dev.flowty.gl.shader.Program;

/**
 * Represents a single float uniform value
 */
public class Uniform1f extends Uniform {

  private float value;

  /**
   * @param program The program
   * @param name    The uniform name
   */
  public Uniform1f(Program program, String name) {
    super(program, name);
  }

  /**
   * Sets the value
   *
   * @param v the new value
   */
  public void set(float v) {
    value = v;
  }

  @Override
  public void populate() {
    glUniform1f(location(), value);
  }
}
