package dev.flowty.gl.framework.display;

import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.flowty.gl.config.model.annote.ChoiceFor;
import dev.flowty.gl.config.model.annote.ChoiceOf;
import dev.flowty.gl.config.model.annote.TypeHint;
import dev.flowty.gl.config.model.codec.ColourCodec;
import dev.flowty.gl.framework.input.Input;
import dev.flowty.gl.util.Colour;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows display interaction
 */
public class Display {

  private static final Logger LOG = LoggerFactory.getLogger(Display.class);

  private static final boolean DEBUG = Boolean.getBoolean("gl_debug");
  public static final String WINDOWED_DISPLAY_NAME = "window";

  private final String title;
  private final Input input;

  private long window = -1;

  private int clearColour = Colour.BLACK;

  private int msaaSamples = 4;

  private String name = WINDOWED_DISPLAY_NAME;

  private final Vector2i resolution = new Vector2i(1280, 800);

  private final Vector2f logicalResolution = new Vector2f(800, 600);

  private final List<Runnable> creationListeners = new ArrayList<>();

  private final List<Runnable> destructionListeners = new ArrayList<>();

  private PolygonMode polygonMode = PolygonMode.FILL;

  private int windowRefreshLimit = -1;
  private boolean windowRefreshBlocked = false;

  /**
   * @param title window title
   * @param input input handling, so that listeners can be added on display init
   */
  public Display(String title, Input input) {
    this.title = title;
    this.input = input;
  }

  /**
   * Adds a listener that will be notified when the display changes and opengl stuff needs to be
   * recreated
   *
   * @param l the listener
   * @return <code>this</code>
   */
  public Display withCreationListener(Runnable l) {
    creationListeners.add(l);
    return this;
  }

  /**
   * Adds a listener that will be notified when the display is about to be destroyed
   *
   * @param l the listener
   * @return {@code this}
   */
  public Display withDestructionListener(Runnable l) {
    destructionListeners.add(l);
    return this;
  }

  /**
   * @return The colour-buffer clear value
   */
  @JsonProperty("clear_colour")
  @JsonSerialize(using = ColourCodec.Write.class)
  @TypeHint(Colour.class)
  public int clearColour() {
    return clearColour;
  }

  /**
   * @param c The colour-buffer clear value
   * @return this
   */
  @JsonProperty("clear_colour")
  @JsonDeserialize(using = ColourCodec.Read.class)
  public Display clearColour(int c) {
    clearColour = c;

    if (window != -1) {
      glClearColor(
          Colour.redf(clearColour()),
          Colour.greenf(clearColour()),
          Colour.bluef(clearColour()),
          Colour.alphaf(clearColour()));
    }

    return this;
  }

  /**
   * @return The desired number of samples for anti-aliasing
   */
  @JsonProperty("msaa_samples")
  @ChoiceOf(ints = {0, 1, 2, 4, 8, 16})
  public int msaaSamples() {
    return msaaSamples;
  }

  /**
   * @param s The desired number of samples for anti-aliasing
   * @return <code>this</code>
   */
  @JsonProperty("msaa_samples")
  public Display msaaSamples(int s) {
    if (msaaSamples != s) {
      msaaSamples = s;
      refresh();
    }
    return this;
  }

  /**
   * @return the rasterisation mode
   */
  @JsonProperty("polygon_mode")
  public PolygonMode mode() {
    return polygonMode;
  }

  /**
   * @param m the rasterisation mode
   * @return <code>this</code>
   */
  @JsonProperty("polygon_mode")
  public Display mode(PolygonMode m) {
    if (polygonMode != m) {
      polygonMode = m;
      if (window != -1) {
        glPolygonMode(GL_FRONT_AND_BACK, polygonMode.mode);
      }
    }
    return this;
  }

  private void refresh() {
    if (window != -1) {
      if (windowRefreshLimit != 0) {
        destroy();
        create();
        show();
        if (windowRefreshLimit > 0) {
          windowRefreshLimit--;
        }
      } else {
        windowRefreshBlocked = true;
      }
    }
  }

  /**
   * Sets the name of the display that we'd like to be on
   *
   * @param n The desired display name, or <code>null</code> for windowed mode
   * @return <code>this</code>
   */
  @JsonProperty("name")
  public Display name(String n) {
    if (!Objects.equals(name, n)) {
      name = n;
      long monitor = getMonitor(name);
      if (monitor != NULL) {
        name = GLFW.glfwGetMonitorName(monitor);
        GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
        GLFW.glfwSetWindowMonitor(window, monitor,
            0, 0,
            mode.width(), mode.height(),
            mode.refreshRate());
      } else if (window != -1) {
        name = WINDOWED_DISPLAY_NAME;
        GLFW.glfwSetWindowMonitor(window, NULL,
            100, 100, 1280, 800, 0);
      }
    }
    return this;
  }

