package dev.flowty.gl.config.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.Test;

/**
 * Illustrates {@link ObjectConfig} usage
 */
@SuppressWarnings("static-method")
class ObjectConfigTest {

  @JsonPropertyOrder({"branch", "hidden"})
  private static class Root {

    @JsonProperty
    public String accessible = "a";

    @JsonProperty
    private String hidden = "b";

    private String encapsulated = "c";

    @JsonProperty
    public final Branch branch = new Branch();

    @JsonProperty("encapsulated")
    public String encapsulated() {
      return encapsulated;
    }

    @JsonProperty("encapsulated")
    public Root encapsulated(String e) {
      encapsulated = e;
      return this;
    }

    @JsonProperty("hidden")
    private String hidden() {
      return hidden;
    }

    @JsonProperty("hidden")
    private Root hidden(String h) {
      hidden = h;
      return this;
    }

    @JsonProperty("only_getter")
    public String getter() {
      return hidden;
    }

    @JsonProperty("only_setter")
    private Root setter(String h) {
      hidden = h;
      return this;
    }
  }

  private static class Branch {

    @JsonProperty("custom_name")
    public float named = 1.123f;

    @JsonProperty
    public final Leaf leaf = new Leaf();

    @JsonProperty
    public void action() {
      // nowt
    }
  }

  private static class Leaf {

    @JsonProperty
    public int count = 5;

    @JsonProperty("renamed_action")
    public void action() {
      // nowt
    }
  }

  /**
   * Demonstrates variable visibility
   */
  @Test
  void fields() {
    Root t = new Root();
    ObjectConfig oc = new ObjectConfig("testy", t);
    assertEquals("""
            testy/
              branch/
                action
                custom_name
                leaf/
                  count
                  renamed_action
              accessible
              encapsulated""",
        oc.tree(""));
  }
}
