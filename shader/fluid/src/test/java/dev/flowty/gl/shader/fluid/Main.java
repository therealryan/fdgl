package dev.flowty.gl.shader.fluid;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import dev.flowty.gl.framework.Game;
import dev.flowty.gl.framework.Runner;
import dev.flowty.gl.framework.display.Display;
import dev.flowty.gl.framework.input.Input;
import dev.flowty.gl.shader.flat.ColouredShape;
import dev.flowty.gl.shader.flat.FlatColourRenderer;
import dev.flowty.gl.shape.Shape;
import dev.flowty.gl.shape.font.GlyphShape;
import dev.flowty.gl.shape.font.ShapeFont;
import dev.flowty.gl.util.Colour;
import java.awt.Font;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Interactive fluid app
 */
public class Main implements Game {

  /**
   * @param args from commandline
   */
  public static void main(String[] args) {
    new Runner(new Main()).run();
  }

  private static final ColouredShape TRIANGLE = new ColouredShape(new Shape(
      new float[]{
          40 * Math.cos(Math.toRadians(90)),
          40 * Math.sin(Math.toRadians(90)),
          0,
          40 * Math.cos(Math.toRadians(210)),
          40 * Math.sin(Math.toRadians(210)),
          0,
          40 * Math.cos(Math.toRadians(330)),
          40 * Math.sin(Math.toRadians(330)),
          0,
      },
      new int[]{
          0, 1, 2}),
      Colour.GREEN);

  private final FluidSimulation fluid = new FluidSimulation();
  private FlatColourRenderer flat;
  private final ShapeFont font = new ShapeFont(new GlyphShape(Font.decode("monospace")));
  private final Shape character = font.of('#').transform(new Matrix4f().scale(10));
  private final Input input = new Input();
  private final Display display = new Display("fluid", input);

  private final Vector2f point = new Vector2f(400, 300);

  private float time = 0;

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
    fluid.withScreenRes(display.resolution());
    fluid.withLogicalRes(display.logicalDimensions());

    flat = new FlatColourRenderer(5);
    flat.program().projection.update(m -> m
        .setOrtho2DLH(
            0, display.logicalDimensions().x(),
            0, display.logicalDimensions().y()));
    splats();
  }

  @Override
  public void destroyGL() {
    fluid.destroy();
  }

  @Override
  public void initialiseState() {
    // nowt
  }

  @Override
  public boolean advance(float delta) {

    fluid.advance(delta);

    if (input.keyboard().down(GLFW.GLFW_KEY_SPACE)) {
      fluid.dye().with(
          m -> m.translate(point.x, point.y, 0),
          r -> r.draw(character, Colour.GREEN));

      fluid.velocity().with(
          m -> m.translate(point.x, point.y, 0),
          r -> r.draw(character,
              new Vector2f(400 - point.x, 300 - point.y)
                  .mul(0.25f)));
    }

    if (input.keyboard().down(GLFW.GLFW_KEY_LEFT)) {
      point.x -= 10;
    }
    if (input.keyboard().down(GLFW.GLFW_KEY_RIGHT)) {
      point.x += 10;
    }
    if (input.keyboard().down(GLFW.GLFW_KEY_UP)) {
      point.y += 10;
    }
    if (input.keyboard().down(GLFW.GLFW_KEY_DOWN)) {
      point.y -= 10;
    }

    if (input.keyboard().pressed(GLFW.GLFW_KEY_R)) {
      // provoke the init/destroy behaviour
      display().msaaSamples(display.msaaSamples() + 1);
    }

    point.x = Math.max(0, Math.min(800, point.x));
    point.y = Math.max(0, Math.min(600, point.y));

    time += delta;
    while (time > 1) {
      time -= 1;
      splats();
    }

    return !input.keyboard().down(GLFW.GLFW_KEY_ESCAPE);
  }

  @Override
  public void draw() {

    fluid.render(display.resolution());

    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    flat.with(
        m -> m.translate(point.x, point.y, 0),
        r -> r.draw(TRIANGLE));
    flat.render();
    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
  }

  private void splats() {
    fluid.splat(
        new Vector2f(0.15f, 0.15f),
        new Vector2f(500, 500),
        new Vector3f(1, 0, 0));
    fluid.splat(
        new Vector2f(0.85f, 0.85f),
        new Vector2f(-500, -500),
        new Vector3f(0, 0, 1));
  }
}
