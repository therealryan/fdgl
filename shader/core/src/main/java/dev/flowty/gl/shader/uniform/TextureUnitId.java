package dev.flowty.gl.shader.uniform;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

import dev.flowty.gl.shader.Program;
import org.lwjgl.opengl.GL13;

/**
 * Represents a single integer uniform value, where that is being used to identify a texture unit.
 * This calls allows you to use the constants like {@link GL13#GL_TEXTURE1} when setting the uniform
 * value, while this class handles translating that into the 0-based values used in the shader.
 */
public class TextureUnitId extends Uniform1i {

  /**
   * @param program The program
   * @param name    The uniform name
   */
  public TextureUnitId(Program program, String name) {
    super(program, name);
  }

  @Override
  public void set(int v) {
    super.set(v - GL_TEXTURE0);
  }
}
