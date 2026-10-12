package com.javacompare.model;

import java.util.List;

/**
 * Represents a single aligned row in the dual-pane comparison view.
 *
 * @param leftLineNum   1-based line number in original file (or -1 if spacer/not present)
 * @param leftText      text content in original file (or empty if spacer)
 * @param rightLineNum  1-based line number in modified file (or -1 if spacer/not present)
 * @param rightText     text content in modified file (or empty if spacer)
 * @param type          diff classification (EQUAL, INSERT, DELETE, CHANGE, SPACER)
 * @param leftSpans     intra-line character/word highlight spans for left text
 * @param rightSpans    intra-line character/word highlight spans for right text
 */
public record DiffRow(
        int leftLineNum,
        String leftText,
        int rightLineNum,
        String rightText,
        DiffType type,
        List<TextSpan> leftSpans,
        List<TextSpan> rightSpans
) {
    public boolean isDifference() {
        return type == DiffType.INSERT || type == DiffType.DELETE || type == DiffType.CHANGE;
    }

    public static DiffRow equal(int leftNum, String left, int rightNum, String right) {
        return new DiffRow(leftNum, left, rightNum, right, DiffType.EQUAL, List.of(), List.of());
    }

    public static DiffRow delete(int leftNum, String left) {
        return new DiffRow(leftNum, left, -1, "", DiffType.DELETE, List.of(), List.of());
    }

    public static DiffRow insert(int rightNum, String right) {
        return new DiffRow(-1, "", rightNum, right, DiffType.INSERT, List.of(), List.of());
    }

    public static DiffRow change(int leftNum, String left, int rightNum, String right,
                                 List<TextSpan> leftSpans, List<TextSpan> rightSpans) {
        return new DiffRow(leftNum, left, rightNum, right, DiffType.CHANGE, leftSpans, rightSpans);
    }
}
