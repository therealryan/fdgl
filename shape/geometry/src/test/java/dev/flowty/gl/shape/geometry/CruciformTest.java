package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.topology.Cross;
import dev.flowty.gl.test.shape.ShapeAssert;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises {@link Cruciform}
 */
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class CruciformTest {

  /**
   * Illustrates {@link Cruciform} geometry
   */
  @Test
  void unit() {
    ShapeAssert.assertGeometry(new Cross()
        .with(Cruciform.unit(0.4f))
        .transform(new Matrix4f().scale(300)));
  }
}