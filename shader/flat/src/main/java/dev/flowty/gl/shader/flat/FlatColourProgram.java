package dev.flowty.gl.shader.flat;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import dev.flowty.gl.shader.FragmentShader;
import dev.flowty.gl.shader.Program;
import dev.flowty.gl.shader.VertexShader;
import dev.flowty.gl.shader.uniform.UniformMatrix4f;

/**
 * A program that produces per-vertex coloured output
 */
public class FlatColourProgram extends Program {

  /**
   * The projection matrix
   */
  public UniformMatrix4f projection = new UniformMatrix4f(this, "projection_matrix");

  /**
   * The vertex array attribute index for vertex position data
   */
  public static final int VERTEX_ARRAY_ATTRIBUTE_INDEX = 0;
  /**
   * The vertex array attribute index for vertex colour data
   */
  public static final int COLOUR_ARRAY_ATTRIBUTE_INDEX = 1;

  /***/
  public FlatColourProgram() {
    super(
        new VertexShader("""
            #version 330 core
            layout (location = 0) in vec4 vertex;
            layout (location = 1) in vec4 in_colour;
            
            uniform mat4 projection_matrix;
            
            smooth out vec4 colour;
            
            void main() {
                gl_Position = projection_matrix * vertex;
                colour = in_colour;
            }
            """),
        new FragmentShader("""
            #version 330 core
            smooth in vec4 colour;
            
            out vec4 frag_colour;
            
            void main() {
                frag_colour = colour;
            }
            """));

    uniforms(projection);
  }

  @Override
  public FlatColourProgram use() {
    super.use();
    glEnableVertexAttribArray(VERTEX_ARRAY_ATTRIBUTE_INDEX);
    glEnableVertexAttribArray(COLOUR_ARRAY_ATTRIBUTE_INDEX);
    return this;
  }

}
