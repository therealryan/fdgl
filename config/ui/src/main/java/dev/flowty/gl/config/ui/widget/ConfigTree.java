package dev.flowty.gl.config.ui.widget;

import static java.lang.Math.clamp;
import static java.util.stream.Collectors.joining;

import dev.flowty.gl.config.model.Config;
import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.limit.Choice;
import dev.flowty.gl.config.model.var.Action;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;
import dev.flowty.gl.config.ui.Value;
import dev.flowty.gl.config.ui.Widget;
import dev.flowty.gl.util.Colour;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A text-based widget that allows a {@link Config} tree to be manipulated
 */
public class ConfigTree extends AbstractEventWidget {

  private static final Map<Class<?>, Widget.Builder> BUILDERS = new HashMap<>();

  /**
   * Adds a widget
   *
   * @param type    The variable type that is controlled by the widget
   * @param builder How to build the widget
   */
  public static void registerWidget(Class<?> type, Widget.Builder builder) {
    BUILDERS.put(type, builder);
  }

  /**
   * The colour for the active variable
   */
  public static final int ACTIVE_COLOUR = Colour.WHITE;
  /**
   * The colour for inactive variables
   */
  public static final int INACTIVE_COLOUR = Colour.GREY;

  private final CharGrid charGrid;
  private final Deque<Config> path = new ArrayDeque<>();
  private Config selected;
  private Widget widget = null;

  /**
   * @param charGrid where the widget gets drawn to
   * @param root     The root {@link Config} element
   */
  public ConfigTree(CharGrid charGrid, Config root) {
    this.charGrid = charGrid;
    path.push(root);
    selected = root.children().getFirst();
    update(0);
  }

  @Override
  public Widget inputEvent(boolean yes, boolean no, boolean up, boolean down) {
    if (widget != null) {
      widget.inputEvent(yes, no, up, down);
      return this;
    }
    return super.inputEvent(yes, no, up, down);
  }

  @Override
  public Widget inputState(boolean yes, boolean no, boolean up, boolean down) {
    if (widget != null) {
      widget.inputState(yes, no, up, down);
    }
    return super.inputState(yes, no, up, down);
  }

  @Override
  protected void yes() {
    haptic.yes();
    if (selected instanceof Action a) {
      a.set(null);
    } else if (selected instanceof Variable var) {
      widget = widgetFor(var);
    } else {
      path.addLast(selected);
      selected = path.peekLast().children().getFirst();
    }
  }

  @Override
  protected void no() {
    if (path.size() > 1) {
      selected = path.removeLast();
      haptic.no();
    }
  }

  @Override
  protected void up() {
    select(-1, haptic::up);
  }

  @Override
  protected void down() {
    select(+1, haptic::down);
  }

  private void select(int inc, Runnable hpt) {
    List<Config> children = path.peekLast().children();
    List<String> names = children.stream().map(Config::name).toList();
    int idx = names.indexOf(selected.name());
    int nidx = clamp(idx + inc, 0, children.size() - 1);

    selected = children.get(nidx);
    if (nidx != idx && hpt != null) {
      hpt.run();
    }
  }

  /**
   * @return The current object being configured
   */
  public Object subject() {
    return path.getLast().subject();
  }

  @Override
  public Widget update(float delta) {
    select(0, null);

    charGrid.colour(INACTIVE_COLOUR);
    charGrid.pen(p -> p.set(0, 0));
    charGrid.box(charGrid.width(), charGrid.height(), content -> {
      content.colour(ACTIVE_COLOUR);
      String pathString = path.stream()
          .map(Config::name)
          .collect(joining("/", "/", "/"));
      if (pathString.length() > content.width()) {
        pathString = "…" + pathString.substring(
            clamp(pathString.length() - content.width(),
                0, pathString.length() - 1));
      }
      content.write(pathString);
      Config current = path.peekLast();
      content.pen(p -> p.set(p.row + 1, 0));
      content.write("┌");
      content.write("─".repeat(Math.max(0,
          pathString.length() - current.name().length() - 2)));
      content.write("┴");
      content.write("─".repeat(Math.max(0,
          Math.min(pathString.length() - 3, current.name().length() - 2))));
      content.write("┘");
      List<Config> children = current.children();

      int width = children.stream()
          .mapToInt(c -> c.name().length())
          .max().orElse(0) + 4;

      for (Config child : children) {
        content.pen(p -> p.set(p.row + 1, 0));
        content.colour(ACTIVE_COLOUR)
            .write(child != children.getLast()
                ? "├"
                : "└");

        content.colour(INACTIVE_COLOUR);
        String left = " ";
        String right = " ";
        String name = child.name();
        String value = "";

        if (child.name().equals(selected.name())) {
          content.colour(ACTIVE_COLOUR);
          left = "[";
          right = "]";
        }

        if (child instanceof Variable var) {
          value = Value.of(var);
        } else {
          name += "/";
        }
        content.write(left)
            .write(name)
            .write(right)
            .write(" ".repeat(width - name.length()))
            .write(value)
            .colour(INACTIVE_COLOUR);
      }
    });

    if (selected != null) {
      String description = Optional.ofNullable(widget)
          .map(Widget::description)
          .orElse(selected.description());

      if (!description.isBlank()) {

        String[] lines = description.split("\n");
        int width = Stream.of(lines).mapToInt(String::length).max().orElse(10);
        int height = lines.length;

        textGrid()
            .colour(INACTIVE_COLOUR)
            .pen(p -> p.set(charGrid.height() - 2 - height,
                charGrid.width() - width - 3))
            .box(width + 3, 2 + height, c -> c
                .colour(INACTIVE_COLOUR)
                .write(description));
      }
    }

    if (widget != null) {
      widget.update(delta);

      Outcome o = widget.outcome();
      if (o == Outcome.YES || o == Outcome.NO) {
        widget = null;
        select(0, null); // refresh the selected variable to catch limit changes

        if (o == Outcome.YES && path.peekLast().wizard()) {
          select(+1, null);
        }
      }
    }

    return this;
  }

  @Override
  public CharGrid textGrid() {
    return charGrid;
  }

  public Widget widget() {
    return widget;
  }

  private Widget widgetFor(Variable var) {
    Config current = path.peekLast();
    List<Config> children = current.children();
    int width = children.stream()
        .mapToInt(c -> c.name().length())
        .max().orElse(0) + 4;
    int row = children.indexOf(var);

    Coordinate valueCoordinate = new Coordinate().set(row + 3, width + 4);

    if (var.limit() instanceof Choice || var.type().isEnum()) {
      return new ChoiceWidget(charGrid, valueCoordinate, var).with(haptic);
    }
    if (var.widgetHint() == Colour.class) {
      return new ColourWidget(charGrid, valueCoordinate, var).with(haptic);
    }
    if (var.type() == Integer.class || var.type() == int.class) {
      return new IntWidget(charGrid, valueCoordinate, var).with(haptic);
    }
    if (var.type() == Boolean.class || var.type() == boolean.class) {
      return new BooleanWidget(charGrid, valueCoordinate, var).with(haptic);
    }
    if (var.type() == char[].class) {
      return new CharArrayWidget(charGrid, valueCoordinate, var).with(haptic);
    }
    if (var.type() == float.class || Number.class.isAssignableFrom(var.type())) {
      return new FloatWidget(charGrid, valueCoordinate, var).with(haptic);
    }
    if (BUILDERS.containsKey(var.type())) {
      return BUILDERS.get(var.type()).build(charGrid, valueCoordinate, var).with(haptic);
    }
    throw new IllegalStateException("No widget for " + var.type() + " " + var.widgetHint());
  }
}
