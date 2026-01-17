package dev.flowty.gl.shader.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * Allows the comparison of two images
 */
public class ImageDiff {

  /**
   * Compares two images
   *
   * @param expected The expected image
   * @param actual   The actual image
   */
  public static void compare(Path expected, Path actual) {
    try {
      Files.createDirectories(expected.getParent());
      BufferedImage exp = ImageIO.read(expected.toFile());

      Files.createDirectories(actual.getParent());
      BufferedImage act = ImageIO.read(actual.toFile());
      assertEquals(
          exp.getWidth(),
          act.getWidth(),
          "width of " + actual);
      assertEquals(
          exp.getHeight(),
          act.getHeight(),
          "height of " + actual);
      for (int x = 0; x < exp.getWidth(); x++) {
        for (int y = 0; y < exp.getHeight(); y++) {
          if (exp.getRGB(x, y) != act.getRGB(x, y)) {
            generateComparison(expected, exp, actual, act);
            assertEquals(
                Integer.toHexString(
                    exp.getRGB(x, y)),
                Integer.toHexString(
                    act.getRGB(x, y)),
                "pixel " + x + "," + y + " differs between \n"
                    + expected + "\n" + actual);
          }
        }
      }
    } catch (IOException e) {
      throw new AssertionError("Failed to compare images\n"
          + expected + "\n" + actual,
          e);
    }
  }

  private static void generateComparison(
      Path expected, BufferedImage expImage,
      Path actual, BufferedImage actImage) {
    try {
      String css = """
          
          		    * {box-sizing: border-box;}
          		    .img-comp-container {
          		      position: relative;
          		      height: 200px; /*should be the same height as the images*/
          		    }
          		    .img-comp-img {
          		      position: absolute;
          		      width: auto;
          		      height: auto;
          		      overflow: hidden;
          		      background-image: linear-gradient(45deg, #808080 25%, transparent 25%), linear-gradient(-45deg, #808080 25%, transparent 25%), linear-gradient(45deg, transparent 75%, #808080 75%), linear-gradient(-45deg, transparent 75%, #808080 75%);
                    background-size: 20px 20px;
                    background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
          		    }
          		    .img-comp-img img {
          		      display: block;
          		      vertical-align: middle;
          		    }
          		    .img-comp-slider {
          		      position: absolute;
          		      z-index: 9;
          		      cursor: ew-resize;
          		      /*set the appearance of the slider:*/
          		      width: 40px;
          		      height: 40px;
          		      background-color: #2196F3;
          		      opacity: 0.7;
          		      border-radius: 50%;
          		    }
          """;
      String js = """
          
          		    function initComparisons() {
          		      var x, i;
          		      /* Find all elements with an "overlay" class: */
          		      x = document.getElementsByClassName("img-comp-overlay");
          		      for (i = 0; i < x.length; i++) {
          		        /* Once for each "overlay" element:
          		        pass the "overlay" element as a parameter when executing the compareImages function: */
          		        compareImages(x[i]);
          		      }
          		      function compareImages(img) {
          		        var slider, img, clicked = 0, w, h;
          		        /* Get the width and height of the img element */
          		        w = img.offsetWidth;
          		        h = img.offsetHeight;
          		        /* Set the width of the img element to 50%: */
          		        img.style.width = (w / 2) + "px";
          		        /* Create slider: */
          		        slider = document.createElement("DIV");
          		        slider.setAttribute("class", "img-comp-slider");
          		        /* Insert slider */
          		        img.parentElement.insertBefore(slider, img);
          		        /* Position the slider in the middle: */
          		        slider.style.top = (h / 2) - (slider.offsetHeight / 2) + "px";
          		        slider.style.left = (w / 2) - (slider.offsetWidth / 2) + "px";
          		        /* Execute a function when the mouse button is pressed: */
          		        slider.addEventListener("mousedown", slideReady);
          		        /* And another function when the mouse button is released: */
          		        window.addEventListener("mouseup", slideFinish);
          		        /* Or touched (for touch screens: */
          		        slider.addEventListener("touchstart", slideReady);
          		         /* And released (for touch screens: */
          		        window.addEventListener("touchend", slideFinish);
          		        function slideReady(e) {
          		          /* Prevent any other actions that may occur when moving over the image: */
          		          e.preventDefault();
          		          /* The slider is now clicked and ready to move: */
          		          clicked = 1;
          		          /* Execute a function when the slider is moved: */
          		          window.addEventListener("mousemove", slideMove);
          		          window.addEventListener("touchmove", slideMove);
          		        }
          		        function slideFinish() {
          		          /* The slider is no longer clicked: */
          		          clicked = 0;
          		        }
          		        function slideMove(e) {
          		          var pos;
          		          /* If the slider is no longer clicked, exit this function: */
          		          if (clicked == 0) return false;
          		          /* Get the cursor's x position: */
          		          pos = getCursorPos(e)
          		          /* Prevent the slider from being positioned outside the image: */
          		          if (pos < 0) pos = 0;
          		          if (pos > w) pos = w;
          		          /* Execute a function that will resize the overlay image according to the cursor: */
          		          slide(pos);
          		        }
          		        function getCursorPos(e) {
          		          var a, x = 0;
          		          e = (e.changedTouches) ? e.changedTouches[0] : e;
          		          /* Get the x positions of the image: */
          		          a = img.getBoundingClientRect();
          		          /* Calculate the cursor's x coordinate, relative to the image: */
          		          x = e.pageX - a.left;
          		          /* Consider any page scrolling: */
          		          x = x - window.pageXOffset;
          		          return x;
          		        }
          		        function slide(x) {
          		          /* Resize the image: */
          		          img.style.width = x + "px";
          		          /* Position the slider: */
          		          slider.style.left = img.offsetWidth - (slider.offsetWidth / 2) + "px";
          		        }
          		      }
          		    }
          """;
      String html = String.format(
          """
              <!DOCTYPE html>
              <html lang="en">
                <head>
                  <title>%s</title>
                  <style>%s    </style>
                  <script>%s    </script>
                </head>
                <body onload="initComparisons()">
                  <details>
                    <summary>Expected</summary>
                    <img src="%s" width="%s" height="%s">
                  </details>
                  <details>
                    <summary>Actual</summary>
                    <img src="%s" width="%s" height="%s">
                  </details>
                  <details open>
                    <summary>Comparison</summary>
                    <div class="img-comp-container">
                      <div class="img-comp-img">
                        <img src="%s" width="%s" height="%s">
                      </div>
                      <div class="img-comp-img img-comp-overlay">
                        <img src="%s" width="%s" height="%s">
                      </div>
                    </div>
                  </details>
                </body>
              </html>""",
          actual, css, js,
          expected.toAbsolutePath(), expImage.getWidth(), expImage.getHeight(),
          actual.toAbsolutePath(), actImage.getWidth(), actImage.getHeight(),
          actual.toAbsolutePath(), actImage.getWidth(), actImage.getHeight(),
          expected.toAbsolutePath(), expImage.getWidth(), expImage.getHeight());
      Path path = actual.getParent()
          .resolve(actual.getFileName().toString().replace(".png", ".html"));
      Files.writeString(path, html);

      if (Desktop.isDesktopSupported()) {
        if (Desktop.getDesktop().isSupported(Action.BROWSE)) {
          Desktop.getDesktop().browse(path.toUri());
        } else {
          new ProcessBuilder("xdg-open", path.toUri().toString()).start();
        }
      }
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }
}
