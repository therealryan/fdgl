package dev.flowty.gl.framework.input;

import dev.flowty.gl.util.Range;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

/**
 * Encapsulates gamepad state
 */
public class Gamepad {

  public enum Axis {
    LEFT_X(1),
    LEFT_Y(-1),
    RIGHT_X(1),
    RIGHT_Y(-1),
    LEFT_TRIGGER(1) {
      @Override
      public float get(GLFWGamepadState state) {
        // raw trigger values go between:
        //  * -1 (not pulled at all)
        //  *  1 (fully pulled)
        // which isn't super intuitive, so we've scaling from 0 to 1
        return (super.get(state) + 1) / 2;
      }
    },
    RIGHT_TRIGGER(1) {
      @Override
      public float get(GLFWGamepadState state) {
        return (super.get(state) + 1) / 2;
      }
    },
    ;

    private final float factor;

    Axis(float factor) {
      this.factor = factor;
    }

    /**
     * @param state The raw input state
     * @return The normalised axis value, in range -1 to 1
     */
    public float get(GLFWGamepadState state) {
      return factor * state.axes(ordinal());
    }
  }

  public enum SubAxis {
    L_L(Axis.LEFT_X, 0, -1),
    L_R(Axis.LEFT_X, 0, 1),
    L_U(Axis.LEFT_Y, 0, 1),
    L_D(Axis.LEFT_Y, 0, -1),

    R_L(Axis.RIGHT_X, 0, -1),
    R_R(Axis.RIGHT_X, 0, 1),
    R_U(Axis.RIGHT_Y, 0, 1),
    R_D(Axis.RIGHT_Y, 0, -1),

    L_T(Axis.LEFT_TRIGGER, 0, 1),
    R_T(Axis.RIGHT_TRIGGER, 0, 1),
    ;

    private final Axis axis;
    private final Range range;

    SubAxis(Axis axis, float none, float full) {
      this.axis = axis;
      this.range = new Range(none, full);
    }

    /**
     * @param state The raw input state
     * @return The normalised subaxis value, in range 0 to 1
     */
    public float get(GLFWGamepadState state) {
      return Range.UNIT.limit(range.toRatio(axis.get(state)));
    }
  }

  public enum Stick {
    LEFT(Axis.LEFT_X, Axis.LEFT_Y),
    RIGHT(Axis.RIGHT_X, Axis.RIGHT_Y),
    ;

    public final Axis x;
    public final Axis y;

    Stick(Axis x, Axis y) {
      this.x = x;
      this.y = y;
    }
  }

  /**
   * Gamepad buttons
   */
  public enum Button {
    /**
     * Face button
     */
    A("Ⓐ"),
    /**
     * Face button
     */
    B("Ⓑ"),
    /**
     * Face button
     */
    X("Ⓧ"),
    /**
     * Face button
     */
    Y("Ⓨ"),
    /***/
    LEFT_BUMPER("🄻"),
    /***/
    RIGHT_BUMPER("🅁"),
    /***/
    BACK("back"),
    /***/
    START("start"),
    /***/
    GUIDE("guide"),
    /**
     * thumbstick click
     */
    LEFT_THUMB("Ⓛ"),
    /**
     * thumbstick click
     */
    RIGHT_THUMB("Ⓡ"),
    /**
     * dpad
     */
    DPAD_UP("⮉"),
    /**
     * dpad
     */
    DPAD_RIGHT("⮊"),
    /**
     * dpad
     */
    DPAD_DOWN("⮋"),
    /**
     * dpad
     */
    DPAD_LEFT("⮈"),
    ;

    /**
     * I've added these, but font support is not great
     */
    public final String icon;

    Button(String icon) {
      this.icon = icon;
    }
  }

  private final int id;
  private String name = null;

  private boolean present = false;
  private GLFWGamepadState previous = GLFWGamepadState.create();
  private GLFWGamepadState state = GLFWGamepadState.create();

  /**
   * @param id The underlying pad ID
   */
  public Gamepad(int id) {
    this.id = id;
  }

  /**
   * Initialises gamepad state
   *
   * @return <code>this</code>
   */
  public Gamepad initialise() {
    GLFW.glfwGetGamepadState(id, previous);
    present = GLFW.glfwGetGamepadState(id, state);
    name = GLFW.glfwGetGamepadName(id);
    return this;
  }

  /**
   * @return The underlying pad ID
   */
  public int id() {
    return id;
  }

  /**
   * @return <code>true</code> if the gamepad exists
   */
  public boolean present() {
    return present;
  }

  /**
   * @return human-readable pad name
   */
  public String name() {
    if (!present) {
      return "missing";
    }
    return name != null
        ? name
        : "pad_" + id;
  }

  /**
   * Advances pad state, enabling event detection
   *
   * @return <code>this</code>
   */
  public Gamepad advance() {
    GLFWGamepadState tmp = previous;
    previous = state;
    state = tmp;
    present = GLFW.glfwGetGamepadState(id, state);
    return this;
  }

  /**
   * Determines button state
   *
   * @param b a button
   * @return <code>true</code> if the button is currently pressed
   */
  public boolean down(Button b) {
    if (!present) {
      return false;
    }

    return state.buttons(b.ordinal()) == GLFW.GLFW_PRESS;
  }

  /**
   * Captures button press events
   *
   * @param b a button
   * @return <code>true</code> if the button has been pressed since the last frame
   */
  public boolean pressed(Button b) {
    if (!present) {
      return false;
    }

    return state.buttons(b.ordinal()) == GLFW.GLFW_PRESS
        && previous.buttons(b.ordinal()) == GLFW.GLFW_RELEASE;
  }

  /**
   * Captures button release events
   *
   * @param b a button
   * @return <code>true</code> if the button has been released since the last
   * frame
   */
  public boolean released(Button b) {
    if (!present) {
      return false;
    }

    return state.buttons(b.ordinal()) == GLFW.GLFW_RELEASE
        && previous.buttons(b.ordinal()) == GLFW.GLFW_PRESS;
  }

  /**
   * Gets axis state
   *
   * @param axis The axis to query
   * @return The axis value, range -1 to 1
   */
  public float get(Axis axis) {
    return axis.get(state);
  }

  /**
   * Gets sub-axis state
   *
   * @param axis The sub-axis to query
   * @return The sub-axis value, range 0 to 1
   */
  public float get(SubAxis axis) {
    return axis.get(state);
  }

  /**
   * Gets previous axis state
   *
   * @param axis The axis to query
   * @return The old axis value, range -1 to 1
   */
  public float previous(Axis axis) {
    return axis.get(previous);
  }

  /**
   * Gets previous sub-axis state
   *
   * @param axis The sub-axis to query
   * @return The old sub-axis value, range 0 to 1
   */
  public float previous(SubAxis axis) {
    return axis.get(previous);
  }

  @Override
  public String toString() {
    return String.format(""
            + "%s\n"
            + " LS %+.2f|%+.2f RS %+.2f|%+.2f\n"
            + " LT %+.2f RT %+.2f\n",
        name(),
        get(Axis.LEFT_X), get(Axis.LEFT_Y), get(Axis.RIGHT_X), get(Axis.RIGHT_Y),
        get(Axis.LEFT_TRIGGER), get(Axis.RIGHT_TRIGGER));
  }
}
