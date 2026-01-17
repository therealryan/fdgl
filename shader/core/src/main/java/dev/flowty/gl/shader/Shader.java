package dev.flowty.gl.shader;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

/**
 * A shader object
 */
public abstract class Shader {

  private final String source;
  private final int type;

  /**
   * The ID of the shader
   */
  private int handle = 0;

  /**
   * @param source The source code of the shader
   * @param type   The shader type
   */
  protected Shader(String source, int type) {
    this.source = source;
    this.type = type;
  }

  /**
   * @return The ID of the compiled shader
   */
  public int handle() {
    if (handle == 0) {
      handle = glCreateShader(type);
      glShaderSource(handle, source);
      glCompileShader(handle);
      int status = glGetShaderi(handle, GL_COMPILE_STATUS);
      if (status == 0) {
        throw new IllegalStateException(String.format(
            "Failed to compile %s!\n%s",
            getClass().getSimpleName(),
            glGetShaderInfoLog(handle)));
      }
    }
    return handle;
  }

  /**
   * Deletes the shader
   */
  public void delete() {
    glDeleteShader(handle);
    handle = 0;
  }
}
