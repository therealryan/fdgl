package dev.flowty.gl.config.model.var;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flowty.gl.config.model.Config;
import dev.flowty.gl.config.model.ObjectConfig;
import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.annote.Description;
import dev.flowty.gl.config.model.annote.TypeHint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A variable that controls a single field accessed via methods
 */
public class EncapsulatedVariable extends MemberVariable {

  private final Method getter;
  private final Method setter;

  /**
   * @param owner access to the object that owns the field
   * @param a     A setter or getter
   * @param b     A setter or getter
   */
  public EncapsulatedVariable(ObjectConfig owner, Method a, Method b) {
    super(owner,
        agreed(JsonProperty.class, JsonProperty::value, a, b),
        Optional.ofNullable(agreed(Description.class, Description::value, a, b))
            .orElse(""),
        Stream.of(a, b)
            .filter(m -> m.getParameterCount() == 1)
            .map(m -> m.getParameterTypes()[0])
            .findAny().orElseThrow(),
        agreed(TypeHint.class, TypeHint::value, a, b));

    getter = Stream.of(a, b)
        .filter(m -> m.getParameterCount() == 0)
        .findAny()
        .orElseThrow(() -> new IllegalStateException(
            "Neither " + a + " nor " + b + " look like getters"));
    setter = Stream.of(a, b)
        .filter(m -> m.getParameterCount() == 1)
        .findAny()
        .orElseThrow(() -> new IllegalStateException(
            "Neither " + a + " nor " + b + " look like setters"));
  }

  @Override
  public Object get() {
    try {
      return getter.invoke(subject());
    } catch (Exception e) {
      throw new IllegalStateException("Failed to invoke " + getter + " on " + subject(), e);
    }
  }

  @Override
  public void set(Object value) {
    try {
      setter.invoke(subject(), value);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to invoke " + getter + " on " + subject(), e);
    }
  }

  private static <A extends Annotation, V> V agreed(Class<A> annotation, Function<A, V> value,
      Method... methods) {
    Set<V> values = Stream.of(methods)
        .map(m -> m.getAnnotation(annotation))
        .filter(Objects::nonNull)
        .map(value)
        .collect(Collectors.toCollection(HashSet::new));

    if (values.size() > 1) {
      throw new IllegalArgumentException(""
          + "Found distinct " + annotation + " values " + values
          + " on purportedly linked methods " + Arrays.toString(methods));
    }

    return values.isEmpty() ? null : values.iterator().next();
  }

  /**
   * Extracts variables from an object
   *
   * @param config The parent {@link Config}
   * @return a list of {@link Variable}s
   */
  public static List<EncapsulatedVariable> harvest(ObjectConfig config) {
    Map<String, List<Method>> methods = new HashMap<>();
    for (Method m : config.subject().getClass().getMethods()) {
      JsonProperty jp = m.getAnnotation(JsonProperty.class);
      if (jp != null) {
        boolean supportedType = false;
        supportedType |= m.getParameterCount() == 0 && isScalar(m.getReturnType());
        supportedType |= m.getParameterCount() == 1
            && isScalar(m.getParameterTypes()[0]);
        if (supportedType) {
          methods.computeIfAbsent(jp.value(), n -> new ArrayList<>()).add(m);
        }
      }
    }
    return methods.values().stream()
        .filter(l -> l.size() == 2)
        .map(l -> new EncapsulatedVariable(
            config, l.get(0), l.get(1)))
        .collect(toList());
  }

  /**
   * @param method a method
   * @return the variable name for the encapsulated variable, or <code>null</code> if it is not a
   * variable setter or getter
   */
  public static String name(Method method) {
    return agreed(JsonProperty.class, JsonProperty::value, method);
  }
}
