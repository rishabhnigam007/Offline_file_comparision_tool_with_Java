package com.javacompare.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import com.javacompare.model.DiffOptions;
import com.javacompare.model.DiffResult;
import com.javacompare.model.DiffRow;
import com.javacompare.model.DiffType;
import com.javacompare.model.TextSpan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * High-performance Myers diff computation service with synchronized row alignment,
 * intra-line word difference detection, and filter options.
 */
public class DiffService {

    private static final Logger log = LoggerFactory.getLogger(DiffService.class);
    private final WordDiffCalculator wordDiffCalculator;

    public DiffService() {
        this.wordDiffCalculator = new WordDiffCalculator();
    }

    public DiffService(WordDiffCalculator wordDiffCalculator) {
        this.wordDiffCalculator = Objects.requireNonNull(wordDiffCalculator);
    }

    /**
     * Computes the aligned comparison result for the two input texts.
     */
    public DiffResult compare(String originalText, String modifiedText, DiffOptions options) {
        if (originalText == null) originalText = "";
        if (modifiedText == null) modifiedText = "";
        if (options == null) options = DiffOptions.defaults();

        long startTime = System.currentTimeMillis();
        List<String> rawOriginalLines = splitLines(originalText);
        List<String> rawModifiedLines = splitLines(modifiedText);

        int origLineCount = rawOriginalLines.size();
        int modLineCount = rawModifiedLines.size();

        // If both inputs are completely empty
        if (originalText.isEmpty() && modifiedText.isEmpty()) {
            return new DiffResult(List.of(), 0, List.of(), 0, 0, 0, 0, 0);
        }

        // Prepare normalized lines for diff computation based on options
        List<String> normalizedOriginal = normalizeLines(rawOriginalLines, options);
        List<String> normalizedModified = normalizeLines(rawModifiedLines, options);

        // Compute Patch using Myers Diff Algorithm
        Patch<String> patch = DiffUtils.diff(normalizedOriginal, normalizedModified);
        List<AbstractDelta<String>> deltas = patch.getDeltas();

        List<DiffRow> alignedRows = new ArrayList<>();
        int origIndex = 0;
        int modIndex = 0;

        int totalDiffs = deltas.size();
        int insertionsCount = 0;
        int deletionsCount = 0;
        int modificationsCount = 0;

        for (AbstractDelta<String> delta : deltas) {
            int deltaOrigPos = delta.getSource().getPosition();
            int deltaModPos = delta.getTarget().getPosition();

            // 1. Add identical / equal rows before this delta
            while (origIndex < deltaOrigPos && modIndex < deltaModPos) {
                alignedRows.add(new DiffRow(
                        origIndex + 1,
                        rawOriginalLines.get(origIndex),
                        modIndex + 1,
                        rawModifiedLines.get(modIndex),
                        DiffType.EQUAL,
                        List.of(),
                        List.of()
                ));
                origIndex++;
                modIndex++;
            }

            // Catch-up if one side had extra lines
            while (origIndex < deltaOrigPos) {
                alignedRows.add(new DiffRow(
                        origIndex + 1,
                        rawOriginalLines.get(origIndex),
                        -1,
                        "",
                        DiffType.DELETE,
                        List.of(),
                        List.of()
                ));
                origIndex++;
                deletionsCount++;
            }

            while (modIndex < deltaModPos) {
                alignedRows.add(new DiffRow(
                        -1,
                        "",
                        modIndex + 1,
                        rawModifiedLines.get(modIndex),
                        DiffType.INSERT,
                        List.of(),
                        List.of()
                ));
                modIndex++;
                insertionsCount++;
            }

            // 2. Process Delta type
            if (delta instanceof ChangeDelta<String> changeDelta) {
                List<String> srcLines = changeDelta.getSource().getLines();
                List<String> tgtLines = changeDelta.getTarget().getLines();
                modificationsCount++;

                int maxLen = Math.max(srcLines.size(), tgtLines.size());
                for (int i = 0; i < maxLen; i++) {
                    int leftLineNum = (i < srcLines.size()) ? (origIndex + i + 1) : -1;
                    String leftLine = (i < srcLines.size()) ? rawOriginalLines.get(origIndex + i) : "";

                    int rightLineNum = (i < tgtLines.size()) ? (modIndex + i + 1) : -1;
                    String rightLine = (i < tgtLines.size()) ? rawModifiedLines.get(modIndex + i) : "";

                    List<TextSpan> leftSpans = List.of();
                    List<TextSpan> rightSpans = List.of();

                    DiffType rowType;
                    if (i < srcLines.size() && i < tgtLines.size()) {
                        rowType = DiffType.CHANGE;
                        var wordDiff = wordDiffCalculator.computeWordDiff(leftLine, rightLine, options.ignoreCase());
                        leftSpans = wordDiff.leftSpans();
                        rightSpans = wordDiff.rightSpans();
                    } else if (i < srcLines.size()) {
                        rowType = DiffType.DELETE;
                    } else {
                        rowType = DiffType.INSERT;
                    }

                    alignedRows.add(new DiffRow(
                            leftLineNum, leftLine,
                            rightLineNum, rightLine,
                            rowType, leftSpans, rightSpans
                    ));
                }

                origIndex += srcLines.size();
                modIndex += tgtLines.size();

            } else if (delta instanceof DeleteDelta<String> deleteDelta) {
                List<String> srcLines = deleteDelta.getSource().getLines();
                deletionsCount += srcLines.size();

                for (int i = 0; i < srcLines.size(); i++) {
                    alignedRows.add(new DiffRow(
                            origIndex + i + 1,
                            rawOriginalLines.get(origIndex + i),
                            -1,
                            "",
                            DiffType.DELETE,
                            List.of(),
                            List.of()
                    ));
                }
                origIndex += srcLines.size();

            } else if (delta instanceof InsertDelta<String> insertDelta) {
                List<String> tgtLines = insertDelta.getTarget().getLines();
                insertionsCount += tgtLines.size();

                for (int i = 0; i < tgtLines.size(); i++) {
                    alignedRows.add(new DiffRow(
                            -1,
                            "",
                            modIndex + i + 1,
                            rawModifiedLines.get(modIndex + i),
                            DiffType.INSERT,
                            List.of(),
                            List.of()
                    ));
                }
                modIndex += tgtLines.size();
            }
        }

        // 3. Add remaining equal lines after all deltas
        while (origIndex < rawOriginalLines.size() && modIndex < rawModifiedLines.size()) {
            alignedRows.add(new DiffRow(
                    origIndex + 1,
                    rawOriginalLines.get(origIndex),
                    modIndex + 1,
                    rawModifiedLines.get(modIndex),
                    DiffType.EQUAL,
                    List.of(),
                    List.of()
            ));
            origIndex++;
            modIndex++;
        }

        while (origIndex < rawOriginalLines.size()) {
            alignedRows.add(new DiffRow(
                    origIndex + 1,
                    rawOriginalLines.get(origIndex),
                    -1,
                    "",
                    DiffType.DELETE,
                    List.of(),
                    List.of()
            ));
            origIndex++;
            deletionsCount++;
        }

        while (modIndex < rawModifiedLines.size()) {
            alignedRows.add(new DiffRow(
                    -1,
                    "",
                    modIndex + 1,
                    rawModifiedLines.get(modIndex),
                    DiffType.INSERT,
                    List.of(),
                    List.of()
            ));
            modIndex++;
            insertionsCount++;
        }

        // Apply Hide Unchanged filter if requested
        if (options.hideUnchanged() && (insertionsCount + deletionsCount + modificationsCount) > 0) {
            alignedRows = applyHideUnchangedFilter(alignedRows);
        }

        // Collect indices where differences occur
        List<Integer> diffRowIndices = new ArrayList<>();
        for (int i = 0; i < alignedRows.size(); i++) {
            if (alignedRows.get(i).isDifference()) {
                diffRowIndices.add(i);
            }
        }
        totalDiffs = diffRowIndices.size();
        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Diff computed in {}ms: {} diffs ({} added, {} deleted, {} modified), {} total rows",
                elapsed, totalDiffs, insertionsCount, deletionsCount, modificationsCount, alignedRows.size());

