package dev.flowty.gl.shader;

import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

import dev.flowty.gl.shader.uniform.Uniform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A complete shader program
 */
public abstract class Program {

  private final VertexShader vertex;
  private final FragmentShader fragment;
  private final List<Uniform> uniforms = new ArrayList<>();

  /**
   * The ID of the linked program
   */
  private int handle = 0;

  /**
   * Links shaders into a program
   *
   * @param vertex   The vertex shader
   * @param fragment The fragment shader
   */
  public Program(VertexShader vertex, FragmentShader fragment) {
    this.vertex = vertex;
    this.fragment = fragment;
  }

  /**
   * Populates the uniforms for automatic handling
   *
   * @param u The uniforms in the program
   */
  protected void uniforms(Uniform... u) {
    if (!uniforms.isEmpty()) {
      throw new IllegalStateException("Unexpected double-population of uniforms");
    }
    Collections.addAll(uniforms, u);
  }

  /**
   * @return The ID of the linked program
   */
  public int handle() {
    if (handle == 0) {
      handle = glCreateProgram();
      try {
        glAttachShader(handle, vertex.handle());
        glAttachShader(handle, fragment.handle());
      } catch (Exception e) {
        throw new IllegalStateException(String.format(
            "Failed to compile %s!\n",
            getClass().getSimpleName()), e);
      }
      glLinkProgram(handle);
      int success = glGetProgrami(handle, GL_LINK_STATUS);
      if (success == 0) {
        throw new IllegalStateException(String.format(
            "Failed to link %s!\n",
            getClass().getSimpleName(),
            glGetProgramInfoLog(handle)));
      }
      vertex.delete();
      fragment.delete();
    }
    return handle;
  }

  /**
   * Activates the program for rendering
   *
   * @return <code>this</code>
   */
  public Program use() {
    glUseProgram(handle());
    for (Uniform uniform : uniforms) {
      uniform.populate();
    }
    return this;
  }

  /**
   * Deletes the program
   */
  public void delete() {
    glDeleteProgram(handle);
    handle = 0;
    for (Uniform uniform : uniforms) {
      uniform.delete();
    }
  }
}
