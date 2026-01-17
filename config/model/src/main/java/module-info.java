module dev.flowty.gl.config.model {
  requires dev.flowty.gl.util;
  requires com.fasterxml.jackson.databind;

  exports dev.flowty.gl.config.model;
  exports dev.flowty.gl.config.model.annote;
  exports dev.flowty.gl.config.model.codec;
  exports dev.flowty.gl.config.model.limit;
  exports dev.flowty.gl.config.model.var;
}