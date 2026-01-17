package dev.flowty.gl.framework.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import org.lwjgl.glfw.GLFW;

/**
 * Encapsulates the current state of control input
 */
public class Input {

  private final Keyboard keyboard = new Keyboard();

  private final GamePads pads = new GamePads();

  /**
   * Called at the end of a tick to enable event detection
   *
   * @return <code>this</code>
   */
  public Input advance() {
    keyboard.advance();
    pads.advance();
    return this;
  }

  /**
   * Initialises input state
   *
   * @param window the glfw window handle
   */
  @SuppressWarnings("resource")
  public void initialise(long window) {

    // make this configurable
    GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);

    keyboard().initialise(window);

    // Setup a key callback. It will be called every time a key is pressed, repeated
    // or released.
    glfwSetKeyCallback(window, (wndw, key, scancode, action, mods) -> {
      if (key == GLFW.GLFW_KEY_UNKNOWN) {
        // this is not useful to us - it happens on the steam deck's volume buttons
        return;
      }

      if (action == GLFW_PRESS) {
        keyboard().set(key, true);
      }
      if (action == GLFW_RELEASE) {
        keyboard().set(key, false);
      }
    });

    pads.initialise();
  }

  /**
   * @return Keyboard state
   */
  public Keyboard keyboard() {
    return keyboard;
  }

  /**
   * @return gamepad state
   */
  public GamePads gamepads() {
    return pads;
  }
}
