package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Shape;
import dev.flowty.gl.shape.topology.Fan;
import dev.flowty.gl.shape.topology.Loop;
import dev.flowty.gl.shape.topology.Wheel;
import dev.flowty.gl.test.shape.ShapeAssert;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises {@link Circular} geometries
 */
@SuppressWarnings("static-method")
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class CircularTest {

  /**
   * Demonstrates a filled circle
   */
  @Test
  void fan() {
    ShapeAssert.assertGeometry(new Fan(16).with(Circular.fan(150)));
  }

  /**
   * Demonstrates a filled circle with a central vertex
   */
  @Test
  void wheel() {
    ShapeAssert.assertGeometry(new Wheel(16).with(Circular.wheel(150)));
  }

  /**
   * Demonstrates a centered border
   */
  @Test
  void border() {
    Shape circle = new Fan(16).with(Circular.fan(150));
    Shape border = new Loop(16).with(Circular.border(150, 40))
        .transform(new Matrix4f().translate(340, 0, 0));

    ShapeAssert.assertGeometry(new Shape(circle, border));
  }

  /**
   * Demonstrates an inner border
   */
  @Test
  void innerBorder() {
    Shape circle = new Fan(16).with(Circular.fan(150));
    Shape border = new Loop(16).with(Circular.innerBorder(150, 40))
        .transform(new Matrix4f().translate(320, 0, 0));

    ShapeAssert.assertGeometry(new Shape(circle, border));
  }

  /**
   * Demonstrates an outer border
   */
  @Test
  void outerBorder() {
    Shape circle = new Fan(16).with(Circular.fan(150));
    Shape border = new Loop(16).with(Circular.outerBorder(150, 40))
        .transform(new Matrix4f().translate(360, 0, 0));

    ShapeAssert.assertGeometry(new Shape(circle, border));
  }
}
