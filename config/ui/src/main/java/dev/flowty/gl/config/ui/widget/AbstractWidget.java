package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.ui.Widget;

/**
 * Convenience superclass
 */
public abstract class AbstractWidget implements Widget {

  /**
   * The non-visual event feedback mechanism
   */
  protected Haptic haptic = Haptic.EMPTY;

  /**
   * The outcome of the widget
   */
  protected Outcome outcome = Outcome.CONTINUE;

  @Override
  public Widget with(Haptic h) {
    haptic = h;
    return this;
  }

  @Override
  public Outcome outcome() {
    return outcome;
  }
}
