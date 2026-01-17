package dev.flowty.gl.config.ui;

/**
 * Capturing the topology of
 * <a href="https://en.wikipedia.org/wiki/Box-drawing_characters">Box-drawing
 * characters</a>:
 * <table>
 * <tbody>
 * <tr>
 * <td>&nbsp;</td>
 * <td>0</td>
 * <td>1</td>
 * <td>2</td>
 * <td>3</td>
 * <td>4</td>
 * <td>5</td>
 * <td>6</td>
 * <td>7</td>
 * <td>8</td>
 * <td>9</td>
 * <td>A</td>
 * <td>B</td>
 * <td>C</td>
 * <td>D</td>
 * <td>E</td>
 * <td>F</td>
 * </tr>
 * <tr>
 * <td>U+250x</td>
 * <td title="U+2500: BOX DRAWINGS LIGHT HORIZONTAL">─</td>
 * <td title="U+2501: BOX DRAWINGS HEAVY HORIZONTAL">━</td>
 * <td title="U+2502: BOX DRAWINGS LIGHT VERTICAL">│</td>
 * <td title="U+2503: BOX DRAWINGS HEAVY VERTICAL">┃</td>
 * <td title="U+2504: BOX DRAWINGS LIGHT TRIPLE DASH HORIZONTAL">┄</td>
 * <td title="U+2505: BOX DRAWINGS HEAVY TRIPLE DASH HORIZONTAL">┅</td>
 * <td title="U+2506: BOX DRAWINGS LIGHT TRIPLE DASH VERTICAL">┆</td>
 * <td title="U+2507: BOX DRAWINGS HEAVY TRIPLE DASH VERTICAL">┇</td>
 * <td title="U+2508: BOX DRAWINGS LIGHT QUADRUPLE DASH HORIZONTAL">┈</td>
 * <td title="U+2509: BOX DRAWINGS HEAVY QUADRUPLE DASH HORIZONTAL">┉</td>
 * <td title="U+250A: BOX DRAWINGS LIGHT QUADRUPLE DASH VERTICAL">┊</td>
 * <td title="U+250B: BOX DRAWINGS HEAVY QUADRUPLE DASH VERTICAL">┋</td>
 * <td title="U+250C: BOX DRAWINGS LIGHT DOWN AND RIGHT">┌</td>
 * <td title="U+250D: BOX DRAWINGS DOWN LIGHT AND RIGHT HEAVY">┍</td>
 * <td title="U+250E: BOX DRAWINGS DOWN HEAVY AND RIGHT LIGHT">┎</td>
 * <td title="U+250F: BOX DRAWINGS HEAVY DOWN AND RIGHT">┏</td>
 * </tr>
 * <tr>
 * <td>U+251x</td>
 * <td title="U+2510: BOX DRAWINGS LIGHT DOWN AND LEFT">┐</td>
 * <td title="U+2511: BOX DRAWINGS DOWN LIGHT AND LEFT HEAVY">┑</td>
 * <td title="U+2512: BOX DRAWINGS DOWN HEAVY AND LEFT LIGHT">┒</td>
 * <td title="U+2513: BOX DRAWINGS HEAVY DOWN AND LEFT">┓</td>
 * <td title="U+2514: BOX DRAWINGS LIGHT UP AND RIGHT">└</td>
 * <td title="U+2515: BOX DRAWINGS UP LIGHT AND RIGHT HEAVY">┕</td>
 * <td title="U+2516: BOX DRAWINGS UP HEAVY AND RIGHT LIGHT">┖</td>
 * <td title="U+2517: BOX DRAWINGS HEAVY UP AND RIGHT">┗</td>
 * <td title="U+2518: BOX DRAWINGS LIGHT UP AND LEFT">┘</td>
 * <td title="U+2519: BOX DRAWINGS UP LIGHT AND LEFT HEAVY">┙</td>
 * <td title="U+251A: BOX DRAWINGS UP HEAVY AND LEFT LIGHT">┚</td>
 * <td title="U+251B: BOX DRAWINGS HEAVY UP AND LEFT">┛</td>
 * <td title="U+251C: BOX DRAWINGS LIGHT VERTICAL AND RIGHT">├</td>
 * <td title="U+251D: BOX DRAWINGS VERTICAL LIGHT AND RIGHT HEAVY">┝</td>
 * <td title="U+251E: BOX DRAWINGS UP HEAVY AND RIGHT DOWN LIGHT">┞</td>
 * <td title="U+251F: BOX DRAWINGS DOWN HEAVY AND RIGHT UP LIGHT">┟</td>
 * </tr>
 * <tr>
 * <td>U+252x</td>
 * <td title="U+2520: BOX DRAWINGS VERTICAL HEAVY AND RIGHT LIGHT">┠</td>
 * <td title="U+2521: BOX DRAWINGS DOWN LIGHT AND RIGHT UP HEAVY">┡</td>
 * <td title="U+2522: BOX DRAWINGS UP LIGHT AND RIGHT DOWN HEAVY">┢</td>
 * <td title="U+2523: BOX DRAWINGS HEAVY VERTICAL AND RIGHT">┣</td>
 * <td title="U+2524: BOX DRAWINGS LIGHT VERTICAL AND LEFT">┤</td>
 * <td title="U+2525: BOX DRAWINGS VERTICAL LIGHT AND LEFT HEAVY">┥</td>
 * <td title="U+2526: BOX DRAWINGS UP HEAVY AND LEFT DOWN LIGHT">┦</td>
 * <td title="U+2527: BOX DRAWINGS DOWN HEAVY AND LEFT UP LIGHT">┧</td>
 * <td title="U+2528: BOX DRAWINGS VERTICAL HEAVY AND LEFT LIGHT">┨</td>
 * <td title="U+2529: BOX DRAWINGS DOWN LIGHT AND LEFT UP HEAVY">┩</td>
 * <td title="U+252A: BOX DRAWINGS UP LIGHT AND LEFT DOWN HEAVY">┪</td>
 * <td title="U+252B: BOX DRAWINGS HEAVY VERTICAL AND LEFT">┫</td>
 * <td title="U+252C: BOX DRAWINGS LIGHT DOWN AND HORIZONTAL">┬</td>
 * <td title="U+252D: BOX DRAWINGS LEFT HEAVY AND RIGHT DOWN LIGHT">┭</td>
 * <td title="U+252E: BOX DRAWINGS RIGHT HEAVY AND LEFT DOWN LIGHT">┮</td>
 * <td title="U+252F: BOX DRAWINGS DOWN LIGHT AND HORIZONTAL HEAVY">┯</td>
 * </tr>
 * <tr>
 * <td>U+253x</td>
 * <td title="U+2530: BOX DRAWINGS DOWN HEAVY AND HORIZONTAL LIGHT">┰</td>
 * <td title="U+2531: BOX DRAWINGS RIGHT LIGHT AND LEFT DOWN HEAVY">┱</td>
 * <td title="U+2532: BOX DRAWINGS LEFT LIGHT AND RIGHT DOWN HEAVY">┲</td>
 * <td title="U+2533: BOX DRAWINGS HEAVY DOWN AND HORIZONTAL">┳</td>
 * <td title="U+2534: BOX DRAWINGS LIGHT UP AND HORIZONTAL">┴</td>
 * <td title="U+2535: BOX DRAWINGS LEFT HEAVY AND RIGHT UP LIGHT">┵</td>
 * <td title="U+2536: BOX DRAWINGS RIGHT HEAVY AND LEFT UP LIGHT">┶</td>
 * <td title="U+2537: BOX DRAWINGS UP LIGHT AND HORIZONTAL HEAVY">┷</td>
 * <td title="U+2538: BOX DRAWINGS UP HEAVY AND HORIZONTAL LIGHT">┸</td>
 * <td title="U+2539: BOX DRAWINGS RIGHT LIGHT AND LEFT UP HEAVY">┹</td>
 * <td title="U+253A: BOX DRAWINGS LEFT LIGHT AND RIGHT UP HEAVY">┺</td>
 * <td title="U+253B: BOX DRAWINGS HEAVY UP AND HORIZONTAL">┻</td>
 * <td title="U+253C: BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL">┼</td>
 * <td title="U+253D: BOX DRAWINGS LEFT HEAVY AND RIGHT VERTICAL LIGHT">┽</td>
 * <td title="U+253E: BOX DRAWINGS RIGHT HEAVY AND LEFT VERTICAL LIGHT">┾</td>
 * <td title="U+253F: BOX DRAWINGS VERTICAL LIGHT AND HORIZONTAL HEAVY">┿</td>
 * </tr>
 * <tr>
 * <td>U+254x</td>
 * <td title="U+2540: BOX DRAWINGS UP HEAVY AND DOWN HORIZONTAL LIGHT">╀</td>
 * <td title="U+2541: BOX DRAWINGS DOWN HEAVY AND UP HORIZONTAL LIGHT">╁</td>
 * <td title="U+2542: BOX DRAWINGS VERTICAL HEAVY AND HORIZONTAL LIGHT">╂</td>
 * <td title="U+2543: BOX DRAWINGS LEFT UP HEAVY AND RIGHT DOWN LIGHT">╃</td>
 * <td title="U+2544: BOX DRAWINGS RIGHT UP HEAVY AND LEFT DOWN LIGHT">╄</td>
 * <td title="U+2545: BOX DRAWINGS LEFT DOWN HEAVY AND RIGHT UP LIGHT">╅</td>
 * <td title="U+2546: BOX DRAWINGS RIGHT DOWN HEAVY AND LEFT UP LIGHT">╆</td>
 * <td title="U+2547: BOX DRAWINGS DOWN LIGHT AND UP HORIZONTAL HEAVY">╇</td>
 * <td title="U+2548: BOX DRAWINGS UP LIGHT AND DOWN HORIZONTAL HEAVY">╈</td>
 * <td title="U+2549: BOX DRAWINGS RIGHT LIGHT AND LEFT VERTICAL HEAVY">╉</td>
 * <td title="U+254A: BOX DRAWINGS LEFT LIGHT AND RIGHT VERTICAL HEAVY">╊</td>
 * <td title="U+254B: BOX DRAWINGS HEAVY VERTICAL AND HORIZONTAL">╋</td>
 * <td title="U+254C: BOX DRAWINGS LIGHT DOUBLE DASH HORIZONTAL">╌</td>
 * <td title="U+254D: BOX DRAWINGS HEAVY DOUBLE DASH HORIZONTAL">╍</td>
 * <td title="U+254E: BOX DRAWINGS LIGHT DOUBLE DASH VERTICAL">╎</td>
 * <td title="U+254F: BOX DRAWINGS HEAVY DOUBLE DASH VERTICAL">╏</td>
 * </tr>
 * <tr>
 * <td>U+255x</td>
 * <td title="U+2550: BOX DRAWINGS DOUBLE HORIZONTAL">═</td>
 * <td title="U+2551: BOX DRAWINGS DOUBLE VERTICAL">║</td>
 * <td title="U+2552: BOX DRAWINGS DOWN SINGLE AND RIGHT DOUBLE">╒</td>
 * <td title="U+2553: BOX DRAWINGS DOWN DOUBLE AND RIGHT SINGLE">╓</td>
 * <td title="U+2554: BOX DRAWINGS DOUBLE DOWN AND RIGHT">╔</td>
 * <td title="U+2555: BOX DRAWINGS DOWN SINGLE AND LEFT DOUBLE">╕</td>
 * <td title="U+2556: BOX DRAWINGS DOWN DOUBLE AND LEFT SINGLE">╖</td>
 * <td title="U+2557: BOX DRAWINGS DOUBLE DOWN AND LEFT">╗</td>
 * <td title="U+2558: BOX DRAWINGS UP SINGLE AND RIGHT DOUBLE">╘</td>
 * <td title="U+2559: BOX DRAWINGS UP DOUBLE AND RIGHT SINGLE">╙</td>
 * <td title="U+255A: BOX DRAWINGS DOUBLE UP AND RIGHT">╚</td>
 * <td title="U+255B: BOX DRAWINGS UP SINGLE AND LEFT DOUBLE">╛</td>
 * <td title="U+255C: BOX DRAWINGS UP DOUBLE AND LEFT SINGLE">╜</td>
 * <td title="U+255D: BOX DRAWINGS DOUBLE UP AND LEFT">╝</td>
 * <td title="U+255E: BOX DRAWINGS VERTICAL SINGLE AND RIGHT DOUBLE">╞</td>
 * <td title="U+255F: BOX DRAWINGS VERTICAL DOUBLE AND RIGHT SINGLE">╟</td>
 * </tr>
 * <tr>
 * <td>U+256x</td>
 * <td title="U+2560: BOX DRAWINGS DOUBLE VERTICAL AND RIGHT">╠</td>
 * <td title="U+2561: BOX DRAWINGS VERTICAL SINGLE AND LEFT DOUBLE">╡</td>
 * <td title="U+2562: BOX DRAWINGS VERTICAL DOUBLE AND LEFT SINGLE">╢</td>
 * <td title="U+2563: BOX DRAWINGS DOUBLE VERTICAL AND LEFT">╣</td>
 * <td title="U+2564: BOX DRAWINGS DOWN SINGLE AND HORIZONTAL DOUBLE">╤</td>
 * <td title="U+2565: BOX DRAWINGS DOWN DOUBLE AND HORIZONTAL SINGLE">╥</td>
 * <td title="U+2566: BOX DRAWINGS DOUBLE DOWN AND HORIZONTAL">╦</td>
 * <td title="U+2567: BOX DRAWINGS UP SINGLE AND HORIZONTAL DOUBLE">╧</td>
 * <td title="U+2568: BOX DRAWINGS UP DOUBLE AND HORIZONTAL SINGLE">╨</td>
 * <td title="U+2569: BOX DRAWINGS DOUBLE UP AND HORIZONTAL">╩</td>
 * <td title="U+256A: BOX DRAWINGS VERTICAL SINGLE AND HORIZONTAL DOUBLE">╪</td>
 * <td title="U+256B: BOX DRAWINGS VERTICAL DOUBLE AND HORIZONTAL SINGLE">╫</td>
 * <td title="U+256C: BOX DRAWINGS DOUBLE VERTICAL AND HORIZONTAL">╬</td>
 * <td title="U+256D: BOX DRAWINGS LIGHT ARC DOWN AND RIGHT">╭</td>
 * <td title="U+256E: BOX DRAWINGS LIGHT ARC DOWN AND LEFT">╮</td>
 * <td title="U+256F: BOX DRAWINGS LIGHT ARC UP AND LEFT">╯</td>
 * </tr>
 * <tr>
 * <td>U+257x</td>
 * <td title="U+2570: BOX DRAWINGS LIGHT ARC UP AND RIGHT">╰</td>
 * <td title="U+2571: BOX DRAWINGS LIGHT DIAGONAL UPPER RIGHT TO LOWER LEFT">╱
 * </td>
 * <td title="U+2572: BOX DRAWINGS LIGHT DIAGONAL UPPER LEFT TO LOWER RIGHT">╲
 * </td>
 * <td title="U+2573: BOX DRAWINGS LIGHT DIAGONAL CROSS">╳</td>
 * <td title="U+2574: BOX DRAWINGS LIGHT LEFT">╴</td>
 * <td title="U+2575: BOX DRAWINGS LIGHT UP">╵</td>
 * <td title="U+2576: BOX DRAWINGS LIGHT RIGHT">╶</td>
 * <td title="U+2577: BOX DRAWINGS LIGHT DOWN">╷</td>
 * <td title="U+2578: BOX DRAWINGS HEAVY LEFT">╸</td>
 * <td title="U+2579: BOX DRAWINGS HEAVY UP">╹</td>
 * <td title="U+257A: BOX DRAWINGS HEAVY RIGHT">╺</td>
 * <td title="U+257B: BOX DRAWINGS HEAVY DOWN">╻</td>
 * <td title="U+257C: BOX DRAWINGS LIGHT LEFT AND HEAVY RIGHT">╼</td>
 * <td title="U+257D: BOX DRAWINGS LIGHT UP AND HEAVY DOWN">╽</td>
 * <td title="U+257E: BOX DRAWINGS HEAVY LEFT AND LIGHT RIGHT">╾</td>
 * <td title="U+257F: BOX DRAWINGS HEAVY UP AND LIGHT DOWN">╿</td>
 * </tr>
 * </tbody>
 * </table>
 */
