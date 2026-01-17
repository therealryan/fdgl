package dev.flowty.gl.config.model.var;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flowty.gl.config.model.ObjectConfig;
import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.annote.Description;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A variable that just calls a method rather than controlling a value
 */
public class Action extends MemberVariable {

  private final Method action;

  /**
   * @param owner  The object to invoke the method on
   * @param action The method to invoke
   */
  public Action(ObjectConfig owner, Method action) {
    super(owner,
        Optional.of(action)
            .map(f -> f.getAnnotation(JsonProperty.class))
            .map(JsonProperty::value)
            .filter(s -> !s.isEmpty())
            .orElse(action.getName()),
        Optional.ofNullable(action.getAnnotation(Description.class))
            .map(Description::value)
            .orElse(""),
        void.class, // type
        null // no type hint
    );
    this.action = action;
  }

  @Override
  public Object get() {
    return null;
  }

  @Override
  public void set(Object value) {
    try {
      action.invoke(subject());
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to invoked " + action + " on " + subject(), e);
    }
  }

  /**
   * Extracts action {@link Variable}s from an {@link ObjectConfig}
   *
   * @param config The configurable object
   * @return A list of actions that the object provides
   */
  public static List<Action> harvest(ObjectConfig config) {
    return Stream.of(config.subject().getClass().getMethods())
        .filter(m -> m.isAnnotationPresent(JsonProperty.class))
        .filter(m -> m.getReturnType() == void.class && m.getParameterCount() == 0)
        .map(m -> new Action(config, m))
        .collect(toList());
  }
}
