package dev.flowty.gl.config.model;

import static java.util.stream.Collectors.toList;

import dev.flowty.gl.config.model.annote.EnablementFor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

/**
 * Controls whether a variable is enabled for editing or not
 */
public interface Enabled extends BooleanSupplier {

  /**
   * Gets the enablement flag for a variable
   *
   * @param subject The object being configured
   * @param name    The variable name
   * @return the enablement flag
   */
  static Enabled harvest(Object subject, String name) {

    List<Enabled> enableds = Stream.of(
            harvestFor(subject, name))
        .filter(Objects::nonNull)
        .collect(toList());
    if (enableds.isEmpty()) {
      return () -> true;
    }
    if (enableds.size() > 1) {
      throw new IllegalStateException(
          "Multiple enableds found for " + subject + "/" + name + " : " + enableds);
    }
    return enableds.getFirst();
  }

  /**
   * Extracts an enablement flag, based on {@link EnablementFor} annotations
   *
   * @param subject The object being configured
   * @param name    The variable name
   * @return The {@link Enabled} flag, or <code>null</code> for no flag
   */
  static Enabled harvestFor(Object subject, String name) {
    List<Method> sources = new ArrayList<>();

    for (Method method : subject.getClass().getMethods()) {
      EnablementFor ef = method.getAnnotation(EnablementFor.class);
      if (ef != null && Stream.of(ef.value()).anyMatch(name::equals)) {
        if (method.getParameterCount() != 0) {
          throw new IllegalStateException(
              ef.getClass().getSimpleName()
                  + " implies no parameters, but " + method
                  + " has some");
        }
        if (method.getReturnType() != boolean.class) {
          throw new IllegalStateException(
              ef.getClass().getSimpleName()
                  + " implies boolean return type, but " + method
                  + " doesn't do that");
        }
        sources.add(method);
      }
    }

    if (sources.isEmpty()) {
      return null;
    }

    return () -> {
      boolean enabled = true;
      for (Method m : sources) {
        try {
          enabled &= (Boolean) m.invoke(subject);
        } catch (Exception e) {
          throw new IllegalStateException("Failed to extract boolean return from " + m, e);
        }
      }
      return enabled;
    };
  }
}
