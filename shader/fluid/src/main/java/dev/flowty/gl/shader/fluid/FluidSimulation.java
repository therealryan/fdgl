package dev.flowty.gl.shader.fluid;

import static org.joml.Math.round;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;

import dev.flowty.gl.shader.fbo.Blit;
import dev.flowty.gl.shader.fbo.BufferedTextureFBO;
import dev.flowty.gl.shader.fbo.CopyProgram;
import dev.flowty.gl.shader.fbo.TextureFBO;
import dev.flowty.gl.shader.fbo.TextureFBO.Filter;
import dev.flowty.gl.shader.fbo.TextureFBO.Format;
import dev.flowty.gl.shader.flat.FlatColourRenderer;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3fc;

/**
 * Lifted entirely from https://github.com/PavelDoGreat/WebGL-Fluid-Simulation
 */
public class FluidSimulation {

  /**
   * Simulation parameters
   */
  public static class Config {

    private final int minSimRes = 128;
    private final int minDyeRes = 1024;

    private final float densityDissipation = 1;
    private final float velocityDissipation = 0.2f;
    private final float pressure = 0.8f;
    private final int pressureIterations = 20;
    private final float curl = 30;
  }

  private final Config config = new Config();

  private final Vector2i screenRes = new Vector2i(1, 1);
  private final Vector2i simRes = new Vector2i(1, 1);
  private final Vector2i dyeRes = new Vector2i(1, 1);

  private BufferedTextureFBO dye = new BufferedTextureFBO(
      dyeRes, Format.RGBA, Filter.LINEAR);
  private BufferedTextureFBO velocity = new BufferedTextureFBO(
      simRes, Format.RG, Filter.LINEAR);
  private TextureFBO divergence = new TextureFBO(
      simRes, Format.R, Filter.NEAREST);
  private TextureFBO curl = new TextureFBO(
      simRes, Format.R, Filter.NEAREST);
  private BufferedTextureFBO pressure = new BufferedTextureFBO(
      simRes, Format.R, Filter.NEAREST);

  private final Blit blit = new Blit();
  private final CopyProgram copyProgram = new CopyProgram();
  private final ClearProgram clearProgram = new ClearProgram();
  private final AdvectionProgram advectionProgram = new AdvectionProgram();
  private final DivergenceProgram divergenceProgram = new DivergenceProgram();
  private final CurlProgram curlProgram = new CurlProgram();
  private final VorticityProgram vorticityProgram = new VorticityProgram();
  private final PressureProgram pressureProgram = new PressureProgram();
  private final GradientSubtractProgram gradientSubtractProgram = new GradientSubtractProgram();

  private final SplatProgram splatProgram = new SplatProgram();

  private final DisplayProgram displayProgram = new DisplayProgram();

  private final FlatColourRenderer dyeRenderer = new FlatColourRenderer(8);
  private final VelocityRenderer velocityRenderer = new VelocityRenderer(8);
  private final FloatRenderer pressureRenderer = new FloatRenderer(8);

  /**
   * Call this to update the screen resolution
   *
   * @param res in pixels
   * @return <code>this</code>
   */
  public FluidSimulation withScreenRes(Vector2ic res) {
    if (screenRes.x() != res.x() || screenRes.y() != res.y()) {
      screenRes.set(res);
      setRes(simRes, config.minSimRes);
      setRes(dyeRes, config.minDyeRes);

      dye = dye.resize(dyeRes, copyProgram, blit);
      velocity = velocity.resize(simRes, copyProgram, blit);
      divergence = divergence.resize(simRes, copyProgram, blit);
      curl = curl.resize(simRes, copyProgram, blit);
      pressure = pressure.resize(simRes, copyProgram, blit);
      TextureFBO.unbind();

      float simTexelWidth = 1f / simRes.x();
      float simTexelHeight = 1f / simRes.y();
      curlProgram.texelSize.value.set(simTexelWidth, simTexelHeight);
      vorticityProgram.texelSize.value.set(simTexelWidth, simTexelHeight);
      divergenceProgram.texelSize.value.set(simTexelWidth, simTexelHeight);
      pressureProgram.texelSize.value.set(simTexelWidth, simTexelHeight);
      gradientSubtractProgram.texelSize.value.set(simTexelWidth, simTexelHeight);
      advectionProgram.texelSize.value.set(simTexelWidth, simTexelHeight);

      splatProgram.aspectRatio.set(screenRes.x() / screenRes.y());
    }
    return this;
  }

  /**
   * Call this to update the logical resolution
   *
   * @param res in logical units
   * @return <code>this</code>
   */
  public FluidSimulation withLogicalRes(Vector2fc res) {
    dyeRenderer.program().projection.update(m -> m
        .setOrtho2DLH(
            0, res.x(),
            0, res.y()));
    velocityRenderer.program().projection.update(m -> m
        .setOrtho2DLH(
            0, res.x(),
            0, res.y()));
    pressureRenderer.program().projection.update(m -> m
        .setOrtho2DLH(
            0, res.x(),
            0, res.y()));
    return this;
  }

  /**
   * @return The means by which to render to the dye
   */
  public FlatColourRenderer dye() {
    return dyeRenderer;
  }

  /**
   * @return The means by which to set flow velocities
   */
  public VelocityRenderer velocity() {
    return velocityRenderer;
  }

  /**
   * @return The means by which to set pressure
   */
  public FloatRenderer pressure() {
    return pressureRenderer;
  }

