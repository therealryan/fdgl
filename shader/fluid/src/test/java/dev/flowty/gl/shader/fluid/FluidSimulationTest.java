package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.test.RenderTest;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises {@link FluidSimulation}
 */
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "no gfx support")
class FluidSimulationTest {

  private FluidSimulation fluid;

  /**
   * Builds a fluid simulation with a few splats pushed around in a circle, advances it for a few
   * frames, then renders it
   */
  @Test
  void cycle() {
    new RenderTest(display -> {
      if (fluid == null) {
        fluid = new FluidSimulation().withScreenRes(display.resolution());

        fluid.splat(
            new Vector2f(0.25f, 0.25f),
            new Vector2f(500, 0),
            new Vector3f(10, 0, 0));

        fluid.splat(
            new Vector2f(0.5f, 0.25f),
            new Vector2f(0, 0),
            new Vector3f(5, 5, 5));

        fluid.splat(
            new Vector2f(0.75f, 0.25f),
            new Vector2f(0, 500),
            new Vector3f(0, 10, 0));

        fluid.splat(
            new Vector2f(0.75f, 0.5f),
            new Vector2f(0, 0),
            new Vector3f(5, 5, 5));

        fluid.splat(
            new Vector2f(0.75f, 0.75f),
            new Vector2f(-500, 0),
            new Vector3f(0, 0, 10));

        fluid.splat(
            new Vector2f(0.5f, 0.75f),
            new Vector2f(0, 0),
            new Vector3f(5, 5, 5));

        fluid.splat(
            new Vector2f(0.25f, 0.75f),
            new Vector2f(0, -500),
            new Vector3f(10, 10, 10));

        fluid.splat(
            new Vector2f(0.25f, 0.5f),
            new Vector2f(0, 0),
            new Vector3f(5, 5, 5));

        for (int j = 0; j < 50; j++) {
          fluid.advance(0.01f);
        }
      }

      fluid.render(display.resolution());

    }).assertRenderResults();
  }

  /**
   * Builds a fluid simulation with a few splats pushed into the center, advances it for a few
   * frames, then renders it
   */
  @Test
  void clash() {
    new RenderTest(display -> {
      if (fluid == null) {
        fluid = new FluidSimulation().withScreenRes(display.resolution());

        fluid.splat(
            new Vector2f(0.5f, 0.5f),
            new Vector2f(0, 0),
            new Vector3f(5, 5, 5));

        fluid.splat(
            new Vector2f(0.25f, 0.25f),
            new Vector2f(500, 500),
            new Vector3f(10, 0, 0));

        fluid.splat(
            new Vector2f(0.75f, 0.25f),
            new Vector2f(-500, 500),
            new Vector3f(0, 10, 0));

        fluid.splat(
            new Vector2f(0.75f, 0.75f),
            new Vector2f(-500, -500),
            new Vector3f(0, 0, 10));

        fluid.splat(
            new Vector2f(0.25f, 0.75f),
            new Vector2f(500, -500),
            new Vector3f(10, 10, 10));

        for (int j = 0; j < 50; j++) {
          fluid.advance(0.01f);
        }
      }

      fluid.render(display.resolution());

    }).assertRenderResults();
  }
}
