package dev.flowty.gl.shader.fbo;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL45.glCreateFramebuffers;

import java.nio.ByteBuffer;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL45;

/**
 * Represents a
 * <a href="https://www.khronos.org/opengl/wiki/Framebuffer_Object">Frame Buffer
 * Object</a> backed by a texture
 */
public class TextureFBO {

  /**
   * Backing texture formats
   */
  public enum Format {
    /**
     * 4 float channels
     */
    RGBA(GL_RGBA16F, GL_RGBA),
    /**
     * 2 float channels
     */
    RG(GL_RG16F, GL_RG),
    /**
     * 1 float channel
     */
    R(GL_R16F, GL_RED),
    ;

    private final int internalFormat;
    private final int format;

    Format(int internalFormat, int format) {
      this.internalFormat = internalFormat;
      this.format = format;
    }
  }

  /**
   * Values for texture min and mag filters
   */
  public enum Filter {
    /**
     * Returns the value of the texture element that is nearest (in Manhattan distance) to the
     * center of the pixel being textured.
     */
    LINEAR(GL_LINEAR),
    /**
     * Returns the weighted average of the four texture elements that are closest to the center of
     * the pixel being textured. These can include border texture elements, depending on the values
     * of GL_TEXTURE_WRAP_S and GL_TEXTURE_WRAP_T, and on the exact mapping.
     */
    NEAREST(GL_NEAREST);

    private final int filter;

    Filter(int filter) {
      this.filter = filter;
    }

  }

  private final Vector2i size = new Vector2i();
  private final Format format;
  private final Filter filter;

  private int texture = 0;
  private int fbo = 0;

  /**
   * @param size   in pixels
   * @param format pixel format
   * @param filter texture min/mag filter
   */
  public TextureFBO(Vector2ic size, Format format, Filter filter) {
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
  public TextureFBO resize(Vector2ic newSize, CopyProgram copy, Blit blit) {
    if (size.x() != newSize.x() || size.y() != newSize.y()) {
      TextureFBO resized = new TextureFBO(newSize, format, filter);
      copy.from(attach(GL_TEXTURE0));
      copy.use();
      blit.to(resized);
      destroy();
      return resized;
    }
    return this;
  }

  /**
   * Deletes the framebuffer and texture
   */
  public void destroy() {
    glDeleteFramebuffers(fbo);
    fbo = 0;
    glDeleteTextures(texture);
    texture = 0;
  }

  /**
   * Sets this FBO's backing texture to be read from a texture unit
   *
   * @param textureUnit The index of the texture unit
   * @return the index of the texture unit
   * @see GL13#GL_TEXTURE0
   */
  public int attach(int textureUnit) {
    glActiveTexture(textureUnit);
    glBindTexture(GL_TEXTURE_2D, texture());
    return textureUnit;
  }

  /**
   * Sets this FBO as the render target
   */
  public void bind() {
    glViewport(0, 0, size.x(), size.y());
    glBindFramebuffer(GL_FRAMEBUFFER, fbo());
  }

  /**
   * Unsets all FBO render targets
   */
  public static void unbind() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
  }

  private int texture() {
    if (texture == 0) {
      texture = GL45.glCreateTextures(GL_TEXTURE_2D);
      glBindTexture(GL_TEXTURE_2D, texture);
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter.filter);
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter.filter);
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
      glTexImage2D(GL_TEXTURE_2D, 0, format.internalFormat, size.x(), size.y(), 0, format.format,
          GL_FLOAT, (ByteBuffer) null);
    }
    return texture;
  }

  private int fbo() {
    if (fbo == 0) {
      fbo = glCreateFramebuffers();
      glBindFramebuffer(GL_FRAMEBUFFER, fbo);
      glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture(), 0);
      glViewport(0, 0, size.x(), size.y());
      glClear(GL_COLOR_BUFFER_BIT);
    }
    return fbo;
  }
}
