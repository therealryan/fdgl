package dev.flowty.gl.shape.topology;

import dev.flowty.gl.shape.Shape;
import dev.flowty.gl.test.shape.ShapeAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Asserts on the geometries of various {@link Shape}s
 */
@SuppressWarnings("static-method")
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class ShapesTest {

  /**
   * Illustrates {@link Triangle} geometry
   */
  @Test
  void triangle() {
    ShapeAssert.assertGeometry(new Triangle()
        .vertex(0, v -> v.set(10, 10, 0))
        .vertex(1, v -> v.set(100, 200, 0))
        .vertex(2, v -> v.set(200, 100, 0)));
  }

  /**
   * Illustrates {@link Quad} geometry
   */
  @Test
  void quad() {
    ShapeAssert.assertGeometry(new Quad()
        .vertex(0, v -> v.set(10, 10, 0))
        .vertex(1, v -> v.set(10, 200, 0))
        .vertex(2, v -> v.set(200, 10, 0))
        .vertex(3, v -> v.set(200, 200, 0)));
  }

  /**
   * Illustrates {@link Strip} geometry
   */
  @Test
  void strip() {
    ShapeAssert.assertGeometry(new Strip(3)
        .vertex(0, v -> v.set(10, 10, 0))
        .vertex(1, v -> v.set(10, 200, 0))
        .vertex(2, v -> v.set(100, 10, 0))
        .vertex(3, v -> v.set(100, 200, 0))
        .vertex(4, v -> v.set(200, 10, 0))
        .vertex(5, v -> v.set(200, 200, 0))
        .vertex(6, v -> v.set(300, 10, 0))
        .vertex(7, v -> v.set(300, 200, 0)));
  }

  /**
   * Illustrates {@link Loop} geometry
   */
  @Test
  void loop() {
    ShapeAssert.assertGeometry(new Loop(4)
        .vertex(0, v -> v.set(10, 10, 0))
        .vertex(1, v -> v.set(100, 100, 0))
        .vertex(2, v -> v.set(400, 10, 0))
        .vertex(3, v -> v.set(300, 100, 0))
        .vertex(4, v -> v.set(400, 400, 0))
        .vertex(5, v -> v.set(300, 300, 0))
        .vertex(6, v -> v.set(10, 400, 0))
        .vertex(7, v -> v.set(100, 300, 0)));
  }

  /**
   * Illustrates {@link Shape} combination
   */
  @Test
  void combine() {
    ShapeAssert.assertGeometry(new Shape(
        new Triangle()
            .vertex(0, v -> v.set(10, 10, 0))
            .vertex(1, v -> v.set(10, 200, 0))
            .vertex(2, v -> v.set(50, 170, 0)),
        new Quad()
            .vertex(0, v -> v.set(80, 10, 0))
            .vertex(1, v -> v.set(80, 200, 0))
            .vertex(2, v -> v.set(200, 80, 0))
            .vertex(3, v -> v.set(200, 200, 0))));
  }

  /**
   * Illustrates {@link Fan} geometry
   */
  @Test
  void fan() {
    ShapeAssert.assertGeometry(new Fan(3)
        .vertex(0, v -> v.set(100, 100, 0))
        .vertex(1, v -> v.set(200, 200, 0))
        .vertex(2, v -> v.set(200, 0, 0))
        .vertex(3, v -> v.set(0, 0, 0))
        .vertex(4, v -> v.set(0, 200, 0)));
  }

  /**
   * Illustrates {@link Wheel} geometry
   */
  @Test
  void wheel() {
    ShapeAssert.assertGeometry(new Wheel(3)
        .vertex(0, v -> v.set(100, 100, 0))
        .vertex(1, v -> v.set(200, 200, 0))
        .vertex(2, v -> v.set(200, 0, 0))
        .vertex(3, v -> v.set(0, 100, 0)));
  }
}
