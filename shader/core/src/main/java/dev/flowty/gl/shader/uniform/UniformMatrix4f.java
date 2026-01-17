package dev.flowty.gl.shader.uniform;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import dev.flowty.gl.shader.Program;
import java.nio.FloatBuffer;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

/**
 * A 4x4 float matrix uniform
 */
public class UniformMatrix4f extends Uniform {

  private final Matrix4f matrix = new Matrix4f();
  private final FloatBuffer matrixFb = BufferUtils.createFloatBuffer(16);

  /**
   * @param program The program
   * @param name    The uniform name
   */
  public UniformMatrix4f(Program program, String name) {
    super(program, name);
  }

  /**
   * Updates the matrix
   *
   * @param m How to update the matrix
   */
  public void update(Consumer<Matrix4f> m) {
    m.accept(matrix);
  }

  @Override
  public void populate() {
    matrix.get(matrixFb);
    glUniformMatrix4fv(location(), false, matrixFb);
  }
}
