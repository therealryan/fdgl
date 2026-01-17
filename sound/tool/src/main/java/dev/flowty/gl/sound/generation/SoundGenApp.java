package dev.flowty.gl.sound.generation;

import dev.flowty.gl.sound.generation.var.Lerp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * A gui that affords playing around with {@link TerrainSound}
 */
public class SoundGenApp extends JFrame {

  private static final long serialVersionUID = 1L;

  private final TerrainSound sound = new TerrainSound();

  private Clip clip;

  private final JButton play = new JButton("Play");

  private final JButton save = new JButton("Save");

  private final JButton load = new JButton("Load");

  private final JButton export = new JButton("Export");

  private final String[] buttonNames = {"Mutate", "Randomise", "Coin", "Laser",
      "Explosion", "Powerup", "Hit", "Jump"};

  private final JButton[] buttons = new JButton[buttonNames.length];

  private final JSpinner length = new JSpinner(new SpinnerNumberModel(1, 0.0001, 20, 0.1));

  private final JSpinner sSamples = new JSpinner(new SpinnerNumberModel(8, 1, 16, 1));

  private final TerrainPlot volumePlot = new TerrainPlot(sound.volume, 0, 1);

  private JComboBox<String> baseWaveChoice;

  private final TerrainPlot freqPlot = new TerrainPlot(sound.baseFrequency, 0, 2000);

  private JComboBox<String> vibWaveChoice;

  private final TerrainPlot vibFreqPlot = new TerrainPlot(sound.vibratoFrequency, 0, 100);

  private final TerrainPlot vibAmpPlot = new TerrainPlot(sound.vibratoAmplitude, 0, 500);

  private final TerrainPlot flangeDelayPlot = new TerrainPlot(sound.flangeDelay, 0, 0.02f);

  private final TerrainPlot flangeAlphaPlot = new TerrainPlot(sound.flangeAlpha, 0, 1);

  private final VariablePlot frequency = new VariablePlot(sound.getFrequency(), 0, 2500);

  private final VariablePlot[] plots = {volumePlot, freqPlot, vibFreqPlot,
      vibAmpPlot, flangeDelayPlot, flangeAlphaPlot, frequency};

  private final JFileChooser chooser;

  private final ChangeListener spinnerListener = e -> {
    if (e.getSource() == length) {
      sound.length = ((Number) length.getValue()).floatValue();

      for (VariablePlot vp : plots) {
        vp.refresh();
      }
    } else if (e.getSource() == sSamples) {
      sound.superSamples = ((Number) sSamples.getValue()).intValue();
    }
  };

  private final ActionListener buttonListener = e -> {
    String name = ((JButton) e.getSource()).getText();
    int index = -1;
    for (int i = 0; i < buttonNames.length; i++) {
      if (buttonNames[i].equals(name)) {
        index = i;
        break;
      }
    }

    switch (index) {
      case 0:
        sound.mutate();
        break;
      case 1:
        sound.randomise();
        break;
      case 2:
        sound.coin();
        break;
      case 3:
        sound.laser();
        break;
      case 4:
        sound.explosion();
        break;
      case 5:
        sound.powerup();
        break;
      case 6:
        sound.hit();
        break;
      case 7:
        sound.jump();
        break;
      default:
        assert false : index;
    }

    length.removeChangeListener(spinnerListener);
    length.setValue(Float.valueOf(sound.length));
    length.addChangeListener(spinnerListener);
    baseWaveChoice.setSelectedIndex(sound.wave.ordinal());
    vibWaveChoice.setSelectedIndex(sound.vibrato.ordinal());
    for (VariablePlot p : plots) {
      p.refresh();
    }

    play();
  };

