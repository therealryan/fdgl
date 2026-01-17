package dev.flowty.gl.sound.generation;

import java.nio.ShortBuffer;

/**
 * Interface for post-processing effects on sounds
 */
public interface PostProcess {

  /**
   * Process the sound data
   *
   * @param data       The sound data
   * @param sampleRate The number of samples per second
   */
  void process(ShortBuffer data, int sampleRate);
}
