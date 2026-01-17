package dev.flowty.gl.config.ui;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.var.Action;
import dev.flowty.gl.util.Colour;
import java.util.Optional;
import java.util.function.Function;

/**
 * Produces readable summaries for {@link Variable} values
 */
public class Value {

  private static final Function<Object, String> DEFAULT = String::valueOf;
  private static final Function<Object, String> RGBA = value -> Optional
      .ofNullable(value)
      .filter(Integer.class::isInstance)
      .map(v -> Colour.toString((int) v))
      .orElse("???");
  private static final Function<Object, String> CHAR_ARRAY = value -> Optional
      .ofNullable(value)
      .filter(char[].class::isInstance)
      .map(v -> String.valueOf((char[]) v))
      .orElse("???");

  private Value() {
    // no instances
  }

  /**
   * Generates a readable string for a variable value
   *
   * @param var The variable
   * @return The readable string
   */
  public static String of(Variable var) {
    if (var instanceof Action) {
      return "";
    }
    Class<?> type = var.type();
    Class<?> hint = var.widgetHint();

    Function<Object, String> stringifier = DEFAULT;
    if (type == int.class && hint == Colour.class) {
      stringifier = RGBA;
    } else if (type == char[].class) {
      stringifier = CHAR_ARRAY;
    }

    return stringifier.apply(var.get());
  }
}
