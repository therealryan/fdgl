package dev.flowty.gl.shader.fluid;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import dev.flowty.gl.shader.flat.AbstractFlatColourRenderer;
import dev.flowty.gl.shader.flat.FlatColourProgram;
import dev.flowty.gl.shape.Shape;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

/**
 * Used to draw one-per-vertex non-normalised float values onto the fluid simulations data textures
 */
public class FloatRenderer extends AbstractFlatColourRenderer<FloatRenderer> {

  private FloatBuffer components;
  private int componentBufferHandle = -1;

  /**
   * @param stackdepth The size of the matrix stack
   */
  public FloatRenderer(int stackdepths) {
    super(stackdepths);
    components = BufferUtils.createFloatBuffer(DEFAULT_VERTEX_COUNT);
  }

  /**
   * Sets the value in a given area
   *
   * @param s     area geometry
   * @param value the value to set
   * @return {@code this}
   */
  public FloatRenderer draw(Shape s, float value) {
    addGeometry(s);

    for (int i = 0; i < s.vertices(); i++) {
      components.put(value);
    }
    return this;
  }

  private int componentBufferHandle() {
    if (componentBufferHandle == -1) {
      componentBufferHandle = glGenBuffers();
    }
    return componentBufferHandle;
  }

  @Override
  protected void growColours() {
    FloatBuffer grownComponents = BufferUtils.createFloatBuffer(components.capacity() * 2);
    components.flip();
    grownComponents.put(components);
    components = grownComponents;
  }

  @Override
  protected void bindColours() {
    components.flip();
    glBindBuffer(GL_ARRAY_BUFFER, componentBufferHandle());
    glBufferData(GL_ARRAY_BUFFER, components, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(FlatColourProgram.COLOUR_ARRAY_ATTRIBUTE_INDEX,
        1, GL_FLOAT, false, 0, 0);
  }

  @Override
  protected void clearColours() {
    components.clear();
  }

}