  /**
   * Updates the simulation
   *
   * @param delta in seconds
   * @return <code>this</code>
   */
  public FluidSimulation advance(float delta) {

    // for additive rather than overwrite:
    // glBlendFunc(GL_ONE, GL_ONE);
    // not sure if it's better or not...

    // first we draw our dye additions
    dye.read().bind();
    dyeRenderer.render();

    // then the velocity additions
    velocity.read().bind();
    velocityRenderer.render();

    // then pressure
    pressure.read().bind();
    pressureRenderer.render();

    // then we get into the meat of the simulation...
    glDisable(GL_BLEND);

    // curl data updated from velocity
    curlProgram.velocity.set(velocity.read().attach(GL_TEXTURE0));
    curlProgram.use();
    blit.to(curl);

    // velocity data updated from itself and curl data
    vorticityProgram.velocity.set(velocity.read().attach(GL_TEXTURE0));
    vorticityProgram.curl.set(curl.attach(GL_TEXTURE1));
    vorticityProgram.curlFactor.set(config.curl);
    vorticityProgram.dt.set(delta);
    vorticityProgram.use();
    blit.to(velocity.write());
    velocity.swap();

    // divergence data updated from velocity
    divergenceProgram.velocity.set(velocity.read().attach(GL_TEXTURE0));
    divergenceProgram.use();
    blit.to(divergence);

    // pressure globally attenuated...
    clearProgram.source.set(pressure.read().attach(GL_TEXTURE0));
    clearProgram.value.set(config.pressure);
    clearProgram.use();
    blit.to(pressure.write());
    pressure.swap();

    // ... and then iteratively settled using divergence data
    pressureProgram.divergence.set(divergence.attach(GL_TEXTURE0));
    pressureProgram.use();
    for (int i = 0; i < config.pressureIterations; i++) {
      pressureProgram.pressure.set(pressure.read().attach(GL_TEXTURE1));
      pressureProgram.pressure.populate();
      blit.to(pressure.write());
      pressure.swap();
    }

    // velocity updated from pressure
    gradientSubtractProgram.pressure.set(pressure.read().attach(GL_TEXTURE0));
    gradientSubtractProgram.velocity.set(velocity.read().attach(GL_TEXTURE1));
    gradientSubtractProgram.use();
    blit.to(velocity.write());
    velocity.swap();

    // velocity transported over space
    advectionProgram.velocity.set(velocity.read().attach(GL_TEXTURE0));
    advectionProgram.source.set(GL_TEXTURE0);
    advectionProgram.dt.set(delta);
    advectionProgram.dissipation.set(config.velocityDissipation);
    advectionProgram.use();
    blit.to(velocity.write());
    velocity.swap();

    // colour transported over space
    advectionProgram.velocity.set(velocity.read().attach(GL_TEXTURE0));
    advectionProgram.source.set(dye.read().attach(GL_TEXTURE1));
    advectionProgram.dt.set(delta);
    advectionProgram.dissipation.set(config.densityDissipation);
    advectionProgram.use();
    blit.to(dye.write());
    dye.swap();

    TextureFBO.unbind();
    return this;
  }

  /**
   * Renders the simulation
   *
   * @param resolution in pixels
   */
  public void render(Vector2ic resolution) {
    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    displayProgram.source.set(dye.read().attach(GL_TEXTURE0));
    displayProgram.use();
    blit.toDisplay(resolution);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  }

  private void setRes(Vector2i res, int min) {
    float aspectRatio = (float) screenRes.x() / screenRes.y();
    if (aspectRatio < 1) {
      aspectRatio = 1f / aspectRatio;
    }

    int max = round(min * aspectRatio);

    if (screenRes.x() > screenRes.y()) {
      res.set(max, min);
    } else {
      res.set(min, max);
    }
  }

  /**
   * Plops a splat onto the simulation
   *
   * @param center splat center
   * @param motion splat velocity
   * @param colour RGB components in range 0-1
   */
  public void splat(Vector2fc center, Vector2fc motion, Vector3fc colour) {
    float r = 0.25f / 100;
    float ar = screenRes.x / screenRes.y;
    if (ar > 1) {
      r *= ar;
    }

    splatProgram.target.set(velocity.read().attach(GL_TEXTURE0));
    splatProgram.point.value.set(center);
    splatProgram.colour.value.set(motion, 0);
    splatProgram.radius.set(r);
    splatProgram.use();
    blit.to(velocity.write());
    velocity.swap();

    splatProgram.target.set(dye.read().attach(GL_TEXTURE0));
    splatProgram.point.value.set(center);
    splatProgram.colour.value.set(colour);
    splatProgram.radius.set(r);
    splatProgram.use();
    blit.to(dye.write());
    dye.swap();

    TextureFBO.unbind();
  }

  public void destroy() {
    dye.destroy();
    velocity.destroy();
    divergence.destroy();
    curl.destroy();
    pressure.destroy();
    blit.destroy();
    copyProgram.delete();
    clearProgram.delete();
    advectionProgram.delete();
    divergenceProgram.delete();
    curlProgram.delete();
    vorticityProgram.delete();
    pressureProgram.delete();
    gradientSubtractProgram.delete();
    splatProgram.delete();
    displayProgram.delete();

    dyeRenderer.destroy();
    velocityRenderer.destroy();
    pressureRenderer.destroy();
  }
}
