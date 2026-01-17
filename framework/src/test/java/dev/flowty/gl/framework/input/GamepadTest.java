package dev.flowty.gl.framework.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import dev.flowty.gl.framework.input.Gamepad.Axis;
import dev.flowty.gl.framework.input.Gamepad.Button;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.lwjgl.glfw.GLFW;

/**
 * Exercises gamepad functionality
 */
@SuppressWarnings("static-method")
class GamepadTest {

  /**
   * Checks that our button enum ordinals match the proper ID values
   *
   * @return test instances
   */
  @TestFactory
  Stream<DynamicNode> buttons() {
    return Stream.of(Button.values())
        .map(b -> dynamicTest(b.name(), () -> assertEquals(
            GLFW.class.getField("GLFW_GAMEPAD_BUTTON_" + b.name()).getInt(null),
            b.ordinal())));
  }

  /**
   * Checks that our axis enum ordinals match the proper ID values
   *
   * @return test instances
   */
  @TestFactory
  Stream<DynamicNode> axes() {
    return Stream.of(Axis.values())
        .map(a -> dynamicTest(a.name(), () -> assertEquals(
            GLFW.class.getField("GLFW_GAMEPAD_AXIS_" + a.name()).getInt(null),
            a.ordinal())));
  }
}
