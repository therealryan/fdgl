package dev.flowty.gl.shape;

import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A geometric shape, defined by vertices and triangles formed by indexing into the vertex list
 */
public class Shape {

  /**
   * The vertices of the shape, in x,y,z triples
   */
  public final float[] vertices;
  /**
   * The triangle indices, in counter-clockwise a,b,c triples
   */
  public final int[] triangles;

  /**
   * @param vertices  vertex positions
   * @param triangles triangle vertex indices
   */
  public Shape(float[] vertices, int[] triangles) {
    this.vertices = vertices;
    this.triangles = triangles;
  }

  /**
   * Combines shapes into one geometry
   *
   * @param constituents The shapes to combine
   */
  public Shape(Shape... constituents) {
    int vc = 0;
    int tc = 0;
    for (Shape s : constituents) {
      vc += s.vertices.length;
      tc += s.triangles.length;
    }

    vertices = new float[vc];
    triangles = new int[tc];

    int vi = 0;
    int vo = 0;
    int ti = 0;
    for (Shape s : constituents) {
      System.arraycopy(s.vertices, 0, vertices, vi, s.vertices.length);
      vi += s.vertices.length;
      for (int triangle : s.triangles) {
        triangles[ti] = triangle + vo;
        ti++;
      }
      vo += s.vertices();
    }
  }

  /**
   * Applies an update to the {@link Shape}
   *
   * @param update how to alter the {@link Shape}
   * @return <code>this</code>
   */
  public Shape with(Consumer<Shape> update) {
    update.accept(this);
    return this;
  }

  /**
   * Transforms the vertices of the {@link Shape}
   *
   * @param tr The transformation vector
   * @return <code>this</code>
   */
  public Shape transform(Matrix4f tr) {
    Vector4f vertex = new Vector4f();
    for (int i = 0; i < vertices(); i++) {
      vertex(i, v -> {
        vertex.set(v, 1);
        vertex.mul(tr);
        v.set(vertex);
      });
    }
    return this;
  }

  /**
   * @return The number of vertices
   */
  public int vertices() {
    return vertices.length / 3;
  }

  /**
   * @return the number of triangles
   */
  public int triangles() {
    return triangles.length / 3;
  }

  /**
   * Updates a vertex location
   *
   * @param idx    The vertex index
   * @param update How to update the vertex
   * @return <code>this</code>
   */
  public Shape vertex(int idx, Consumer<Vector3f> update) {
    Vector3f v = vertex(idx, new Vector3f());
    update.accept(v);
    vertices[idx * 3 + 0] = v.x;
    vertices[idx * 3 + 1] = v.y;
    vertices[idx * 3 + 2] = v.z;
    return this;
  }

  /**
   * Extracts a vertex location
   *
   * @param idx The vertex index
   * @param v   The destination vector
   * @return the populated destination vector
   */
  public Vector3f vertex(int idx, Vector3f v) {
    v.set(
        vertices[idx * 3 + 0],
        vertices[idx * 3 + 1],
        vertices[idx * 3 + 2]);
    return v;
  }

  /**
   * Extracts a vertex location
   *
   * @param idx The vertex index
   * @param v   The destination vector
   * @return the populated destination vector
   */
  public Vector4f vertex(int idx, Vector4f v) {
    v.set(
        vertices[idx * 3 + 0],
        vertices[idx * 3 + 1],
        vertices[idx * 3 + 2],
        1);
    return v;
  }

  /**
   * Computes the bounding box of the shape
   *
   * @param b destination object
   * @return The populated bounds
   */
  public Bounds bounds(Bounds b) {
    b.clear();
    for (int i = 0; i < vertices(); i++) {
      vertex(i, v -> b.include(v));
    }
    return b;
  }

}
