package dev.flowty.gl.config.model.annote;

import dev.flowty.gl.config.model.Variable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to a method that returns an array, allows a {@link Variable} to be furnished with a set
 * of discrete possible values
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChoiceFor {

  /**
   * @return The name of the variable
   */
  String value();
}
