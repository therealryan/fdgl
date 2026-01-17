package dev.flowty.gl.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.flowty.gl.config.model.annote.Description;
import dev.flowty.gl.config.model.annote.Wizard;
import dev.flowty.gl.config.model.var.Action;
import dev.flowty.gl.config.model.var.EncapsulatedVariable;
import dev.flowty.gl.config.model.var.FieldVariable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides reflective access to {@link JsonProperty} fields of an object
 */
public class ObjectConfig implements Config {

  private final String name;
  private final String description;
  private final Object subject;
  private final Enabled enabled;
  private final boolean wizard;

  /**
   * @param name    The name of the {@link Config}
   * @param subject The object to control
   */
  public ObjectConfig(String name, Object subject) {
    this(name, subject, () -> true);
  }

  private ObjectConfig(String name, Object subject, Enabled enabled) {
    this.name = name;
    this.description = Optional.ofNullable(subject.getClass().getAnnotation(Description.class))
        .map(Description::value)
        .orElse("");
    this.subject = subject;
    wizard = subject.getClass().isAnnotationPresent(Wizard.class);
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

  @Override
  public List<Config> children() {
    List<Config> children = new ArrayList<>();
    children.addAll(FieldVariable.harvest(this));
    children.addAll(EncapsulatedVariable.harvest(this));
    children.addAll(Action.harvest(this));
    children.addAll(harvest(subject));
    children.removeIf(v -> !v.enabled());
    children.sort(Comparator.comparing(Config::name, order(subject)));
    return children;
  }

  @Override
  public boolean enabled() {
    return enabled.getAsBoolean();
  }

  @Override
  public boolean wizard() {
    return wizard;
  }

  /**
   * @return the object under control
   */
  public Object subject() {
    return subject;
  }

  private static List<ObjectConfig> harvest(Object subject) {
    List<ObjectConfig> children = new ArrayList<>();
    for (Field field : subject.getClass().getFields()) {
      JsonProperty jp = field.getAnnotation(JsonProperty.class);
      if (jp != null && !Variable.isScalar(field.getType())) {
        try {
          String name = Optional.of(jp)
              .map(JsonProperty::value)
              .filter(s -> !s.isEmpty())
              .orElse(field.getName());
          children.add(new ObjectConfig(
              name,
              field.get(subject),
              Enabled.harvest(subject, name)));
        } catch (Exception e) {
          throw new IllegalStateException("Failed to extract child config from " + field, e);
        }
      }
    }

    // TODO: encapsulated complex types, but I doubt we'll ever have any

    return children;
  }

  @SuppressWarnings("boxing")
  private static Comparator<String> order(Object subject) {
    Map<String, Integer> order = new HashMap<>();
    JsonPropertyOrder jpo = subject.getClass().getAnnotation(JsonPropertyOrder.class);
    if (jpo != null) {
      for (int i = 0; i < jpo.value().length; i++) {
        order.put(jpo.value()[i], i);
      }
    }

    return Comparator
        .comparing((String a) -> order.getOrDefault(a, Integer.MAX_VALUE))
        .thenComparing(a -> a);
  }
}
