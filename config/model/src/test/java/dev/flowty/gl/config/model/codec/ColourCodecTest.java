package dev.flowty.gl.config.model.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.flowty.gl.util.Colour;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link ColourCodec}
 */
@SuppressWarnings("static-method")
class ColourCodecTest {

  private static final ObjectMapper JSON = JsonMapper.builder()
      .disable(MapperFeature.AUTO_DETECT_CREATORS,
          MapperFeature.AUTO_DETECT_FIELDS,
          MapperFeature.AUTO_DETECT_GETTERS,
          MapperFeature.AUTO_DETECT_IS_GETTERS)
      .enable(SerializationFeature.INDENT_OUTPUT)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .build();

  private static class Parent {

    @JsonProperty
    public final Child child = new Child();
  }

  private static class Child {

    @JsonProperty()
    @JsonDeserialize(using = ColourCodec.Read.class)
    @JsonSerialize(using = ColourCodec.Write.class)
    public int exposed = Colour.RED;

    @JsonProperty("hidden")
    private int hidden = Colour.BLUE;

    @JsonProperty("hidden")
    @JsonSerialize(using = ColourCodec.Write.class)
    public int getHidden() {
      return hidden;
    }

    @JsonProperty("hidden")
    @JsonDeserialize(using = ColourCodec.Read.class)
    public Child setHidden(int h) {
      hidden = h;
      return this;
    }
  }

  /**
   * Illustrates colour serialisation
   *
   * @throws Exception on failure
   */
  @Test
  void write() throws Exception {
    Parent p = new Parent();

    assertEquals("""
            {
              "child" : {
                "exposed" : "255:000:000:255",
                "hidden" : "000:000:255:255"
              }
            }""",
        JSON.writeValueAsString(p)
            .replace("\r", ""));
  }

  /**
   * Illustrates colour parsing
   *
   * @throws Exception on failure
   */
  @Test
  void create() throws Exception {
    Parent p = JSON.readValue("""
        {
          "child" : {
            "exposed" : "000:255:000:255",
            "hidden" : "000:000:255:128"
          }
        }""", Parent.class);

    assertEquals("000:255:000:255", Colour.toString(p.child.exposed));
    assertEquals("000:000:255:128", Colour.toString(p.child.getHidden()));
  }

  /**
   * Illustrates updating an existing object
   *
   * @throws Exception on failure
   */
  @Test
  void update() throws Exception {
    Parent p = new Parent();
    JSON.readerForUpdating(p).readValue("""
        {
          "child" : {
            "exposed" : "000:255:000:255",
            "hidden" : "000:000:255:128"
          }
        }""");
    assertEquals("000:255:000:255", Colour.toString(p.child.exposed));
    assertEquals("000:000:255:128", Colour.toString(p.child.getHidden()));
  }
}
