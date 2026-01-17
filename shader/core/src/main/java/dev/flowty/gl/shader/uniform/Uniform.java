package dev.flowty.gl.shader.uniform;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

import dev.flowty.gl.shader.Program;

/**
 * Represents a single uniform variable in a program
 */
public abstract class Uniform {

  private final Program program;
  private final String name;
  private int location = -1;

  /**
   * @param program The owning program
   * @param name    The uniform name
   */
  public Uniform(Program program, String name) {
    this.program = program;
    this.name = name;
  }

  /**
   * @return The uniform location
   */
  public int location() {
    if (location == -1) {
      location = glGetUniformLocation(program.handle(), name);
    }
    return location;
  }

  /**
   * Causes the location to be refreshed on next access
   */
  public void delete() {
    location = -1;
  }

  /**
   * Implement this to populate the uniform value into opengl
   */
  public abstract void populate();
}
