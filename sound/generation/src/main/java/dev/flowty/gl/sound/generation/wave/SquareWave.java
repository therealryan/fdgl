package dev.flowty.gl.sound.generation.wave;

import dev.flowty.gl.sound.generation.Waveform;

/**
 * A square wave
 */
public class SquareWave extends Waveform {

  /**
   * The proportion of time that the wave spends at 1, the rest of the time the wave is at -1;
   */
  public float dutyCycle = 0.5f;

  @Override
  public float valueForPhase(float phase) {
    return phase < dutyCycle ? 1 : -1;
  }
}
