package dev.flowty.gl.config.ui;

import dev.flowty.gl.config.model.Config;
import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;
import dev.flowty.gl.config.ui.widget.Haptic;

/**
 * Input/output API for {@link Config} user interface elements
 */
public interface Widget {

  /**
   * The outcome of a widget
   */
  enum Outcome {
    /**
     * The widget has completed but the edit was rejected
     */
    NO,
    /**
     * The widget has not completed
     */
    CONTINUE,
    /**
     * The widget has completed and the edit was accepted
     */
    YES
  }

  /**
   * Called when an input is activated
   *
   * @param yes  <code>true</code> if the user has activated the yes input
   * @param no   <code>true</code> if the user has activated the no input
   * @param up   <code>true</code> if the user has activated the up input
   * @param down <code>true</code> if the user has activated the down input
   * @return <code>this</code>
   */
  Widget inputEvent(boolean yes, boolean no, boolean up, boolean down);

  /**
   * Called when input state has changed
   *
   * @param yes  <code>true</code> if the yes input is active
   * @param no   <code>true</code> if the no input is active
   * @param up   <code>true</code> if the up input is active
   * @param down <code>true</code> if the down input is active
   * @return <code>this</code>
   */
  Widget inputState(boolean yes, boolean no, boolean up, boolean down);

  /**
   * Advances state
   *
   * @param delta in seconds
   * @return <code>this</code>
   */
  Widget update(float delta);

  /**
   * @return The UI state
   */
  CharGrid textGrid();

  /**
   * @return whether the widget has completed or not
   */
  Outcome outcome();

  /**
   * Supplies the non-visual feedback mechanism
   *
   * @param haptic feedback events should be supplied to this
   * @return <code>this</code>
   */
  Widget with(Haptic haptic);

  /**
   * @return a human-readable description for the current value, or null
   */
  default String description() {
    return null;
  }

  /**
   * Interface for objects that can build widgets
   */
  interface Builder {

    /**
     * @param charGrid the grid to draw on
     * @param location The location to draw at
     * @param variable the variable to control
     * @return a widget
     */
    Widget build(CharGrid charGrid, Coordinate location, Variable variable);
  }
}