public class BoxDrawing {

  /**
   * Line weights
   */
  @SuppressWarnings("javadoc")
  public enum Weight {
    LIGHT,
    HEAVY,
    DOUBLE,
    ASCII
  }

  /**
   * Line characters
   */
  @SuppressWarnings("javadoc")
  public enum Line {
    LIGHT_SOLID(Weight.LIGHT, '─', '│'),
    LIGHT_DOUBLE(Weight.LIGHT, '╌', '╎'),
    LIGHT_TRIPLE(Weight.LIGHT, '┄', '┆'),
    LIGHT_QUAD(Weight.LIGHT, '┈', '┊'),
    HEAVY_SOLID(Weight.HEAVY, '━', '┃'),
    HEAVY_DOUBLE(Weight.HEAVY, '╍', '╏'),
    HEAVY_TRIPLE(Weight.HEAVY, '┅', '┇'),
    HEAVY_QUAD(Weight.HEAVY, '┉', '┋'),
    DOUBLE(Weight.DOUBLE, '═', '║'),
    ASCII(Weight.ASCII, '-', '|'),
    ;

    public final Weight weight;
    public final char horizontal;
    public final char vertical;

    Line(Weight w, char h, char v) {
      weight = w;
      horizontal = h;
      vertical = v;
    }
  }

