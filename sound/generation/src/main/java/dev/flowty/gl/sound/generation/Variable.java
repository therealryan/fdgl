package dev.flowty.gl.sound.generation;

/**
 * Defines how a value changes over time
 */
public interface Variable {

  /**
   * Gets the value
   *
   * @param time The time, in seconds
   * @return the value at the specified time
   */
  float getValue(float time);
}
