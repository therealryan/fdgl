package dev.flowty.gl.shader.fbo;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;

/**
 * A program that copies from a texture
 */
public class CopyProgram extends Program {

  /**
   * The texture unit to copy from
   */
  public final TextureUnitId source = new TextureUnitId(this, "uTexture");
  /**
   * The vertex array attribute index for vertex position data
   */
  public static final int VERTEX_ARRAY_ATTRIBUTE_INDEX = 0;

  /***/
  public CopyProgram() {
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
            
            smooth in vec2 vUv;
            uniform sampler2D uTexture;
            
            void main () {
              gl_FragColor = texture2D(uTexture, vUv);
            }"""));
    uniforms(source);
  }

  /**
   * Sets the ID of the texture unit to copy from
   *
   * @param tu The texture unit id
   * @return <code>this</code>
   */
  public CopyProgram from(int tu) {
    source.set(tu);
    return this;
  }

}