  /**
   * Line junction characters. Names are in <code>up_down_left_right</code> order
   */
  @SuppressWarnings("javadoc")
  public enum Join {
    N_N_L_N(null, null, Weight.LIGHT, null, '╴'),
    L_N_N_N(Weight.LIGHT, null, null, null, '╵'),
    N_N_N_L(null, null, null, Weight.LIGHT, '╶'),
    N_L_N_N(null, Weight.LIGHT, null, null, '╷'),

    N_N_H_N(null, null, Weight.HEAVY, null, '╸'),
    H_N_N_N(Weight.HEAVY, null, null, null, '╹'),
    N_N_N_H(null, null, null, Weight.HEAVY, '╺'),
    N_H_N_N(null, Weight.HEAVY, null, null, '╻'),

    N_N_L_H(null, null, Weight.LIGHT, null, '╼'),
    L_H_N_N(Weight.LIGHT, null, null, null, '╽'),
    N_N_H_L(null, null, null, Weight.LIGHT, '╾'),
    H_L_N_N(null, Weight.LIGHT, null, null, '╿'),

    N_N_L_L_1(null, null, Weight.LIGHT, Weight.LIGHT, '─'),
    L_L_N_N_1(Weight.LIGHT, Weight.LIGHT, null, null, '│'),
    N_N_L_L_2(null, null, Weight.LIGHT, Weight.LIGHT, '╌'),
    L_L_N_N_2(Weight.LIGHT, Weight.LIGHT, null, null, '╎'),
    N_N_L_L_3(null, null, Weight.LIGHT, Weight.LIGHT, '┄'),
    L_L_N_N_3(Weight.LIGHT, Weight.LIGHT, null, null, '┆'),
    N_N_L_L_4(null, null, Weight.LIGHT, Weight.LIGHT, '┈'),
    L_L_N_N_4(Weight.LIGHT, Weight.LIGHT, null, null, '┊'),