  /**
   *
   */
  public SoundGenApp(Path workingDirectory) {
    super("SoundGen");
    chooser = new JFileChooser(workingDirectory.toFile());

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    try {
      clip = AudioSystem.getClip();
    } catch (LineUnavailableException e) {
      e.printStackTrace();
      System.exit(-1);
    }

    Box bp = new Box(BoxLayout.Y_AXIS);
    bp.add(play);

    String[] wc = new String[TerrainSound.WaveType.values().length];
    for (int i = 0; i < wc.length; i++) {
      wc[i] = TerrainSound.WaveType.values()[i].toString();
    }

    baseWaveChoice = new JComboBox<>(wc);
    baseWaveChoice.setSelectedIndex(sound.wave.ordinal());
    vibWaveChoice = new JComboBox<>(wc);
    vibWaveChoice.setSelectedIndex(sound.vibrato.ordinal());

    baseWaveChoice.addItemListener(
        e -> sound.wave = TerrainSound.WaveType.values()[baseWaveChoice.getSelectedIndex()]);

    vibWaveChoice.addItemListener(e -> {
      sound.vibrato = TerrainSound.WaveType.values()[vibWaveChoice.getSelectedIndex()];

      frequency.v = sound.getFrequency();
      frequency.refresh();
    });

    play.addActionListener(e -> play());

    save.addActionListener(e -> {
      chooser.setSelectedFile(new File("sound.gls"));
      int r = chooser.showSaveDialog(SoundGenApp.this);

      if (r == JFileChooser.APPROVE_OPTION) {
        try (OutputStream os = Files.newOutputStream(chooser.getSelectedFile().toPath())) {
          sound.write(os);
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    });

    load.addActionListener(e -> {
      chooser.setSelectedFile(new File("sound.ruglsg"));
      int r = chooser.showOpenDialog(SoundGenApp.this);

      if (r == JFileChooser.APPROVE_OPTION) {
        try (FileInputStream fis = new FileInputStream(chooser.getSelectedFile())) {
          sound.read(fis);

          for (VariablePlot vp : plots) {
            vp.refresh();
          }

          length.removeChangeListener(spinnerListener);
          length.setValue(Float.valueOf(sound.length));
          length.addChangeListener(spinnerListener);

          baseWaveChoice.setSelectedIndex(sound.wave.ordinal());
          vibWaveChoice.setSelectedIndex(sound.vibrato.ordinal());
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });

    export.addActionListener(e -> {
      chooser.setSelectedFile(new File("sound.wav"));
      int r = chooser.showSaveDialog(SoundGenApp.this);

      if (r == JFileChooser.APPROVE_OPTION) {
        try {
          WavUtil.saveAsWav(44100, sound.generate(44100), chooser
              .getSelectedFile().getAbsolutePath());
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });

    length.addChangeListener(spinnerListener);
    sSamples.addChangeListener(spinnerListener);

    Box pBox = new Box(BoxLayout.Y_AXIS);
    pBox.add(wrap(volumePlot, "Volume"));
    pBox.add(wrap(freqPlot, "Base frequency"));
    pBox.add(wrap(vibFreqPlot, "Vibrato frequency"));
    pBox.add(wrap(vibAmpPlot, "Vibrato depth"));
    pBox.add(wrap(frequency, "Output frequency"));
    pBox.add(wrap(flangeDelayPlot, "Flange delay"));
    pBox.add(wrap(flangeAlphaPlot, "Flange alpha"));

    Box nePanel = new Box(BoxLayout.Y_AXIS);
    nePanel.add(wrap(baseWaveChoice, "Signal"));
    nePanel.add(wrap(vibWaveChoice, "Vibrato"));
    nePanel.add(wrap(length, "Length"));
    // nePanel.add( wrap( sSamples, "Samples" ) );

    Box ecPanel = new Box(BoxLayout.Y_AXIS);
    ecPanel.add(wrap(play, null));

    Box sePanel = new Box(BoxLayout.Y_AXIS);

    for (int i = 0; i < buttonNames.length; i++) {
      buttons[i] = new JButton(buttonNames[i]);
      buttons[i].addActionListener(buttonListener);
      sePanel.add(wrap(buttons[i], null));
    }

    sePanel.add(wrap(save, null));
    sePanel.add(wrap(load, null));
    sePanel.add(wrap(export, null));

    JPanel east = new JPanel(new BorderLayout());
    east.add(nePanel, BorderLayout.NORTH);
    east.add(ecPanel, BorderLayout.CENTER);
    east.add(sePanel, BorderLayout.SOUTH);

    getContentPane().add(pBox, BorderLayout.CENTER);
    getContentPane().add(east, BorderLayout.EAST);

    pack();
  }

  private static Component wrap(Component c, String title) {
    JPanel p = new JPanel(new BorderLayout());
    p.setBorder(new TitledBorder(title));
    p.add(c, BorderLayout.CENTER);
    return p;
  }

  private void play() {
    if (clip.isOpen()) {
      clip.close();
    }

    ByteBuffer pcm = sound.generate(44100);

    try (AudioInputStream ais = WavUtil.getAudioStream(44100, pcm)) {
      clip.open(ais);
      clip.start();
    } catch (LineUnavailableException | IOException e) {
      e.printStackTrace();
    }
  }

  private class VariablePlot extends JPanel {

    private static final long serialVersionUID = 1L;

    private Variable v;

    /**
     * min value
     */
    protected float min;

    /**
     * max value
     */
    protected float max;

    /**
     * set to true to refresh
     */
    protected boolean pointsDirty = true;

    private int[] xpoints = {}, ypoints = {};

    /**
     *
     */
    protected int x = 0;

    /**
     *
     */
    protected int y = 0;

    /**
     *
     */
    protected float[] temp = new float[2];

    /**
     *
     */
    protected boolean mouseOver = false;

    private VariablePlot(Variable v, float min, float max) {
      this.v = v;
      this.min = min;
      this.max = max;

      addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          refresh();
        }
      });

      addMouseMotionListener(new MouseMotionListener() {
        @Override
        public void mouseMoved(MouseEvent e) {
          x = e.getX();
          y = e.getY();

          repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
          x = e.getX();
          y = e.getY();

          repaint();
        }
      });

      addMouseWheelListener(e -> {
        float m = e.getWheelRotation() > 0 ? 0.8f : 1.2f;

        m = (float) Math.pow(m, Math.abs(e.getWheelRotation()));

        VariablePlot.this.max *= m;

        if (m != 1) {
          refresh();
        }

        repaint();
      });

      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseExited(MouseEvent e) {
          mouseOver = false;
          repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          mouseOver = true;
          repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.isShiftDown()) {
            // set the min and max
            String s = (String) JOptionPane.showInputDialog(SoundGenApp.this,
                "Specify bounds e.eg. \"0 100\"", "Edit bounds",
                JOptionPane.QUESTION_MESSAGE, null, null,
                VariablePlot.this.min + " " + VariablePlot.this.max);

            if (s != null) {
              String[] mm = s.split("\\s");

              try {
                VariablePlot.this.min = Float.parseFloat(mm[0]);
                VariablePlot.this.max = Float.parseFloat(mm[1]);

                refresh();
              } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
              }
            }
          }
        }
      });
    }

