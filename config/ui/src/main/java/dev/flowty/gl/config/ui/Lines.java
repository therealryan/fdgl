package dev.flowty.gl.config.ui;

import dev.flowty.gl.config.ui.BoxDrawing.Join;
import dev.flowty.gl.config.ui.BoxDrawing.Weight;
import java.util.Optional;

/**
 * Supports drawing lines on a {@link CharGrid}
 */
public class Lines {

  private final CharGrid charGrid;
  private final Weight weight;
  private final Weight[][] lineGrid;

  Lines(CharGrid charGrid, Weight weight) {
    this.charGrid = charGrid;
    this.weight = weight;
    lineGrid = new Weight[charGrid.height()][charGrid.width()];
  }

  public Lines vertical(int column, int from, int to) {
    for (int i = Math.min(from, to); i < Math.max(from, to); i++) {
      lineGrid[i][column] = weight;
    }
    return this;
  }

  public Lines horizontal(int row, int from, int to) {
    for (int i = Math.min(from, to); i < Math.max(from, to); i++) {
      lineGrid[row][i] = weight;
    }
    return this;
  }

  void render() {
    for (int row = 0; row < lineGrid.length; row++) {
      for (int col = 0; col < lineGrid[row].length; col++) {
        Weight center = get(row, col);
        if (center != null) {
          Join join = Join.match(
              get(row - 1, col),
              get(row + 1, col),
              get(row, col - 1),
              get(row, col + 1));
          if (join != null) {
            charGrid.set(row, col, join.character);
          }
        }
      }
    }
  }

  private Weight get(int row, int col) {
    if (row < 0 || row >= lineGrid.length || col < 0 || col >= lineGrid[row].length) {
      return null;
    }
    return lineGrid[row][col];
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int row = 0; row < lineGrid.length; row++) {
      for (int col = 0; col < lineGrid[row].length; col++) {
        sb.append(Optional.ofNullable(get(row, col))
            .map(w -> w.name().charAt(0))
            .orElse(' '));
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
