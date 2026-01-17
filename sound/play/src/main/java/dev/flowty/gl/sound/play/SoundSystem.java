package dev.flowty.gl.sound.play;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates OpenAL
 */
public class SoundSystem {

  private static final Logger LOG = LoggerFactory.getLogger(SoundSystem.class);
  /**
   * The maximum number of sources that will be constructed. The actual number available may be less
   * than this
   */
  public static int MAX_SOURCES = 64;

  private final long device;
  private final Source[] sources;
  private final List<WeakReference<Sound>> sounds = new LinkedList<>();

  /**
   * Initialises the {@link SoundSystem}
   */
  @SuppressWarnings("boxing")
  public SoundSystem() {
    // Start by acquiring the default device
    device = ALC10.alcOpenDevice((ByteBuffer) null);
    // Create a handle for the device capabilities, as well.
    ALCCapabilities deviceCaps = ALC.createCapabilities(device);
    // Create context (often already present, but here, necessary)
    IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);

    // Note the manner in which parameters are provided to OpenAL...
    contextAttribList.put(ALC10.ALC_REFRESH);
    contextAttribList.put(60);

    contextAttribList.put(ALC10.ALC_SYNC);
    contextAttribList.put(ALC10.ALC_FALSE);

    contextAttribList.put(0);
    contextAttribList.flip();

    // create the context with the provided attributes
    long newContext = ALC10.alcCreateContext(device, contextAttribList);

    if (!ALC10.alcMakeContextCurrent(newContext)) {
      throw new IllegalStateException("Failed to make context current");
    }

    AL.createCapabilities(deviceCaps);

    LOG.info("OpenAL vendor: {}", AL10.alGetString(AL10.AL_VENDOR));
    LOG.info("OpenAL version: {}", AL10.alGetString(AL10.AL_VERSION));
    LOG.info("OpenAL renderer: {}", AL10.alGetString(AL10.AL_RENDERER));
    if (LOG.isDebugEnabled()) {
      LOG.debug("OpenAL extensions: {}", AL10.alGetString(AL10.AL_EXTENSIONS));
    }

    // lets see how many sources we can make
    List<Source> sourceList = new ArrayList<>();
    Source s = null;
    do {
      s = null;
      try {
        s = new Source();
        sourceList.add(s);
      } catch (Exception e) {
        LOG.info("After {} sources", sourceList.size(), e);
      }
    }
    while (s != null && MAX_SOURCES > sourceList.size());

    sources = sourceList.toArray(new Source[sourceList.size()]);

    LOG.info("{} OpenAL sources available", sources.length);

    if (sources.length == 0) {
      throw new IllegalStateException("No sources could be created");
    }
  }

  /**
   * Registers a sound to be deleted cleanly on {@link #destroy()} call
   *
   * @param s the sound
   */
  public void registerSound(Sound s) {
    sounds.add(new WeakReference<>(s));
  }

  /**
   * Deletes all {@link Sound}s and {@link Source}s, tears down the OpenAL context
   */
  public void destroy() {
    if (sources != null) {
      // unbind sources
      for (Source source : sources) {
        source.bindSound(null);
      }
    }

    // delete sounds
    for (WeakReference<Sound> ws : sounds) {
      if (ws.get() != null) {
        ws.get().destroy();
      }
    }

    if (sources != null) {
      // delete sources
      for (Source source : sources) {
        source.destroy();
      }
    }

    ALC10.alcCloseDevice(device);
  }

  /**
   * Advances the {@link SoundSystem}, updating the idle status of all sources
   */
  private void advance() {
    for (Source source : sources) {
      source.updateIdleStatus();
    }
  }

  /**
   * Gets the first idle {@link Source}, or the source with the minimum {@link Source#priority}
   *
   * @return A {@link Source}, or null if all sources are reserved
   */
  public Source getSource() {
    advance();

    Source minPriority = sources[0];

    for (Source element : sources) {
      if (element.isIdle() && !element.isLocked()) {
        return element.reset();
      }

      if (element.priority < minPriority.priority || minPriority.isLocked()) {
        minPriority = element;
      }
    }

    if (minPriority.isLocked()) {
      return null;
    }

    return minPriority.reset();
  }

  /**
   * List the sources that are currently playing
   *
   * @return the non-idle sources
   */
  public Source[] getPlayingSources() {
    List<Source> sl = new ArrayList<>();
    for (Source element : sources) {
      if (element.isPlaying()) {
        sl.add(element);
      }
    }

    return sl.toArray(new Source[sl.size()]);
  }

  /**
   * Sets the master volume
   *
   * @param gain 0 for mute, 1 for no change, otherwise logarithmic scale
   */
  public static void setListenerGain(float gain) {
    AL10.alListenerf(AL10.AL_GAIN, Math.max(0, gain));
  }

  /**
   * Sets the listener's position
   *
   * @param x position
   * @param y position
   * @param z position
   */
  public static void setListenerPosition(float x, float y, float z) {
    AL10.alListener3f(AL10.AL_POSITION, x, y, z);
  }

  /**
   * Sets the listener's velocity
   *
   * @param x velocity
   * @param y velocity
   * @param z velocity
   */
  public static void setListenerVelocity(float x, float y, float z) {
    AL10.alListener3f(AL10.AL_VELOCITY, x, y, z);
  }

  /**
   * Sets the listener's orientation, by means of forward and up vectors
   *
   * @param fx forward
   * @param fy forward
   * @param fz forward
   * @param ux up
   * @param uy up
   * @param uz up
   */
  public static void setListenerOrientation(float fx, float fy, float fz, float ux,
      float uy, float uz) {
    FloatBuffer buff = BufferUtils.createFloatBuffer(6);
    buff.put(fx).put(fy).put(fz);
    buff.put(ux).put(uy).put(uz);

    AL10.alListenerfv(AL10.AL_ORIENTATION, buff);
  }
}
