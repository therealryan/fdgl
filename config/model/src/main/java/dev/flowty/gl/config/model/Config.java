package dev.flowty.gl.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A single node in a {@link JsonProperty} tree
 */
public interface Config {

  /**
   * @return the name of this config
   */
  String name();

  /**
   * @return a short description of the config
   */
  String description();

  /**
   * @return The thing being configured
   */
  default Object subject() {
    return null;
  }

  /**
   * @return child configs, or <code>null</code> if this is a leaf
   */
  List<Config> children();

  /**
   * @return true if the {@link Config} can be altered
   */
  boolean enabled();

  /**
   * @return <code>true</code> if the config is a wizard and should auto-advance
   */
  boolean wizard();

  /**
   * Builds a string representation of the config tree
   *
   * @param indent The indent at this level
   * @return The string representation
   */
  default String tree(String indent) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent).append(name());
    List<Config> children = children();
    if (children != null) {
      sb.append("/");
      for (Config child : children) {
        sb.append("\n").append(child.tree(indent + "  "));
      }
    }

    return sb.toString();
  }
}
