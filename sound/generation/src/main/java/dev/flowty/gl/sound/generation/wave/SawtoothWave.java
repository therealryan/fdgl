package dev.flowty.gl.sound.generation.wave;

import dev.flowty.gl.sound.generation.Waveform;

/**
 * A sawtooth {@link Waveform}
 */
public class SawtoothWave extends Waveform {

  @Override
  public float valueForPhase(float phase) {
    return 1 - 2 * phase;
  }
}
