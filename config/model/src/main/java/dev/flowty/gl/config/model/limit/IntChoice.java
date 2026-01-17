package dev.flowty.gl.config.model.limit;

/**
 * Choices are defined by an integer array
 */
class IntChoice implements Choice<Integer> {

  private final String[] choices;

  /**
   * @param choices the choices
   */
  IntChoice(int[] choices) {
    this.choices = new String[choices.length];
    for (int i = 0; i < choices.length; i++) {
      this.choices[i] = String.valueOf(choices[i]);
    }
  }

  @Override
  public String[] choices() {
    return choices;
  }

  @Override
  public Integer chosen(int index) {
    return Integer.valueOf(choices[index]);
  }

}
