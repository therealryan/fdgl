package dev.flowty.gl.shader.uniform;

import static org.lwjgl.opengl.GL20.glUniform2f;

import dev.flowty.gl.shader.Program;
import org.joml.Vector2f;

/**
 * Represents a 2-value float vector uniform
 */
public class Uniform2f extends Uniform {

  /**
   * The value
   */
  public final Vector2f value = new Vector2f();

  /**
   * @param program The program
   * @param name    The uniform name
   */
  public Uniform2f(Program program, String name) {
    super(program, name);
  }

  @Override
  public void populate() {
    glUniform2f(location(), value.x(), value.y());
  }
}
