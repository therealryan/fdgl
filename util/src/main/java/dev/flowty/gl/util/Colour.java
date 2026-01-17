package dev.flowty.gl.util;

import java.nio.ByteOrder;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for working with natively-ordered integer-packed RGBA-format colours. Beware that byte
 * order can change from machine to machine, so if you're saving these values to disk, use
 * {@link #toBigEndian(int)} to write and {@link #fromBigEndian(int)} to read
 */
public class Colour {

  private static final int redOffset, greenOffset, blueOffset, alphaOffset;

  static {
    boolean big = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    redOffset = big ? 24 : 0;
    greenOffset = big ? 16 : 8;
    blueOffset = big ? 8 : 16;
    alphaOffset = big ? 0 : 24;
  }

  /***/
  public static final int WHITE = packFloat(1, 1, 1, 1);

  /***/
  public static final int BLACK = packFloat(0, 0, 0, 1);

  /***/
  public static final int GREY = packFloat(0.5f, 0.5f, 0.5f, 1);

  /***/
  public static final int DARK_GREY = packFloat(0.25f, 0.25f, 0.25f, 1);

  /***/
  public static final int LIGHT_GRAY = packFloat(0.75f, 0.75f, 0.75f, 1);

  /***/
  public static final int RED = packFloat(1, 0, 0, 1);

  /***/
  public static final int GREEN = packFloat(0, 1, 0, 1);

  /***/
  public static final int BLUE = packFloat(0, 0, 1, 1);

  /***/
  public static final int YELLOW = packFloat(1, 1, 0, 1);

  /***/
  public static final int CYAN = packFloat(0, 1, 1, 1);

  /***/
  public static final int MAGENTA = packFloat(1, 0, 1, 1);

  /***/
  public static final int ORANGE = packFloat(1, 0.5f, 0, 1);

  /***/
  public static final int SPRING_GREEN = packFloat(0.5f, 1, 0, 1);

  /***/
  public static final int TURQUOISE = packFloat(0, 1, 0.5f, 1);

  /***/
  public static final int OCEAN = packFloat(0, 0.5f, 1, 1);

  /***/
  public static final int VIOLET = packFloat(0.5f, 0, 1, 1);

  /***/
  public static final int RASPBERRY = packFloat(1, 0, 0.5f, 1);

  private Colour() {
    // no instances
  }

  /**
   * Converts from a native-order packed colour int to a big-endian packed colour int
   *
   * @param rgba packed colour value
   * @return The big-endian int
   */
  public static int toBigEndian(int rgba) {
    return redi(rgba) << 24 | greeni(rgba) << 16 | bluei(rgba) << 8
        | alphai(rgba);
  }

  /**
   * Converts from a big-endian packed colouor int to a native-order packed colour int
   *
   * @param rgba packed colour value
   * @return the native-order int
   */
  public static int fromBigEndian(int rgba) {
    int r = rgba >> 24 & 0xff;
    int g = rgba >> 16 & 0xff;
    int b = rgba >> 8 & 0xff;
    int a = rgba >> 0 & 0xff;
    return packInt(r, g, b, a);
  }

  /**
   * Packs colour components into an integer
   *
   * @param red   range 0-255
   * @param green range 0-255
   * @param blue  range 0-255
   * @param alpha range 0-255
   * @return a packed colour value
   */
  public static int packInt(int red, int green, int blue, int alpha) {
    int r = (red & 0xff) << redOffset;
    int g = (green & 0xff) << greenOffset;
    int b = (blue & 0xff) << blueOffset;
    int a = (alpha & 0xff) << alphaOffset;

    return r | g | b | a;
  }

  /**
   * Packs colour components into an integer
   *
   * @param r range 0-1
   * @param g range 0-1
   * @param b range 0-1
   * @param a range 0-1
   * @return a packed colour value
   */
  public static int packFloat(float r, float g, float b, float a) {
    return packInt((int) (r * 255f), (int) (g * 255f), (int) (b * 255f),
        (int) (a * 255f));
  }

  /**
   * Extracts the red component
   *
   * @param rgba packed colour value
   * @return The component 0-1
   */
  public static float redf(int rgba) {
    return redi(rgba) / 255f;
  }

  /**
   * Extracts the green component
   *
   * @param rgba packed colour value
   * @return The component 0-1
   */
  public static float greenf(int rgba) {
    return greeni(rgba) / 255f;
  }

  /**
   * Extracts the blue component
   *
   * @param rgba packed colour value
   * @return The component 0-1
   */
  public static float bluef(int rgba) {
    return bluei(rgba) / 255f;
  }

  /**
   * Extracts the alpha component
   *
   * @param rgba packed colour value
   * @return The component 0-1
   */
  public static float alphaf(int rgba) {
    return alphai(rgba) / 255f;
  }

  /**
   * Extracts the red component
   *
   * @param rgba packed colour value
   * @return The component 0-255
   */
  public static int redi(int rgba) {
    return rgba >> redOffset & 0xff;
  }

  /**
   * Extracts the green component
   *
   * @param rgba packed colour value
   * @return The component 0-255
   */
  public static int greeni(int rgba) {
    return rgba >> greenOffset & 0xff;
  }

  /**
   * Extracts the blue component
   *
   * @param rgba packed colour value
   * @return The component 0-255
   */
  public static int bluei(int rgba) {
    return rgba >> blueOffset & 0xff;
  }

  /**
   * Extracts the alpha component
   *
   * @param rgba packed colour value
   * @return The component 0-255
   */
  public static int alphai(int rgba) {
    return rgba >> alphaOffset & 0xff;
  }

  /**
   * Mask to get only the alpha bits
   */
  private static final int alphaMask = 0xff << alphaOffset;

  /**
   * Mask to get only the colour bits
   */
  private static final int colourmask = ~alphaMask;

  /**
   * Amends a colour by changing the alpha component
   *
   * @param colour source colour
   * @param alpha  new alpha component (0-255)
   * @return new colour
   */
  public static int withAlphai(int colour, int alpha) {
    int c = colour & colourmask;
    int a = (alpha & 0xff) << alphaOffset;
    return c | a;
  }

  /**
   * Amends a colour by changing the alpha component
   *
   * @param colour source colour
   * @param alpha  new alpha component (0-1)
   * @return new colour
   */
  public static int withAlphaf(int colour, float alpha) {
    int c = colour & colourmask;
    int a = (((int) (alpha * 255)) & 0xff) << alphaOffset;
    return c | a;
  }

  /**
   * Amends an array of colour ints by changing the alpha component
   *
   * @param colours source colours
   * @param alpha   new alpha component (0-255)
   */
  public static void withAlphai(int[] colours, int alpha) {
    for (int i = 0; i < colours.length; i++) {
      colours[i] = withAlphai(colours[i], alpha);
    }
  }

  /**
   * @param rgba packed colour value
   * @return a string in <code>r:g:b:a</code> format
   */
  @SuppressWarnings("boxing")
  public static String toString(int rgba) {
    return String.format("%03d:%03d:%03d:%03d",
        redi(rgba), greeni(rgba), bluei(rgba), alphai(rgba));
  }

  /**
   * @param rgba packed colour value
   * @return a string in hex-encoded <code>RRGGBBAA</code> format
   */
  public static String asHexCode(int rgba) {
    return String.format("%02x%02x%02x%02x",
        redi(rgba), greeni(rgba), bluei(rgba), alphai(rgba));
  }

  private static final Pattern COMPONENTS = Pattern.compile(
      "(\\d{3}):(\\d{3}):(\\d{3}):(\\d{3})");

  /**
   * Parses the output of {@link #toString()}
   *
   * @param s the string
   * @return the colour
   */
  @SuppressWarnings("boxing")
  public static int parse(String s) {
    return Optional.ofNullable(s)
        .map(COMPONENTS::matcher)
        .filter(Matcher::matches)
        .map(m -> packInt(
            Integer.parseInt(m.group(1)),
            Integer.parseInt(m.group(2)),
            Integer.parseInt(m.group(3)),
            Integer.parseInt(m.group(4))))
        .orElseThrow(() -> new IllegalArgumentException(
            "Failed to parse " + s + " as " + COMPONENTS));

  }
}
