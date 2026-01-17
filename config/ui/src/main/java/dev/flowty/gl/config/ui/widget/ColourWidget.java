package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.limit.Range;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;
import dev.flowty.gl.config.ui.Widget;
import dev.flowty.gl.util.Colour;
import java.util.function.IntUnaryOperator;

/**
 * Manipulates {@link Colour} {@link Variable}s
 */
public class ColourWidget extends AbstractWidget {

  private enum Component {
    R(Colour::redi), G(Colour::greeni), B(Colour::bluei), A(Colour::alphai);

    private final IntUnaryOperator get;

    Component(IntUnaryOperator get) {
      this.get = get;
    }

    int get(int colour) {
      return get.applyAsInt(colour);
    }
  }

  private final CharGrid charGrid;
  private final Variable variable;
  private final Coordinate location;
  private int currentValue;
  private int cmpIdx = 0;
  private Widget cmpWidget;

  /**
   * @param charGrid the grid to draw to
   * @param location The location of the widget
   * @param variable The variable to set
   */
  public ColourWidget(CharGrid charGrid, Coordinate location, Variable variable) {
    this.charGrid = charGrid;
    this.location = location;
    this.variable = variable;
    currentValue = (int) variable.get();
    cmpWidget = componentWidget();
  }

  private Widget componentWidget() {
    Component cmp = Component.values()[cmpIdx];
    Variable var = new Variable(cmp.name(), "", float.class, null, new Range(0, 255, 0),
        () -> true) {
      @Override
      public void set(Object value) {
        int v = (int) value;
        currentValue = Colour.packInt(
            Component.R == cmp ? v : Component.R.get(currentValue),
            Component.G == cmp ? v : Component.G.get(currentValue),
            Component.B == cmp ? v : Component.B.get(currentValue),
            Component.A == cmp ? v : Component.A.get(currentValue));
      }

      @Override
      public Object get() {
        return cmp.get(currentValue);
      }
    };

    Coordinate loc = new Coordinate().set(location.row, location.column);
    loc.column += cmpIdx * 4;
    return new IntWidget(charGrid, loc, var)
        .liveUpdate(true)
        .with(haptic);
  }

  @Override
  public Widget inputEvent(boolean yes, boolean no, boolean up, boolean down) {
    cmpWidget.inputEvent(yes, no, up, down);
    Outcome o = cmpWidget.outcome();

    if (o == Outcome.YES) {
      if (cmpIdx == Component.values().length - 1) {
        variable.set(currentValue);
        outcome = Outcome.YES;
      } else {
        cmpIdx++;
      }
      cmpWidget = componentWidget();
    }

    if (o == Outcome.NO) {
      if (cmpIdx == 0) {
        outcome = Outcome.NO;
      } else {
        cmpIdx--;
      }
      cmpWidget = componentWidget();
    }

    return this;
  }

  @Override
  public Widget inputState(boolean yes, boolean no, boolean up, boolean down) {
    cmpWidget.inputState(yes, no, up, down);
    return this;
  }

  @Override
  public Widget update(float delta) {
    charGrid.pen(p -> p.set(location.row, location.column))
        .colour(currentValue)
        .write(Colour.toString(currentValue));
    cmpWidget.update(delta);
    return this;
  }

  @Override
  public CharGrid textGrid() {
    return charGrid;
  }

}
