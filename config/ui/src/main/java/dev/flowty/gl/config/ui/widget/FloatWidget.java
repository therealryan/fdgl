package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.limit.Range;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;
import dev.flowty.gl.config.ui.Widget;
import java.util.Optional;

/**
 * Manipulates numeric widgets
 */
public class FloatWidget extends AbstractWidget {

  private static final char[] DIGITS = "0123456789".toCharArray();
  private static final char[] SIGN = "-+".toCharArray();

  private final CharGrid charGrid;
  private final Variable variable;
  private final Range range;
  private final Coordinate location;
  private final char[] currentValue;
  private int idx = 0;
  private Widget cmpWidget;
  private boolean liveUpdate = false;
  private final boolean signed;

  public FloatWidget(CharGrid charGrid, Coordinate location, Variable variable) {
    this.charGrid = charGrid;
    this.location = location;
    this.variable = variable;
    range = Optional.of(variable)
        .map(Variable::limit)
        .filter(Range.class::isInstance)
        .map(Range.class::cast)
        .orElse(Range.NO_LIMIT);
    if (!range.isClosed()) {
      throw new IllegalStateException("open range on " + variable);
    }
    signed = range.hasNegative();
    currentValue = range.format(((Number) variable.get()).floatValue()).toCharArray();
    cmpWidget = elementWidget();
  }

  /**
   * @param b whether to continually update the value or only on acceptance
   * @return <code>this</code>
   */
  public FloatWidget liveUpdate(boolean b) {
    liveUpdate = b;
    return this;
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
    return new CharacterWidget(charGrid, loc, var,
        idx == 0 && range.hasNegative()
            ? SIGN
            : DIGITS)
        .alwaysSet(true)
        .liveUpdate(true)
        .with(haptic);
  }

  @Override
  public Widget inputEvent(boolean yes, boolean no, boolean up, boolean down) {

    Outcome o = cmpWidget.inputEvent(yes, no, up, down).outcome();

    float prospect = Float.parseFloat(new String(currentValue));
    float nv = range.clamp(prospect);
    System.arraycopy(
        range.format(nv).toCharArray(), 0,
        currentValue, 0, currentValue.length);

    if (o == Outcome.YES) {
      if (idx == currentValue.length - 1) {
        variable.set(cast(nv));
        outcome = Outcome.YES;
        haptic.yes();
      } else {
        idx++;
        if (currentValue[idx] == '.') {
          idx++;
        }
      }
      cmpWidget = elementWidget();
    }

    if (o == Outcome.NO) {
      if (idx == 0) {
        outcome = Outcome.NO;
        haptic.no();
      } else {
        idx--;
        if (currentValue[idx] == '.') {
          idx--;
        }
      }
      cmpWidget = elementWidget();
    }

    return this;
  }

  @Override
  public Widget inputState(boolean yes, boolean no, boolean up, boolean down) {
    cmpWidget.inputState(yes, no, up, down);
    if (liveUpdate) {
      float prospect = Float.parseFloat(new String(currentValue));
      float nv = range.clamp(prospect);
      variable.set(cast(nv));
    }
    return this;
  }

  protected Object cast(float newValue) {
    return newValue;
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
