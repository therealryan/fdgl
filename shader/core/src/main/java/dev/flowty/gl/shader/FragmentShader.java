package dev.flowty.gl.shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;

/**
 * A fragment shader
 */
public class FragmentShader extends Shader {

  /**
   * @param source shader source code
   */
  public FragmentShader(String source) {
    super(source, GL_FRAGMENT_SHADER);
  }
}
