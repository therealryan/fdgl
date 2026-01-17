package dev.flowty.gl.sound.generation.wave;

import dev.flowty.gl.sound.generation.Waveform;

/**
 * Sine waveform
 */
public class SineWave extends Waveform {

  @Override
  public float valueForPhase(float phase) {
    return (float) Math.sin(Math.PI * 2 * phase);
  }
}
