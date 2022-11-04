package com.ajanuary.openenum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class OpenEnumTest {
  enum TestEnum {
    SomeValue
  }

  @Test
  void mapping_enum_applies_function_to_enum() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromEnum(TestEnum.SomeValue);

    String result = openEnum.map(TestEnum::toString).orElse(String::toUpperCase);

    assertEquals("SomeValue", result);
  }

  @Test
  void mapping_unknown_applies_function_to_unknown() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromUnknown("unknown");

    String result = openEnum.map(TestEnum::toString).orElse(String::toUpperCase);

    assertEquals("UNKNOWN", result);
  }

  @Test
  void accepting_enum_value_applies_enum_consumer() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromEnum(TestEnum.SomeValue);

    AtomicReference<String> result = new AtomicReference<>();
    openEnum.accept(e -> result.set(e.toString())).orElse(u -> result.set(u.toUpperCase()));

    assertEquals("SomeValue", result.get());
  }

  @Test
  void accepting_unknown_value_applies_unknown_consumer() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromUnknown("unknown");

    AtomicReference<String> result = new AtomicReference<>();
    openEnum.accept(e -> result.set(e.toString())).orElse(u -> result.set(u.toUpperCase()));

    assertEquals("UNKNOWN", result.get());
  }

  @Test
  void toString_on_enum_value_contains_enum_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromEnum(TestEnum.SomeValue);

    assertEquals("OpenEnum{SomeValue}", openEnum.toString());
  }

  @Test
  void toString_on_unknown_value_contains_enum_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromUnknown("unknown");

    assertEquals("OpenEnum{unknown:unknown}", openEnum.toString());
  }

  @Test
  void enum_value_must_not_be_null() {
    assertThrows(IllegalArgumentException.class, () -> OpenEnum.fromEnum(null));
  }

  @Test
  void equals_fulfills_contract() {
    EqualsVerifier.forClass(OpenEnum.class).withNonnullFields("enumValue").verify();
  }

  @Test
  void enum_value_is_enum_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromEnum(TestEnum.SomeValue);

    assertTrue(openEnum.isEnumValue());
  }

  @Test
  void enum_value_is_not_unknown_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromEnum(TestEnum.SomeValue);

    assertFalse(openEnum.isUnknownValue());
  }

  @Test
  void unknown_value_is_unknown_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromUnknown("unknown");

    assertTrue(openEnum.isUnknownValue());
  }

  @Test
  void unknown_value_is_not_enum_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromUnknown("unknown");

    assertFalse(openEnum.isEnumValue());
  }

  @Test
  void can_get_enum_value_from_enum_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromEnum(TestEnum.SomeValue);

    TestEnum enumValue = openEnum.getEnumValue();

    assertEquals(TestEnum.SomeValue, enumValue);
  }

  @Test
  void cannot_get_enum_value_from_unknown_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromUnknown("unknown");

    assertThrows(NoSuchElementException.class, openEnum::getEnumValue);
  }

  @Test
  void can_get_unknown_value_from_unknown_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromUnknown("unknown");

    String unknownValue = openEnum.getUnknownValue();

    assertEquals("unknown", unknownValue);
  }

  @Test
  void cannot_get_unknown_value_from_enum_value() {
    OpenEnum<TestEnum, String> openEnum = OpenEnum.fromEnum(TestEnum.SomeValue);

    assertThrows(NoSuchElementException.class, openEnum::getUnknownValue);
  }
}
