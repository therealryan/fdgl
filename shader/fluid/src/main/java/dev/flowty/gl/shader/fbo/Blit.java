package dev.flowty.gl.shader.fbo;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

import java.nio.IntBuffer;
import org.joml.Vector2ic;
import org.lwjgl.BufferUtils;

/**
 * Draws a single screen-sized quad to a {@link TextureFBO} target
 */
public class Blit {

  private static final float[] VERTICES = {
      -1, -1,
      -1, 1,
      1, 1,
      1, -1};

  private int vertexBuffer = 0;

  private static final IntBuffer INDICES = BufferUtils.createIntBuffer(6);

  static {
    INDICES.put(0, new int[]{0, 1, 2, 0, 2, 3});
  }

  /**
   * Draws to a target FBO
   *
   * @param target The render target
   */
  public void to(TextureFBO target) {
    target.bind();
    render();
  }

  /**
   * Draws to the display
   *
   * @param resolution in pixels
   */
  public void toDisplay(Vector2ic resolution) {
    TextureFBO.unbind();
    glViewport(0, 0, resolution.x(), resolution.y());
    render();
  }

  private void render() {
    if (vertexBuffer == 0) {
      vertexBuffer = glCreateBuffers();
    }
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, VERTICES, GL_STATIC_DRAW);
    glEnableVertexAttribArray(CopyProgram.VERTEX_ARRAY_ATTRIBUTE_INDEX);
    glVertexAttribPointer(CopyProgram.VERTEX_ARRAY_ATTRIBUTE_INDEX,
        2, GL_FLOAT, false, 0, 0);

    glDrawElements(GL_TRIANGLES, INDICES);
  }

  /**
   * Deletes our geometry buffers
   */
  public void destroy() {
    glDeleteBuffers(vertexBuffer);
    vertexBuffer = 0;
  }
}
