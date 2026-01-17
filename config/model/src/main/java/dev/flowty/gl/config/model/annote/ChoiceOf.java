package dev.flowty.gl.config.model.annote;

import dev.flowty.gl.config.model.Variable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a {@link Variable} to be furnished with a set of discrete possible values
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ChoiceOf {

  /**
   * @return The range of possible integer values
   */
  int[] ints() default {};

  /**
   * @return The range of possible string values
   */
  String[] strings() default {};
}
