package com.ajanuary.openenum;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An enum wrapper that allows enums to be extended with unknown values.
 *
 * <p>Contains either an enum value, or an object that represents the unknown value.
 *
 * <p>The open enum pattern can be useful for parsing network APIs where you know that today it will
 * be one of a closed set of values, but in the future it might return other values.
 *
 * @param <T> type of the enum
 * @param <U> type of the unknown value
 */
public final class OpenEnum<T extends Enum<T>, U> {
  private final T enumValue;
  private final U unknownValue;

  private OpenEnum(T enumValue, U unknownValue) {
    this.enumValue = enumValue;
    this.unknownValue = unknownValue;
  }

  /**
   * Create an OpenEnum with a given enum value.
   *
   * @param enumValue the enum value to assign to the OpenEnum
   * @return an {@code OpenEnum} containing the given enum value. Must not be null.
   * @param <T> type of the enum
   * @param <U> type of the unknown value
   */
  public static <T extends Enum<T>, U> OpenEnum<T, U> fromEnum(T enumValue) {
    if (enumValue == null) {
      throw new IllegalArgumentException("enumValue cannot be null");
    }
    return new OpenEnum<>(enumValue, null);
  }

  /**
   * Create an OpenEnum with a value that is not present on the enum.
   *
   * @param unknownValue the unknown value to assign to the OpenEnum
   * @return an {@code OpenEnum} containing the given unknown value. May be null.
   * @param <T> type of the enum
   * @param <U> type of the unknown value
   */
  public static <T extends Enum<T>, U> OpenEnum<T, U> fromUnknown(U unknownValue) {
    return new OpenEnum<>(null, unknownValue);
  }

  /**
   * If an enum value is present, returns {@code true}, otherwise {@code false}.
   *
   * @return {@code true} if an enum value is present, otherwise {@code false}
   */
  public boolean isEnumValue() {
    return enumValue != null;
  }

  /**
   * If an unknown value is present, returns {@code true}, otherwise {@code false}.
   *
   * @return {@code true} if an unknown value is present, otherwise {@code false}
   */
  public boolean isUnknownValue() {
    return enumValue == null;
  }

  /**
   * If an enum value is present, returns the value, otherwise throws NoSuchElementException.
   *
   * @return the enum value this {@code OpenEnum} contains
   * @throws NoSuchElementException if no enum value is present
   */
  public T getEnumValue() {
    if (!isEnumValue()) {
      throw new NoSuchElementException("No enum value present");
    }
    return enumValue;
  }

  /**
   * If an unknown value is present, returns the value, otherwise throws NoSuchElementException.
   *
   * @return the unknown value this {@code OpenEnum} contains
   * @throws NoSuchElementException if no unknown value is present
   */
  public U getUnknownValue() {
    if (!isUnknownValue()) {
      throw new NoSuchElementException("No unknown value present");
    }
    return unknownValue;
  }

  /**
   * Apply a function to the enum value, if one is present.
   *
   * <p>The function isn't applied until {@link MappingInProgress#orElse} is called.
   *
   * @param enumMapper function to apply to the enum value, if one is present
   * @return an instance to call {@link MappingInProgress#orElse} on
   * @param <V> return type of {@code enumMapper}
   */
  public <V> MappingInProgress<V> map(Function<T, V> enumMapper) {
    return new MappingInProgress<>(enumMapper);
  }

  /**
   * Apply a consumer to the enum value, if one is present.
   *
   * <p>The consumer isn't applied until {@link ConsumingInProgress#orElse} is called.
   *
   * @param enumConsumer consumer to apply to the enum value, if one is present
   * @return an instance to call {@link ConsumingInProgress#orElse} on
   */
  public ConsumingInProgress accept(Consumer<T> enumConsumer) {
    return new ConsumingInProgress(enumConsumer);
  }

  /**
   * If an enum is present, return an Optional containing the value. Otherwise return an empty
   * Optional value.
   *
   * @return the enum value wrapped in an {@link Optional} if present, otherwise {@link
   *     Optional#empty()}
   */
  public Optional<T> asOptional() {
    if (isUnknownValue()) {
      return Optional.empty();
    }
    return Optional.of(enumValue);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OpenEnum<?, ?> openEnum)) {
      return false;
    }
    return enumValue.equals(openEnum.enumValue)
        && Objects.equals(unknownValue, openEnum.unknownValue);
  }

  @Override
  public String toString() {
    if (enumValue == null) {
      return "OpenEnum{unknown:" + unknownValue + "}";
    }
    return "OpenEnum{" + enumValue + "}";
  }

  @Override
  public int hashCode() {
    return Objects.hash(enumValue, unknownValue);
  }

  /**
   * An object representing the ongoing in-progress mapping of an OpenEnum.
   *
   * <p>Call {@link #orElse} to execute the mapping.
   *
   * @param <V> return type of the mapping
   */
  public class MappingInProgress<V> {
    private final Function<T, V> enumMapper;

    MappingInProgress(Function<T, V> enumMapper) {
      this.enumMapper = enumMapper;
    }

    /**
     * Apply a function to the unknown value, if one is present. Otherwise, apply the enum function
     * to the enum value.
     *
     * @param unknownMapper function to apply to the unknown value, if one is present
     * @return the result of applying either the enum function to the enum value, or the unknown
     *     function to the unknown value
     */
    public V orElse(Function<U, V> unknownMapper) {
      if (enumValue == null) {
        return unknownMapper.apply(unknownValue);
      }
      return enumMapper.apply(enumValue);
    }
  }

  /**
   * An object representing the ongoing in-progress consumption of an OpenEnum.
   *
   * <p>Call {@link #orElse} to execute the consumption.
   */
  public class ConsumingInProgress {
    private final Consumer<T> enumConsumer;

    ConsumingInProgress(Consumer<T> enumConsumer) {
      this.enumConsumer = enumConsumer;
    }

    /**
     * Apply a consumer to the unknown value, if one is present. Otherwise, apply the enum consumer
     * to the enum value.
     *
     * @param unknownConsumer consumer to apply to the unknown value, if one is present
     */
    public void orElse(Consumer<U> unknownConsumer) {
      if (enumValue == null) {
        unknownConsumer.accept(unknownValue);
      } else {
        enumConsumer.accept(enumValue);
      }
    }
  }
}
