package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;
import dev.flowty.gl.shader.uniform.Uniform1f;
import dev.flowty.gl.shader.uniform.Uniform2f;

/**
 * A program that simulates fluid transport of content
 */
class AdvectionProgram extends Program {

  /**
   * Source of velocity data
   */
  public final TextureUnitId velocity = new TextureUnitId(this, "uVelocity");
  /**
   * Source of advected content data
   */
  public final TextureUnitId source = new TextureUnitId(this, "uSource");
  /**
   * Size of simulation-data texels
   */
  public final Uniform2f texelSize = new Uniform2f(this, "texelSize");
  /**
   * Timestep
   */
  public final Uniform1f dt = new Uniform1f(this, "dt");
  /**
   * Content dissipation factor
   */
  public final Uniform1f dissipation = new Uniform1f(this, "dissipation");

  /***/
  AdvectionProgram() {
    super(
        new VertexShader("""
            #version 330 core
            precision highp float;
            
            layout (location = 0) in vec2 vertex;
            
            smooth out vec2 vUv;
            
            void main () {
              vUv = vertex * 0.5 + 0.5;
              gl_Position = vec4(vertex, 0.0, 1.0);
            }"""),
        new FragmentShader("""
            #version 330 core
            precision highp float;
            precision highp sampler2D;
            
            smooth in vec2 vUv;
            
            uniform sampler2D uVelocity;
            uniform sampler2D uSource;
            uniform vec2 texelSize;
            uniform float dt;
            uniform float dissipation;
            
            void main () {
              vec2 coord = vUv - dt * texture2D(uVelocity, vUv).xy * texelSize;
              vec4 result = texture2D(uSource, coord);
              float decay = 1.0 + dissipation * dt;
              gl_FragColor = result / decay;
            }"""));
    uniforms(velocity, source, texelSize, dt, dissipation);
  }
}
