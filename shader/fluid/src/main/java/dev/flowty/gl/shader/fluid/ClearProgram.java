package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;
import dev.flowty.gl.shader.uniform.Uniform1f;

/**
 * A program that copies from a texture, attenuated by some factor
 */
class ClearProgram extends Program {

  /**
   * Source data
   */
  public final TextureUnitId source = new TextureUnitId(this, "uTexture");
  /**
   * Attenuation factor
   */
  public final Uniform1f value = new Uniform1f(this, "value");

  /***/
  ClearProgram() {
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
            precision mediump float;
            precision mediump sampler2D;
            
            smooth in highp vec2 vUv;
            
            uniform sampler2D uTexture;
            uniform float value;
            
            void main () {
              gl_FragColor = value * texture2D(uTexture, vUv);
            }"""));
    uniforms(source, value);
  }
}
