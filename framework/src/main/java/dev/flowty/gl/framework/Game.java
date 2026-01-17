package dev.flowty.gl.framework;

import dev.flowty.gl.framework.display.Display;
import dev.flowty.gl.framework.input.Input;

/**
 * Interface for a game
 */
public interface Game {

  /**
   * One sixtieth of a second
   */
  float SIXTY_PER_SECOND_STEP = 1f / 60;

  /**
   * Gets the logic timestep
   *
   * @return in seconds
   */
  default float logicAdvance() {
    return SIXTY_PER_SECOND_STEP;
  }

  /**
   * Gets the object that should be supplied with input state
   *
   * @return The input object
   */
  Input input();

  /**
   * Gets the object that controls display state
   *
   * @return the display object
   */
  Display display();

  /**
   * Called when the window is initialised
   */
  void initialiseGL();

  /**
   * Called when the window is about to be destroyed
   */
  void destroyGL();

  /**
   * Perform one-time setup work for game logic
   */
  void initialiseState();

  /**
   * Advances the game state
   *
   * @param delta The number of seconds that has passed since the last call to
   *              {@link #advance(float)}
   * @return <code>true</code> if the game should continue, <code>false</code> if
   * it should exit
   */
  boolean advance(float delta);

  /**
   * Draws the current game state
   */
  void draw();
}
