package dev.flowty.gl.doc;

import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

/**
 * Ensures that module readme headers are accurate
 */
public class ReadmeInterlink {

  private static final String TITLE_START = "<!-- title start -->";
  private static final String TITLE_END = "<!-- title end -->";

  private final String rootUrl;

  public ReadmeInterlink(String rootUrl) {
    this.rootUrl = rootUrl;
  }

  public boolean regenerate(PomData pom) {
    return Util.insert(pom.dirPath().resolve("README.md"),
        TITLE_START,
        existing -> compliant(existing, pom) ? existing : title(pom),
        TITLE_END);
  }

  private boolean compliant(String section, PomData pom) {
    return section.contains(pom.name())
        && section.contains(pom.description())
        && section.contains(parentLink(pom))
        && section.contains(javadocBadge(pom))
        && childLinks(pom)
        .map(String::trim)
        .allMatch(section::contains);
  }

  private String parentLink(PomData pom) {
    if (pom.parent() == null) {
      // root readme, nowhere to go
      return "";
    }
    if (pom.parent().parent() == null) {
      // 1st-level, github has a weird thing where just linking to `..` and hoping for
      // the root page results in a 404. It works fine on stash ¯\_(ツ)_/¯
      return String.format("\n * [../%s](%s) %s",
          pom.parent().name(), rootUrl, pom.parent().description());
    }
    // general case, works on 2nd level and, presumably, beyond
    return String.format("\n * [../%s](..) %s",
        pom.parent().name(), pom.parent().description());
  }

  private static String javadocBadge(PomData pom) {
    if ("jar".equals(pom.packaging())) {
      return String.format(""
              + "["
              + "![javadoc](https://javadoc.io/badge2/%s/%s/javadoc.svg)"
              + "]"
              + "(https://javadoc.io/doc/%s/%s)",
          pom.groupId(), pom.artifactId(), pom.groupId(), pom.artifactId());
    }

    return "";
  }

  private static Stream<String> childLinks(PomData pom) {
    return pom.modules()
        .map(child -> String.format(" * [%s](%s) %s\n",
            child.name(),
            pom.dirPath().relativize(child.dirPath()),
            child.description()));
  }

  private String title(PomData pom) {
    return String.format(""
                + "# %s\n" // name
                + "\n"
                + "%s\n" // description
                + "\n"
                + "%s\n" // javadoc badge
                + "%s\n" // parent link
                + "%s", // child links
            pom.name(),
            pom.description(),
            javadocBadge(pom),
            parentLink(pom),
            childLinks(pom).collect(joining()))
        .trim();
  }
}
