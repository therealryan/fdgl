package dev.flowty.gl.config.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.flowty.gl.config.ui.BoxDrawing.Weight;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link Lines}
 */
class LinesTest {

  @Test
  void horizontal() {
    CharGrid tg = new CharGrid().size(10, 3);
    tg.lines(Weight.LIGHT, l -> l
        .horizontal(1, 1, 9));

    assertEquals(""
        + "          \n"
        + " ╶──────╴ \n"
        + "          \n", tg.toString());
  }

  @Test
  void vertical() {
    CharGrid tg = new CharGrid().size(3, 10);
    tg.lines(Weight.LIGHT, l -> l
        .vertical(1, 1, 9));

    assertEquals(""
        + "   \n"
        + " ╷ \n"
        + " │ \n"
        + " │ \n"
        + " │ \n"
        + " │ \n"
        + " │ \n"
        + " │ \n"
        + " ╵ \n"
        + "   \n", tg.toString());
  }

  @Test
  void zig() {
    CharGrid tg = new CharGrid().size(10, 10);
    tg.lines(Weight.LIGHT, l -> l
        .horizontal(1, 1, 5)
        .vertical(5, 1, 8)
        .horizontal(8, 5, 9));

    assertEquals(""
        + "          \n"
        + " ╶───┐    \n"
        + "     │    \n"
        + "     │    \n"
        + "     │    \n"
        + "     │    \n"
        + "     │    \n"
        + "     │    \n"
        + "     └──╴ \n"
        + "          \n", tg.toString());
  }

  @Test
  void menu() {
    CharGrid tg = new CharGrid().size(10, 10);
    tg.lines(Weight.LIGHT, l -> l
        .horizontal(1, 1, 5)
        .vertical(5, 1, 8)
        .horizontal(4, 5, 9)
        .horizontal(6, 5, 9)
        .horizontal(7, 5, 9)
        .horizontal(8, 5, 9));

    assertEquals(""
        + "          \n"
        + " ╶───┐    \n"
        + "     │    \n"
        + "     │    \n"
        + "     ├──╴ \n"
        + "     │    \n"
        + "     ├┬┬┐ \n"
        + "     ├┼┼┤ \n"
        + "     └┴┴┘ \n"
        + "          \n", tg.toString());
  }
}