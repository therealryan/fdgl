package dev.flowty.gl.sound.play;

import org.lwjgl.openal.AL10;

/**
 * Sound formats
 */
public enum Format {

  /***/
  MONO_16(AL10.AL_FORMAT_MONO16, 2),
  /***/
  MONO_8(AL10.AL_FORMAT_MONO8, 1),
  /***/
  STEREO_16(AL10.AL_FORMAT_STEREO16, 4),
  /***/
  STEREO_8(AL10.AL_FORMAT_STEREO8, 2);

  /**
   * The OpenAL format flag
   */
  public final int alFormat;

  /**
   * The number of bytes used for each sample
   */
  public final int bytesPerSample;

  Format(int f, int b) {
    alFormat = f;
    bytesPerSample = b;
  }
}
