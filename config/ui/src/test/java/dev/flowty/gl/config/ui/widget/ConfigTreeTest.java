package dev.flowty.gl.config.ui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flowty.gl.config.model.ObjectConfig;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.Widget;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link ConfigTree}
 */
@SuppressWarnings("static-method")
class ConfigTreeTest {

  /**
   * Illustrates tree behaviour
   */
  @Test
  void test() {
    Widget cfgt = new ConfigTree(new CharGrid().size(14, 6),
        new ObjectConfig("root", new Root()));

    assertEquals("""
        ┌────────────┐
        │/root/      │
        │┌┴──┘       │
        │├[abc/]     │
        │└ def/      │
        └────────────┘
        """, cfgt.textGrid().toString());

    cfgt.inputEvent(false, false, false, true).update(0);

    assertEquals("""
        ┌────────────┐
        │/root/      │
        │┌┴──┘       │
        │├ abc/      │
        │└[def/]     │
        └────────────┘
        """, cfgt.textGrid().toString());

    cfgt.inputEvent(true, false, false, false).update(0);

    assertEquals("""
        ┌────────────┐
        │/root/def/  │
        │┌─────┴─┘   │
        │├[ghi/]     │
        │└ jkl/      │
        └────────────┘
        """, cfgt.textGrid().toString());

    cfgt.inputEvent(true, false, false, false).update(0);

    assertEquals("""
        ┌────────────┐
        │…ot/def/ghi/│
        │┌───────┴─┘ │
        │├[mno]    pq│
        │└ stu     vw│
        └────────────┘
        """, cfgt.textGrid().toString());

    cfgt.inputEvent(false, true, false, false).update(0);

    assertEquals("""
        ┌────────────┐
        │/root/def/  │
        │┌─────┴─┘   │
        │├[ghi/]     │
        │└ jkl/      │
        └────────────┘
        """, cfgt.textGrid().toString());
  }

  /**
   * The root of the config tree
   */
  public static class Root {

    /***/
    @JsonProperty
    public Branch abc = new Branch();
    /***/
    @JsonProperty
    public Branch def = new Branch();
  }

  /**
   * The branch of the tree
   */
  public static class Branch {

    /***/
    @JsonProperty
    public Leaf ghi = new Leaf();
    /***/
    @JsonProperty
    public Leaf jkl = new Leaf();
  }

  /**
   * The leaf of the tree
   */
  public static class Leaf {

    /***/
    @JsonProperty
    public String mno = "pqr";
    /***/
    @JsonProperty
    public String stu = "vwx";
  }
}
