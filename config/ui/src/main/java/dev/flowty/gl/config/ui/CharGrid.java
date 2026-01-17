package dev.flowty.gl.config.ui;

import dev.flowty.gl.config.ui.BoxDrawing.Join;
import dev.flowty.gl.config.ui.BoxDrawing.Line;
import dev.flowty.gl.config.ui.BoxDrawing.Weight;
import dev.flowty.gl.util.Colour;
import java.util.function.Consumer;

/**
 * A grid of characters
 */
public class CharGrid {

  private char[][] characters = {};
  private int[][] colours = {};
  private final Coordinate min = new Coordinate();
  private final Coordinate max = new Coordinate();
  private final Coordinate pen = new Coordinate();
  private int colour = Colour.WHITE;
  private Line line = Line.LIGHT_SOLID;

  /**
   *
   */
  public CharGrid() {
    // nowt
  }

  private CharGrid(CharGrid parent, int minRow, int minColumn, int maxRow, int maxColumn) {
    characters = parent.characters;
    colours = parent.colours;
    min.set(minRow, minColumn);
    max.set(maxRow, maxColumn);
  }

  /**
   * @param width  The number of columns
   * @param height The number of rows
   * @return <code>this</code>
   */
  public CharGrid size(int width, int height) {
    if (width == width() && height == height()) {
      return this;
    }

    char[][] nch = new char[height][width];
    for (int i = 0; i < nch.length && i < characters.length; i++) {
      System.arraycopy(
          characters[i], 0,
          nch[i], 0,
          Math.min(characters[i].length, nch[i].length));
    }
    characters = nch;

    int[][] ncl = new int[height][width];
    for (int i = 0; i < ncl.length && i < colours.length; i++) {
      System.arraycopy(
          colours[i], 0,
          ncl[i], 0,
          Math.min(colours[i].length, ncl[i].length));
    }
    colours = ncl;

    max.set(height, width);
    pen.set(
        Math.clamp(pen.row, 0, height()),
        Math.clamp(pen.column, 0, width()));
    return this;
  }

  /**
   * Clears all content
   *
   * @return <code>this</code>
   */
  public CharGrid clear() {
    return fill(0, 0, height(), width(), (char) 0)
        .pen(p -> p.set(0, 0))
        .line(Line.LIGHT_SOLID);
  }

  /**
   * Captures the current state of the grid text
   *
   * @param dest Where to store the grid text
   * @return The grid text - this might be a different reference than the one you supplied!
   */
  public char[][] capture(char[][] dest) {
    if (dest == null || dest.length != characters.length) {
      dest = new char[characters.length][];
    }
    for (int i = 0; i < dest.length; i++) {
      if (dest[i] == null || dest[i].length != characters[i].length) {
        dest[i] = new char[characters[i].length];
      }
      System.arraycopy(characters[i], 0, dest[i], 0, dest[i].length);
    }
    return dest;
  }

  /**
   * Captures the current state of the grid colours
   *
   * @param dest Where to store the grid colours
   * @return The grid colours - this might be a different reference than the one you supplied!
   */
  public int[][] capture(int[][] dest) {
    if (dest == null || dest.length != colours.length) {
      dest = new int[colours.length][];
    }
    for (int i = 0; i < dest.length; i++) {
      if (dest[i] == null || dest[i].length != colours[i].length) {
        dest[i] = new int[colours[i].length];
      }
      System.arraycopy(colours[i], 0, dest[i], 0, dest[i].length);
    }
    return dest;
  }

  /**
   * Sets the location for subsequent calls
   *
   * @param update how to move the pen.
   * @return <code>this</code>
   */
  public CharGrid pen(Consumer<Coordinate> update) {
    update.accept(pen);
    return this;
  }

  /**
   * Sets the colour for subsequent calls
   *
   * @param c The new {@link Colour}
   * @return <code>this</code>
   */
  public CharGrid colour(int c) {
    colour = c;
    return this;
  }

