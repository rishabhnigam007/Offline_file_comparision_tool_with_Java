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
 * Generates standalone HTML comparison reports.
 */
public class ReportExporter {

    public static void exportHtmlReport(DiffResult result, String leftTitle, String rightTitle, File targetFile) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"UTF-8\">\n");
        html.append("<title>Comparison Report - ").append(escapeHtml(leftTitle)).append(" vs ").append(escapeHtml(rightTitle)).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f6f8fa; margin: 0; padding: 20px; color: #1f2328; }\n");
        html.append(".header { background: #ffffff; padding: 16px 24px; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px; border: 1px solid #d0d7de; }\n");
        html.append(".header h1 { margin: 0 0 8px 0; font-size: 20px; }\n");
        html.append(".meta { font-size: 13px; color: #656d76; }\n");
        html.append(".stats { margin-top: 10px; display: flex; gap: 16px; font-weight: 600; font-size: 13px; }\n");
        html.append(".stat-diff { color: #cf222e; } .stat-add { color: #1a7f37; } .stat-mod { color: #9e6a03; }\n");
        html.append(".diff-table { width: 100%; border-collapse: collapse; font-family: 'JetBrains Mono', Consolas, Monaco, monospace; font-size: 13px; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border: 1px solid #d0d7de; }\n");
        html.append(".diff-table th { background: #f6f8fa; padding: 10px 12px; text-align: left; font-size: 12px; border-bottom: 1px solid #d0d7de; }\n");
        html.append(".diff-table td { padding: 4px 8px; line-height: 1.5; vertical-align: top; white-space: pre-wrap; word-break: break-all; }\n");
        html.append(".gutter { width: 45px; text-align: right; color: #8c959f; user-select: none; background: #f6f8fa; border-right: 1px solid #d0d7de; padding-right: 8px; font-size: 12px; }\n");
        html.append(".row-insert { background: #dafbe1; }\n");
        html.append(".row-insert .gutter { color: #1a7f37; background: #dafbe1; font-weight: bold; }\n");
        html.append(".row-delete { background: #ffebe9; }\n");
        html.append(".row-delete .gutter { color: #cf222e; background: #ffebe9; font-weight: bold; }\n");
        html.append(".row-change { background: #fff8c5; }\n");
        html.append(".row-change .gutter { color: #9e6a03; background: #fff8c5; font-weight: bold; }\n");
        html.append(".row-spacer { background: #f6f8fa; color: #8c959f; font-style: italic; text-align: center; }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class=\"header\">\n");
        html.append("  <h1>Parallel Comparison Report</h1>\n");
        html.append("  <div class=\"meta\">Generated on ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</div>\n");
        html.append("  <div class=\"stats\">\n");
        html.append("    <span class=\"stat-diff\">Total Differences: ").append(result.totalDifferences()).append("</span>\n");
        html.append("    <span class=\"stat-add\">+").append(result.insertionsCount()).append(" Added</span>\n");
        html.append("    <span class=\"stat-diff\">-").append(result.deletionsCount()).append(" Deleted</span>\n");
        html.append("    <span class=\"stat-mod\">~").append(result.modificationsCount()).append(" Modified</span>\n");
        html.append("  </div>\n");
        html.append("</div>\n");

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

        html.append("  </tbody>\n</table>\n</body>\n</html>");
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
