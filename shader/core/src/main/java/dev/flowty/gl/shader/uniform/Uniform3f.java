package dev.flowty.gl.shader.uniform;

import static org.lwjgl.opengl.GL20.glUniform3f;

import dev.flowty.gl.shader.Program;
import org.joml.Vector3f;

/**
 * Represents a 3-value float vector uniform
 */
public class Uniform3f extends Uniform {

  /**
   * The value
   */
  public final Vector3f value = new Vector3f();

  /**
   * @param program The program
   * @param name    The uniform name
   */
  public Uniform3f(Program program, String name) {
    super(program, name);
  }

  @Override
  public void populate() {
    glUniform3f(location(), value.x(), value.y(), value.z());
  }
}