    N_N_H_H_1(null, null, Weight.HEAVY, Weight.HEAVY, '━'),
    H_H_N_N_1(Weight.HEAVY, Weight.HEAVY, null, null, '┃'),
    N_N_H_H_2(null, null, Weight.HEAVY, Weight.HEAVY, '╍'),
    H_H_N_N_2(Weight.HEAVY, Weight.HEAVY, null, null, '╏'),
    N_N_H_H_3(null, null, Weight.HEAVY, Weight.HEAVY, '┅'),
    H_H_N_N_3(Weight.HEAVY, Weight.HEAVY, null, null, '┇'),
    N_N_H_H_4(null, null, Weight.HEAVY, Weight.HEAVY, '┉'),
    H_H_N_N_4(Weight.HEAVY, Weight.HEAVY, null, null, '┋'),

    N_N_D_D(null, null, Weight.DOUBLE, Weight.DOUBLE, '═'),
    D_D_N_N(Weight.DOUBLE, Weight.DOUBLE, null, null, '║'),

    N_L_N_L(null, Weight.LIGHT, null, Weight.LIGHT, '┌'),
    N_L_N_H(null, Weight.LIGHT, null, Weight.HEAVY, '┍'),
    N_H_N_L(null, Weight.HEAVY, null, Weight.LIGHT, '┎'),
    N_H_N_H(null, Weight.HEAVY, null, Weight.HEAVY, '┏'),

