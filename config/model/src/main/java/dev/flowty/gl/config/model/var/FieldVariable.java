package dev.flowty.gl.config.model.var;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flowty.gl.config.model.ObjectConfig;
import dev.flowty.gl.config.model.annote.Description;
import dev.flowty.gl.config.model.annote.TypeHint;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A variable that controls a single accessible field
 */
public class FieldVariable extends MemberVariable {

  private final Field field;

  /**
   * @param owner access to the object that owns the field
   * @param field The field
   */
  public FieldVariable(ObjectConfig owner, Field field) {
    super(owner,
        name(field),
        Optional.ofNullable(field.getAnnotation(Description.class))
            .map(Description::value)
            .orElse(""),
        field.getType(),
        Optional.of(field)
            .map(f -> f.getAnnotation(TypeHint.class))
            .map(TypeHint::value)
            .orElse(null));
    this.field = field;
  }

  @Override
  public void set(Object value) {
    try {
      field.set(subject(), value);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to set " + field + " on " + subject() + " to " + value, e);
    }
  }

  @Override
  public Object get() {
    try {
      return field.get(subject());
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to get " + field + " from " + subject(), e);
    }
  }

  /**
   * Extracts configurable fields from an object
   *
   * @param config The configurable object
   * @return The variables
   */
  public static List<FieldVariable> harvest(ObjectConfig config) {
    return Stream.of(config.subject().getClass().getFields())
        .filter(f -> f.isAnnotationPresent(JsonProperty.class))
        .filter(f -> isScalar(f.getType()))
        .map(f -> new FieldVariable(config, f))
        .collect(toList());
  }

  /**
   * @param field a field
   * @return the variable name for that field, or <code>null</code> if it is not a variable
   */
  public static String name(Field field) {
    Optional<JsonProperty> annote = Optional.of(field)
        .map(f -> f.getAnnotation(JsonProperty.class));
    if (annote.isEmpty()) {
      return null;
    }
    return annote
        .map(JsonProperty::value)
        .filter(s -> !s.isEmpty())
        .orElse(field.getName());
  }
}