  /**
   * Draws maximally-joined-up lines
   *
   * @param weight line weight
   * @param lines  line data
   * @return {@code this}
   */
  public CharGrid lines(Weight weight, Consumer<Lines> lines) {
    Lines l = new Lines(this, weight);
    lines.accept(l);
    l.render();
    return this;
  }

  /**
   * Draws joined-up paths
   *
   * @param weight line weight
   * @param paths  path data
   * @return {@code this}
   */
  public CharGrid paths(Weight weight, Consumer<Paths> paths) {
    Paths p = new Paths(this, weight);
    paths.accept(p);
    p.render();
    return this;
  }

  /**
   * Writes text into the grid, advancing the pen
   *
   * @param text the text
   * @return <code>this</code>
   */
  public CharGrid write(String text) {
    int startColumn = pen.column;
    text.chars()
        .forEach(c -> {
          if (c == '\n') {
            pen.row++;
            pen.column = startColumn;
          } else {
            if (pen.column <= width()) {
              set(pen.row, pen.column, (char) c);
            }
            pen.column++;
          }
        });
    return this;
  }

  public CharGrid write(char... text) {
    return write(new String(text));
  }

  /**
   * Fills a section of a row
   *
   * @param row       The row index
   * @param minColumn The leftmost column index
   * @param maxColumn The rightmost column index
   * @param c         The fill character
   * @return <code>this</code>
   */
  public CharGrid rowFill(int row, int minColumn, int maxColumn, char c) {
    return fill(row, minColumn, row + 1, maxColumn, c);
  }

  /**
   * Fills a section of a column
   *
   * @param column The column index
   * @param minRow The uppermost row index
   * @param maxRow The lowermost row index
   * @param c      the fill character
   * @return <code>this</code>
   */
  public CharGrid columnFill(int column, int minRow, int maxRow, char c) {
    return fill(minRow, column, maxRow, column + 1, c);
  }

  /**
   * Fills a box
   *
   * @param minRow    The uppermost row index
   * @param minColumn The leftmost column index
   * @param maxRow    The lowermost row index
   * @param maxColumn The rightmost column index
   * @param c         the fill character
   * @return <code>this</code>
   */
  public CharGrid fill(int minRow, int minColumn, int maxRow, int maxColumn, char c) {
    for (int column = minColumn; column < maxColumn; column++) {
      for (int row = minRow; row < maxRow; row++) {
        set(row, column, c);
      }
    }
    return this;
  }

  /**
   * Sets the line type
   *
   * @param l the new line type
   * @return <code>this</code>
   */
  public CharGrid line(Line l) {
    line = l;
    return this;
  }

  /**
   * Draws a filled box, with contents
   *
   * @param width   The number of columns
   * @param height  The number of rows
   * @param content The box content
   * @return <code>this</code>
   */
  @SafeVarargs
  public final CharGrid box(int width, int height, Consumer<CharGrid>... content) {
    fill(
        pen.row, pen.column,
        pen.row + height, pen.column + width,
        (char) 0);
    draw(
        pen.row, line, pen.row + height, line,
        pen.column, line, pen.column + width, line);

    if (content.length != 0) {
      CharGrid child = new CharGrid(this,
          pen.row + 1, pen.column + 1,
          pen.row + height - 2, pen.column + width - 2);
      for (Consumer<CharGrid> c : content) {
        c.accept(child);
      }
    }

    heal(pen.row, pen.row + height,
        pen.column, pen.column + width);

    return this;
  }

  /**
   * Draws a box
   *
   * @param minRow    The uppermost row index
   * @param top       The top line
   * @param maxRow    The lowermost row index
   * @param bottom    The bottom line
   * @param minColumn The leftmost column index
   * @param left      The left line
   * @param maxColumn The rightmost column index
   * @param right     The right line
   * @return <code>this</code>
   */
  private CharGrid draw(
      int minRow, Line top,
      int maxRow, Line bottom,
      int minColumn, Line left,
      int maxColumn, Line right) {
    columnFill(minColumn, minRow + 1, maxRow - 1, left.vertical);
    columnFill(maxColumn - 1, minRow + 1, maxRow - 1, right.vertical);
    rowFill(minRow, minColumn + 1, maxColumn - 1, top.horizontal);
    rowFill(maxRow - 1, minColumn + 1, maxColumn - 1, bottom.horizontal);

    set(minRow, minColumn, Join.match(
        null, left.weight,
        null, top.weight).character);
    set(minRow, maxColumn - 1, Join.match(
        null, right.weight,
        top.weight, null).character);
    set(maxRow - 1, minColumn, Join.match(
        left.weight, null,
        null, bottom.weight).character);
    set(maxRow - 1, maxColumn - 1, Join.match(
        right.weight, null,
        top.weight, null).character);

    heal(minRow, maxRow, minColumn, maxColumn);

    return this;
  }

