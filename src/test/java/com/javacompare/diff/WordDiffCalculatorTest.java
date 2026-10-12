package com.javacompare.diff;

import com.javacompare.model.DiffType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WordDiffCalculatorTest {

    private WordDiffCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new WordDiffCalculator();
    }

    @Test
    @DisplayName("Should detect single word change (hii vs hi)")
    void shouldDetectWordChange() {
        var result = calculator.computeWordDiff("hii", "hi", false);

        assertThat(result.leftSpans()).hasSize(1);
        assertThat(result.rightSpans()).hasSize(1);

        assertThat(result.leftSpans().get(0).type()).isEqualTo(DiffType.DELETE);
        assertThat(result.rightSpans().get(0).type()).isEqualTo(DiffType.INSERT);
    }

    @Test
    @DisplayName("Should return empty spans for identical strings")
    void shouldReturnEmptyForIdentical() {
        var result = calculator.computeWordDiff("public class Calculator", "public class Calculator", false);

        assertThat(result.leftSpans()).isEmpty();
        assertThat(result.rightSpans()).isEmpty();
    }

    @Test
    @DisplayName("Should respect ignoreCase option")
    void shouldRespectIgnoreCase() {
        var withCaseSensitive = calculator.computeWordDiff("Hello World", "hello world", false);
        assertThat(withCaseSensitive.leftSpans()).isNotEmpty();

        var withCaseInsensitive = calculator.computeWordDiff("Hello World", "hello world", true);
        assertThat(withCaseInsensitive.leftSpans()).isEmpty();
    }
}