    N_L_L_N(null, Weight.LIGHT, Weight.LIGHT, null, '┐'),
    N_L_H_N(null, Weight.LIGHT, Weight.HEAVY, null, '┑'),
    N_H_L_N(null, Weight.HEAVY, Weight.LIGHT, null, '┒'),
    N_H_H_N(null, Weight.HEAVY, Weight.HEAVY, null, '┓'),

    L_N_N_L(Weight.LIGHT, null, null, Weight.LIGHT, '└'),
    L_N_N_H(Weight.LIGHT, null, null, Weight.HEAVY, '┕'),
    H_N_N_L(Weight.HEAVY, null, null, Weight.LIGHT, '┖'),
    H_N_N_H(Weight.HEAVY, null, null, Weight.HEAVY, '┗'),

    L_N_L_N(Weight.LIGHT, null, Weight.LIGHT, null, '┘'),
    L_N_H_N(Weight.LIGHT, null, Weight.HEAVY, null, '┙'),
    H_N_L_N(Weight.HEAVY, null, Weight.LIGHT, null, '┚'),
    H_N_H_N(Weight.HEAVY, null, Weight.HEAVY, null, '┛'),

    L_L_N_L(Weight.LIGHT, Weight.LIGHT, null, Weight.LIGHT, '├'),
    L_L_N_H(Weight.LIGHT, Weight.LIGHT, null, Weight.HEAVY, '┝'),
    H_L_N_L(Weight.HEAVY, Weight.LIGHT, null, Weight.LIGHT, '┞'),
    L_H_N_L(Weight.LIGHT, Weight.HEAVY, null, Weight.LIGHT, '┟'),
    H_H_N_L(Weight.HEAVY, Weight.HEAVY, null, Weight.LIGHT, '┠'),
    H_L_N_H(Weight.HEAVY, Weight.LIGHT, null, Weight.HEAVY, '┡'),
    L_H_N_H(Weight.LIGHT, Weight.HEAVY, null, Weight.HEAVY, '┢'),
    H_H_N_H(Weight.HEAVY, Weight.HEAVY, null, Weight.HEAVY, '┣'),

