package dev.flowty.gl.config.ui;

import dev.flowty.gl.config.ui.BoxDrawing.Join;
import dev.flowty.gl.config.ui.BoxDrawing.Weight;
import java.util.Optional;

/**
 * Supports drawing paths on a {@link CharGrid}
 */
public class Paths {

  private final CharGrid charGrid;
  private final Join[][] joinGrid;

  private int row = 0;
  private int col = 0;
  private Weight weight;

  Paths(CharGrid charGrid, Weight weight) {
    this.charGrid = charGrid;
    this.weight = weight;
    this.joinGrid = new Join[charGrid.height()][charGrid.width()];

    for (int row = 0; row < joinGrid.length; row++) {
      for (int col = 0; col < joinGrid[row].length; col++) {
        set(Join.of(charGrid.get(row, col)), row, col);
      }
    }
  }

  /**
   * Moves the pen to a new location
   *
   * @param row the new row index
   * @param col the new columns index
   * @return {@code this}
   */
  public Paths moveTo(int row, int col) {
    this.row = row;
    this.col = col;
    return this;
  }

  /**
   * Changes the path weight
   *
   * @param weight the new weight
   * @return {@code this}
   */
  public Paths weight(Weight weight) {
    this.weight = weight;
    return this;
  }

  /**
   * Draws a horizontal path form the current location
   *
   * @param target The new column index
   * @return {@code this}
   */
  public Paths horizontalTo(int target) {
    while (col < target) {
      transition(0, 1);
    }
    while (col > target) {
      transition(0, -1);
    }
    return this;
  }

  /**
   * Draws a vertical path from the current location
   *
   * @param target the new row index
   * @return {@code this}
   */
  public Paths verticalTo(int target) {
    while (row < target) {
      transition(1, 0);
    }
    while (row > target) {
      transition(-1, 0);
    }
    return this;
  }

  private void transition(int ri, int ci) {
    // leave current cell
    addLink(ri, ci);
    // move
    row += ri;
    col += ci;
    // enter new cell
    addLink(-ri, -ci);
  }

  private void addLink(int ri, int ci) {
    Optional<Join> current = Optional.ofNullable(get(row, col));
    Weight up = Optional.ofNullable(ri == -1 ? weight : null)
        .orElse(current.map(j -> j.up).orElse(null));
    Weight down = Optional.ofNullable(ri == 1 ? weight : null)
        .orElse(current.map(j -> j.down).orElse(null));
    Weight left = Optional.ofNullable(ci == -1 ? weight : null)
        .orElse(current.map(j -> j.left).orElse(null));
    Weight right = Optional.ofNullable(ci == 1 ? weight : null)
        .orElse(current.map(j -> j.right).orElse(null));
    Join match = Join.match(up, down, left, right);
    set(match, row, col);
  }

  void render() {
    for (int row = 0; row < joinGrid.length; row++) {
      for (int col = 0; col < joinGrid[row].length; col++) {
        Join j = get(row, col);
        if (j != null) {
          charGrid.set(row, col, j.character);
        }
      }
    }
  }

  private Join get(int row, int col) {
    if (row < 0 || row >= joinGrid.length || col < 0 || col >= joinGrid[row].length) {
      return null;
    }
    return joinGrid[row][col];
  }

  private void set(Join join, int row, int col) {
    if (row < 0 || row >= joinGrid.length || col < 0 || col >= joinGrid[row].length) {
      // out of bounds
      return;
    }
    joinGrid[row][col] = join;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int row = 0; row < joinGrid.length; row++) {
      for (int col = 0; col < joinGrid[row].length; col++) {
        sb.append(Optional.ofNullable(get(row, col))
            .map(w -> w.character)
            .orElse(' '));
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
