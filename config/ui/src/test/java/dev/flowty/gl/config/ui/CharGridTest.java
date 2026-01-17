package dev.flowty.gl.config.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.flowty.gl.config.ui.BoxDrawing.Line;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link CharGrid}
 */
@SuppressWarnings("static-method")
class CharGridTest {

  /**
   * Illustrates pen control
   */
  @Test
  void position() {
    CharGrid tg = new CharGrid().size(10, 4);
    tg.write("foo");
    tg.write("bar");
    tg.write("\nbaz");
    tg.pen(p -> p.set(3, 2)).write("abcdefghijk");

    assertEquals(""
        + "foobar    \n"
        + "      baz \n"
        + "          \n"
        + "  abcdefgh\n", tg.toString());
  }

  /**
   * Illustrates area filling
   */
  @Test
  void fill() {
    CharGrid tg = new CharGrid().size(10, 10);
    tg.rowFill(0, 1, 9, 't');
    tg.rowFill(9, 1, 9, 'b');
    tg.columnFill(0, 1, 9, 'l');
    tg.columnFill(9, 1, 9, 'r');
    tg.fill(3, 3, 7, 7, 'm');
    assertEquals(""
        + " tttttttt \n"
        + "l        r\n"
        + "l        r\n"
        + "l  mmmm  r\n"
        + "l  mmmm  r\n"
        + "l  mmmm  r\n"
        + "l  mmmm  r\n"
        + "l        r\n"
        + "l        r\n"
        + " bbbbbbbb \n", tg.toString());
  }

  /**
   * Illustrates box drawing
   */
  @Test
  void box() {
    assertEquals(""
            + "┌──┐\n"
            + "│  │\n"
            + "│  │\n"
            + "└──┘\n",
        new CharGrid().size(4, 4)
            .box(4, 4)
            .toString());

    assertEquals(""
            + "╔══╗\n"
            + "║  ║\n"
            + "║  ║\n"
            + "╚══╝\n",
        new CharGrid().size(4, 4)
            .line(Line.DOUBLE)
            .box(4, 4)
            .toString());

    assertEquals(""
            + "┌────┐  \n"
            + "│    │  \n"
            + "│ ╔══╧═╗\n"
            + "│ ║foob║\n"
            + "│t║    ║\n"
            + "└─╢    ║\n"
            + "  ║    ║\n"
            + "  ╚════╝\n",
        new CharGrid().size(8, 8)
            .box(6, 6, b -> b
                .pen(p -> p.set(3, 0))
                .write("text"))
            .pen(p -> p.set(2, 2))
            .line(Line.DOUBLE)
            .box(6, 6, b -> b
                .write("foobar"))
            .toString());

    assertEquals(""
            + "+----+  \n"
            + "|    |  \n"
            + "| +--+-+\n"
            + "| |foob|\n"
            + "|t|    |\n"
            + "+-+    |\n"
            + "  |    |\n"
            + "  +----+\n",
        new CharGrid().size(8, 8)
            .line(Line.ASCII)
            .box(6, 6, b -> b
                .pen(p -> p.set(3, 0))
                .write("text"))
            .pen(p -> p.set(2, 2))
            .box(6, 6, b -> b
                .write("foobar"))
            .toString());
  }

  /**
   * Line content inside boxes is healed onto the perimeter
   */
  @Test
  void contentHeal() {
    assertEquals(""
            + "┌─┐\n"
            + "╞═╡\n"
            + "└─┘\n",
        new CharGrid().size(3, 3)
            .box(3, 3, b -> b
                .write("══"))
            .toString());
    assertEquals(""
            + "┌╥┐\n"
            + "│║│\n"
            + "└╨┘\n",
        new CharGrid().size(3, 3)
            .box(3, 3, b -> b
                .write("║"))
            .toString());
  }
}
