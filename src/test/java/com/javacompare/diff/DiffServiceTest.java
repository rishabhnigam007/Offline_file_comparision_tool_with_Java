package com.javacompare.diff;

import com.javacompare.model.DiffOptions;
import com.javacompare.model.DiffResult;
import com.javacompare.model.DiffType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiffServiceTest {

    private DiffService diffService;

    @BeforeEach
    void setUp() {
        diffService = new DiffService();
    }

    @Test
    @DisplayName("Should detect identical texts")
    void shouldDetectIdenticalTexts() {
        String text = "line 1\nline 2\nline 3";
        DiffResult result = diffService.compare(text, text, DiffOptions.defaults());

        assertThat(result.isIdentical()).isTrue();
        assertThat(result.totalDifferences()).isEqualTo(0);
        assertThat(result.rows()).hasSize(3);
        assertThat(result.rows()).allMatch(r -> r.type() == DiffType.EQUAL);
    }

    @Test
    @DisplayName("Should detect modification matching screenshot demo: 'hii' vs 'hi'")
    void shouldDetectSingleModification() {
        DiffResult result = diffService.compare("hii", "hi", DiffOptions.defaults());

        assertThat(result.totalDifferences()).isEqualTo(1);
        assertThat(result.rows()).hasSize(1);
        assertThat(result.rows().get(0).type()).isEqualTo(DiffType.CHANGE);
        assertThat(result.rows().get(0).leftText()).isEqualTo("hii");
        assertThat(result.rows().get(0).rightText()).isEqualTo("hi");
    }

    @Test
    @DisplayName("Should align insertions with spacers on the left")
    void shouldAlignInsertionsWithSpacers() {
        String original = "line 1\nline 3";
        String modified = "line 1\nline 2\nline 3";

        DiffResult result = diffService.compare(original, modified, DiffOptions.defaults());

        assertThat(result.totalDifferences()).isEqualTo(1);
        assertThat(result.insertionsCount()).isEqualTo(1);

        // Expect 3 rows: equal, insert, equal
        assertThat(result.rows()).hasSize(3);
        assertThat(result.rows().get(0).type()).isEqualTo(DiffType.EQUAL);
        assertThat(result.rows().get(1).type()).isEqualTo(DiffType.INSERT);
        assertThat(result.rows().get(1).leftLineNum()).isEqualTo(-1);
        assertThat(result.rows().get(1).rightText()).isEqualTo("line 2");
        assertThat(result.rows().get(2).type()).isEqualTo(DiffType.EQUAL);
    }

    @Test
    @DisplayName("Should align deletions with spacers on the right")
    void shouldAlignDeletionsWithSpacers() {
        String original = "line 1\nline 2\nline 3";
        String modified = "line 1\nline 3";

        DiffResult result = diffService.compare(original, modified, DiffOptions.defaults());

        assertThat(result.totalDifferences()).isEqualTo(1);
        assertThat(result.deletionsCount()).isEqualTo(1);

        assertThat(result.rows()).hasSize(3);
        assertThat(result.rows().get(0).type()).isEqualTo(DiffType.EQUAL);
        assertThat(result.rows().get(1).type()).isEqualTo(DiffType.DELETE);
        assertThat(result.rows().get(1).leftText()).isEqualTo("line 2");
        assertThat(result.rows().get(1).rightLineNum()).isEqualTo(-1);
        assertThat(result.rows().get(2).type()).isEqualTo(DiffType.EQUAL);
    }

    @Test
    @DisplayName("Should trim whitespace when trimWhitespace option is enabled")
    void shouldTrimWhitespaceWhenEnabled() {
        String original = "  line 1  ";
        String modified = "line 1";

        DiffOptions withoutTrim = new DiffOptions(false, false, false, false, false);
        DiffResult resWithout = diffService.compare(original, modified, withoutTrim);
        assertThat(resWithout.totalDifferences()).isEqualTo(1);

        DiffOptions withTrim = new DiffOptions(false, true, false, false, false);
        DiffResult resWith = diffService.compare(original, modified, withTrim);
        assertThat(resWith.totalDifferences()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should hide unchanged lines when hideUnchanged option is enabled")
    void shouldHideUnchangedLinesWhenEnabled() {
        StringBuilder sbOrig = new StringBuilder();
        StringBuilder sbMod = new StringBuilder();

        for (int i = 1; i <= 50; i++) {
            if (i == 25) {
                sbOrig.append("original line 25\n");
                sbMod.append("modified line 25\n");
            } else {
                sbOrig.append("common line ").append(i).append("\n");
                sbMod.append("common line ").append(i).append("\n");
            }
        }

        DiffOptions hideOpt = new DiffOptions(false, false, false, true, false);
        DiffResult result = diffService.compare(sbOrig.toString(), sbMod.toString(), hideOpt);

        assertThat(result.totalDifferences()).isEqualTo(1);
        // Should have collapsed rows with spacer indicators
        assertThat(result.rows().size()).isLessThan(30);
        assertThat(result.rows()).anyMatch(r -> r.type() == DiffType.SPACER);
    }
}
