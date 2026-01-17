package dev.flowty.gl.config.model.limit;

/**
 * Choices are defined by a string array
 */
class StringChoice implements Choice<String> {

  private final String[] choices;

  /**
   * @param choices the choices
   */
  StringChoice(String[] choices) {
    this.choices = choices;
  }

  @Override
  public String[] choices() {
    return choices;
  }

  @Override
  public String chosen(int index) {
    return choices[index];
  }

}
