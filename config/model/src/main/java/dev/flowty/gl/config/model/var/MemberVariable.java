package dev.flowty.gl.config.model.var;

import dev.flowty.gl.config.model.Enabled;
import dev.flowty.gl.config.model.Limits;
import dev.flowty.gl.config.model.ObjectConfig;
import dev.flowty.gl.config.model.Variable;

/**
 * Superclass for {@link Variable}s that are members of {@link ObjectConfig}s
 */
abstract class MemberVariable extends Variable {

  private final ObjectConfig config;

  /**
   * @param config     The owning object
   * @param name       the variable name
   * @param type       The variable type
   * @param widgetHint type hint for widget support
   */
  protected MemberVariable(ObjectConfig config, String name, String description, Class<?> type,
      Class<?> widgetHint) {
    super(name, description, type, widgetHint,
        Limits.harvest(config.subject(), name),
        Enabled.harvest(config.subject(), name));
    this.config = config;
  }

  /**
   * @return The underlying configurable object
   */
  public Object subject() {
    return config.subject();
  }
}
