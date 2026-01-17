package dev.flowty.gl.shader.flat;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import dev.flowty.gl.shape.Shape;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

/**
 * Draws geometry coloured by bytes-packed-into-an-int RGBA colours
 */
public class FlatColourRenderer extends AbstractFlatColourRenderer<FlatColourRenderer> {

  private IntBuffer colours;
  private int colourBufferHandle = -1;

  /**
   * @param stackDepth The size of the matrix stack
   */
  public FlatColourRenderer(int stackDepth) {
    super(stackDepth);
    colours = BufferUtils.createIntBuffer(DEFAULT_VERTEX_COUNT);
  }

  private int colourBufferHandle() {
    if (colourBufferHandle == -1) {
      colourBufferHandle = glGenBuffers();
    }
    return colourBufferHandle;
  }

  /**
   * Adds geometry to be rendered
   *
   * @param cs The shape that we want to be drawn
   * @return <code>this</code>
   */
  public FlatColourRenderer draw(ColouredShape cs) {
    addGeometry(cs);
    colours.put(cs.colours);
    return this;
  }

  /**
   * Adds geometry to be rendered
   *
   * @param s      The shape that we want to be drawn
   * @param colour The colour for all vertices
   * @return <code>this</code>
   */
  public FlatColourRenderer draw(Shape s, int colour) {
    addGeometry(s);
    for (int i = 0; i < s.vertices(); i++) {
      colours.put(colour);
    }
    return this;
  }

  @Override
  protected void growColours() {
    IntBuffer grownColours = BufferUtils.createIntBuffer(colours.capacity() * 2);
    colours.flip();
    grownColours.put(colours);
    colours = grownColours;
  }

  @Override
  protected void bindColours() {
    colours.flip();
    glBindBuffer(GL_ARRAY_BUFFER, colourBufferHandle());
    glBufferData(GL_ARRAY_BUFFER, colours, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(FlatColourProgram.COLOUR_ARRAY_ATTRIBUTE_INDEX,
        4, GL_UNSIGNED_BYTE, true, 0, 0);
  }

  @Override
  protected void clearColours() {
    colours.clear();
  }

}
