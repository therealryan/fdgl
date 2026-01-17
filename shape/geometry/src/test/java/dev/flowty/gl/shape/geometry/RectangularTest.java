package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Shape;
import dev.flowty.gl.shape.topology.Loop;
import dev.flowty.gl.shape.topology.Quad;
import dev.flowty.gl.test.shape.ShapeAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises {@link Rectangular} geometries
 */
@SuppressWarnings("static-method")
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class RectangularTest {

  /**
   * Demonstrates quad vertices
   */
  @Test
  void quad() {
    ShapeAssert.assertGeometry(new Quad()
        .with(Rectangular.quad(0, 0, 150, 150)));
  }

  /**
   * Demonstrates a centered border
   */
  @Test
  void border() {

    float x = 0;
    Shape quad = new Quad()
        .with(Rectangular.quad(0, 0, 150, 150));
    x += 190;
    Shape border = new Loop(4)
        .with(Rectangular.border(x, 0, x + 150, 150, 40));

    ShapeAssert.assertGeometry(new Shape(quad, border));
  }

  /**
   * Demonstrates an inner border
   */
  @Test
  void innerBorder() {

    float x = 0;
    Shape quad = new Quad()
        .with(Rectangular.quad(0, 0, 150, 150));
    x += 170;
    Shape inner = new Loop(4)
        .with(Rectangular.innerborder(x, 0, x + 150, 150, 40));

    ShapeAssert.assertGeometry(new Shape(quad, inner));
  }

  /**
   * Demonstrates an outer border
   */
  @Test
  void outerBorder() {
    float x = 0;
    Shape quad = new Quad()
        .with(Rectangular.quad(0, 0, 150, 150));
    x += 210;
    Shape outer = new Loop(4)
        .with(Rectangular.outerborder(x, 0, x + 150, 150, 40));

    ShapeAssert.assertGeometry(new Shape(quad, outer));
  }
}
