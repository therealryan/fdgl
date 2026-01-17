package dev.flowty.gl.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RangeTest {

  @Test
  void wrap() {
    Assertions.assertEquals(0, Range.wrap(0, 0, 10));
    Assertions.assertEquals(1, Range.wrap(1, 0, 10));
    Assertions.assertEquals(9, Range.wrap(9, 0, 10));
    Assertions.assertEquals(0, Range.wrap(10, 0, 10));
    Assertions.assertEquals(9, Range.wrap(-1, 0, 10));
    Assertions.assertEquals(1, Range.wrap(11, 0, 10));
    Assertions.assertEquals(1, Range.wrap(1001, 0, 10));
  }
}