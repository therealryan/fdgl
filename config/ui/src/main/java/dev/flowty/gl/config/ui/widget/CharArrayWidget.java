package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;
import dev.flowty.gl.config.ui.Widget;
import java.util.Arrays;

/**
 * Manipulates <code>char[]</code> {@link Variable}s
 */
public class CharArrayWidget extends AbstractWidget {

  private final CharGrid charGrid;
  private final Variable variable;
  private final Coordinate location;
  private char[] currentValue;
  private int idx = 0;
  private Widget cmpWidget;

  /**
   * @param charGrid The grid to draw to
   * @param location The widget location
   * @param variable The variable to set
   */
  public CharArrayWidget(CharGrid charGrid, Coordinate location, Variable variable) {
    this.charGrid = charGrid;
    this.location = location;
    this.variable = variable;
    currentValue = (char[]) variable.get();
    currentValue = Arrays.copyOf(currentValue, currentValue.length);
    cmpWidget = elementWidget();
  }

  private Widget elementWidget() {
    Variable var = new Variable("[" + idx + "]", "", char.class, null, null, () -> true) {
      @Override
      public void set(Object value) {
        currentValue[idx] = (char) value;
      }

      @Override
      public Object get() {
        return currentValue[idx];
      }
    };

    Coordinate loc = new Coordinate().set(location.row, location.column + idx);
    return new CharacterWidget(charGrid, loc, var)
        .alwaysSet(true)
        .with(haptic);
  }

  @Override
  public Widget inputEvent(boolean yes, boolean no, boolean up, boolean down) {
    Outcome o = cmpWidget.inputEvent(yes, no, up, down).outcome();

    if (o == Outcome.YES) {
      if (idx == currentValue.length - 1) {
        variable.set(currentValue);
        outcome = Outcome.YES;
        haptic.yes();
      } else {
        idx++;
      }
      cmpWidget = elementWidget();
    }

    if (o == Outcome.NO) {
      if (idx == 0) {
        outcome = Outcome.NO;
        haptic.no();
      } else {
        idx--;
      }
      cmpWidget = elementWidget();
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
    charGrid.pen(p -> p.set(location.row - 1, location.column - 1))
        .box(currentValue.length + 2, 3,
            cnt -> cnt.write(new String(currentValue)));
    cmpWidget.update(delta);
    return this;
  }

  @Override
  public CharGrid textGrid() {
    return charGrid;
  }

}
