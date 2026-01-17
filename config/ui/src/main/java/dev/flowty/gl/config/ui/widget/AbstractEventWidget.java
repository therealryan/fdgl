package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.ui.Widget;

/**
 * A widget that is entirely driven by input events
 */
public abstract class AbstractEventWidget extends AbstractWidget {

  @Override
  public Widget inputEvent(boolean yes, boolean no, boolean up, boolean down) {
    if (yes) {
      yes();
    }
    if (no) {
      no();
    }
    if (up) {
      up();
    }
    if (down) {
      down();
    }
    return this;
  }

  /**
   * Called when the yes input is pressed
   */
  protected abstract void yes();

  /**
   * Called when the no input is pressed
   */
  protected abstract void no();

  /**
   * Called when the up input is pressed
   */
  protected abstract void up();

  /**
   * Called when the down input is pressed
   */
  protected abstract void down();

  @Override
  public Widget inputState(boolean yes, boolean no, boolean up, boolean down) {
    // no state-based behaviour
    return this;
  }
}
