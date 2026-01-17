package dev.flowty.gl.framework.display;

import org.lwjgl.opengl.GL11;

/**
 * Rasterisation modes
 */
public enum PolygonMode {
  /**
   * Triangles are filled
   */
  FILL(GL11.GL_FILL),
  /**
   * Triangles are outlined
   */
  LINE(GL11.GL_LINE),
  /**
   * Vertices are pointed
   */
  POINT(GL11.GL_POINT),
  ;

  /**
   * The second argument to {@link GL11#glPolygonMode(int, int)}
   */
  public final int mode;

  PolygonMode(int mode) {
    this.mode = mode;
  }
}