        return new DiffResult(
                alignedRows,
                totalDiffs,
                diffRowIndices,
                origLineCount,
                modLineCount,
                insertionsCount,
                deletionsCount,
                modificationsCount
        );
    }

    private List<String> splitLines(String text) {
        if (text.isEmpty()) return List.of();
        String[] lines = text.split("\\R", -1);
        return Arrays.asList(lines);
    }

    private List<String> normalizeLines(List<String> lines, DiffOptions options) {
        List<String> normalized = new ArrayList<>(lines.size());
        for (String line : lines) {
            String processed = line;
            if (options.trimWhitespace()) {
                processed = processed.trim();
            }
            if (options.ignoreCase()) {
                processed = processed.toLowerCase();
            }
            normalized.add(processed);
        }
        return normalized;
    }

    /**
     * Collapses long consecutive unchanged runs, keeping 2 context lines around diffs.
     */
    private List<DiffRow> applyHideUnchangedFilter(List<DiffRow> rows) {
        final int contextSize = 2;
        boolean[] keep = new boolean[rows.size()];

        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).isDifference()) {
                for (int c = Math.max(0, i - contextSize); c <= Math.min(rows.size() - 1, i + contextSize); c++) {
                    keep[c] = true;
                }
            }
        }

        List<DiffRow> filtered = new ArrayList<>();
        int i = 0;
        while (i < rows.size()) {
            if (keep[i]) {
                filtered.add(rows.get(i));
                i++;
            } else {
                int skipped = 0;
                while (i < rows.size() && !keep[i]) {
                    skipped++;
                    i++;
                }
                // Add a folding spacer row indicator
                filtered.add(new DiffRow(-1, "··· (" + skipped + " unchanged lines hidden) ···",
                        -1, "··· (" + skipped + " unchanged lines hidden) ···",
                        DiffType.SPACER, List.of(), List.of()));
            }
        }
        return filtered;
    }
}
