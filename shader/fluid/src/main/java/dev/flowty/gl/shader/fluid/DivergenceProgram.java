package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;
import dev.flowty.gl.shader.uniform.Uniform2f;

/**
 * <blockquote cite=
 * "https://developer.nvidia.com/gpugems/gpugems/part-vi-beyond-triangles/chapter-38-fast-fluid-dynamics-simulation-gpu">
 * Divergence, which appears in Equation 2, has an important physical significance. It is the rate
 * at which "density" exits a given region of space. In the Navier-Stokes equations, it is applied
 * to the velocity of the flow, and it measures the net change in velocity across a surface
 * surrounding a small piece of the fluid. <cite>GPU Gems, 38.2.3</cite> </blockquote>
 */
class DivergenceProgram extends Program {

  /**
   * Source of velocity data
   */
  public final TextureUnitId velocity = new TextureUnitId(this, "uVelocity");
  /**
   * Size of simulation texels
   */
  public final Uniform2f texelSize = new Uniform2f(this, "texelSize");

  /***/
  DivergenceProgram() {
    super(
        new VertexShader("""
            #version 330 core
            precision highp float;
            
            layout (location = 0) in vec2 vertex;
            
            smooth out vec2 vUv;
            smooth out vec2 vL;
            smooth out vec2 vR;
            smooth out vec2 vT;
            smooth out vec2 vB;
            
            uniform vec2 texelSize;
            
            void main () {
              vUv = vertex * 0.5 + 0.5;
              vL = vUv - vec2(texelSize.x, 0.0);
              vR = vUv + vec2(texelSize.x, 0.0);
              vT = vUv + vec2(0.0, texelSize.y);
              vB = vUv - vec2(0.0, texelSize.y);
              gl_Position = vec4(vertex, 0.0, 1.0);
            }
            """),
        new FragmentShader("""
            #version 330 core
            precision mediump float;
            precision mediump sampler2D;
            
            smooth in vec2 vUv;
            smooth in vec2 vL;
            smooth in vec2 vR;
            smooth in vec2 vT;
            smooth in vec2 vB;
            
            uniform sampler2D uVelocity;
            
            void main () {
              float L = texture2D(uVelocity, vL).x;
              float R = texture2D(uVelocity, vR).x;
              float T = texture2D(uVelocity, vT).y;
              float B = texture2D(uVelocity, vB).y;
            
              vec2 C = texture2D(uVelocity, vUv).xy;
              if (vL.x < 0.0) { L = -C.x; }
              if (vR.x > 1.0) { R = -C.x; }
              if (vT.y > 1.0) { T = -C.y; }
              if (vB.y < 0.0) { B = -C.y; }
            
              float div = 0.5 * (R - L + T - B);
              gl_FragColor = vec4(div, 0.0, 0.0, 1.0);
            }"""));
    uniforms(velocity, texelSize);
  }
}
