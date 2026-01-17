package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;
import dev.flowty.gl.shader.uniform.Uniform2f;

/**
 * A program that subtracts pressure gradient from velocity - this gives us velocity from
 * high-pressure areas to low-pressure areas.
 */
class GradientSubtractProgram extends Program {

  /**
   * Pressure data
   */
  public final TextureUnitId pressure = new TextureUnitId(this, "uPressure");
  /**
   * Velocity data
   */
  public final TextureUnitId velocity = new TextureUnitId(this, "uVelocity");
  /**
   * Simulation texel size
   */
  public final Uniform2f texelSize = new Uniform2f(this, "texelSize");

  /***/
  GradientSubtractProgram() {
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
            }"""),
        new FragmentShader("""
            #version 330 core
            precision mediump float;
            precision mediump sampler2D;
            
            smooth in highp vec2 vUv;
            smooth in highp vec2 vL;
            smooth in highp vec2 vR;
            smooth in highp vec2 vT;
            smooth in highp vec2 vB;
            
            uniform sampler2D uPressure;
            uniform sampler2D uVelocity;
            
            void main () {
                float L = texture2D(uPressure, vL).x;
                float R = texture2D(uPressure, vR).x;
                float T = texture2D(uPressure, vT).x;
                float B = texture2D(uPressure, vB).x;
                vec2 velocity = texture2D(uVelocity, vUv).xy;
                velocity.xy -= vec2(R - L, T - B);
                gl_FragColor = vec4(velocity, 0.0, 1.0);
            }"""));
    uniforms(pressure, velocity, texelSize);
  }
}
