package dev.flowty.gl.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reduces event-routing complexity
 */
public class Router {

  private final Map<Class<? extends Event>,
      List<Listener<?>>> listeners = new HashMap<>();

  private static final Map<Class<?>, Set<Class<? extends Event>>> TARGETS = new HashMap<>();

  private static Set<Class<? extends Event>> targets(Class<? extends Event> type) {
    return TARGETS.computeIfAbsent(type,
        _ -> {
          Set<Class<? extends Event>> types = new HashSet<>();
          types.add(type);
          for (Class<?> iface : type.getInterfaces()) {
            if (Event.class.isAssignableFrom(iface)) {
              types.add((Class<? extends Event>) iface);
            }
          }
          if (Event.class.isAssignableFrom(type.getSuperclass())) {
            types.addAll(targets((Class<? extends Event>) type.getSuperclass()));
          }
          return types;
        });
  }

  /**
   * Routes an event to appropriate listeners
   *
   * @param e the event
   */
  @SuppressWarnings("unchecked")
  public <E extends Event> void post(E e) {
    targets(e.getClass()).forEach(target ->
        listeners.getOrDefault(target, Collections.emptyList())
            .forEach(l -> ((Listener<E>) l).accept(e)));
  }

  /**
   * The supplied listener will start receiving events of the specified type
   *
   * @param <E>      event type
   * @param type     event type
   * @param listener will start receiving events of that type
   * @return The listener
   */
  public <E extends Event> Runnable register(Class<E> type, Listener<E> listener) {
    listeners.computeIfAbsent(type, t -> new ArrayList<>()).add(listener);
    return () -> deregister(listener);
  }

  /**
   * Stops a listener from receiving <b>any</b> events
   *
   * @param listener will no longer receive events
   */
  public void deregister(Listener<?> listener) {
    listeners.values().forEach(l -> l.remove(listener));
  }

  @Override
  public String toString() {
    return String.format("Total: %s%s",
        listeners.values().stream()
            .mapToInt(List::size)
            .sum(),
        listeners.values().stream()
            .flatMap(List::stream)
            .map(l -> l.getClass().toString())
            .collect(Collectors.toMap(
                k -> k,
                v -> 1,
                (a, b) -> a + b))
            .entrySet().stream()
            .map(e -> "\n  " + e.getKey() + " = " + e.getValue())
            .sorted()
            .collect(Collectors.joining()));
  }
}
