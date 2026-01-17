package dev.flowty.gl.framework;

import java.util.Map;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.APIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs GLFW errors to slf4j
 */
class GLFWErrorLogger implements GLFWErrorCallbackI {

  private static final Logger LOG = LoggerFactory.getLogger(GLFWErrorCallback.class);

  private final Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens(
      (field, value) -> 0x10000 < value.intValue() && value.intValue() < 0x20000,
      null, GLFW.class);

  @Override
  public void invoke(int error, long description) {
    if (LOG.isErrorEnabled()) {
      LOG.error("Error: {}\n{}",
          ERROR_CODES.get(Integer.valueOf(error)),
          GLFWErrorCallback.getDescription(description));
    }
    throw new IllegalStateException();
  }

}
