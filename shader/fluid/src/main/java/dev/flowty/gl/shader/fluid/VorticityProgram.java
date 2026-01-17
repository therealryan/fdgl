package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;
import dev.flowty.gl.shader.uniform.Uniform1f;
import dev.flowty.gl.shader.uniform.Uniform2f;

/**
 * A program that maintains vortex features in the face of simulation data losses
 */
class VorticityProgram extends Program {

  /**
   * Simulation texel size
   */
  public final Uniform2f texelSize = new Uniform2f(this, "texelSize");
  /**
   * Velocity data
   */
  public final TextureUnitId velocity = new TextureUnitId(this, "uVelocity");
  /**
   * Curl data
   */
  public final TextureUnitId curl = new TextureUnitId(this, "uCurl");
  /**
   * Timestep
   */
  public final Uniform1f dt = new Uniform1f(this, "dt");
  /**
   * Curliness factor
   */
  public final Uniform1f curlFactor = new Uniform1f(this, "curl");

  /***/
  VorticityProgram() {
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
            precision highp float;
            precision highp sampler2D;
            
            smooth in vec2 vUv;
            smooth in vec2 vL;
            smooth in vec2 vR;
            smooth in vec2 vT;
            smooth in vec2 vB;
            
            uniform sampler2D uVelocity;
            uniform sampler2D uCurl;
            uniform float curl;
            uniform float dt;
            
            void main () {
              float L = texture2D(uCurl, vL).x;
              float R = texture2D(uCurl, vR).x;
              float T = texture2D(uCurl, vT).x;
              float B = texture2D(uCurl, vB).x;
              float C = texture2D(uCurl, vUv).x;
            
              vec2 force = 0.5 * vec2(abs(T) - abs(B), abs(R) - abs(L));
              force /= length(force) + 0.0001;
              force *= curl * C;
              force.y *= -1.0;
            
              vec2 velocity = texture2D(uVelocity, vUv).xy;
              velocity += force * dt;
              velocity = min(max(velocity, -1000.0), 1000.0);
              gl_FragColor = vec4(velocity, 0.0, 1.0);
            }"""));
    uniforms(curl, curlFactor, dt, texelSize, velocity);
  }
}
