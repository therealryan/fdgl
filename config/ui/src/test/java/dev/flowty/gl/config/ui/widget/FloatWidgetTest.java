package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.limit.Range;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link FloatWidget}
 */
class FloatWidgetTest {

  @Test
  void advance() {
    CharGrid grid = new CharGrid().size(8, 3);
    Coordinate coord = new Coordinate().set(1, 1);
    AtomicReference<Float> value = new AtomicReference<>(123.456f);
    Variable variable = new Variable("name", "desc", Float.class, Float.class,
        new Range(-10, 20, 2),
        () -> true) {
      @Override
      public Object get() {
        return value.get();
      }

      @Override
      public void set(Object v) {
        value.set(Float.parseFloat(String.valueOf(v)));
      }
    };
    FloatWidget widget = new FloatWidget(grid, coord, variable);

    widget.update(0);
    Assertions.assertEquals(""
            + "┌△─────┐\n"
            + "│+20.00│\n"
            + "└▽─────┘",
        grid.toString().trim());

    widget.inputEvent(true, false, false, false);
    widget.update(0);
    Assertions.assertEquals(""
            + "┌─△────┐\n"
            + "│+20.00│\n"
            + "└─▽────┘",
        grid.toString().trim());

    widget.inputEvent(true, false, false, false);
    widget.update(0);
    Assertions.assertEquals(""
            + "┌──△───┐\n"
            + "│+20.00│\n"
            + "└──▽───┘",
        grid.toString().trim());

    widget.inputEvent(true, false, false, false);
    widget.update(0);
    Assertions.assertEquals(""
            + "┌────△─┐\n"
            + "│+20.00│\n"
            + "└────▽─┘",
        grid.toString().trim());

    widget.inputEvent(true, false, false, false);
    widget.update(0);
    Assertions.assertEquals(""
            + "┌─────△┐\n"
            + "│+20.00│\n"
            + "└─────▽┘",
        grid.toString().trim());
  }
}