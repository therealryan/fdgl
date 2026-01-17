package dev.flowty.gl.config.ui.widget;

import static java.lang.Math.clamp;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.limit.Choice;
import dev.flowty.gl.config.model.limit.EnumChoice;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;
import dev.flowty.gl.config.ui.Widget;
import dev.flowty.gl.util.Colour;
import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * Widget for choosing from discrete items
 */
public class ChoiceWidget extends AbstractEventWidget {

  private final CharGrid charGrid;
  private final Variable variable;
  private final Choice<?> choice;
  private final Coordinate location;
  private int selected;

  /**
   * @param charGrid The grid to draw on
   * @param location The grid location to draw at
   * @param variable The variable to alter
   */
  @SuppressWarnings("unchecked")
  public ChoiceWidget(CharGrid charGrid, Coordinate location, Variable variable) {
    this.charGrid = charGrid;
    this.location = location;
    this.variable = variable;
    Choice<?> c = (Choice<?>) variable.limit();
    if (c == null && variable.type().isEnum()) {
      c = new EnumChoice<>((Class<Enum<?>>) variable.type());
    }
    choice = c;

    Object value = variable.get();
    for (int i = 0; i < choice.choices().length; i++) {
      if (choice.chosen(i).equals(value)) {
        selected = i;
      }
    }
  }

  @Override
  protected void yes() {
    variable.set(choice.chosen(selected));
    outcome = Outcome.YES;
    haptic.yes();
  }

  @Override
  protected void no() {
    outcome = Outcome.NO;
    haptic.no();
  }

  @Override
  protected void up() {
    int ns = clamp(selected - 1, 0, choice.choices().length - 1);
    if (ns != selected) {
      selected = ns;
      haptic.up();
    }
  }

  @Override
  protected void down() {
    int ns = clamp(selected + 1, 0, choice.choices().length - 1);
    if (ns != selected) {
      selected = ns;
      haptic.down();
    }
  }

  @Override
  public Widget update(float delta) {
    if (choice.choices().length == 0) {
      outcome = Outcome.NO;
    } else {
      int height = choice.choices().length + 2;
      int width = Stream.of(choice.choices())
          .mapToInt(String::length)
          .max().orElse(4);
      if (outcome == Outcome.CONTINUE) {
        charGrid
            .pen(p -> p.set(location.row - selected - 1, location.column - 1))
            .box(width + 2, height, cnt -> {
              for (int i = 0; i < choice.choices().length; i++) {
                int idx = i;
                cnt.pen(p -> p.set(idx, 0))
                    .colour(colour(idx))
                    .write(choice.choices()[idx]);
              }
            });
        charGrid.pen(p -> p.set(location.row, location.column - 1))
            .write("├");
        charGrid.pen(p -> p.set(location.row, location.column + width))
            .write("┤");
      }
    }
    return this;
  }

  private int colour(int idx) {
    if (variable.widgetHint() == Colour.class) {
      String name = choice.choices()[idx];
      for (Field field : Colour.class.getFields()) {
        if (name.equalsIgnoreCase(field.getName())) {
          try {
            return field.getInt(null);
          } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to access " + field, e);
          }
        }
      }

    }

    return idx == selected
        ? ConfigTree.ACTIVE_COLOUR
        : ConfigTree.INACTIVE_COLOUR;

  }

  @Override
  public String description() {
    return choice.descriptions()[selected];
  }

  @Override
  public CharGrid textGrid() {
    return charGrid;
  }

}