    private void recreatePoints() {
      if (pointsDirty) {
        xpoints = new int[getWidth()];
        ypoints = new int[xpoints.length];

        float ts = sound.length / getWidth();

        float[] f = new float[2];

        for (int i = 0; i < xpoints.length; i++) {
          float time = ts * i;
          float value = v.getValue(time);

          f[0] = time;
          f[1] = value;

          toPixels(f);

          xpoints[i] = (int) f[0];
          ypoints[i] = (int) f[1];
        }

        pointsDirty = false;
      }
    }

    /**
     *
     */
    protected void refresh() {
      pointsDirty = true;
      repaint();
    }

    /**
     * @param coords The value coordinate to convert
     */
    protected void toPixels(float[] coords) {
      float pixelsPerSecond = getWidth() / sound.length;
      float pixelsperValue = getHeight() / (max - min);

      coords[0] = coords[0] * pixelsPerSecond;
      coords[1] = Math.round(getHeight() - (coords[1] - min) * pixelsperValue);
    }

    /**
     * @param coords The pixel coordinates to convert
     */
    protected void toValues(float[] coords) {
      coords[0] = Math.max(0, coords[0]);
      coords[0] = Math.min(getWidth(), coords[0]);
      coords[1] = Math.max(0, coords[1]);
      coords[1] = Math.min(getHeight(), coords[1]);

      float secondsPerPixel = sound.length / getWidth();
      float valuesPerPixel = (max - min) / getHeight();

      coords[0] = coords[0] * secondsPerPixel;
      coords[1] = min + (getHeight() - coords[1]) * valuesPerPixel;
    }

