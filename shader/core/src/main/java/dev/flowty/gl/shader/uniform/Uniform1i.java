package dev.flowty.gl.shader.uniform;

import static org.lwjgl.opengl.GL20.glUniform1i;

import dev.flowty.gl.shader.Program;

/**
 * Represents a single integer uniform value
 */
public class Uniform1i extends Uniform {

  private int value;

  /**
   * @param program The program
   * @param name    The uniform name
   */
  public Uniform1i(Program program, String name) {
    super(program, name);
  }

  /**
   * Sets the value
   *
   * @param v the new value
   */
  public void set(int v) {
    value = v;
  }

  @Override
  public void populate() {
    glUniform1i(location(), value);
  }
}
