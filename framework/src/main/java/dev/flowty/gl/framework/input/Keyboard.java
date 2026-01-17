package dev.flowty.gl.framework.input;

import java.lang.reflect.Field;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.lwjgl.glfw.GLFW;

/**
 * Encapsulates keyboard state
 */
public class Keyboard {

  private static final Field[] KEY_FIELDS = Stream.of(GLFW.class.getFields())
      .filter(f -> f.getName().startsWith("GLFW_KEY_"))
      .filter(f -> !"GLFW_KEY_UNKNOWN".equals(f.getName()))
      .filter(f -> !"GLFW_KEY_LAST".equals(f.getName()))
      .toArray(Field[]::new);

  private static final int[] KEY_CODES = Stream.of(KEY_FIELDS)
      .mapToInt(f -> {
        try {
          return f.getInt(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          throw new IllegalStateException("Failed to access " + f, e);
        }
      })
      .toArray();
  private static final String[] KEY_NAMES = Stream.of(KEY_FIELDS)
      .map(f -> f.getName().substring("GLFW_KEY_".length()))
      .toArray(String[]::new);

  private boolean[] previous = new boolean[GLFW.GLFW_KEY_LAST + 1];
  private boolean[] now = new boolean[GLFW.GLFW_KEY_LAST + 1];

  /**
   * Initialises keyboard state
   *
   * @param window the GLFW window handle
   * @return <code>this</code>
   */
  public Keyboard initialise(long window) {
    for (int i : KEY_CODES) {
      now[i] = GLFW.glfwGetKey(window, i) == GLFW.GLFW_PRESS;
    }
    return this;
  }

  /**
   * Sets the state of a key
   *
   * @param key  The keycode
   * @param down <code>true</code> if the key is now pressed, <code>false</code>
   *             if releasedd
   * @return <code>this</code>
   */
  public Keyboard set(int key, boolean down) {
    if (key > 1024) {
      throw new IllegalArgumentException(key + " is an unlikely key code");
    }

    while (key >= now.length) {
      now = grow(now);
      previous = grow(previous);
    }
    now[key] = down;
    return this;
  }

  private static boolean[] grow(boolean[] array) {
    boolean[] grown = new boolean[array.length * 2];
    System.arraycopy(array, 0, grown, 0, array.length);
    return grown;
  }

  /**
   * @param key the keycode
   * @return <code>true</code> if the key is currently pressed
   */
  public boolean down(int key) {
    if (0 <= key && key <= now.length) {
      return now[key];
    }
    return false;
  }

  /**
   * @param keys A collection of keycodes
   * @return <code>true</code> if any of them are currently pressed
   */
  public boolean anyDown(int... keys) {
    for (int i : keys) {
      if (down(i)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param key the keycode
   * @return <code>true</code> if the key has been pressed since the last frame
   */
  public boolean pressed(int key) {
    if (0 <= key && key <= now.length) {
      return now[key] && !previous[key];
    }
    return false;
  }

  /**
   * @param keys a collection of keycodes
   * @return <code>true</code> if any of them have been pressed since the last
   * frame
   */
  public boolean anyPressed(int... keys) {
    for (int i : keys) {
      if (pressed(i)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param key the keycode
   * @return <code>true</code> if the key has been released since the last frame
   */
  public boolean released(int key) {
    if (0 <= key && key <= now.length) {
      return !now[key] && previous[key];
    }
    return false;
  }

  /**
   * Called at the end of a frame to enable event detection
   *
   * @return <code>this</code>
   */
  public Keyboard advance() {
    System.arraycopy(now, 0, previous, 0, now.length);
    return this;
  }

  /**
   * @return A stream of all valid key codes
   */
  public static IntStream keyCodes() {
    return IntStream.of(KEY_CODES);
  }

  /**
   * @param key a key code
   * @return The human-readable name of that key
   */
  public static String name(int key) {
    String name = GLFW.glfwGetKeyName(key, -1);
    if (name == null) {
      for (int i = 0; i < KEY_CODES.length; i++) {
        if (key == KEY_CODES[i]) {
          name = KEY_NAMES[i];
        }
      }
    }
    return name;
  }
}
