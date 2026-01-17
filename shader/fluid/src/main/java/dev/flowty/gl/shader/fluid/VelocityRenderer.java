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
import org.joml.Vector2fc;
import org.lwjgl.BufferUtils;

/**
 * Used to draw <code>float (x,y)</code> vector data onto the fluid simulation's velocity data
 * texture.
 */
public class VelocityRenderer extends AbstractFlatColourRenderer<VelocityRenderer> {

  private FloatBuffer components;
  private int componentBufferHandle = -1;

  /**
   * @param stackdepth The size of the matrix stack
   */
  public VelocityRenderer(int stackdepth) {
    super(stackdepth);
    components = BufferUtils.createFloatBuffer(DEFAULT_VERTEX_COUNT * 2);
  }

  /**
   * Sets the velocity in a given area
   *
   * @param s        The area in which to set the velocity
   * @param velocity The velocity
   * @return <code>this</code>
   */
  public VelocityRenderer draw(Shape s, Vector2fc velocity) {
    addGeometry(s);
    for (int i = 0; i < s.vertices(); i++) {
      components.put(velocity.x());
      components.put(velocity.y());
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
        2, GL_FLOAT, false, 0, 0);
  }

  @Override
  protected void clearColours() {
    components.clear();
  }

}
