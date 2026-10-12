package com.javacompare.model;

/**
 * Represents an intra-line highlight span (for highlighting specific words or characters changed within a line).
 *
 * @param start  0-based character index start
 * @param length number of characters in the span
 * @param type   the diff type (e.g. INSERT, DELETE, CHANGE)
 */
public record TextSpan(int start, int length, DiffType type) {
    public int end() {
        return start + length;
    }
}
