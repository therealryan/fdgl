package dev.flowty.gl.shape.font;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Combines multiple non-intersecting {@link LinearRing}s into a {@link MultiPolygon} where the
 * interior is defined by the even-odd winding rule
 */
public class EvenOddPolygonBuilder {

  /**
   * Applies the even-odd winding rule to a set of line loops
   *
   * @param geoFact utility methods
   * @param rings   A set of simple line loops with no mutual intersections
   * @return the geometry of applying the even-odd rule to the rings
   */
  public static MultiPolygon build(GeometryFactory geoFact, LinearRing... rings) {
    for (int i = 0; i < rings.length; i++) {
      if (!rings[i].isSimple()) {
        throw new IllegalArgumentException("non-simple " + rings[i]);
      }
      for (int j = i + 1; j < rings.length; j++) {
        if (rings[i].intersects(rings[j])) {
//					throw new IllegalArgumentException( rings[i] + " intersects with " + rings[j] );
        }
      }
    }

    ContainmentNode root = new ContainmentNode(null);
    for (LinearRing ring : rings) {
      root.add(geoFact.createPolygon(ring));
    }
    return geoFact.createMultiPolygon(
        root.buildPolygons(geoFact, false, new ArrayList<>())
            .toArray(Polygon[]::new));
  }

  private static class ContainmentNode {

    public Polygon area;
    public final Collection<ContainmentNode> contents = new ArrayList<>();

    ContainmentNode(Polygon area) {
      this.area = area;
    }

    boolean add(Polygon candidate) {
      if (area == null || area.contains(candidate)) {

        // does it belong to any of the children?
        for (ContainmentNode content : contents) {
          if (content.add(candidate)) {
            return true;
          }
        }

        // it's not contained by any of the children, so it must be ours
        ContainmentNode node = new ContainmentNode(candidate);

        // check if any of our current children _actually_ belong to our new child
        Iterator<ContainmentNode> children = contents.iterator();
        while (children.hasNext()) {
          ContainmentNode child = children.next();
          if (node.area.contains(child.area)) {
            children.remove();
            node.contents.add(child);
          }
        }

        contents.add(node);
        return true;
      }
      return false;
    }

    List<Polygon> buildPolygons(GeometryFactory geoFact, boolean isPolygon,
        List<Polygon> polygons) {

      if (isPolygon) {
        // this level of the tree represents polygons - our direct children are the
        // holes in our shell
        Polygon polygon = geoFact.createPolygon(
            area.getExteriorRing(),
            contents.stream()
                .map(child -> child.area.getExteriorRing())
                .toArray(LinearRing[]::new));
        polygon.normalize();
        polygons.add(polygon);
      }

      // recurse to the next level, flipping the polygon/hole flag as we go
      for (ContainmentNode child : contents) {
        child.buildPolygons(geoFact, !isPolygon, polygons);
      }

      return polygons;
    }
  }
}
