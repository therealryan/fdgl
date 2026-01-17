package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Bounds;
import dev.flowty.gl.shape.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.joml.Vector2f;

public class Layout {

  private final Node root;

  public <T extends Shape> Layout(T shape, Vector2f position, Consumer<Node>... descendants) {
    root = new Node(shape, 0, 0, position);
    for (Consumer<Node> descendant : descendants) {
      descendant.accept(root);
    }
  }

  public float into(float minx, float miny, float maxx, float maxy) {
    Bounds cover = root.cover(new Bounds());
    float scale = Math.min((maxx - minx) / cover.width(), (maxy - miny) / cover.height());
    Vector2f center = new Vector2f(
        cover.min.x() + cover.width() / 2,
        cover.min.y() + cover.height() / 2);
    Vector2f target = new Vector2f(
        minx + (maxx - minx) / 2,
        miny + (maxy - miny) / 2);
    Vector2f translation = new Vector2f(
        -center.x() * scale + target.x(),
        -center.y() * scale + target.y());
    root.layout(scale, translation);
    return scale;
  }

  public class Node {

    private final Bounds shapeBounds;
    private final Vector2f offset = new Vector2f();
    private final List<Node> children = new ArrayList<>();
    private final Vector2f position;

    private <T extends Shape> Node(T shape, float x, float y, Vector2f position) {
      this.shapeBounds = shape.bounds(new Bounds())
          .translate(x, y, 0);
      this.offset.set(x, y);
      this.position = position;
    }

    public <T extends Shape> Node child(T shape, Vector2f position, float x, float y,
        Consumer<Node>... descendants) {
      Node child = new Node(shape,
          offset.x() + x,
          offset.y() + y,
          position);
      for (Consumer<Node> descendant : descendants) {
        descendant.accept(child);
      }
      children.add(child);
      return this;
    }

    public Bounds cover(Bounds bounds) {
      bounds.include(shapeBounds);
      children.forEach(child -> child.cover(bounds));
      return bounds;
    }

    private void layout(float scale, Vector2f translation) {

      position.set(
          scale * offset.x() + translation.x(),
          scale * offset.y() + translation.y()
      );
      children.forEach(child -> child.layout(scale, translation));
    }
  }
}
