package com.javacompare.model;

import java.util.List;

/**
 * Encapsulates the complete result of a comparison operation.
 *
 * @param rows               the list of aligned display rows
 * @param totalDifferences   total number of difference chunks
 * @param diffRowIndices     list of row indexes where differences occur
 * @param originalLineCount  number of lines in the original text
 * @param modifiedLineCount  number of lines in the modified text
 * @param insertionsCount    count of added lines
 * @param deletionsCount     count of deleted lines
 * @param modificationsCount count of modified lines
 */
public record DiffResult(
        List<DiffRow> rows,
        int totalDifferences,
        List<Integer> diffRowIndices,
        int originalLineCount,
        int modifiedLineCount,
        int insertionsCount,
        int deletionsCount,
        int modificationsCount
) {
    public static DiffResult empty() {
        return new DiffResult(List.of(), 0, List.of(), 0, 0, 0, 0, 0);
    }

    public boolean isIdentical() {
        return totalDifferences == 0;
    }
}
