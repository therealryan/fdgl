package dev.flowty.gl.framework;

/**
 * Represents a single screen in a game, e.g.: the main menu, the settings menu, the loading screen,
 * the game itself, the post-game scorecard, etc
 */
public interface Screen {

  /**
   * Advances the screen state
   *
   * @param delta The number of seconds that has passed since the last call to
   *              {@link #advance(float)}
   */
  void advance(float delta);

  /**
   * Draws the current screen state
   */
  void draw();

  /**
   * @return The next screen (probably <code>this</code>), or <code>null</code> if the game should
   * end
   */
  Screen next();
}