    L_L_L_N(Weight.LIGHT, Weight.LIGHT, Weight.LIGHT, null, '┤'),
    L_L_H_N(Weight.LIGHT, Weight.LIGHT, Weight.HEAVY, null, '┥'),
    H_L_L_N(Weight.HEAVY, Weight.LIGHT, Weight.LIGHT, null, '┦'),
    L_H_L_N(Weight.LIGHT, Weight.HEAVY, Weight.LIGHT, null, '┧'),
    H_H_L_N(Weight.HEAVY, Weight.HEAVY, Weight.LIGHT, null, '┨'),
    H_L_H_N(Weight.HEAVY, Weight.LIGHT, Weight.HEAVY, null, '┩'),
    L_H_H_N(Weight.LIGHT, Weight.HEAVY, Weight.HEAVY, null, '┪'),
    H_H_H_N(Weight.HEAVY, Weight.HEAVY, Weight.HEAVY, null, '┫'),

    N_L_L_L(null, Weight.LIGHT, Weight.LIGHT, Weight.LIGHT, '┬'),
    N_L_H_L(null, Weight.LIGHT, Weight.HEAVY, Weight.LIGHT, '┭'),
    N_L_L_H(null, Weight.LIGHT, Weight.LIGHT, Weight.HEAVY, '┮'),
    N_L_H_H(null, Weight.LIGHT, Weight.HEAVY, Weight.HEAVY, '┯'),
    N_H_L_L(null, Weight.HEAVY, Weight.LIGHT, Weight.LIGHT, '┰'),
    N_H_H_L(null, Weight.HEAVY, Weight.HEAVY, Weight.LIGHT, '┱'),
    N_H_L_H(null, Weight.HEAVY, Weight.LIGHT, Weight.HEAVY, '┲'),
    N_H_H_H(null, Weight.HEAVY, Weight.HEAVY, Weight.HEAVY, '┳'),

