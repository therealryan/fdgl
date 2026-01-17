package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.limit.Choice;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;

public class BooleanWidget extends ChoiceWidget {

  public static final Choice<Object> LIMIT = new Choice<>() {
    @Override
    public String[] choices() {
      return new String[]{"true", "false"};
    }

    @Override
    public Object chosen(int index) {
      return index == 0;
    }
  };

  public BooleanWidget(CharGrid charGrid, Coordinate valueCoordinate, Variable var) {
    super(charGrid, valueCoordinate, new Variable(
        var.name(), var.description(), var.type(), var.widgetHint(), LIMIT, () -> var.enabled()) {

      @Override
      public Object get() {
        return var.get();
      }

      @Override
      public void set(Object value) {
        var.set(value);
      }
    });
  }

}
