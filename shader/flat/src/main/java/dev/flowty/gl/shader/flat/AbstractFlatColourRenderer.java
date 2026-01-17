package dev.flowty.gl.shader.flat;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import dev.flowty.gl.shape.Shape;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

/**
 * Draws coloured geometry
 *
 * @param <S> self type
 */
public abstract class AbstractFlatColourRenderer<S extends AbstractFlatColourRenderer<S>>
    extends Renderer<S, FlatColourProgram> {

  /**
   * The number of vertices that can be batched immediately after construction
   */
  protected static final int DEFAULT_VERTEX_COUNT = 100;
  private FloatBuffer vertices;
  private IntBuffer triangles;

  private int vertexBufferHandle = -1;

  private final Vector4f vertex = new Vector4f();

  /**
   * @param stackDepth The size of the matrix stack
   */
  public AbstractFlatColourRenderer(int stackDepth) {
    super(new FlatColourProgram(), stackDepth);

    vertices = BufferUtils.createFloatBuffer(3 * DEFAULT_VERTEX_COUNT);
    triangles = BufferUtils.createIntBuffer(3 * 512);
  }

  private int vertexBufferHandle() {
    if (vertexBufferHandle == -1) {
      vertexBufferHandle = glGenBuffers();
    }
    return vertexBufferHandle;
  }

  /**
   * Adds shape geometry for rendering
   *
   * @param cs The shape to render
   */
  protected void addGeometry(Shape cs) {

    while (vertices.remaining() < cs.vertices.length) {
      growVerts();
    }

    while (triangles.remaining() < cs.triangles.length) {
      growTris();
    }

    // populate the triangle indices
    int existingVertexCount = vertices.position() / 3;
    for (int index : cs.triangles) {
      triangles.put(index + existingVertexCount);
    }

    Matrix4f transform = transform();
    transform.determineProperties();
    if ((transform.properties() & Matrix4fc.PROPERTY_IDENTITY) == 0) {
      // transform is not identity! transform each vertex
      for (int i = 0; i < cs.vertices(); i++) {
        cs.vertex(i, vertex);
        vertex.mul(transform);
        vertices.put(vertex.x / vertex.w);
        vertices.put(vertex.y / vertex.w);
        vertices.put(vertex.z / vertex.w);
      }
    } else {
      // otherwise add the vertex data directly
      vertices.put(cs.vertices);
    }

    /**
     * TODO: it'd be nice to avoid this per-vertex work on the CPU. We should be
     * able to:
     * <ul>
     * <li>Have a uniform buffer object where we store the matrices</li>
     * <li>Have a vertex attribute that indexes into that array</li>
     * <li>Do the transform in the vertex shader</li>
     * </ul>
     * I had a crack at it, but I'm not clever enough to avoid a black screen
     */
  }

  private void growVerts() {
    FloatBuffer grownVerts = BufferUtils.createFloatBuffer(vertices.capacity() * 2);
    vertices.flip();
    grownVerts.put(vertices);
    vertices = grownVerts;

    growColours();
  }

  /**
   * Implement this to double the number of vertex colours that can be stored for rendering
   */
  protected abstract void growColours();

  private void growTris() {
    IntBuffer grownTris = BufferUtils.createIntBuffer(triangles.capacity() * 2);
    triangles.flip();
    grownTris.put(triangles);
    triangles = grownTris;
  }

  /**
   * Renders batched geometry
   *
   * @return <code>this</code>
   */
  public S render() {

    program().use();

    // flip the buffers to be read from
    vertices.flip();
    triangles.flip();

    // bind vertex data
    glBindBuffer(GL_ARRAY_BUFFER, vertexBufferHandle());
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(FlatColourProgram.VERTEX_ARRAY_ATTRIBUTE_INDEX,
        3, GL_FLOAT, false, 0, 0);

    bindColours();

    // draw the triangles
    glDrawElements(GL_TRIANGLES, triangles);

    // reset for the next frame
    vertices.clear();
    triangles.clear();
    clearColours();
    return self();
  }

  /**
   * Destroys the GL state used by this renderer
   */
  public S destroy() {
    program().delete();
    if (vertexBufferHandle != -1) {
      glDeleteBuffers(vertexBufferHandle);
      vertexBufferHandle = -1;
    }
    return self();
  }

  /**
   * Implement this to bind the vertex colours for rendering
   */
  protected abstract void bindColours();

  /**
   * Implement this to clear vertex colour storage
   */
  protected abstract void clearColours();

}