    L_N_L_L(Weight.LIGHT, null, Weight.LIGHT, Weight.LIGHT, '┴'),
    L_N_H_L(Weight.LIGHT, null, Weight.HEAVY, Weight.LIGHT, '┵'),
    L_N_L_H(Weight.LIGHT, null, Weight.LIGHT, Weight.HEAVY, '┶'),
    L_N_H_H(Weight.LIGHT, null, Weight.HEAVY, Weight.HEAVY, '┷'),
    H_N_L_L(Weight.HEAVY, null, Weight.LIGHT, Weight.LIGHT, '┸'),
    H_N_H_L(Weight.HEAVY, null, Weight.HEAVY, Weight.LIGHT, '┹'),
    H_N_L_H(Weight.HEAVY, null, Weight.LIGHT, Weight.HEAVY, '┺'),
    H_N_H_H(Weight.HEAVY, null, Weight.HEAVY, Weight.HEAVY, '┻'),

    L_L_L_L(Weight.LIGHT, Weight.LIGHT, Weight.LIGHT, Weight.LIGHT, '┼'),
    L_L_H_L(Weight.LIGHT, Weight.LIGHT, Weight.HEAVY, Weight.LIGHT, '┽'),
    L_L_L_H(Weight.LIGHT, Weight.LIGHT, Weight.LIGHT, Weight.HEAVY, '┾'),
    L_L_H_H(Weight.LIGHT, Weight.LIGHT, Weight.HEAVY, Weight.HEAVY, '┿'),

    H_L_L_L(Weight.HEAVY, Weight.LIGHT, Weight.LIGHT, Weight.LIGHT, '╀'),
    L_H_L_L(Weight.LIGHT, Weight.HEAVY, Weight.LIGHT, Weight.LIGHT, '╁'),
    H_H_L_L(Weight.HEAVY, Weight.HEAVY, Weight.LIGHT, Weight.LIGHT, '╂'),

    H_L_H_L(Weight.HEAVY, Weight.LIGHT, Weight.HEAVY, Weight.LIGHT, '╃'),
    H_L_L_H(Weight.HEAVY, Weight.LIGHT, Weight.LIGHT, Weight.HEAVY, '╄'),
    L_H_H_L(Weight.LIGHT, Weight.HEAVY, Weight.HEAVY, Weight.LIGHT, '╅'),
    L_H_L_H(Weight.LIGHT, Weight.HEAVY, Weight.LIGHT, Weight.HEAVY, '╆'),

    H_L_H_H(Weight.HEAVY, Weight.LIGHT, Weight.HEAVY, Weight.HEAVY, '╇'),
    L_H_H_H(Weight.LIGHT, Weight.HEAVY, Weight.HEAVY, Weight.HEAVY, '╈'),
    H_H_H_L(Weight.HEAVY, Weight.HEAVY, Weight.HEAVY, Weight.LIGHT, '╉'),
    H_H_L_H(Weight.HEAVY, Weight.HEAVY, Weight.LIGHT, Weight.HEAVY, '╊'),

    H_H_H_H(Weight.HEAVY, Weight.HEAVY, Weight.HEAVY, Weight.HEAVY, '╋'),

    N_L_N_D(null, Weight.LIGHT, null, Weight.DOUBLE, '╒'),
    N_D_N_L(null, Weight.DOUBLE, null, Weight.LIGHT, '╓'),
    N_D_N_D(null, Weight.DOUBLE, null, Weight.DOUBLE, '╔'),

    N_L_D_N(null, Weight.LIGHT, Weight.DOUBLE, null, '╕'),
    N_D_L_N(null, Weight.DOUBLE, Weight.LIGHT, null, '╖'),
    N_D_D_N(null, Weight.DOUBLE, Weight.DOUBLE, null, '╗'),

    L_N_N_D(Weight.LIGHT, null, null, Weight.DOUBLE, '╘'),
    D_N_N_L(Weight.DOUBLE, null, null, Weight.LIGHT, '╙'),
    D_N_N_D(Weight.DOUBLE, null, null, Weight.DOUBLE, '╚'),