  /**
   * @return The name of the display that we're on
   */
  @JsonProperty("name")
  public String name() {
    return name;
  }

  /**
   * @return An array of possible display names
   */
  @ChoiceFor("name")
  public static String[] names() {
    PointerBuffer monitors = GLFW.glfwGetMonitors();
    if (monitors != null) {
      String[] names = new String[monitors.limit() - monitors.position() + 1];
      names[0] = WINDOWED_DISPLAY_NAME;
      for (int i = 0; i < names.length - 1; i++) {
        names[i + 1] = GLFW.glfwGetMonitorName(monitors.get(monitors.position() + i));
      }
      Arrays.sort(names);
      return names;
    }
    return new String[0];
  }

  /**
   * @return The display resolution
   */
  public Vector2ic resolution() {
    return resolution;
  }

  /**
   * Called in response to a window resize
   *
   * @param res display resolution
   * @return <code>this</code>
   */
  private Display resolution(Vector2i res) {
    LOG.info("Resized to {}x{}", res.x(), res.y());
    resolution.set(res);
    if (resolution.x == 0 || resolution.y == 0) {
      // this happens on windows when a full-screen window loses focus
      // we have to avoid the divide-by-zero in logicalHeight
    } else {
      // refresh the logical dimensions
      logicalHeight(logicalResolution.y);
    }
    return this;
  }

  public Display setSize(int width, int height) {
    if (window == -1) {
      resolution.set(width, height);
    } else {
      GLFW.glfwSetWindowSize(window, width, height);
    }
    return this;
  }

  // The mode-switching stuff works in that we call the correct GLFW methods, but
  // it's utterly fucked as far as monitors actually changing config in a sane way
  // goes. Not worth it.
  /*
   * @JsonProperty("mode") public String mode() { long monitor =
   * GLFW.glfwGetWindowMonitor( window ); if( monitor == 0 ) { return "window"; }
   * GLFWVidMode mode = GLFW.glfwGetVideoMode( monitor ); return new Mode( mode
   * ).toString(); }
   *
   * @JsonProperty("mode") public Display mode( String res ) { Mode mode =
   * Mode.from( res ); if( mode != null ) { long monitor =
   * GLFW.glfwGetWindowMonitor( window ); if( monitor != 0 ) {
   * GLFW.glfwSetWindowMonitor( window, monitor, 0, 0, mode.width, mode.height,
   * mode.refresh ); } } return this; }
   *
   * @ChoiceFor("mode") public String[] modes() { long monitor =
   * GLFW.glfwGetWindowMonitor( window ); if( monitor == 0 ) { return new String[]
   * { "window" }; } GLFWVidMode.Buffer modes = GLFW.glfwGetVideoModes( monitor );
   * String[] ma = new String[modes.limit() - modes.position()]; for( int i = 0; i
   * < ma.length; i++ ) { GLFWVidMode mode = modes.get(); ma[i] = new Mode( mode
   * ).toString(); } return ma; }
   *
   * private static class Mode { private final int width, height, refresh;
   *
   * private Mode( int width, int height, int refresh ) { this.width = width;
   * this.height = height; this.refresh = refresh; }
   *
   * private Mode( GLFWVidMode mode ) { this( mode.width(), mode.height(),
   * mode.refreshRate() ); }
   *
   * private static final Pattern REGEX = Pattern.compile( "(\\d+)x(\\d+)@(\\d+)"
   * );
   *
   * private static Mode from( String s ) { Matcher m = REGEX.matcher( s ); if(
   * m.matches() ) { int i = 1; return new Mode( Integer.parseInt( m.group( i++ )
   * ), Integer.parseInt( m.group( i++ ) ), Integer.parseInt( m.group( i++ ) ) );
   * } return null; }
   *
   * @Override public String toString() { return String.format( "%sx%s@%s", width,
   * height, refresh ); } } /* /** Sets the logical height of the display
   *
   * @param height The height of the display in the units we want to work in
   *
   * @return <code>this</code>
   */

  /**
   * Sets the logical height of the display, updating the logical display size according to the
   * physical dimensions
   *
   * @param height The new height
   * @return <code>this</code>
   */
  public Display logicalHeight(float height) {
    logicalResolution.set(
        resolution.x * (height / resolution.y),
        resolution.y * (height / resolution.y));
    return this;
  }

