package dev.flowty.gl.doc;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Util {

  /**
   * Regenerates a section of a file and throws a failed comparison test if the file was altered by
   * that act.
   *
   * @param path    The file to operate on
   * @param start   The line at which to insert the content
   * @param content How to mutate the existing content of the section
   * @param end     The line at which the content ends
   * @return {@code true} if the file was changed by the insert
   * @throws Exception IO failure
   */
  static boolean insert(Path path, String start, UnaryOperator<String> content, String end) {
    try {
      String existing = "";
      if (Files.exists(path)) {
        existing = new String(Files.readAllBytes(path), UTF_8);
      }
      List<String> regenerated = new ArrayList<>();

      Iterator<String> exi = Arrays.asList(existing.split("\n", -1)).iterator();
      while (exi.hasNext()) {
        boolean found = false;
        while (exi.hasNext() && !found) {
          String line = exi.next();
          if (line.trim().equals(start)) {
            found = true;
            break;
          }
          regenerated.add(line);
        }
        if (found) {
          StringBuilder existingContent = new StringBuilder();
          boolean endFound = false;
          while (exi.hasNext() && !endFound) {
            String line = exi.next();
            if (line.trim().equals(end)) {
              endFound = true;
            } else {
              existingContent.append(line).append("\n");
            }
          }

          // add the new content
          regenerated.add(start);
          regenerated.add("");
          regenerated.add(content.apply(existingContent.toString().trim()));
          regenerated.add("");
          regenerated.add(end);
        }
      }

      // write the file
      String newContent = regenerated.stream().collect(Collectors.joining("\n"));
      Files.write(path, newContent.getBytes(UTF_8));

      return !existing.equals(newContent);
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }
}
