package com.javacompare.util;

import com.javacompare.model.DiffResult;
import com.javacompare.model.DiffRow;
import com.javacompare.model.DiffType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates standalone Neo-Brutalist HTML comparison reports.
 * High-contrast, bold 2-3px solid black outlines, hard offset drop shadows,
 * and saturated color blocking (Electric Yellow, Lime Green, Coral Pink).
 */
public class ReportExporter {

    public static void exportHtmlReport(DiffResult result, String leftTitle, String rightTitle, File targetFile) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"UTF-8\">\n");
        html.append("<title>Neo-Brutalist Comparison Report - ").append(escapeHtml(leftTitle)).append(" vs ").append(escapeHtml(rightTitle)).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: #fdfaf3; margin: 0; padding: 24px; color: #000000; }\n");
        html.append(".header { background: #ffffff; padding: 20px 24px; border-radius: 8px; border: 3px solid #000000; box-shadow: 5px 5px 0px #000000; margin-bottom: 24px; }\n");
        html.append(".header h1 { margin: 0 0 6px 0; font-size: 22px; font-weight: 900; text-transform: uppercase; letter-spacing: 0.5px; }\n");
        html.append(".meta { font-size: 13px; font-weight: 600; color: #555555; }\n");
        html.append(".stats { margin-top: 14px; display: flex; flex-wrap: wrap; gap: 12px; font-size: 12px; }\n");
        html.append(".stat-badge { padding: 6px 14px; border-radius: 6px; border: 2px solid #000000; box-shadow: 2px 2px 0px #000000; font-weight: 800; text-transform: uppercase; display: inline-flex; align-items: center; }\n");
        html.append(".stat-diff { background: #ff4365; color: #ffffff; }\n");
        html.append(".stat-add { background: #00f076; color: #000000; }\n");
        html.append(".stat-mod { background: #ffe600; color: #000000; }\n");
        html.append(".diff-card { background: #ffffff; border-radius: 8px; border: 3px solid #000000; box-shadow: 6px 6px 0px #000000; overflow: hidden; }\n");
        html.append(".diff-table { width: 100%; border-collapse: collapse; font-family: 'JetBrains Mono', Consolas, Monaco, monospace; font-size: 13px; }\n");
        html.append(".diff-table th { background: #ffe600; padding: 12px 14px; text-align: left; font-size: 12px; font-weight: 800; text-transform: uppercase; color: #000000; border-bottom: 2px solid #000000; }\n");
        html.append(".diff-table td { padding: 4px 8px; line-height: 1.5; vertical-align: top; white-space: pre-wrap; word-break: break-all; }\n");
        html.append(".gutter { width: 48px; text-align: right; color: #666666; user-select: none; background: #f5f1e9; border-right: 2px solid #000000; padding-right: 10px; font-size: 12px; font-weight: 700; }\n");
        html.append(".row-insert { background: #e6fce8; color: #007a33; font-weight: 600; }\n");
        html.append(".row-insert .gutter { color: #007a33; background: #c8fad1; font-weight: 900; }\n");
        html.append(".row-delete { background: #ffebee; color: #c40026; font-weight: 600; }\n");
        html.append(".row-delete .gutter { color: #c40026; background: #ffd1d8; font-weight: 900; }\n");
        html.append(".row-change { background: #fff9db; color: #b35900; font-weight: 600; }\n");
        html.append(".row-change .gutter { color: #b35900; background: #ffeaa7; font-weight: 900; }\n");
        html.append(".row-spacer { background: #f2f2f2; color: #888888; font-style: italic; text-align: center; }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class=\"header\">\n");
        html.append("  <h1>⚡ Neo-Brutalist Comparison Report</h1>\n");
        html.append("  <div class=\"meta\">Generated on ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</div>\n");
        html.append("  <div class=\"stats\">\n");
        html.append("    <span class=\"stat-badge stat-diff\">Total Differences: ").append(result.totalDifferences()).append("</span>\n");
        html.append("    <span class=\"stat-badge stat-add\">+").append(result.insertionsCount()).append(" Added</span>\n");
        html.append("    <span class=\"stat-badge stat-diff\">-").append(result.deletionsCount()).append(" Deleted</span>\n");
        html.append("    <span class=\"stat-badge stat-mod\">~").append(result.modificationsCount()).append(" Modified</span>\n");
        html.append("  </div>\n");
        html.append("</div>\n");

        html.append("<div class=\"diff-card\">\n");
        html.append("<table class=\"diff-table\">\n");
        html.append("  <thead>\n    <tr>\n");
        html.append("      <th class=\"gutter\">#</th>\n      <th style=\"width:48%;\">").append(escapeHtml(leftTitle)).append(" (Original)</th>\n");
        html.append("      <th class=\"gutter\">#</th>\n      <th style=\"width:48%;\">").append(escapeHtml(rightTitle)).append(" (Modified)</th>\n");
        html.append("    </tr>\n  </thead>\n  <tbody>\n");

        for (DiffRow row : result.rows()) {
            String cssClass = switch (row.type()) {
                case INSERT -> "row-insert";
                case DELETE -> "row-delete";
                case CHANGE -> "row-change";
                case SPACER -> "row-spacer";
                case EQUAL -> "";
            };

            html.append("    <tr class=\"").append(cssClass).append("\">\n");
            html.append("      <td class=\"gutter\">").append(row.leftLineNum() > 0 ? row.leftLineNum() : "").append("</td>\n");
            html.append("      <td>").append(escapeHtml(row.leftText())).append("</td>\n");
            html.append("      <td class=\"gutter\">").append(row.rightLineNum() > 0 ? row.rightLineNum() : "").append("</td>\n");
            html.append("      <td>").append(escapeHtml(row.rightText())).append("</td>\n");
            html.append("    </tr>\n");
        }

        html.append("  </tbody>\n</table>\n</div>\n</body>\n</html>");
        Files.writeString(targetFile.toPath(), html.toString());
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}