  /**
   * @return The logical dimensions of the display
   */
  public Vector2fc logicalDimensions() {
    return logicalResolution;
  }

  /**
   * Creates the display and opengl context
   */
  @SuppressWarnings("resource")
  public void create() {
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW.GLFW_SAMPLES, msaaSamples());

    if (DEBUG) {
      glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
    }

    long monitor = getMonitor(name());
    if (monitor != NULL) {
      name = GLFW.glfwGetMonitorName(monitor);
      GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
      resolution.set(mode.width(), mode.height());
      glfwWindowHint(GLFW_RED_BITS, mode.redBits());
      glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
      glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
      glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
    }

    LOG.info("Requesting {}x{} window on {}", resolution.x, resolution.y, monitor);
    window = glfwCreateWindow(resolution.x, resolution.y, title, monitor, NULL);

    if (window == NULL) {
      throw new IllegalStateException("Failed to create the GLFW window");
    }

    // store the res that we ended up with
    try (MemoryStack ms = MemoryStack.stackPush()) {
      IntBuffer w = ms.mallocInt(1);
      IntBuffer h = ms.mallocInt(1);
      GLFW.glfwGetWindowSize(window, w, h);
      resolution(new Vector2i(w.get(), h.get()));
    }
    // we want to be notified about resizes
    glfwSetWindowSizeCallback(window,
        (wndw, width, height) -> resolution(new Vector2i(width, height)));
    // and we want the viewport to fill the display
    GLFW.glfwSetFramebufferSizeCallback(window, (wndw, width, height) -> {
      LOG.info("framebuffer resized to {}x{}", width, height);
      glViewport(0, 0, width, height);
    });

    input.initialise(window);

    // Make the OpenGL context current
    glfwMakeContextCurrent(window);
    // Enable v-sync
    glfwSwapInterval(1);

    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    if (DEBUG) {
      GLUtil.setupDebugMessageCallback();
    }

    glClearColor(
        Colour.redf(clearColour()),
        Colour.greenf(clearColour()),
        Colour.bluef(clearColour()),
        Colour.alphaf(clearColour()));

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glPolygonMode(GL_FRONT_AND_BACK, polygonMode.mode);
    glEnable(GL11.GL_DEPTH_TEST);
    glDepthFunc(GL11.GL_LEQUAL);

    creationListeners.forEach(Runnable::run);
  }

  /**
   * Makes the display visible
   */
  public void show() {
    glfwShowWindow(window);
  }

  /**
   * Cleans up window resources
   */
  public void destroy() {
    destructionListeners.forEach(Runnable::run);

    if (window != -1 && window != NULL) {
      GLFW.glfwDestroyWindow(window);
      window = -1;
    }
  }

  /**
   * @return <code>true</code> if window closure has been requested
   */
  public boolean shouldClose() {
    return GLFW.glfwWindowShouldClose(window);
  }

  /**
   * Swaps the render buffers to display drawn content
   */
  public void swapBuffers() {
    GLFW.glfwSwapBuffers(window);
  }

  private static long getMonitor(String name) {
    if (WINDOWED_DISPLAY_NAME.equals(name)) {
      return NULL;
    }

    PointerBuffer monitors = GLFW.glfwGetMonitors();
    if (monitors != null) {
      for (int i = monitors.position(); i < monitors.limit(); i++) {
        long m = monitors.get(i);
        String n = GLFW.glfwGetMonitorName(m);
        if (n.equals(name)) {
          return m;
        }
      }
      if (LOG.isWarnEnabled()) {
        LOG.warn("Monitor '{}' not found in [{}]",
            name,
            streaming(monitors)
                .mapToObj(GLFW::glfwGetMonitorName)
                .collect(Collectors.joining(", ")));
        LOG.warn("Defaulting to primary");
      }

      return GLFW.glfwGetPrimaryMonitor();
    } else {
      LOG.warn("No monitors found!");
    }
    return NULL;
  }

  private static LongStream streaming(PointerBuffer buffer) {
    long[] values = new long[buffer.limit() - buffer.position()];
    buffer.get(values, buffer.position(), values.length);
    return LongStream.of(values);
  }

  public Display withWindowRefreshLimit(int count) {
    windowRefreshLimit = count;
    return this;
  }

  public boolean windowRefreshBlocked() {
    return windowRefreshBlocked;
  }
}
