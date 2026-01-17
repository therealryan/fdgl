package dev.flowty.gl.shape.geometry;

import dev.flowty.gl.shape.Shape;
import dev.flowty.gl.shape.topology.Cross;
import dev.flowty.gl.shape.topology.Loop;
import dev.flowty.gl.shape.topology.Wheel;
import dev.flowty.gl.test.shape.ShapeAssert;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Exercises {@link Layout}
 */
@DisabledIfSystemProperty(named = "ci", matches = "true",
    disabledReason = "Off-by-one errors on rendered text location")
class LayoutTest {

  @Test
  void unit() {
    Shape root = new Cross().with(Cruciform.unit(0.4f));
    Vector2f position = new Vector2f();
    Layout layout = new Layout(root, position);

    float scale = layout.into(10, 20, 110, 170);

    Shape result = new Shape(
        new Loop(4).with(Rectangular
            .outerborder(10, 20, 110, 170, 2)),
        root.transform(new Matrix4f()
            .translate(position.x(), position.y(), 0)
            .scale(scale))
    );
    ShapeAssert.assertShape(result);
  }

  @Test
  void origin() {
    Shape root = new Cross().with(Cruciform.unit(0.4f))
        .transform(new Matrix4f()
            .translate(-1, -1, 0)
            .scale(2));
    Vector2f position = new Vector2f();
    Layout layout = new Layout(root, position);

    float scale = layout.into(10, 20, 110, 170);

    Shape result = new Shape(
        new Loop(4).with(Rectangular
            .outerborder(10, 20, 110, 170, 2)),
        root.transform(new Matrix4f()
            .translate(position.x(), position.y(), 0)
            .scale(scale))
    );
    ShapeAssert.assertShape(result);
  }

  @Test
  void pair() {
    Shape root = new Cross().with(Cruciform.unit(0.4f))
        .transform(new Matrix4f()
            .translate(-1, -1, 0)
            .scale(2));
    Vector2f rootPosition = new Vector2f();
    Shape child = new Wheel(32).with(Circular.wheel(1));
    Vector2f childPosition = new Vector2f();
    Layout layout = new Layout(root, rootPosition,
        r -> r.child(child, childPosition, 0, 2));

    float scale = layout.into(10, 20, 110, 170);

    Shape result = new Shape(
        new Loop(4).with(Rectangular
            .outerborder(10, 20, 110, 170, 2)),
        root.transform(new Matrix4f()
            .translate(rootPosition.x(), rootPosition.y(), 0)
            .scale(scale)),
        child.transform(new Matrix4f()
            .translate(childPosition.x(), childPosition.y(), 0)
            .scale(scale))
    );
    ShapeAssert.assertShape(result);
  }

  @Test
  void triple() {
    Shape root = new Cross().with(Cruciform.unit(0.4f))
        .transform(new Matrix4f()
            .translate(-1, -1, 0)
            .scale(2));
    Vector2f rootPosition = new Vector2f();
    Shape child = new Wheel(32).with(Circular.wheel(1));
    Vector2f childPosition = new Vector2f();
    Shape grandchild = new Wheel(4).with(Circular.wheel(1));
    Vector2f grandchildPosition = new Vector2f();
    Layout layout = new Layout(root, rootPosition,
        r -> r.child(child, childPosition, 0, 2,
            c -> c.child(grandchild, grandchildPosition, 2, 0)));

    float scale = layout.into(10, 20, 110, 170);

    Shape result = new Shape(
        new Loop(4).with(Rectangular
            .outerborder(10, 20, 110, 170, 2)),
        root.transform(new Matrix4f()
            .translate(rootPosition.x(), rootPosition.y(), 0)
            .scale(scale)),
        child.transform(new Matrix4f()
            .translate(childPosition.x(), childPosition.y(), 0)
            .scale(scale)),
        grandchild.transform(new Matrix4f()
            .translate(grandchildPosition.x(), grandchildPosition.y(), 0)
            .scale(scale))
    );
    ShapeAssert.assertShape(result);
  }
}