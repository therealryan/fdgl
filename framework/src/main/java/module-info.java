module dev.flowty.gl.framework {
  requires java.desktop;

  requires com.fasterxml.jackson.databind;
  requires org.lwjgl.natives;
  requires org.lwjgl.glfw.natives;
  requires org.lwjgl.opengl.natives;
  requires org.joml;
  requires org.slf4j;

  requires dev.flowty.gl.util;
  requires dev.flowty.gl.config.model;
  requires org.lwjgl.opengl;

  exports dev.flowty.gl.framework;
  exports dev.flowty.gl.framework.display;
  exports dev.flowty.gl.framework.input;
}