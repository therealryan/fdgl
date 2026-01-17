package dev.flowty.gl.config.model.annote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply this to a no-orgs boolean-returning method to add an enablement control for a variable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnablementFor {

  /**
   * @return The names of the variables that the annotated method controls
   */
  String[] value();
}
