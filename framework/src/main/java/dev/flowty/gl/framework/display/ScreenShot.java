package dev.flowty.gl.framework.display;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * Utility class to capture an image of the current framebuffer
 */
public class ScreenShot {

  /**
   * @param file The path to write the image to (in PNG format)
   */
  public static void capture(Path file) {
    try {
      // get the resolution
      int[] viewport = new int[4];
      GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
      int w = viewport[2];
      int h = viewport[3];

      // read the pixels
      ByteBuffer rgb = BufferUtils.createByteBuffer(w * h * 3);
      GL11.glReadPixels(0, 0, w, h, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, rgb);

      // convert to a java image
      int[] pixels = new int[w * h];
      for (int i = 0; i < pixels.length; i++) {
        byte r = rgb.get();
        byte g = rgb.get();
        byte b = rgb.get();

        pixels[i] = 0
            | r << 16 & 0xFF0000
            | g << 8 & 0x00FF00
            | b << 0 & 0x0000FF;
      }
      BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      image.setRGB(0, 0, w, h, pixels, 0, w);

      // flip y-axis
      AffineTransform at = AffineTransform.getScaleInstance(1, -1);
      at.translate(0, -h);
      image = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
          .filter(image, null);

      // save the image
      Files.createDirectories(file.getParent());
      ImageIO.write(image, "png", file.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
