module dev.flowty.gl.shader.test {
  requires java.desktop;

  requires dev.flowty.gl.shader;
  requires dev.flowty.gl.framework;
  requires dev.flowty.gl.shape;
  requires dev.flowty.gl.shape.topology;

  requires org.junit.jupiter.api;
  requires org.joml;

  exports dev.flowty.gl.shader.test;
}