package dev.flowty.gl.config.model.annote;

import dev.flowty.gl.config.model.Config;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Causes the UI for a {@link Config} extracted from a type to auto-advance as variables are set
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Wizard {
  // marker only
}
