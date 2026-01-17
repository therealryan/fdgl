package dev.flowty.gl.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link Router}
 */
@SuppressWarnings("static-method")
class RouterTest {

  /**
   * Events are only delivered to appropriate listeners
   */
  @Test
  void discrimination() {
    Router router = new Router();
    List<String> list = new ArrayList<>();
    AtomicInteger sum = new AtomicInteger();

    router.register(StringEvent.class, e -> list.add(e.value));
    router.register(IntEvent.class, e -> sum.addAndGet(e.value));

    router.post(new StringEvent("hello"));
    router.post(new IntEvent(5));
    router.post(new StringEvent("world"));
    router.post(new IntEvent(3));

    assertEquals("[hello, world]", list.toString());
    assertEquals(8, sum.get());
  }

  private static class StringEvent implements Event {

    private final String value;

    private StringEvent(String value) {
      this.value = value;
    }
  }

  private static class IntEvent implements Event {

    private final int value;

    private IntEvent(int value) {
      this.value = value;
    }
  }
}
