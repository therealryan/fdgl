package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;

/**
 * A program that copies from a texture, setting the alpha channel to the maximum of the red, green
 * and blue channels
 */
class DisplayProgram extends Program {

  /**
   * The texture unit to copy from
   */
  public final TextureUnitId source = new TextureUnitId(this, "uTexture");

  /***/
  DisplayProgram() {
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
            
            uniform sampler2D uTexture;
            
            void main () {
              vec3 c = texture2D(uTexture, vUv).rgb;
              float a = max(c.r, max(c.g, c.b));
              gl_FragColor = vec4(c, a);
            }"""));
    uniforms(source);
  }

  /**
   * Sets the source texture unit
   *
   * @param textureUnit The index of the texture unit
   * @return <code>this</code>
   */
  public DisplayProgram from(int textureUnit) {
    source.set(textureUnit);
    return this;
  }
}