    @Override
    public void paintComponent(Graphics gpoo) {
      recreatePoints();

      Graphics2D g = (Graphics2D) gpoo.create();

      Optional.ofNullable(Toolkit.getDefaultToolkit())
          .map(tk -> tk.getDesktopProperty("awt.font.desktophints"))
          .ifPresent(dh -> g.setRenderingHints((Map<?, ?>) dh));
//			g.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION,
//					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
//			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
//					RenderingHints.VALUE_ANTIALIAS_ON );
//			g.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,
//					RenderingHints.VALUE_COLOR_RENDER_QUALITY );
//			g.setRenderingHint( RenderingHints.KEY_DITHERING,
//					RenderingHints.VALUE_DITHER_ENABLE );
//			g.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS,
//					RenderingHints.VALUE_FRACTIONALMETRICS_ON );
//			g.setRenderingHint( RenderingHints.KEY_INTERPOLATION,
//					RenderingHints.VALUE_INTERPOLATION_BILINEAR );
//			g.setRenderingHint( RenderingHints.KEY_RENDERING,
//					RenderingHints.VALUE_RENDER_QUALITY );
//			g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,
//					RenderingHints.VALUE_STROKE_PURE );

      g.setColor(Color.BLACK);
      g.fillRect(0, 0, getWidth(), getHeight());

      // draw plot
      if (xpoints.length >= 2) {
        g.setColor(Color.WHITE);
        for (int i = 1; i < xpoints.length; i++) {
          g.drawLine(xpoints[i - 1], ypoints[i - 1], xpoints[i], ypoints[i]);
        }
      }

      // draw labels
      g.setColor(Color.GRAY);
      Font f = g.getFont();
      f = f.deriveFont(10.0f);
      g.setFont(f);

      g.drawString("" + max, 1, f.getSize() + 1);
      g.drawString("" + min, 1, getHeight() - 2);

      if (mouseOver) {
        temp[0] = x;
        temp[1] = y;
        toValues(temp);

        g.drawString(temp[0] + ", " + temp[1], x, y);
      }

      g.dispose();
    }
  }

  private class TerrainPlot extends VariablePlot {

    private static final long serialVersionUID = 1L;

    private final Lerp t;

    private float[] dragging = null;

    private final MouseAdapter ma = new MouseAdapter() {

      @Override
      public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if (e.getButton() == MouseEvent.BUTTON1) {
          dragging = findClosest(x, y, 25);
          mouseDragged(e);
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if (dragging != null) {
          if (e.getButton() == MouseEvent.BUTTON1) {
            dragging = null;
          }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          float[] closest = findClosest(x, y, -1);

          if (closest != null) {
            t.removePoint(closest[0], closest[1]);
          } else {
            assert t.points.isEmpty();
          }

          repaint();
        } else if (e.getButton() == MouseEvent.BUTTON1) {
          temp[0] = x;
          temp[1] = y;

          toValues(temp);

          t.addPoint(temp[0], temp[1]);
        }

        TerrainPlot.this.refresh();

        if (TerrainPlot.this == freqPlot || TerrainPlot.this == vibFreqPlot
            || TerrainPlot.this == vibAmpPlot) {
          frequency.refresh();
        }
      }

      @Override
      public void mouseDragged(MouseEvent arg0) {
        x = arg0.getX();
        y = arg0.getY();

        if (dragging != null) {
          temp[0] = x;
          temp[1] = y;

          toValues(temp);

          dragging[0] = temp[0];
          dragging[1] = temp[1];

          t.orderDirty = true;

          TerrainPlot.this.refresh();

          if (TerrainPlot.this == freqPlot || TerrainPlot.this == vibFreqPlot
              || TerrainPlot.this == vibAmpPlot) {
            frequency.refresh();
          }
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        repaint();
      }

      @Override
      public void mouseExited(MouseEvent e) {
        x = -100;
        y = -100;
        repaint();
      }
    };

    private TerrainPlot(Lerp t, float min, float max) {
      super(t, min, max);
      this.t = t;

      addMouseListener(ma);
      addMouseMotionListener(ma);
    }

    @Override
    public void paintComponent(Graphics gpoo) {
      super.paintComponent(gpoo);

      Graphics2D g = (Graphics2D) gpoo;

      for (float[] v : t.points) {
        g.setColor(Color.WHITE);

        temp[0] = v[0];
        temp[1] = v[1];

        toPixels(temp);

        g.drawRect((int) (temp[0] - 4), (int) (temp[1] - 4), 8, 8);
      }

      if (mouseOver) {
        float[] c = findClosest(x, y, 25);

        if (c != null) {
          temp[0] = c[0];
          temp[1] = c[1];

          toPixels(temp);

          g.fillRect((int) (temp[0] - 4), (int) (temp[1] - 4), 8, 8);
          g.drawLine(x, y, (int) temp[0], (int) temp[1]);
        }
      }
    }

    private float[] findClosest(int px, int py, float selectDistance) {
      float minDelta;

      if (selectDistance == -1) {
        minDelta = Float.MAX_VALUE;
      } else {
        minDelta = selectDistance * selectDistance;
      }

      float[] closest = null;

      for (float[] p : t.points) {
        temp[0] = p[0];
        temp[1] = p[1];
        toPixels(temp);

        float dx = px - temp[0];
        float dy = py - temp[1];

        float d = dx * dx + dy * dy;

        if (d < minDelta) {
          minDelta = d;
          closest = p;
        }
      }

      return closest;
    }
  }

  /**
   * @param args from the command line
   */
  public static void main(String[] args) {
    CommandLineArgs cla = new CommandLineArgs(args);

    if (!cla.helpGiven()) {
      SoundGenApp sgt = new SoundGenApp(cla.path());
      sgt.setSize(800, 600);
      sgt.setVisible(true);
    }
  }

  @Command(name = "soundgen", description = "Simple sound generation")
  private static class CommandLineArgs {

    @Option(names = {"-h", "--help"},
        description = "Prints help and exits")
    private boolean help;

    @Option(names = {"-p", "--path"}, description = "working directory")
    private String path = ".";

    /**
     * @param args from the commandline
     */
    CommandLineArgs(String... args) {
      new CommandLine(this).parseArgs(args);
    }

    /**
     * If help has been requested, print it to stdout
     *
     * @return <code>true</code> if help was given
     */
    public boolean helpGiven() {
      if (help) {
        CommandLine.usage(this, System.out);
      }
      return help;
    }

    /**
     * @return The path to the working directory
     */
    public Path path() {
      return Paths.get(path);
    }
  }
}
