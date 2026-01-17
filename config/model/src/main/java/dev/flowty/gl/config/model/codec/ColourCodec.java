package dev.flowty.gl.config.model.codec;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.flowty.gl.util.Colour;
import java.io.IOException;

/**
 * Handles packed colour integers as human-tractable
 * <code>RRR:GGG:BBB:AAA</code> strings
 */
@SuppressWarnings("boxing")
public class ColourCodec {

  /**
   * Writes packed colour integer as an rgba string
   */
  public static class Read extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JacksonException {
      return Colour.parse(p.readValueAs(String.class));
    }
  }

  /**
   * Reads rgba strings as packed colour integers
   */
  public static class Write extends StdSerializer<Integer> {

    private static final long serialVersionUID = 1L;

    /***/
    public Write() {
      super(Integer.class);
    }

    @Override
    public void serialize(Integer value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeString(Colour.toString(value));
    }

  }
}
