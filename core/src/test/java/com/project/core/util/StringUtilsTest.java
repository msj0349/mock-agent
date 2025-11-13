package com.project.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtils Tests")
class StringUtilsTest {

    @Test
    @DisplayName("Should not allow instantiation")
    void testConstructor() throws Exception {
        assertThatThrownBy(() -> {
            var constructor = StringUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        })
                .isInstanceOf(java.lang.reflect.InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return true for empty strings")
    void testIsEmpty_EmptyStrings(String input) {
        assertThat(StringUtils.isEmpty(input)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "test", "  test  "})
    @DisplayName("Should return false for non-empty strings")
    void testIsEmpty_NonEmptyStrings(String input) {
        assertThat(StringUtils.isEmpty(input)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return false for empty strings")
    void testIsNotEmpty_EmptyStrings(String input) {
        assertThat(StringUtils.isNotEmpty(input)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "test", "  test  "})
    @DisplayName("Should return true for non-empty strings")
    void testIsNotEmpty_NonEmptyStrings(String input) {
        assertThat(StringUtils.isNotEmpty(input)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("Should return true for blank strings")
    void testIsBlank_BlankStrings(String input) {
        assertThat(StringUtils.isBlank(input)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"test", " test ", "  test"})
    @DisplayName("Should return false for non-blank strings")
    void testIsBlank_NonBlankStrings(String input) {
        assertThat(StringUtils.isBlank(input)).isFalse();
    }

    @Test
    @DisplayName("Should trim string")
    void testTrim() {
        assertThat(StringUtils.trim("  test  ")).isEqualTo("test");
        assertThat(StringUtils.trim("test")).isEqualTo("test");
        assertThat(StringUtils.trim(null)).isNull();
    }

    @Test
    @DisplayName("Should trim to empty")
    void testTrimToEmpty() {
        assertThat(StringUtils.trimToEmpty("  test  ")).isEqualTo("test");
        assertThat(StringUtils.trimToEmpty("test")).isEqualTo("test");
        assertThat(StringUtils.trimToEmpty(null)).isEqualTo("");
        assertThat(StringUtils.trimToEmpty("")).isEqualTo("");
    }

    @Test
    @DisplayName("Should return default string when null")
    void testDefaultString() {
        assertThat(StringUtils.defaultString(null, "default")).isEqualTo("default");
        assertThat(StringUtils.defaultString("value", "default")).isEqualTo("value");
        assertThat(StringUtils.defaultString("", "default")).isEqualTo("");
    }
}
