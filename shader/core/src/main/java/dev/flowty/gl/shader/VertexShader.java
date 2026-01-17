package dev.flowty.gl.shader;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * A vertex shader
 */
public class VertexShader extends Shader {

  /**
   * @param source shader source code
   */
  public VertexShader(String source) {
    super(source, GL_VERTEX_SHADER);
  }
}
