package dev.flowty.gl.config.ui;

import dev.flowty.gl.config.ui.BoxDrawing.Join;
import dev.flowty.gl.config.ui.BoxDrawing.Line;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Formats a grid of values into aligned text
 */
public class TextCells {

  public enum Border {
    SPACE {
      @Override
      public Optional<String> head(int[] widths) {
        return Optional.empty();
      }

      @Override
      public Optional<String> horizontal(int[] widths) {
        return Optional.empty();
      }

      @Override
      public Optional<String> tail(int[] widths) {
        return Optional.empty();
      }

      @Override
      public String left() {
        return "";
      }

      @Override
      public String separator() {
        return " ";
      }

      @Override
      public String right() {
        return "";
      }
    },
    SINGLE {
      @Override
      public Optional<String> head(int[] widths) {
        return Optional.of(Join.N_L_N_L.character
            + IntStream.of(widths)
            .mapToObj(w -> String.valueOf(Line.LIGHT_SOLID.horizontal).repeat(w))
            .collect(Collectors.joining(String.valueOf(Join.N_L_L_L.character)))
            + Join.N_L_L_N.character);
      }

      @Override
      public Optional<String> horizontal(int[] widths) {
        return Optional.of(Join.L_L_N_L.character
            + IntStream.of(widths)
            .mapToObj(w -> String.valueOf(Line.LIGHT_SOLID.horizontal).repeat(w))
            .collect(Collectors.joining(String.valueOf(Join.L_L_L_L.character)))
            + Join.L_L_L_N.character);
      }

      @Override
      public Optional<String> tail(int[] widths) {
        return Optional.of(Join.L_N_N_L.character
            + IntStream.of(widths)
            .mapToObj(w -> String.valueOf(Line.LIGHT_SOLID.horizontal).repeat(w))
            .collect(Collectors.joining(String.valueOf(Join.L_N_L_L.character)))
            + Join.L_N_L_N.character);
      }

      @Override
      public String left() {
        return String.valueOf(Line.LIGHT_SOLID.vertical);
      }

      @Override
      public String separator() {
        return String.valueOf(Line.LIGHT_SOLID.vertical);
      }

      @Override
      public String right() {
        return String.valueOf(Line.LIGHT_SOLID.vertical);
      }
    },
    DOUBLE {
      @Override
      public Optional<String> head(int[] widths) {
        return Optional.of(Join.N_D_N_D.character
            + IntStream.of(widths)
            .mapToObj(w -> String.valueOf(Line.DOUBLE.horizontal).repeat(w))
            .collect(Collectors.joining(String.valueOf(Join.N_D_D_D.character)))
            + Join.N_D_D_N.character);
      }

      @Override
      public Optional<String> horizontal(int[] widths) {
        return Optional.of(Join.D_D_N_D.character
            + IntStream.of(widths)
            .mapToObj(w -> String.valueOf(Line.DOUBLE.horizontal).repeat(w))
            .collect(Collectors.joining(String.valueOf(Join.D_D_D_D.character)))
            + Join.D_D_D_N.character);
      }

      @Override
      public Optional<String> tail(int[] widths) {
        return Optional.of(Join.D_N_N_D.character
            + IntStream.of(widths)
            .mapToObj(w -> String.valueOf(Line.DOUBLE.horizontal).repeat(w))
            .collect(Collectors.joining(String.valueOf(Join.D_N_D_D.character)))
            + Join.D_N_D_N.character);
      }

      @Override
      public String left() {
        return String.valueOf(Line.DOUBLE.vertical);
      }

      @Override
      public String separator() {
        return String.valueOf(Line.DOUBLE.vertical);
      }

      @Override
      public String right() {
        return String.valueOf(Line.DOUBLE.vertical);
      }
    };

    public abstract Optional<String> head(int[] widths);

    public abstract Optional<String> horizontal(int[] widths);

    public abstract Optional<String> tail(int[] widths);

    public abstract String left();

    public abstract String separator();

    public abstract String right();

  }

  private final List<List<Object>> cells = new ArrayList<>();
  private int x = 0;
  private int y = 0;
  private Border border = Border.SPACE;

  /**
   * Adds a value and advances to the next horizontal cell
   *
   * @param value The value
   * @return {@code this}
   */
  public TextCells cell(Object value) {
    return cell(x++, y, value);
  }

  /**
   * Advances to the next row
   *
   * @return {@code this}
   */
  public TextCells nextRow() {
    x = 0;
    y++;
    return this;
  }

  /**
   * Sets a cell value
   *
   * @param x     The horizontal cell coordinate
   * @param y     The vertical cell coordinate
   * @param value The value
   * @return {@code this}
   */
  public TextCells cell(int x, int y, Object value) {
    while (y > cells.size() - 1) {
      cells.add(new ArrayList<>());
    }
    List<Object> row = cells.get(y);
    while (x > row.size() - 1) {
      row.add(null);
    }
    row.set(x, value);
    return this;
  }

  public Object get(int x, int y) {
    if (y >= cells.size()) {
      return null;
    }
    List<Object> row = cells.get(y);
    if (x >= row.size()) {
      return null;
    }
    return row.get(x);
  }

  /**
   * Sets the border style
   *
   * @param border the new style
   * @return {@code this}
   */
  public TextCells border(Border border) {
    this.border = border;
    return this;
  }

  @Override
  public String toString() {
    if (cells.isEmpty()) {
      return "";
    }

    int columns = cells.stream().mapToInt(List::size).max().getAsInt();
    int[] widths = new int[columns];
    for (int i = 0; i < widths.length; i++) {
      int x = i;
      widths[i] = cells.stream()
          .filter(row -> row.size() > x)
          .map(row -> row.get(x))
          .filter(Objects::nonNull)
          .map(String::valueOf)
          .mapToInt(value -> Stream.of(value.split("\n"))
              .mapToInt(String::length)
              .max().orElse(0))
          .max()
          .orElse(0);
    }

    StringBuilder sb = new StringBuilder();
    border.head(widths).ifPresent(line -> sb.append(line).append("\n"));
    for (int y = 0; y < cells.size(); y++) {
      int fy = y;
      List<String[]> lines = IntStream.range(0, widths.length)
          .mapToObj(x -> Optional.ofNullable(get(x, fy))
              .map(String::valueOf)
              .orElse("")
              .split("\n"))
          .toList();
      int height = lines.stream().mapToInt(l -> l.length).max().orElse(1);
      for (int h = 0; h < height; h++) {
        sb.append(border.left());
        int fh = h;
        for (int x = 0; x < widths.length; x++) {
          String value = Optional.of(lines.get(x))
              .filter(cl -> cl.length > fh)
              .map(cl -> cl[fh])
              .orElse("");
          sb.append(value).append(" ".repeat(widths[x] - value.length()));
          if (x < widths.length - 1) {
            sb.append(border.separator());
          }
        }
        sb.append(border.right());
        if (h < height - 1) {
          sb.append("\n");
        }
      }
      if (y < cells.size() - 1) {
        border.horizontal(widths).ifPresent(line -> sb.append("\n").append(line));
        sb.append("\n");
      }
    }
    border.tail(widths).ifPresent(line -> sb.append("\n").append(line));
    return sb.toString();
  }
}
