package dev.flowty.gl.config.model.limit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link Range}
 */
class RangeTest {

  @Test
  void floatFormat() {
    Range r = new Range(-1, 128, 2);
    Assertions.assertEquals("+123.46", r.format(123.456f));
    Assertions.assertEquals("+128.00", r.format(150));
    Assertions.assertEquals("+005.10", r.format(5.1f));
    Assertions.assertEquals("-000.12", r.format(-0.123f));
    Assertions.assertEquals("-001.00", r.format(-2));
  }

  @Test
  void intFormat() {
    Range r = new Range(-1, 128, 0);
    Assertions.assertEquals("+123", r.format(123.456f));
    Assertions.assertEquals("+128", r.format(150));
    Assertions.assertEquals("+005", r.format(5.1f));
    Assertions.assertEquals("-000", r.format(-0.123f));
    Assertions.assertEquals("-001", r.format(-2));
  }
}