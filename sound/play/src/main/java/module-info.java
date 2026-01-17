module dev.flowty.gl.sound.play {
  requires org.lwjgl.openal;
  requires org.lwjgl.openal.natives;
  requires org.slf4j;

  requires dev.flowty.gl.sound.generation;

  exports dev.flowty.gl.sound.play;
}