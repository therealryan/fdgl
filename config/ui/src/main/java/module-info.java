module dev.flowty.gl.config.ui {

  requires dev.flowty.gl.config.model;
  requires dev.flowty.gl.util;
  requires jdk.xml.dom;
  requires com.fasterxml.jackson.databind;

  exports dev.flowty.gl.config.ui;
  exports dev.flowty.gl.config.ui.widget;
}