package dev.flowty.gl.shape.font;

import dev.flowty.gl.shape.Shape;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.triangulate.polygon.ConstrainedDelaunayTriangulator;

/**
 * Turns {@link Geometry}s into {@link Shape}s
 */
public class Triangulator {

  /**
   * Triangulates geometry
   *
   * @param geometry the geometry
   * @return a renderable shape
   * @throws TriangulationException on failure
   */
  public static Shape triangulate(Geometry geometry) {
    try {
      List<Vector2f> vertexList = buildVertices(geometry);
      Geometry trianglulation = new ConstrainedDelaunayTriangulator(geometry).getResult();
      float[] vertices = flattenVertices(vertexList);
      int[] triangleIndices = indexTriangles(vertexList, trianglulation);
      return new Shape(vertices, triangleIndices);
    } catch (Exception e) {
      throw new TriangulationException(geometry, e);
    }
  }

  private static List<Vector2f> buildVertices(Geometry glyph) {
    List<Vector2f> vertexList = new ArrayList<>();
    for (Coordinate c : glyph.getCoordinates()) {
      Vector2f v = new Vector2f((float) c.getX(), (float) c.getY());
      if (!vertexList.contains(v)) {
        vertexList.add(v);
      }
    }
    return vertexList;
  }

  private static float[] flattenVertices(List<Vector2f> vertexList) {
    float[] vertices = new float[3 * vertexList.size()];
    int vdx = 0;
    for (Vector2f v : vertexList) {
      vertices[vdx] = v.x;
      vdx++;
      vertices[vdx] = v.y;
      vdx++;
      vertices[vdx] = 0;
      vdx++;
    }
    return vertices;
  }

  private static int[] indexTriangles(List<Vector2f> vertexList, Geometry triangles) {
    int[] indices = new int[3 * triangles.getNumGeometries()];
    int idx = 0;
    for (int t = 0; t < triangles.getNumGeometries(); t++) {
      Geometry triangle = triangles.getGeometryN(t);
      for (int i = 0; i < 3; i++) {
        Vector2f v = new Vector2f(
            (float) triangle.getCoordinates()[i].getX(),
            (float) triangle.getCoordinates()[i].getY());
        indices[idx] = vertexList.indexOf(v);
        idx++;
      }
    }
    return indices;
  }

  /**
   * Thrown when triangulation fails
   */
  public static class TriangulationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    /**
     * The troublous geometry
     */
    public final Geometry geometry;

    /**
     * @param geometry The troublous geometry
     * @param cause    The problem
     */
    public TriangulationException(Geometry geometry, Exception cause) {
      super("Failed to triangulate " + geometry, cause);
      this.geometry = geometry;
    }
  }
}
