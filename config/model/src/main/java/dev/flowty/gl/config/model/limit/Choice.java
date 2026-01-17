package dev.flowty.gl.config.model.limit;

import dev.flowty.gl.config.model.Limit;
import dev.flowty.gl.config.model.annote.ChoiceFor;
import dev.flowty.gl.config.model.annote.ChoiceOf;
import dev.flowty.gl.config.model.var.EncapsulatedVariable;
import dev.flowty.gl.config.model.var.FieldVariable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Limits values to a range of discrete choices
 *
 * @param <T> The value type
 */
public interface Choice<T> extends Limit {

  /**
   * @return human-readable representations of the possible values
   */
  String[] choices();

  default String[] descriptions() {
    return new String[choices().length];
  }

  /**
   * @param index The index of the chosen value
   * @return The value
   */
  T chosen(int index);

  /**
   * Extracts a limit for a variable
   *
   * @param subject The configurable object
   * @param name    The variable name
   * @return The limit, or <code>null</code> if it doesn't exist
   */
  static Limit harvestFor(Object subject, String name) {
    List<Method> sources = new ArrayList<>();

    for (Method method : subject.getClass().getMethods()) {
      ChoiceFor cf = method.getAnnotation(ChoiceFor.class);
      if (cf != null && name.equals(cf.value())) {
        if (method.getParameterCount() != 0) {
          throw new IllegalStateException(
              "ChoiceFor implies no parameters, but " + method + " has some");
        }
        sources.add(method);
      }
    }

    if (sources.isEmpty()) {
      return null;
    }

    if (sources.size() > 1) {
      throw new IllegalStateException("Multiple ChoiceFor found for "
          + subject.getClass() + "[" + name + "]");
    }

    return Choice.from(subject, sources.getFirst());
  }

  private static Limit from(Object subject, Method source) {
    try {
      if (int[].class.equals(source.getReturnType())) {
        return new IntChoice((int[]) source.invoke(subject));
      }
      if (String[].class.equals(source.getReturnType())) {
        return new StringChoice((String[]) source.invoke(subject));
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("Failed to get choices from " + source, e);
    }
    throw new IllegalStateException("Unsupported ChoiceOf " + source);
  }

  /**
   * Extracts a limit for a variable
   *
   * @param subject The configurable object
   * @param name    The variable name
   * @return The limit, or <code>null</code> if it doesn't exist
   */
  static Limit harvestOf(Object subject, String name) {
    List<ChoiceOf> choiceOfs = new ArrayList<>();

    for (Field field : subject.getClass().getFields()) {
      ChoiceOf fco = field.getAnnotation(ChoiceOf.class);
      if (fco != null) {
        String fn = FieldVariable.name(field);
        if (name.equals(fn)) {
          choiceOfs.add(fco);
        }
      }
    }

    for (Method method : subject.getClass().getMethods()) {
      ChoiceOf mco = method.getAnnotation(ChoiceOf.class);
      if (mco != null) {
        String mn = EncapsulatedVariable.name(method);
        if (name.equals(mn)) {
          choiceOfs.add(mco);
        }
      }
    }

    if (choiceOfs.isEmpty()) {
      return null;
    }

    if (choiceOfs.size() > 1) {
      throw new IllegalStateException("Multiple ChoiceOf found for "
          + subject.getClass() + "[" + name + "]");
    }

    return Choice.from(subject, name, choiceOfs.getFirst());
  }

  private static Limit from(Object subject, String name, ChoiceOf co) {
    if ((co.ints().length == 0) == (co.strings().length == 0)) {
      throw new IllegalStateException(""
          + "indecisive ChoiceOf " + co + " on "
          + subject.getClass().getName() + "[" + name + "]");
    }

    if (co.ints().length != 0) {
      return new IntChoice(co.ints());
    }
    if (co.strings().length != 0) {
      return new StringChoice(co.strings());
    }

    throw new IllegalStateException("Empty ChoiceOf");
  }
}
