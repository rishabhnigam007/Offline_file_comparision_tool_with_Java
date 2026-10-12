package com.javacompare.diff;

import com.javacompare.model.DiffType;
import com.javacompare.model.TextSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculates intra-line token and word-level differences between two modified lines of text.
 */
public class WordDiffCalculator {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+|\\s+|[^\\w\\s]+");

    public record WordDiffResult(List<TextSpan> leftSpans, List<TextSpan> rightSpans) {}

    /**
     * Computes intra-line character and word level highlights for a changed line.
     */
    public WordDiffResult computeWordDiff(String leftText, String rightText, boolean ignoreCase) {
        if (leftText == null) leftText = "";
        if (rightText == null) rightText = "";

        if (leftText.isEmpty() && rightText.isEmpty()) {
            return new WordDiffResult(List.of(), List.of());
        }

        List<Token> leftTokens = tokenize(leftText);
        List<Token> rightTokens = tokenize(rightText);

        // Compute Longest Common Subsequence (LCS) table
        int m = leftTokens.size();
        int n = rightTokens.size();
        int[][] lcs = new int[m + 1][n + 1];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                boolean match = tokensMatch(leftTokens.get(i).text, rightTokens.get(j).text, ignoreCase);
                if (match) {
                    lcs[i + 1][j + 1] = lcs[i][j] + 1;
                } else {
                    lcs[i + 1][j + 1] = Math.max(lcs[i + 1][j], lcs[i][j + 1]);
                }
            }
        }

        // Backtrack to find changed tokens
        List<TextSpan> leftSpans = new ArrayList<>();
        List<TextSpan> rightSpans = new ArrayList<>();

        int i = m;
        int j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && tokensMatch(leftTokens.get(i - 1).text, rightTokens.get(j - 1).text, ignoreCase)) {
                i--;
                j--;
            } else if (j > 0 && (i == 0 || lcs[i][j - 1] >= lcs[i - 1][j])) {
                Token rt = rightTokens.get(j - 1);
                rightSpans.add(0, new TextSpan(rt.start, rt.text.length(), DiffType.INSERT));
                j--;
            } else if (i > 0 && (j == 0 || lcs[i][j - 1] < lcs[i - 1][j])) {
                Token lt = leftTokens.get(i - 1);
                leftSpans.add(0, new TextSpan(lt.start, lt.text.length(), DiffType.DELETE));
                i--;
            }
        }

        return new WordDiffResult(mergeAdjacentSpans(leftSpans), mergeAdjacentSpans(rightSpans));
    }

    private boolean tokensMatch(String t1, String t2, boolean ignoreCase) {
        return ignoreCase ? t1.equalsIgnoreCase(t2) : t1.equals(t2);
    }

    private List<Token> tokenize(String text) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            tokens.add(new Token(matcher.group(), matcher.start()));
        }
        return tokens;
    }

    private List<TextSpan> mergeAdjacentSpans(List<TextSpan> spans) {
        if (spans.isEmpty()) return spans;
        List<TextSpan> merged = new ArrayList<>();
        TextSpan current = spans.get(0);

        for (int k = 1; k < spans.size(); k++) {
            TextSpan next = spans.get(k);
            if (current.end() == next.start() && current.type() == next.type()) {
                current = new TextSpan(current.start(), current.length() + next.length(), current.type());
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

    private record Token(String text, int start) {}
}
