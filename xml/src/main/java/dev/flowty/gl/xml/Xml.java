package dev.flowty.gl.xml;

/**
 * A convenient way to build XML strings
 */
public class Xml extends AbstractXml<Xml> {

  public Xml(String name) {
    super(name);
  }

  @Override
  protected Xml child(String name) {
    return new Xml(name);
  }
}
