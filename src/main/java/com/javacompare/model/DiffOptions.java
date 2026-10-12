package com.javacompare.model;

/**
 * Options and filters for file and text comparison.
 *
 * @param ignoreCase        treat uppercase and lowercase characters as equal
 * @param trimWhitespace    strip leading and trailing whitespace before comparison
 * @param ignoreBlankLines  skip empty or all-whitespace lines
 * @param hideUnchanged     collapse blocks of identical unchanged lines
 * @param wordWrap          wrap long lines in editor panes
 */
public record DiffOptions(
        boolean ignoreCase,
        boolean trimWhitespace,
        boolean ignoreBlankLines,
        boolean hideUnchanged,
        boolean wordWrap
) {
    public static DiffOptions defaults() {
        return new DiffOptions(
                false, // ignoreCase
                true,  // trimWhitespace (enabled by default)
                false, // ignoreBlankLines
                false, // hideUnchanged
                true   // wordWrap (enabled by default)
        );
    }
}
