package dev.flowty.gl.config.ui.widget;

import dev.flowty.gl.config.model.Variable;
import dev.flowty.gl.config.model.limit.Range;
import dev.flowty.gl.config.ui.CharGrid;
import dev.flowty.gl.config.ui.CharGrid.Coordinate;
import dev.flowty.gl.config.ui.Widget;

/**
 * Manipulates <code>char</code> {@link Variable}s
 */
public class CharacterWidget extends AbstractWidget {

  private static final char[] ALPHANUMERICS = (""
      + "abcdefghijklmnopqrstuvwxyz"
      + "01234567689"
      + "").toCharArray();
  private static final float PERIOD = 0.1f;

  private final CharGrid charGrid;
  private final Variable variable;
  private final Range range;
  private final Coordinate location;
  private final char[] alphabet;
  private int currentValue;

  private boolean upHeld, downHeld;
  private float upHeldFor, downHeldFor;
  private int lastIncrement, lastDecrement;

  private boolean alwaysAccept = false;
  private boolean liveUpdate = false;

  /**
   * Uses the standard {@link #ALPHANUMERICS}
   *
   * @param charGrid the grid to draw to
   * @param location The location of the value in the grid
   * @param variable The variable to manipulate
   */
  public CharacterWidget(CharGrid charGrid, Coordinate location, Variable variable) {
    this(charGrid, location, variable, ALPHANUMERICS);
  }

  /**
   * Uses a custom alphabet
   *
   * @param charGrid the grid to draw to
   * @param location The location of the value in the grid
   * @param variable The variable to manipulate
   * @param alphabet The characters to choose from
   */
  public CharacterWidget(CharGrid charGrid, Coordinate location, Variable variable,
      char[] alphabet) {
    this.charGrid = charGrid;
    this.location = location;
    this.variable = variable;
    range = new Range(0, alphabet.length - 1, 0);
    char value = (char) variable.get();
    this.alphabet = alphabet;
    currentValue = 0;
    while (currentValue < alphabet.length && value != alphabet[currentValue]) {
      currentValue++;
    }
    currentValue = range.clamp(currentValue);
  }

  /**
   * Controls whether the variable is set even on a negative input
   *
   * @param b {@code true} to always set the variable
   * @return {@code this}
   */
  public CharacterWidget alwaysSet(boolean b) {
    alwaysAccept = b;
    return this;
  }

  public CharacterWidget liveUpdate(boolean b) {
    liveUpdate = b;
    return this;
  }

  @Override
  public Widget inputEvent(boolean yes, boolean no, boolean up, boolean down) {
    if (yes) {
      variable.set(alphabet[currentValue]);
      outcome = Outcome.YES;
      if (alwaysAccept) {
        haptic.tick();
      } else {
        haptic.yes();
      }
    }
    if (no) {
      if (alwaysAccept) {
        variable.set(alphabet[currentValue]);
      }
      outcome = Outcome.NO;
      if (alwaysAccept) {
        haptic.tick();
      } else {
        haptic.yes();
      }
    }
    return this;
  }

  @Override
  public Widget inputState(boolean yes, boolean no, boolean up, boolean down) {

    upHeld = up;
    if (!upHeld && upHeldFor > 0) {
      currentValue += increment(upHeldFor);
      upHeldFor = 0;
      haptic.up();
    }

    downHeld = down;
    if (!downHeld && downHeldFor > 0) {
      currentValue -= increment(downHeldFor);
      downHeldFor = 0;
      haptic.down();
    }

    currentValue = range.clamp(currentValue);

    if (liveUpdate) {
      variable.set(alphabet[currentValue]);
    }

    return this;
  }

  /**
   * @param duration how long a button has been pressed for
   * @return the increment value
   */
  static int increment(double duration) {
    int periods = (int) ((duration - 0.1f) / PERIOD);
    return periods + 1;
  }

  @Override
  public Widget update(float delta) {
    if (upHeld) {
      upHeldFor += delta;
    }
    if (downHeld) {
      downHeldFor += delta;
    }

    int increment = increment(upHeldFor);
    int decrement = increment(downHeldFor);
    int prospect = currentValue;
    if (upHeld) {
      prospect += increment;
    }
    if (downHeld) {
      prospect -= decrement;
    }
    prospect = range.clamp(prospect);
    increment = Math.max(1, prospect - currentValue);
    decrement = Math.max(1, currentValue - prospect);

    if (upHeld || downHeld) {
      if (lastIncrement != increment || lastDecrement != decrement) {
        haptic.tick();
      }
    }
    lastIncrement = increment;
    lastDecrement = decrement;

    int height = 5;
    int displayIndex = prospect;
    if (outcome == Outcome.CONTINUE) {
      charGrid
          .pen(p -> p.set(location.row - 1, location.column))
          .colour(upHeld ? ConfigTree.ACTIVE_COLOUR : ConfigTree.INACTIVE_COLOUR)
          .write("△")
          .pen(p -> p.set(location.row, location.column))
          .colour(upHeld || downHeld ? ConfigTree.INACTIVE_COLOUR : ConfigTree.ACTIVE_COLOUR)
          .write(String.valueOf(alphabet[displayIndex]))
          .pen(p -> p.set(location.row + 1, location.column))
          .colour(downHeld ? ConfigTree.ACTIVE_COLOUR : ConfigTree.INACTIVE_COLOUR)
          .write("▽");
    }
    return null;
  }

  @Override
  public CharGrid textGrid() {
    return charGrid;
  }

}
