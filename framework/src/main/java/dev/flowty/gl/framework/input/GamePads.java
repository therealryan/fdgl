package dev.flowty.gl.framework.input;

import dev.flowty.gl.framework.input.Gamepad.Button;
import dev.flowty.gl.framework.input.Gamepad.SubAxis;
import java.util.stream.Stream;
import org.lwjgl.glfw.GLFW;

/**
 * Encapsulates gamepad state
 */
public class GamePads {

  private final Gamepad[] gamepads = new Gamepad[GLFW.GLFW_JOYSTICK_LAST + 1];

  {
    for (int i = 0; i < gamepads.length; i++) {
      gamepads[i] = new Gamepad(i);
    }
  }

  /**
   * Initialises pad state
   */
  void initialise() {
    for (Gamepad gamepad : gamepads) {
      gamepad.initialise();
    }
  }

  /**
   * Enables pad event detection
   */
  void advance() {
    for (Gamepad gamepad : gamepads) {
      gamepad.advance();
    }
  }

  /**
   * @param id a pad index
   * @return the pad
   */
  public Gamepad get(int id) {
    return gamepads[id];
  }

  /**
   * @return all present gamepads
   */
  public Stream<Gamepad> present() {
    return Stream.of(gamepads)
        .filter(Gamepad::present);
  }

  /**
   * @param buttons a set of buttons
   * @return <code>true</code> if any of the buttons has been pressed on any pad
   */
  public boolean anyPressed(Button... buttons) {
    return present().anyMatch(p -> {
      for (Button b : buttons) {
        if (p.pressed(b)) {
          return true;
        }
      }
      return false;
    });
  }

  /**
   * @param buttons a set of buttons
   * @return <code>true</code> if any of the buttons is down on any pad
   */
  public boolean anyDown(Button... buttons) {
    return present().anyMatch(p -> {
      for (Button b : buttons) {
        if (p.down(b)) {
          return true;
        }
      }
      return false;
    });
  }

  /**
   * @param threshold A threshold value
   * @param axes      A set of sub-axes
   * @return {@code true} if any of the axes is over the threshold on any pad
   */
  public boolean anyOver(float threshold, SubAxis[] axes) {
    return present().anyMatch(p -> {
      for (SubAxis axis : axes) {
        if (p.get(axis) >= threshold) {
          return true;
        }
      }
      return false;
    });
  }

  /**
   * @param threshold A threshold value
   * @param axes      A set of sub-axes
   * @return {@code true} if any of the axes is exceeded the threshold in the last tick
   */
  public boolean anyExceeded(float threshold, SubAxis[] axes) {
    return present().anyMatch(p -> {
      for (SubAxis axis : axes) {
        if (p.get(axis) >= threshold && p.previous(axis) < threshold) {
          return true;
        }
      }
      return false;
    });
  }

}
