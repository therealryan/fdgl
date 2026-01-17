package dev.flowty.gl.sound.play;

import dev.flowty.gl.sound.generation.SoundSpec;
import dev.flowty.gl.sound.generation.var.Constant;
import dev.flowty.gl.sound.generation.wave.SineWave;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises sound generation and playback
 */
@SuppressWarnings("static-method")
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "No sound support")
class SoundSystemTest {

  /**
   * Generates and plays a simple beep
   *
   * @throws InterruptedException if we're interrupted while waiting for the sound to end
   */
  @Test
  void beep() throws InterruptedException {
    SoundSystem sys = new SoundSystem();

    SoundSpec spec = new SoundSpec();
    spec.length = 0.2f;
    spec.volumeEnvelope = new Constant(1);
    spec.waveform = new SineWave();
    spec.waveform.frequency = new Constant(1600);

    Sound sound = new Sound(spec);

    Source source = sys.getSource();
    source.bindSound(sound).play();

    Thread.sleep(200);

    sys.destroy();
  }
}
