package dev.flowty.gl.framework;

import static org.lwjgl.opengl.ARBImaging.GL_TABLE_TOO_LARGE;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;

import org.lwjgl.opengl.GL11;

/**
 * Misc utils
 */
public class GLUtil {

  private GLUtil() {
    // no instances
  }

  /**
   * Checks the current context for OpenGL errors.
   *
   * @throws RuntimeException if {@link GL11#glGetError GetError} returns anything other than
   *                          {@link GL11#GL_NO_ERROR NO_ERROR}
   */
  public static void checkGLError() {
    int err = glGetError();
    if (err != GL_NO_ERROR) {
      throw new RuntimeException(String.format(
          "%s [0x%X]",
          getErrorString(err), err));
    }
  }

  /**
   * Translates an OpenGL error code to a String describing the error.
   *
   * @param errorCode the error code, as returned by {@link GL11#glGetError GetError}.
   * @return the error description
   */
  public static String getErrorString(int errorCode) {
    return switch (errorCode) {
      case GL_NO_ERROR -> "No error";
      case GL_INVALID_ENUM -> "Enum argument out of range";
      case GL_INVALID_VALUE -> "Numeric argument out of range";
      case GL_INVALID_OPERATION -> "Operation illegal in current state";
      case GL_STACK_OVERFLOW -> "Command would cause a stack overflow";
      case GL_STACK_UNDERFLOW -> "Command would cause a stack underflow";
      case GL_OUT_OF_MEMORY -> "Not enough memory left to execute command";
      case GL_INVALID_FRAMEBUFFER_OPERATION -> "Framebuffer object is not complete";
      case GL_TABLE_TOO_LARGE -> "The specified table is too large";
      default -> String.format("%s [0x%X]", "Unknown", errorCode);
    };
  }
}
