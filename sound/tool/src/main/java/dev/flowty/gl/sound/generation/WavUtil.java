package dev.flowty.gl.sound.generation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Utility for working with wav files
 */
public class WavUtil {

  /**
   * Gets an {@link AudioInputStream} for a sound
   *
   * @param sampleRate Samples per second
   * @param pcm        The pcm data
   * @return An {@link AudioInputStream}
   */
  public static AudioInputStream getAudioStream(int sampleRate, ByteBuffer pcm) {
    AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);

    byte[] data = new byte[pcm.limit()];
    pcm.get(data);
    pcm.rewind();
    ByteArrayInputStream bais = new ByteArrayInputStream(data);

    return new AudioInputStream(bais, af, data.length / 2);
  }

  /**
   * Saves some sound data as a wav file
   *
   * @param sampleRate Samples per second
   * @param pcm        The pcm data
   * @param fileName   The file to write to
   * @throws IOException if something goes wrong
   */
  public static void saveAsWav(int sampleRate, ByteBuffer pcm,
      String fileName) throws IOException {
    try (AudioInputStream aio = getAudioStream(sampleRate, pcm)) {
      AudioSystem.write(aio, AudioFileFormat.Type.WAVE, new File(fileName));
    }
  }
}
