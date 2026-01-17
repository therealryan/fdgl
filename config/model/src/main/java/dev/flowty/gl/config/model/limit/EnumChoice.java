package dev.flowty.gl.config.model.limit;

import dev.flowty.gl.config.model.annote.Description;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Choices are defined by an enum
 *
 * @param <T> The enum type
 */
public class EnumChoice<T extends Enum<?>> implements Choice<T> {

  private final Class<T> type;

  /**
   * @param type The enum type
   */
  public EnumChoice(Class<T> type) {
    this.type = type;
  }

  @Override
  public String[] choices() {
    return Stream.of(type.getEnumConstants())
        .map(Enum::name)
        .toArray(String[]::new);
  }

  @Override
  public String[] descriptions() {
    return Stream.of(type.getEnumConstants())
        .map(value -> {
          try {
            return Optional.ofNullable(type
                    .getField(value.name())
                    .getAnnotation(Description.class))
                .map(Description::value)
                .orElse(null);
          } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
          }
        })
        .toArray(String[]::new);

  }

  @Override
  public T chosen(int index) {
    return type.getEnumConstants()[index];
  }

}
