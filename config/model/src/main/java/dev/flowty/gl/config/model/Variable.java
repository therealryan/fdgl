package dev.flowty.gl.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides reflective access to a single {@link JsonProperty} field
 */
public abstract class Variable implements Config {

  /**
   * The set of simple field types that are supported by {@link Variable}s rather than
   * {@link ObjectConfig}s
   */
  private static final Set<Class<?>> SCALAR_TYPES = new HashSet<>(Set.of(
      String.class, char[].class, int.class, boolean.class, float.class));

  /**
   * Adds a new type that should be controlled by a single {@link Variable} rather than
   * {@link ObjectConfig}
   *
   * @param c The new type
   */
  public static void registerScalarType(Class<?> c) {
    SCALAR_TYPES.add(c);
  }

  /**
   * @param c The type
   * @return <code>true</code> if that type can be controlled as a
   * {@link Variable} rather than decomposed in a {@link ObjectConfig}
   */
  public static boolean isScalar(Class<?> c) {
    return SCALAR_TYPES.contains(c) || c.isEnum();
  }

  private final String name;
  private final String description;
  private final Class<?> type;
  private final Class<?> widgetHint;
  private final Limit limit;
  private final Enabled enabled;

  /**
   * @param name        Variable name
   * @param description Variable description
   * @param type        Variable type
   * @param widgetHint  A type hint to select more appropriate widgets
   * @param limit       Variable value limitations
   * @param enabled     Whether the variable is enabled or not
   */
  protected Variable(String name, String description, Class<?> type, Class<?> widgetHint,
      Limit limit,
      Enabled enabled) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.widgetHint = widgetHint;
    this.limit = limit;
    this.enabled = enabled;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return description;
  }

  /**
   * @return The variable type
   */
  public Class<?> type() {
    return type;
  }

  /**
   * @return A hint to the type of widget that should be presented
   */
  public Class<?> widgetHint() {
    return widgetHint;
  }

  /**
   * @return value constraints
   */
  public Limit limit() {
    return limit;
  }

  @Override
  public boolean enabled() {
    return enabled.getAsBoolean();
  }

  @Override
  public final List<Config> children() {
    return null;
  }

  @Override
  public final boolean wizard() {
    return false;
  }

  /**
   * @return Variable value
   */
  public abstract Object get();

  /**
   * @param value Variable value
   */
  public abstract void set(Object value);

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Variable other) {
      return name.equals(other.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
