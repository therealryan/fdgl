package dev.flowty.gl.config.model.limit;

import dev.flowty.gl.config.model.Limit;
import dev.flowty.gl.config.model.annote.Decimals;
import dev.flowty.gl.config.model.annote.Max;
import dev.flowty.gl.config.model.annote.Min;
import dev.flowty.gl.config.model.var.EncapsulatedVariable;
import dev.flowty.gl.config.model.var.FieldVariable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A numerical range
 */
public class Range implements Limit {

  /**
   * Imposes no value limit and allows zero decimals
   */
  public static final Range NO_LIMIT = new Range(Float.NaN, Float.NaN, 0);

  private final float minimum;
  private final float maximum;
  private final int decimals;

  /**
   * @param minimum The minimum value, or {@link Float#NaN} for no minimum
   * @param maximum The maximum value, or {@link Float#NaN} for no maximum
   */
  public Range(float minimum, float maximum, int decimals) {
    if (!Float.isNaN(minimum) && !Float.isNaN(maximum)
        && minimum > maximum) {
      throw new IllegalStateException("illegal range [" + minimum + "-" + maximum + "]");
    }
    if (decimals < 0) {
      throw new IllegalStateException("illegals decimals count " + decimals);
    }
    this.minimum = minimum;
    this.maximum = maximum;
    this.decimals = decimals;
  }

  public String format(float value) {
    int totalDigits = (int) Math.ceil(
        Math.log10(
            Math.max(
                Math.abs(maximum),
                Math.abs(minimum))))
        + decimals
        + (decimals > 0 ? 1 : 0);
    String fmt = "%s%0" + totalDigits + "." + decimals + "f";
    float v = clamp(value);
    return String.format(fmt,
        hasNegative() ?
            v >= 0
                ? "+"
                : "-"
            : "",
        Math.abs(v));
  }

  public boolean hasNegative() {
    return Float.isNaN(minimum) || minimum < 0;
  }

  /**
   * @param value an integer value
   * @return The value clamped to lie within the valid range
   */
  public int clamp(int value) {
    int clamped = value;
    if (!Float.isNaN(minimum)) {
      clamped = (int) Math.max(clamped, Math.ceil(minimum));
    }
    if (!Float.isNaN(maximum)) {
      clamped = (int) Math.min(clamped, Math.floor(maximum));
    }
    return clamped;
  }

  /**
   * @param value a float value
   * @return The value clamped to lie within the valid range
   */
  public float clamp(float value) {
    float clamped = value;
    if (!Float.isNaN(minimum)) {
      clamped = Math.max(clamped, minimum);
    }
    if (!Float.isNaN(maximum)) {
      clamped = Math.min(clamped, maximum);
    }
    return clamped;
  }

  /**
   * @return {@code true} if the range has a minimum and maximum
   */
  public boolean isClosed() {
    return !Float.isNaN(minimum) && !Float.isNaN(maximum);
  }

  /**
   * Extracts a {@link Range} limit
   *
   * @param subject The configurable object
   * @param name    The variable name
   * @return The {@link Limit}, or null if one does not exist
   */
  public static Limit harvest(Object subject, String name) {
    List<Min> minimums = new ArrayList<>();
    List<Max> maximums = new ArrayList<>();
    List<Decimals> decimals = new ArrayList<>();

    for (Field field : subject.getClass().getFields()) {
      if (name.equals(FieldVariable.name(field))) {
        Optional.of(field)
            .map(f -> f.getAnnotation(Min.class))
            .ifPresent(minimums::add);
        Optional.of(field)
            .map(f -> f.getAnnotation(Max.class))
            .ifPresent(maximums::add);
        Optional.of(field)
            .map(f -> f.getAnnotation(Decimals.class))
            .ifPresent(decimals::add);
      }
    }

    for (Method method : subject.getClass().getMethods()) {
      if (name.equals(EncapsulatedVariable.name(method))) {
        Optional.of(method)
            .map(f -> f.getAnnotation(Min.class))
            .ifPresent(minimums::add);
        Optional.of(method)
            .map(f -> f.getAnnotation(Max.class))
            .ifPresent(maximums::add);
        Optional.of(method)
            .map(f -> f.getAnnotation(Decimals.class))
            .ifPresent(decimals::add);
      }
    }

    if (minimums.isEmpty() && maximums.isEmpty()) {
      return null;
    }

    if (minimums.size() > 1) {
      throw new IllegalStateException(
          "Multiple minimums found for " + name + " in " + subject);
    }
    if (maximums.size() > 1) {
      throw new IllegalStateException(
          "Multiple maximums found for " + name + " in " + subject);
    }
    if (decimals.size() > 1) {
      throw new IllegalStateException(
          "Multiple decimals found for " + name + " in " + subject);
    }

    return new Range(
        minimums.isEmpty() ? Float.NaN : minimums.getFirst().value(),
        maximums.isEmpty() ? Float.NaN : maximums.getFirst().value(),
        decimals.isEmpty() ? 0 : decimals.getFirst().value());
  }
}
