package dev.flowty.gl.framework;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Game} implementation with {@link Screen} transition behaviour
 */
public abstract class ScreenGame implements Game {

  private static final Logger LOG = LoggerFactory.getLogger(ScreenGame.class);

  protected Screen current;

  /**
   * @return The first screen of the game
   */
  protected abstract Screen initial();

  @Override
  public final void initialiseState() {
    current = initial();
  }

  @Override
  public boolean advance(float delta) {
    if (current != null) {
      current.advance(delta);
      Screen next = current.next();
      if (LOG.isDebugEnabled() && current != next) {
        LOG.debug("Screen transition from {} to {}",
            current.getClass().getSimpleName(),
            Optional.ofNullable(next)
                .map(s -> s.getClass().getSimpleName())
                .orElse("nothing!"));
      }
      current = next;
    }
    return current != null;
  }

  @Override
  public void draw() {
    if (current != null) {
      current.draw();
    }
  }

}
