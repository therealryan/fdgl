package dev.flowty.gl.shader.flat;

import dev.flowty.gl.shader.Program;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

/**
 * Draws geometry
 *
 * @param <S> self type
 * @param <T> program type
 */
public class Renderer<S extends Renderer<S, T>, T extends Program> {

  private final T program;

  private final Matrix4fStack matrixStack;

  /**
   * @param program          The shader program
   * @param matrixStackDepth The maximum depth of the matrix stack
   */
  public Renderer(T program, int matrixStackDepth) {
    this.program = program;
    matrixStack = new Matrix4fStack(matrixStackDepth);
  }

  /**
   * @return the shader program
   */
  public T program() {
    return program;
  }

  /**
   * @return The current matrix
   */
  public Matrix4f transform() {
    return matrixStack;
  }

  /**
   * Performs some rendering under a new transform
   *
   * @param transform How to update the current transform
   * @param action    What to do under that new transform
   * @return <code>this</code>
   */
  public S with(Consumer<Matrix4f> transform, Consumer<S> action) {
    pushMatrix();
    transform.accept(transform());
    action.accept(self());
    popMatrix();
    return self();
  }

  /**
   * @return <code>this</code>
   */
  @SuppressWarnings("unchecked")
  protected S self() {
    return (S) this;
  }

  /**
   * Pushes a matrix onto the stack
   *
   * @return {@code this}
   */
  public S pushMatrix() {
    matrixStack.pushMatrix();
    return self();
  }

  /**
   * Pops a matrix from the stack
   *
   * @return {@code this}
   */
  public S popMatrix() {
    matrixStack.popMatrix();
    return self();
  }
}