  private void heal(int minRow, int maxRow, int minColumn, int maxColumn) {
    for (int x = minColumn; x < maxColumn; x++) {
      heal(minRow, x);
      heal(maxRow - 1, x);
    }
    for (int y = minRow + 1; y < maxRow - 1; y++) {
      heal(y, minColumn);
      heal(y, maxColumn - 1);
    }
  }

  private void heal(int row, int column) {
    Join above = Join.of(get(row - 1, column));
    Join below = Join.of(get(row + 1, column));
    Join sinister = Join.of(get(row, column - 1));
    Join dexter = Join.of(get(row, column + 1));
    Join rep = Join.heal(above, below, sinister, dexter);
    if (rep != null && rep.character != get(row, column)) {
      set(row, column, rep.character);
    }
  }

  /**
   * Gets a single character in the grid
   *
   * @param row    The row index
   * @param column The column index
   * @return The character at that coordinate
   */
  public char get(int row, int column) {
    int r = row + min.row;
    int c = column + min.column;
    if (0 <= r && r < characters.length
        && 0 <= c && c < characters[r].length) {
      return characters[r][c];
    }
    return (char) 0;
  }

  /**
   * Gets a single colour in the grid
   *
   * @param row    The row index
   * @param column The column index
   * @return The {@link Colour} at that coordinate
   */
  public int getColour(int row, int column) {
    int r = row + min.row;
    int c = column + min.column;
    if (0 <= r && r < characters.length
        && 0 <= c && c < characters[r].length) {
      return colours[r][c];
    }
    return (char) 0;
  }

  /**
   * Sets a single character in the grid
   *
   * @param row    The row index
   * @param column The column index
   * @param ch     The new character
   * @return <code>this</code>
   */
  public CharGrid set(int row, int column, char ch) {
    int r = row + min.row;
    int c = column + min.column;
    if (0 <= r && r < characters.length
        && 0 <= c && c < characters[r].length) {
      characters[r][c] = ch;
      colours[r][c] = colour;
    }
    return this;
  }

  /**
   * Sets a single colour in the grid
   *
   * @param row    The row index
   * @param column The column index
   * @param clr    The new colour
   * @return <code>this</code>
   */
  public CharGrid setColour(int row, int column, int clr) {
    int r = row + min.row;
    int c = column + min.column;
    if (0 <= r && r < characters.length
        && 0 <= c && c < characters[r].length) {
      colours[r][c] = clr;
    }
    return this;
  }

  /**
   * @return The number of columns
   */
  public int width() {
    return max.column - min.column;
  }

  /**
   * @return The number of rows
   */
  public int height() {
    return max.row - min.row;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (char[] row : characters) {
      for (char c : row) {
        sb.append(c == 0 ? ' ' : c);
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * A coordinate within the grid
   */
  public static class Coordinate {

    /**
     * The row index
     */
    public int row;
    /**
     * The column index
     */
    public int column;

    /**
     * Sets the coordinate values
     *
     * @param r The row index
     * @param c The column index
     * @return <code>this</code>
     */
    public Coordinate set(int r, int c) {
      row = r;
      column = c;
      return this;
    }

    /**
     * @param r The new row
     */
    public void row(int r) {
      row = r;
    }

    /**
     * @param c the new column
     */
    public void column(int c) {
      column = c;
    }

    @Override
    public String toString() {
      return "row " + row + " col " + column;
    }
  }
}
