package com.javacompare.model;

/**
 * Represents the difference status of a compared line or block.
 */
public enum DiffType {
    /** Lines are identical. */
    EQUAL,
    /** Line added on the right (modified) side. */
    INSERT,
    /** Line deleted on the left (original) side. */
    DELETE,
    /** Line modified between left and right sides. */
    CHANGE,
    /** Blank virtual spacer line inserted to maintain horizontal row alignment. */
    SPACER
}
