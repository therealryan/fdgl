package dev.flowty.gl.shader.fluid;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.TextureUnitId;
import dev.flowty.gl.shader.uniform.Uniform1f;
import dev.flowty.gl.shader.uniform.Uniform2f;
import dev.flowty.gl.shader.uniform.Uniform3f;

/**
 * Plops a guassian splat onto a texture
 */
public class SplatProgram extends Program {

  /**
   * The texture to read from
   */
  public final TextureUnitId target = new TextureUnitId(this, "uTarget");
  /**
   * The aspect ratio of the texture
   */
  public final Uniform1f aspectRatio = new Uniform1f(this, "aspectRatio");
  /**
   * The colour of the splat
   */
  public final Uniform3f colour = new Uniform3f(this, "color");
  /**
   * The splat center
   */
  public final Uniform2f point = new Uniform2f(this, "point");
  /**
   * The splat radius
   */
  public final Uniform1f radius = new Uniform1f(this, "radius");

  /***/
  SplatProgram() {
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
            
            uniform sampler2D uTarget;
            uniform float aspectRatio;
            uniform vec3 color;
            uniform vec2 point;
            uniform float radius;
            
            void main () {
              vec2 p = vUv - point.xy;
              p.x *= aspectRatio;
              vec3 splat = exp(-dot(p, p) / radius) * color;
              vec3 base = texture2D(uTarget, vUv).xyz;
              gl_FragColor = vec4(base + splat, 1.0);
            }"""));
    uniforms(target, aspectRatio, colour, point, radius);
  }
}
