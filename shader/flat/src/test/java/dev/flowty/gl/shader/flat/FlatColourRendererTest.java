package dev.flowty.gl.shader.flat;

import dev.flowty.gl.shader.test.RenderTest;
import dev.flowty.gl.shape.topology.Quad;
import dev.flowty.gl.shape.topology.Triangle;
import dev.flowty.gl.util.Colour;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises {@link FlatColourRenderer}
 */
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "no gfx support")
class FlatColourRendererTest {

  private FlatColourRenderer fcr;
  private static final ColouredShape RED = new ColouredShape(
      new Triangle(), Colour.RED);
  private static final ColouredShape GREEN = new ColouredShape(
      new Triangle(), Colour.GREEN);
  private static final ColouredShape BLUE = new ColouredShape(
      new Triangle(), Colour.BLUE);
  private static final ColouredShape WHITE = new ColouredShape(
      new Quad(), Colour.withAlphai(Colour.WHITE, 128));

  private FlatColourRenderer alt;

  /**
   * Draws a few polygons and then checks the rendered result
   */
  @Test
  void flat() {
    new RenderTest(display -> {
      if (fcr == null) {
        fcr = new FlatColourRenderer(1);
        fcr.program().projection.update(m -> m
            .setOrtho2DLH(
                0, display.logicalDimensions().x(),
                0, display.logicalDimensions().y()));
        alt = new FlatColourRenderer(1);
        alt.program().projection.update(m -> m
            .setOrtho2DLH(
                0, display.logicalDimensions().x(),
                0, display.logicalDimensions().y()));
      }

      float fh = display.logicalDimensions().y();
      float hh = fh / 2;
      float fw = display.logicalDimensions().x();
      float hw = fw / 2;

      RED.vertex(0, v -> v.set(10, 10, 0));
      RED.vertex(1, v -> v.set(10, hh - 10, 0));
      RED.vertex(2, v -> v.set(hw - 10, 10, 0));
      fcr.draw(RED);

      // pull green towards us so it's above white
      float z = -1;
      GREEN.vertex(0, v -> v.set(10, hh + 10, z));
      GREEN.vertex(1, v -> v.set(10, fh - 10, z));
      GREEN.vertex(2, v -> v.set(hw - 10, fh - 10, z));
      fcr.draw(GREEN);

      BLUE.vertex(0, v -> v.set(hw + 10, fh - 10, 0));
      BLUE.vertex(1, v -> v.set(fw - 10, fh - 10, 0));
      BLUE.vertex(2, v -> v.set(fw - 10, hh + 10, 0));
      fcr.draw(BLUE);

      WHITE.vertex(0, v -> v.set(1 * fw / 8, 1 * fh / 8, 0));
      WHITE.vertex(1, v -> v.set(1 * fw / 8, 7 * fh / 8, 0));
      WHITE.vertex(2, v -> v.set(7 * fw / 8, 1 * fh / 8, 0));
      WHITE.vertex(3, v -> v.set(7 * fw / 8, 7 * fh / 8, 0));
      alt.draw(WHITE);

      fcr.render();
      alt.render();
    })
        .assertRenderResults();
  }
}