    L_N_D_N(Weight.LIGHT, null, Weight.DOUBLE, null, '╛'),
    D_N_L_N(Weight.DOUBLE, null, Weight.LIGHT, null, '╜'),
    D_N_D_N(Weight.DOUBLE, null, Weight.DOUBLE, null, '╝'),

    L_L_N_D(Weight.LIGHT, Weight.LIGHT, null, Weight.DOUBLE, '╞'),
    D_D_N_L(Weight.DOUBLE, Weight.DOUBLE, null, Weight.LIGHT, '╟'),
    D_D_N_D(Weight.DOUBLE, Weight.DOUBLE, null, Weight.DOUBLE, '╠'),

    L_L_D_N(Weight.LIGHT, Weight.LIGHT, Weight.DOUBLE, null, '╡'),
    D_D_L_N(Weight.DOUBLE, Weight.DOUBLE, Weight.LIGHT, null, '╢'),
    D_D_D_N(Weight.DOUBLE, Weight.DOUBLE, Weight.DOUBLE, null, '╣'),

    N_L_D_D(null, Weight.LIGHT, Weight.DOUBLE, Weight.DOUBLE, '╤'),
    N_D_L_L(null, Weight.DOUBLE, Weight.LIGHT, Weight.LIGHT, '╥'),
    N_D_D_D(null, Weight.DOUBLE, Weight.DOUBLE, Weight.DOUBLE, '╦'),

    L_N_D_D(Weight.LIGHT, null, Weight.DOUBLE, Weight.DOUBLE, '╧'),
    D_N_L_L(Weight.DOUBLE, null, Weight.LIGHT, Weight.LIGHT, '╨'),
    D_N_D_D(Weight.DOUBLE, null, Weight.DOUBLE, Weight.DOUBLE, '╩'),

    L_L_D_D(Weight.LIGHT, Weight.LIGHT, Weight.DOUBLE, Weight.DOUBLE, '╪'),
    D_D_L_L(Weight.DOUBLE, Weight.DOUBLE, Weight.LIGHT, Weight.LIGHT, '╫'),
    D_D_D_D(Weight.DOUBLE, Weight.DOUBLE, Weight.DOUBLE, Weight.DOUBLE, '╬'),

    A_A_A_A(Weight.ASCII, Weight.ASCII, Weight.ASCII, Weight.ASCII, '+'),

    N_A_A_A(null, Weight.ASCII, Weight.ASCII, Weight.ASCII, '+'),
    A_N_A_A(Weight.ASCII, null, Weight.ASCII, Weight.ASCII, '+'),
    A_A_N_A(Weight.ASCII, Weight.ASCII, null, Weight.ASCII, '+'),
    A_A_A_N(Weight.ASCII, Weight.ASCII, Weight.ASCII, null, '+'),

    N_N_A_A(null, null, Weight.ASCII, Weight.ASCII, '-'),
    N_A_N_A(null, Weight.ASCII, null, Weight.ASCII, '+'),
    N_A_A_N(null, Weight.ASCII, Weight.ASCII, null, '+'),

    A_N_N_A(Weight.ASCII, null, null, Weight.ASCII, '+'),
    A_N_A_N(Weight.ASCII, null, Weight.ASCII, null, '+'),

    A_A_N_N(Weight.ASCII, Weight.ASCII, null, null, '|'),
    ;

    public final Weight up, down, left, right;
    public final char character;

    Join(Weight u, Weight d, Weight l, Weight r, char c) {
      left = l;
      right = r;
      up = u;
      down = d;
      character = c;
    }

    public static Join match(Weight up, Weight down, Weight left, Weight right) {
      for (Join join : values()) {
        if (join.up == up
            && join.down == down
            && join.left == left
            && join.right == right) {
          return join;
        }
      }
      return null;
    }

    public static Join heal(Join above, Join below, Join sinister, Join dexter) {
      return Join.match(
          above != null ? above.down : null,
          below != null ? below.up : null,
          sinister != null ? sinister.right : null,
          dexter != null ? dexter.left : null);
    }

    public static Join of(char c) {
      for (Join j : values()) {
        if (j.character == c) {
          return j;
        }
      }
      return null;
    }
  }
}
