package dev.flowty.gl.config.model.annote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to add type hinting so that appropriate UI widgets can be used
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface TypeHint {

  /**
   * @return the type hint
   */
  Class<?> value();
}
