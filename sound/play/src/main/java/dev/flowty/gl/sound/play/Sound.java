package dev.flowty.gl.sound.play;

import dev.flowty.gl.sound.generation.SoundSpec;
import java.nio.ByteBuffer;
import org.lwjgl.openal.AL10;

/**
 * Encapsulates sound data, possibly loaded into openal.
 */
public class Sound {

  /**
   * Sound data format
   */
  public final Format format = Format.MONO_16;

  /**
   * Sound sample rate
   */
  public final int sampleRate = 44100;

  /**
   * Sound data
   */
  public final ByteBuffer data;

  /**
   * OpenAL buffer name
   */
  private int bufferID = -1;

  /**
   * Builds a new {@link Sound}
   *
   * @param spec sound frequency specification
   */
  public Sound(SoundSpec spec) {
    data = spec.generate(sampleRate);
  }

  /**
   * Loads this sound into OpenAL if needed, and returns the buffer name
   *
   * @return the OpenAL buffer ID
   */
  int getBufferName() {
    if (bufferID == -1) {
      bufferID = AL10.alGenBuffers();
      AL10.alBufferData(bufferID, format.alFormat, data, sampleRate);
    }

    return bufferID;
  }

  /**
   * Unloads this sound from OpenAL
   */
  public void destroy() {
    if (bufferID != -1) {
      AL10.alDeleteBuffers(bufferID);
      bufferID = -1;
    }
  }
}
