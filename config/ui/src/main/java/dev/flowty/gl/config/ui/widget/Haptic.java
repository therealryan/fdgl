package dev.flowty.gl.config.ui.widget;

/**
 * Defines non-visual UI feedback events
 */
public interface Haptic {

  /**
   * A no-op {@link Haptic}, for use as a default
   */
  Haptic EMPTY = new Haptic() {

    @Override
    public void yes() {
      // nowt
    }

    @Override
    public void up() {
      // nowt
    }

    @Override
    public void tick() {
      // nowt
    }

    @Override
    public void no() {
      // nowt
    }

    @Override
    public void down() {
      // nowt
    }
  };

  /**
   * Called when a widget has performed a confirmation action in response to user input
   */
  void yes();

  /**
   * Called when a widget has performed a cancel action in response to user input
   */
  void no();

  /**
   * Called when a widget has performed an up action in response to user input
   */
  void up();

  /**
   * Called when a widget has performed a down action in response to user input
   */
  void down();

  /**
   * Called when a widget has processed an internal update
   */
  void tick();
}
