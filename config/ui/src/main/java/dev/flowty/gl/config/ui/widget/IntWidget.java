package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;

/**
 * Manipulates integer {@link Variable}s
 */
public class IntWidget extends FloatWidget {

  /**
   * @param charGrid the grid to draw to
   * @param location The location of the value in the grid
   * @param variable The variable to manipulate
   */
  public IntWidget(CharGrid charGrid, Coordinate location, Variable variable) {
    super(charGrid, location, variable);
  }

  @Override
  protected Object cast(float newValue) {
    return (int) newValue;
  }
}
