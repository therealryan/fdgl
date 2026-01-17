package dev.flowty.gl.config.ui;

import dev.flowty.gl.config.ui.TextCells.Border;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link TextCells}
 */
class TextCellsTest {

  @Test
  void empty() {
    Assertions.assertEquals("", new TextCells().toString());
  }

  @Test
  void space() {
    TextCells cells = new TextCells()
        .cell("foo").cell(2).nextRow()
        .cell(1).cell("bar")
        .cell(3, 3, "blah");

    Assertions.assertEquals(
        "foo 2        \n"
            + "1   bar      \n"
            + "             \n"
            + "         blah",
        cells
            .border(Border.SPACE)
            .toString());
  }

  @Test
  void single() {
    TextCells cells = new TextCells()
        .cell("foo").cell(2).nextRow()
        .cell(1).cell("bar")
        .cell(3, 3, "blah");

    Assertions.assertEquals(
        "┌───┬───┬┬────┐\n"
            + "│foo│2  ││    │\n"
            + "├───┼───┼┼────┤\n"
            + "│1  │bar││    │\n"
            + "├───┼───┼┼────┤\n"
            + "│   │   ││    │\n"
            + "├───┼───┼┼────┤\n"
            + "│   │   ││blah│\n"
            + "└───┴───┴┴────┘",
        cells
            .border(Border.SINGLE)
            .toString());
  }

  @Test
  void tracks() {
    TextCells cells = new TextCells()
        .cell("foo").cell(2).nextRow()
        .cell(1).cell("bar")
        .cell(3, 3, "blah");

    Assertions.assertEquals(
        "╔═══╦═══╦╦════╗\n"
            + "║foo║2  ║║    ║\n"
            + "╠═══╬═══╬╬════╣\n"
            + "║1  ║bar║║    ║\n"
            + "╠═══╬═══╬╬════╣\n"
            + "║   ║   ║║    ║\n"
            + "╠═══╬═══╬╬════╣\n"
            + "║   ║   ║║blah║\n"
            + "╚═══╩═══╩╩════╝",
        cells
            .border(Border.DOUBLE)
            .toString());
  }

  @Test
  void multiline() {
    Assertions.assertEquals(
        "┌───────┐\n"
            + "│multi  │\n"
            + "│line   │\n"
            + "│content│\n"
            + "└───────┘",
        new TextCells()
            .cell("multi\nline\ncontent")
            .border(Border.SINGLE)
            .toString());
  }
}