package dev.flowty.gl.shader.test;

import dev.flowty.gl.framework.Game;
import dev.flowty.gl.framework.Runner;
import dev.flowty.gl.framework.display.Display;
import dev.flowty.gl.framework.display.ScreenShot;
import dev.flowty.gl.framework.input.Input;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.StackWalker.StackFrame;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import javax.imageio.ImageIO;
import org.joml.Vector2i;

/**
 * A framework for testing OpenGL rendering
 */
public class RenderTest {

  private final Path expected;
  private final Path actual;
  private final Input input = new Input();
  private final Display display;
  private final Consumer<Display> render;
  private Optional<Vector2i> trimTo = Optional.empty();
  private Optional<IntUnaryOperator> pixels = Optional.empty();

  /**
   * @param render The render behaviour to test
   */
  public RenderTest(Consumer<Display> render) {
    this(d -> {
      // nowt
    }, render);
  }

  /**
   * @param init   How to initialise the display
   * @param render The render behaviour to test
   */
  public RenderTest(Consumer<Display> init, Consumer<Display> render) {
    this.render = render;
    StackFrame caller = StackWalker.getInstance().walk(stacks -> stacks
        .filter(s -> !s.getClassName().equals(RenderTest.class.getName()))
        .findFirst()
        .get());
    display = new Display(caller.getMethodName(), input);
    init.accept(display);
    expected = Paths.get("src", "test", "resources",
        caller.getClassName(), caller.getMethodName() + ".png");
    actual = Paths.get("target",
        caller.getClassName(), caller.getMethodName() + ".png");
  }

  private final Game game = new Game() {
    private int drawCount = 0;

    @Override
    public Input input() {
      return input;
    }

    @Override
    public Display display() {
      return display;
    }

    @Override
    public void initialiseGL() {
      // nowt
    }

    @Override
    public void destroyGL() {
      // nowt
    }

    @Override
    public void initialiseState() {
      // nowt
    }

    @Override
    public boolean advance(float delta) {
      if (drawCount > 1) {
        ScreenShot.capture(actual);
        return false;
      }
      return true;
    }

    @Override
    public void draw() {
      render.accept(display);
      drawCount++;
    }
  };

  public RenderTest trimmingTo(int width, int height) {
    trimTo = Optional.of(new Vector2i(width, height));
    return this;
  }

  public RenderTest pixels(IntUnaryOperator pixels) {
    this.pixels = Optional.of(pixels);
    return this;
  }

  /**
   * Call this to provoke the rendering and result comparison
   */
  public void assertRenderResults() {
    new Runner(game).run();

    trimTo.ifPresent(size -> {
      try {
        BufferedImage raw = ImageIO.read(actual.toFile());
        int x = (raw.getWidth() - size.x()) / 2;
        int y = (raw.getHeight() - size.y()) / 2;
        BufferedImage trimmed = raw.getSubimage(x, y, size.x(), size.y());
        ImageIO.write(trimmed, "png", actual.toFile());
      } catch (IOException ioe) {
        throw new UncheckedIOException(ioe);
      }
    });

    pixels.ifPresent(operation -> {
      try {
        BufferedImage raw = ImageIO.read(actual.toFile());
        BufferedImage out = new BufferedImage(raw.getWidth(), raw.getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < raw.getWidth(); x++) {
          for (int y = 0; y < raw.getHeight(); y++) {
            int pixel = raw.getRGB(x, y);
            int argb = operation.applyAsInt(pixel);
            out.setRGB(x, y, argb);
          }
        }
        ImageIO.write(out, "png", actual.toFile());
      } catch (IOException ioe) {
        throw new UncheckedIOException(ioe);
      }

    });

    ImageDiff.compare(expected, actual);
  }

}
