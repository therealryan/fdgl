package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;
import dev.flowty.gl.shader.uniform.Uniform2f;

/**
 * A program that ???
 */
class CurlProgram extends Program {

  /**
   * Source of velocity data
   */
  public final TextureUnitId velocity = new TextureUnitId(this, "uVelocity");
  /**
   * Size of simulation texels
   */
  public final Uniform2f texelSize = new Uniform2f(this, "texelSize");

  /***/
  CurlProgram() {
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
            
            smooth in vec2 vUv;
            smooth in vec2 vL;
            smooth in vec2 vR;
            smooth in vec2 vT;
            smooth in vec2 vB;
            
            uniform sampler2D uVelocity;
            
            void main () {
              float L = texture2D(uVelocity, vL).y;
              float R = texture2D(uVelocity, vR).y;
              float T = texture2D(uVelocity, vT).x;
              float B = texture2D(uVelocity, vB).x;
              float vorticity = R - L - T + B;
              gl_FragColor = vec4(0.5 * vorticity, 0.0, 0.0, 1.0);
            }"""));

    uniforms(velocity, texelSize);
  }
}
