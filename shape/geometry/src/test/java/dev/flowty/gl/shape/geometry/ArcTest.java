package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.topology.Fan;
import dev.flowty.gl.shape.topology.Strip;
import dev.flowty.gl.test.shape.ShapeAssert;
import org.joml.Math;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class ArcTest {

  /**
   * Demonstrates fan geometry.
   */
  @Test
  void fan() {
    ShapeAssert.assertGeometry(new Fan(8)
        .with(Arc.fan(150, 0, Math.PI_OVER_2_f)));
  }

  /**
   * Demonstrates wheel geometry.
   */
  @Test
  void wheel() {
    ShapeAssert.assertGeometry(new Fan(8)
        .with(Arc.wheel(150, 0, Math.PI_OVER_2_f)));
  }

  /**
   * Demonstrates an angle range that rolls over zero
   */
  @Test
  void overZero() {
    ShapeAssert.assertGeometry(new Fan(8)
        .with(Arc.wheel(150, -Math.PI_OVER_2_f / 2, Math.PI_OVER_2_f / 3)));
  }

  /**
   * Demonstrates an angle range over 180 degrees
   */
  @Test
  void reflex() {
    ShapeAssert.assertGeometry(new Fan(8)
        .with(Arc.wheel(150, -Math.PI_OVER_2_f / 2, Math.PI_f * 1.3f)));
  }

  /**
   * Demonstrates the arc border
   */
  @Test
  void border() {
    ShapeAssert.assertGeometry(new Strip(5)
        .with(Arc.border(150, 30, 0, Math.PI_OVER_2_f)));
  }
}