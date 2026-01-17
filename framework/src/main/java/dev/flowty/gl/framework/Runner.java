package dev.flowty.gl.framework;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_EXTENSIONS;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL30.GL_NUM_EXTENSIONS;
import static org.lwjgl.opengl.GL30.glGetStringi;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execution environment for a {@link Game}
 */
public class Runner implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

  private final Game game;

  /**
   * @param game how to build the game
   */
  public Runner(Game game) {
    this.game = game;
  }

  @Override
  public void run() {
    try {
      initialise();
      loop();
    } catch (Exception e) {
      LOG.error("Unexpected failure", e);
    } finally {
      shutdown();
    }
  }

  @SuppressWarnings("resource")
  private void initialise() {
    LOG.info("LWJGL version {}", Version.getVersion());

    GLFWErrorCallback.create(new GLFWErrorLogger()).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    game.display().create();

    LOG.info("GL_VENDOR   {}", glGetString(GL_VENDOR));
    LOG.info("GL_RENDERER {}", glGetString(GL_RENDERER));
    LOG.info("GL_VERSION  {}", glGetString(GL_VERSION));
    LOG.info("GL_SHADING_LANGUAGE_VERSION  {}", glGetString(GL_SHADING_LANGUAGE_VERSION));
    if (LOG.isDebugEnabled()) {
      int extensions = glGetInteger(GL_NUM_EXTENSIONS);
      for (int i = 0; i < extensions; i++) {
        LOG.debug("extension {} {}", i, glGetStringi(GL_EXTENSIONS, i));
      }
    }

    game.initialiseState();
    game.initialiseGL();

    game.display().withCreationListener(game::initialiseGL);
    game.display().withDestructionListener(game::destroyGL);

    // Make the window visible
    game.display().show();

    GLUtil.checkGLError();
  }

  private void loop() {
    boolean keepRunning = true;
    // Run the rendering loop until the game signals completion or the close window
    // button has been clicked
    double previous = GLFW.glfwGetTime();
    float delta = 0;
    while (keepRunning && !game.display().shouldClose()) {
      double now = GLFW.glfwGetTime();
      delta += (float) (now - previous);
      previous = now;
      float step = game.logicAdvance();
      while (keepRunning && delta >= step) {
        keepRunning = game.advance(step);
        delta -= step;
        game.input().advance();
        glfwPollEvents(); // invokes the input callbacks
      }

      if (keepRunning) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        game.draw();
        game.display().swapBuffers();

        GLUtil.checkGLError();
      }
    }
  }

  private void shutdown() {
    game.display().destroy();
    glfwTerminate();
  }

}
