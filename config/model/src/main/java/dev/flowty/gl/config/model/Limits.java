package dev.flowty.gl.config.model;

import dev.flowty.gl.config.model.limit.Choice;
import dev.flowty.gl.config.model.limit.Range;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Utility for extracting {@link Limit} information from variables
 */
public class Limits {

  private static final List<BiFunction<Object, String, Limit>> HARVESTERS = new ArrayList<>();

  /**
   * Adds a new way to extract {@link Limit} information from a variable
   *
   * @param harvester how to extract a {@link Limit}
   */
  public static void register(BiFunction<Object, String, Limit> harvester) {
    HARVESTERS.add(harvester);
  }

  static {
    register(Choice::harvestFor);
    register(Choice::harvestOf);
    register(Range::harvest);
  }

  /**
   * Gets the limit for a variable
   *
   * @param subject The object being configured
   * @param name    The variable name
   * @return the value limit
   */
  public static Limit harvest(Object subject, String name) {
    List<Limit> limits =
        HARVESTERS.stream()
            .map(h -> h.apply(subject, name))
            .filter(Objects::nonNull)
            .toList();
    if (limits.isEmpty()) {
      return null;
    }
    if (limits.size() > 1) {
      throw new IllegalStateException(
          "Multiple limits found for " + subject + "/" + name + " : " + limits);
    }
    return limits.getFirst();
  }
}
