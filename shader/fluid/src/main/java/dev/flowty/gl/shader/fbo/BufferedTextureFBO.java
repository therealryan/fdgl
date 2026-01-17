package dev.flowty.gl.shader.fbo;

import dev.flowty.gl.shader.fbo.TextureFBO.Filter;
import dev.flowty.gl.shader.fbo.TextureFBO.Format;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Two swappable {@link TextureFBO}
 */
public class BufferedTextureFBO {

  private final Vector2i size = new Vector2i();

  private final Format format;
  private final Filter filter;

  private TextureFBO read;
  private TextureFBO write;

  /**
   * @param size   in pixels
   * @param format Texture format
   * @param filter Texture min/mag filter
   */
  public BufferedTextureFBO(Vector2ic size, Format format, Filter filter) {
    this.size.set(size);
    this.format = format;
    this.filter = filter;
  }

  /**
   * Creates a new framebuffer with the same contents as this one. This buffer will be destroyed.
   *
   * @param newSize in pixels
   * @param copy    How to copy contents
   * @param blit    How to copy contents
   * @return The new buffer
   */
  public BufferedTextureFBO resize(Vector2ic newSize, CopyProgram copy, Blit blit) {
    if (size.x() != newSize.x() || size.y() != newSize.y()) {
      BufferedTextureFBO resized = new BufferedTextureFBO(newSize, format, filter);
      if (read != null) {
        resized.read = read.resize(newSize, copy, blit);
      }
      if (write != null) {
        resized.write = write.resize(newSize, copy, blit);
      }
      destroy();
      return resized;
    }
    return this;
  }

  /**
   * Destroys the framebuffers
   */
  public void destroy() {
    if (read != null) {
      read.destroy();
      read = null;
    }
    if (write != null) {
      write.destroy();
      write = null;
    }
  }

  /**
   * @return The read buffer
   */
  public TextureFBO read() {
    if (read == null) {
      read = new TextureFBO(size, format, filter);
    }
    return read;
  }

  /**
   * @return The write buffer
   */
  public TextureFBO write() {
    if (write == null) {
      write = new TextureFBO(size, format, filter);
    }
    return write;
  }

  /**
   * Swaps the read and write buffers
   */
  public void swap() {
    TextureFBO tmp = read;
    read = write;
    write = tmp;
  }
}
