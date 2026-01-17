package dev.flowty.gl.config.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.flowty.gl.config.ui.BoxDrawing.Weight;
import org.junit.jupiter.api.Test;

class PathsTest {

  @Test
  void horizontal() {
    CharGrid tg = new CharGrid().size(10, 3);
    tg.paths(Weight.LIGHT, p -> p
        .moveTo(1, 1)
        .horizontalTo(8));

    assertEquals(""
        + "          \n"
        + " ╶──────╴ \n"
        + "          \n", tg.toString());
  }


  @Test
  void vertical() {
    CharGrid tg = new CharGrid().size(3, 10);
    tg.paths(Weight.LIGHT, p -> p
        .moveTo(1, 1)
        .verticalTo(8));

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
    tg.paths(Weight.LIGHT, p -> p
        .moveTo(1, 1)
        .horizontalTo(5)
        .verticalTo(8)
        .horizontalTo(8));

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
    tg.paths(Weight.LIGHT, p -> p
        .moveTo(1, 1)
        .horizontalTo(5)
        .verticalTo(8)
        .horizontalTo(8)
        .moveTo(4, 5)
        .horizontalTo(8)
        .moveTo(6, 5)
        .horizontalTo(8)
        .moveTo(7, 5)
        .horizontalTo(8));

    assertEquals(""
        + "          \n"
        + " ╶───┐    \n"
        + "     │    \n"
        + "     │    \n"
        + "     ├──╴ \n"
        + "     │    \n"
        + "     ├──╴ \n"
        + "     ├──╴ \n"
        + "     └──╴ \n"
        + "          \n", tg.toString());
  }

  @Test
  void overwrite() {
    CharGrid tg = new CharGrid().size(10, 10);
    tg.paths(Weight.LIGHT, p -> p
        .moveTo(1, 1)
        .horizontalTo(5)
        .verticalTo(8)
        .horizontalTo(8));
    tg.paths(Weight.LIGHT, p -> p
        .moveTo(1, 1)
        .horizontalTo(5)
        .verticalTo(7)
        .horizontalTo(8));
    tg.paths(Weight.LIGHT, p -> p
        .moveTo(1, 1)
        .horizontalTo(5)
        .verticalTo(6)
        .horizontalTo(8));
    tg.paths(Weight.LIGHT, p -> p
        .moveTo(1, 1)
        .horizontalTo(5)
        .verticalTo(4)
        .horizontalTo(8));

    assertEquals(""
        + "          \n"
        + " ╶───┐    \n"
        + "     │    \n"
        + "     │    \n"
        + "     ├──╴ \n"
        + "     │    \n"
        + "     ├──╴ \n"
        + "     ├──╴ \n"
        + "     └──╴ \n"
        + "          \n", tg.toString());
  }

  @Test
  void weight() {
    CharGrid tg = new CharGrid().size(10, 10);
    tg.paths(Weight.HEAVY, p -> p
        .horizontalTo(9)
        .weight(Weight.LIGHT)
        .verticalTo(9)
        .weight(Weight.HEAVY)
        .horizontalTo(0)
        .weight(Weight.LIGHT)
        .verticalTo(0)
    );
    assertEquals(""
        + "┍━━━━━━━━┑\n"
        + "│        │\n"
        + "│        │\n"
        + "│        │\n"
        + "│        │\n"
        + "│        │\n"
        + "│        │\n"
        + "│        │\n"
        + "│        │\n"
        + "┕━━━━━━━━┙\n", tg.toString());
  }

}