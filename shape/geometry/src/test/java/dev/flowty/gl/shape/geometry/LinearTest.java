package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.topology.Loop;
import dev.flowty.gl.shape.topology.Strip;
import dev.flowty.gl.test.shape.ShapeAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises {@link Linear} geometry
 */
@SuppressWarnings("static-method")
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class LinearTest {

  /**
   * Demonstrates the geometry of a single line segment
   */
  @Test
  void segment() {
    ShapeAssert.assertGeometry(new Strip(1).with(Linear.strip(40,
        0, 0,
        150, 150)));
  }

  /**
   * Demonstrates the geometry of two joined segments. It looks like absolute shit, but it's simple
   * and it'll be OK for for point sequences with no sharp corners.
   */
  @Test
  void corner() {
    ShapeAssert.assertGeometry(new Strip(2).with(Linear.strip(40,
        0, 0,
        150, 0,
        150, 150)));
  }

  /**
   * Demonstrates the geometry of a closed line loop
   */
  @Test
  void loop() {
    ShapeAssert.assertGeometry(new Loop(3).with(Linear.loop(40,
        0, 0,
        150, 0,
        75, 75)));
  }
}
