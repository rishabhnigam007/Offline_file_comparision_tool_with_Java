package com.javacompare.ui;

import com.javacompare.model.DiffResult;
import com.javacompare.model.DiffRow;
import com.javacompare.model.DiffType;
import com.javacompare.model.TextSpan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * Git-style dual-pane comparison viewer.
 * Unchanged lines are clean ("leave it"), differences are clearly distinguished (Red for deleted/old, Green for added/new),
 * and gaps/spacers on opposing sides are rendered with explicit diagonal hatch patterns so matching lines remain aligned.
 */
public class DiffViewPanel extends JPanel {

    private final JTable leftTable;
    private final JTable rightTable;
    private final JScrollPane leftScrollPane;
    private final JScrollPane rightScrollPane;
    private final JSplitPane splitPane;
    private final DiffMiniMap miniMap;
    private final ScrollSyncCoordinator scrollCoordinator;

    private final JPanel leftHeaderPanel;
    private final JLabel leftHeaderLabel;
    private final JPanel rightHeaderPanel;
    private final JLabel rightHeaderLabel;

    private DiffResult currentResult = DiffResult.empty();
    private boolean isDarkTheme = false;
    private Consumer<File> onLeftFileDropped;
    private Consumer<File> onRightFileDropped;

    // --- Git Color Palette ---

    // Light Theme (Git Light / GitHub)
    private static final Color LIGHT_BG = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(31, 35, 40);
    private static final Color LIGHT_GUTTER_BG = new Color(246, 248, 250);
    private static final Color LIGHT_GUTTER_TEXT = new Color(110, 119, 129);
    private static final Color LIGHT_BORDER = new Color(208, 215, 222);

    private static final Color LIGHT_INSERT_BG = new Color(230, 255, 236);
    private static final Color LIGHT_INSERT_TEXT = new Color(26, 127, 55);
    private static final Color LIGHT_INSERT_GUTTER_BG = new Color(204, 255, 216);
    private static final Color LIGHT_INSERT_GUTTER_FG = new Color(26, 127, 55);
    private static final Color LIGHT_INSERT_BADGE = new Color(171, 242, 188);

    private static final Color LIGHT_DELETE_BG = new Color(255, 235, 233);
    private static final Color LIGHT_DELETE_TEXT = new Color(207, 34, 46);
    private static final Color LIGHT_DELETE_GUTTER_BG = new Color(255, 220, 215);
    private static final Color LIGHT_DELETE_GUTTER_FG = new Color(207, 34, 46);
    private static final Color LIGHT_DELETE_BADGE = new Color(255, 193, 186);

    private static final Color LIGHT_GAP_BG = new Color(246, 248, 250);
    private static final Color LIGHT_GAP_STRIPE = new Color(218, 222, 227);

    // Dark Theme (Git Dark / GitHub Dark)
    private static final Color DARK_BG = new Color(13, 17, 23);
    private static final Color DARK_TEXT = new Color(201, 209, 217);
    private static final Color DARK_GUTTER_BG = new Color(22, 27, 34);
    private static final Color DARK_GUTTER_TEXT = new Color(139, 148, 158);
    private static final Color DARK_BORDER = new Color(48, 54, 61);

    private static final Color DARK_INSERT_BG = new Color(17, 42, 26);
    private static final Color DARK_INSERT_TEXT = new Color(126, 231, 135);
    private static final Color DARK_INSERT_GUTTER_BG = new Color(23, 61, 34);
    private static final Color DARK_INSERT_GUTTER_FG = new Color(63, 185, 80);
    private static final Color DARK_INSERT_BADGE = new Color(35, 98, 51);

    private static final Color DARK_DELETE_BG = new Color(58, 16, 20);
    private static final Color DARK_DELETE_TEXT = new Color(255, 123, 114);
    private static final Color DARK_DELETE_GUTTER_BG = new Color(77, 21, 25);
    private static final Color DARK_DELETE_GUTTER_FG = new Color(248, 81, 73);
    private static final Color DARK_DELETE_BADGE = new Color(126, 34, 38);

    private static final Color DARK_GAP_BG = new Color(22, 27, 34);
    private static final Color DARK_GAP_STRIPE = new Color(48, 54, 61);

    private static final Font CODE_FONT = new Font("JetBrains Mono", Font.PLAIN, 13);
    private static final Font FALLBACK_CODE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    public DiffViewPanel() {
        setLayout(new BorderLayout());
        setBackground(LIGHT_BG);

        // Left Table (Original)
        leftTable = createDiffTable(true);
        leftScrollPane = new JScrollPane(leftTable);
        leftScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftScrollPane.getViewport().setBackground(LIGHT_BG);

        // Right Table (Modified)
        rightTable = createDiffTable(false);
        rightScrollPane = new JScrollPane(rightTable);
        rightScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightScrollPane.getViewport().setBackground(LIGHT_BG);

        // Pane Headers
        leftHeaderPanel = new JPanel(new BorderLayout());
        leftHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        leftHeaderPanel.setBackground(LIGHT_GUTTER_BG);
        leftHeaderLabel = new JLabel("📄 Original (Left)");
        leftHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        leftHeaderLabel.setForeground(LIGHT_TEXT);
        leftHeaderPanel.add(leftHeaderLabel, BorderLayout.WEST);

        rightHeaderPanel = new JPanel(new BorderLayout());
        rightHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        rightHeaderPanel.setBackground(LIGHT_GUTTER_BG);
        rightHeaderLabel = new JLabel("📄 Modified (Right)");
        rightHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rightHeaderLabel.setForeground(LIGHT_TEXT);
        rightHeaderPanel.add(rightHeaderLabel, BorderLayout.WEST);

        // Left Container
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(leftHeaderPanel, BorderLayout.NORTH);
        leftContainer.add(leftScrollPane, BorderLayout.CENTER);

        // Right Container
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(rightHeaderPanel, BorderLayout.NORTH);
        rightContainer.add(rightScrollPane, BorderLayout.CENTER);

        // MiniMap on right edge
        miniMap = new DiffMiniMap();

        // Scroll Synchronization
        scrollCoordinator = new ScrollSyncCoordinator(leftScrollPane, rightScrollPane);
        miniMap.setOnJumpRequested(fraction -> scrollCoordinator.scrollToFraction(fraction));
        leftScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> updateMiniMapViewport());

        // Split Pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightContainer);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setBackground(LIGHT_BG);

        add(splitPane, BorderLayout.CENTER);
        add(miniMap, BorderLayout.EAST);

        setupDragAndDrop();
    }

    public void setDarkTheme(boolean darkTheme) {
        this.isDarkTheme = darkTheme;
        Color bg = darkTheme ? DARK_BG : LIGHT_BG;
        Color fg = darkTheme ? DARK_TEXT : LIGHT_TEXT;
        Color headerBg = darkTheme ? DARK_GUTTER_BG : LIGHT_GUTTER_BG;
        Color borderCol = darkTheme ? DARK_BORDER : LIGHT_BORDER;

        setBackground(bg);
        splitPane.setBackground(bg);
        leftScrollPane.getViewport().setBackground(bg);
        rightScrollPane.getViewport().setBackground(bg);
        leftTable.setBackground(bg);
        rightTable.setBackground(bg);

        leftHeaderPanel.setBackground(headerBg);
        leftHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        leftHeaderLabel.setForeground(fg);

        rightHeaderPanel.setBackground(headerBg);
        rightHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderCol),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        rightHeaderLabel.setForeground(fg);

        miniMap.setDarkTheme(darkTheme);
        repaint();
    }

    public void setOnFileDroppedListeners(Consumer<File> onLeft, Consumer<File> onRight) {
        this.onLeftFileDropped = onLeft;
        this.onRightFileDropped = onRight;
    }

    public void displayDiff(DiffResult result) {
        this.currentResult = (result != null) ? result : DiffResult.empty();

        DefaultTableModel leftModel = (DefaultTableModel) leftTable.getModel();
        DefaultTableModel rightModel = (DefaultTableModel) rightTable.getModel();

        leftModel.setRowCount(0);
        rightModel.setRowCount(0);

        int maxLen = 0;
        for (DiffRow row : currentResult.rows()) {
            String leftGutter = (row.leftLineNum() > 0) ? String.valueOf(row.leftLineNum()) : "";
            String rightGutter = (row.rightLineNum() > 0) ? String.valueOf(row.rightLineNum()) : "";

            leftModel.addRow(new Object[]{leftGutter, row.leftText(), row});
            rightModel.addRow(new Object[]{rightGutter, row.rightText(), row});

            if (row.leftText() != null && row.leftText().length() > maxLen) {
                maxLen = row.leftText().length();
            }
            if (row.rightText() != null && row.rightText().length() > maxLen) {
                maxLen = row.rightText().length();
            }
        }

        // Adjust preferred content and gutter column widths
        FontMetrics fm = leftTable.getFontMetrics(leftTable.getFont());
        int charW = fm.charWidth('m');
        int contentWidth = Math.max(700, maxLen * charW + 100);
        leftTable.getColumnModel().getColumn(1).setPreferredWidth(contentWidth);
        rightTable.getColumnModel().getColumn(1).setPreferredWidth(contentWidth);

        int maxLine = Math.max(currentResult.originalLineCount(), currentResult.modifiedLineCount());
        int digits = Math.max(3, String.valueOf(maxLine).length());
        int gutterW = Math.max(62, digits * fm.charWidth('9') + 32);
        leftTable.getColumnModel().getColumn(0).setPreferredWidth(gutterW);
        leftTable.getColumnModel().getColumn(0).setMinWidth(gutterW);
        leftTable.getColumnModel().getColumn(0).setMaxWidth(gutterW);
        rightTable.getColumnModel().getColumn(0).setPreferredWidth(gutterW);
        rightTable.getColumnModel().getColumn(0).setMinWidth(gutterW);
        rightTable.getColumnModel().getColumn(0).setMaxWidth(gutterW);

        // Update header badges
        int del = currentResult.deletionsCount();
        int ins = currentResult.insertionsCount();
        int mod = currentResult.modificationsCount();
        leftHeaderLabel.setText("📄 Original" + (del + mod > 0 ? "  (-" + (del + mod) + " lines)" : ""));
        rightHeaderLabel.setText("📄 Modified" + (ins + mod > 0 ? "  (+" + (ins + mod) + " lines)" : ""));

        miniMap.updateDiffResult(currentResult);
        updateMiniMapViewport();
    }

    public void displayDiff(DiffResult result, boolean sideBySide) {
        displayDiff(result);
    }

    public void scrollToDiffIndex(int diffIndex) {
        if (currentResult == null || currentResult.diffRowIndices().isEmpty()) return;
        if (diffIndex < 0 || diffIndex >= currentResult.diffRowIndices().size()) return;

        int targetRow = currentResult.diffRowIndices().get(diffIndex);
        scrollCoordinator.scrollToRow(targetRow, currentResult.rows().size());

        leftTable.setRowSelectionInterval(targetRow, targetRow);
        rightTable.setRowSelectionInterval(targetRow, targetRow);
    }

    public DiffMiniMap getMiniMap() {
        return miniMap;
    }

    private JTable createDiffTable(boolean isLeft) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"#", "Content", "Meta"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(getAppropriateFont());
        table.setRowHeight(24);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setTableHeader(null); // Clean view without default table headers
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide Metadata column (index 2)
        table.removeColumn(table.getColumnModel().getColumn(2));

        // Column 0: Gutter
        table.getColumnModel().getColumn(0).setMaxWidth(62);
        table.getColumnModel().getColumn(0).setMinWidth(62);
        table.getColumnModel().getColumn(0).setPreferredWidth(62);
        table.getColumnModel().getColumn(0).setCellRenderer(new GutterCellRenderer(isLeft));

        // Column 1: Content
        table.getColumnModel().getColumn(1).setCellRenderer(new ContentCellRenderer(isLeft));

        return table;
    }

    private Font getAppropriateFont() {
        Font font = CODE_FONT;
        if (!font.getFamily().equals("JetBrains Mono")) {
            font = FALLBACK_CODE_FONT;
        }
        return font;
    }

    private void updateMiniMapViewport() {
        JScrollBar vBar = leftScrollPane.getVerticalScrollBar();
        int max = vBar.getMaximum();
        int visible = vBar.getVisibleAmount();
        int value = vBar.getValue();

        if (max > 0) {
            double start = (double) value / (double) max;
            double extent = (double) visible / (double) max;
            miniMap.updateViewport(start, extent);
        }
    }

    private void setupDragAndDrop() {
        setupPaneDragAndDrop(leftScrollPane, file -> {
            if (onLeftFileDropped != null) onLeftFileDropped.accept(file);
        });
        setupPaneDragAndDrop(rightScrollPane, file -> {
            if (onRightFileDropped != null) onRightFileDropped.accept(file);
        });
    }

    private void setupPaneDragAndDrop(JComponent comp, Consumer<File> fileConsumer) {
        comp.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) return false;
                try {
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (files != null && !files.isEmpty()) {
                        fileConsumer.accept(files.get(0));
                        return true;
                    }
                } catch (Exception ignored) {}
                return false;
            }
        });
    }

    private static void paintHatchPattern(Graphics2D g2, int width, int height, Color bgColor, Color stripeColor) {
        g2.setColor(bgColor);
        g2.fillRect(0, 0, width, height);

        g2.setColor(stripeColor);
        g2.setStroke(new BasicStroke(1.0f));
        int step = 10;
        for (int x = -height; x < width; x += step) {
            g2.drawLine(x, height, x + height, 0);
        }
    }

    // --- Git-Style Gutter Renderer ---

    private class GutterCellRenderer extends JComponent implements TableCellRenderer {
        private final boolean isLeft;
        private DiffRow currentRow;

        public GutterCellRenderer(boolean isLeft) {
            this.isLeft = isLeft;
            setOpaque(true);
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.currentRow = getDiffRow(table, row);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int lineNum = (currentRow != null) ? (isLeft ? currentRow.leftLineNum() : currentRow.rightLineNum()) : -1;
            boolean isGap = (lineNum <= 0);
            boolean isDiff = (currentRow != null && currentRow.isDifference());

            Color borderCol = isDarkTheme ? DARK_BORDER : LIGHT_BORDER;

            if (isGap) {
                // Gap in gutter: diagonal hatch pattern, no line number
                Color gapBg = isDarkTheme ? DARK_GAP_BG : LIGHT_GAP_BG;
                Color stripeColor = isDarkTheme ? DARK_GAP_STRIPE : LIGHT_GAP_STRIPE;
                paintHatchPattern(g2, w, h, gapBg, stripeColor);
            } else {
                // Background
                Color bg;
                if (!isDiff || currentRow.type() == DiffType.EQUAL) {
                    bg = isDarkTheme ? DARK_GUTTER_BG : LIGHT_GUTTER_BG;
                } else if (isLeft) {
                    bg = isDarkTheme ? DARK_DELETE_GUTTER_BG : LIGHT_DELETE_GUTTER_BG;
                } else {
                    bg = isDarkTheme ? DARK_INSERT_GUTTER_BG : LIGHT_INSERT_GUTTER_BG;
                }

                g2.setColor(bg);
                g2.fillRect(0, 0, w, h);

                // Gutter Text: Line number + Git symbol ('-' or '+')
                Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics(font);
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

                String numStr = String.valueOf(lineNum);
                String sym = "";
                Color fg;

                if (isDiff) {
                    if (isLeft) {
                        sym = "-";
                        fg = isDarkTheme ? DARK_DELETE_GUTTER_FG : LIGHT_DELETE_GUTTER_FG;
                    } else {
                        sym = "+";
                        fg = isDarkTheme ? DARK_INSERT_GUTTER_FG : LIGHT_INSERT_GUTTER_FG;
                    }
                } else {
                    fg = isDarkTheme ? DARK_GUTTER_TEXT : LIGHT_GUTTER_TEXT;
                }

                g2.setColor(fg);

                // Right-aligned line number
                int numX = w - 24 - fm.stringWidth(numStr);
                g2.drawString(numStr, numX, textY);

                // Git symbol on right side of gutter
                if (!sym.isEmpty()) {
                    g2.setFont(font.deriveFont(Font.BOLD));
                    g2.drawString(sym, w - 16, textY);
                }
            }

            // Gutter vertical separator line
            g2.setColor(borderCol);
            g2.drawLine(w - 1, 0, w - 1, h);

            g2.dispose();
        }
    }

    // --- Git-Style Content Cell Renderer ---

    private class ContentCellRenderer extends JComponent implements TableCellRenderer {
        private final boolean isLeft;
        private DiffRow currentRow;
        private String currentText = "";
        private boolean isSelected = false;

        public ContentCellRenderer(boolean isLeft) {
            this.isLeft = isLeft;
            setOpaque(true);
            setFont(getAppropriateFont());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.currentRow = getDiffRow(table, row);
            this.currentText = (value != null) ? value.toString() : "";
            this.isSelected = isSelected;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            boolean isGap = (currentRow != null && (isLeft ? currentRow.leftLineNum() <= 0 : currentRow.rightLineNum() <= 0));
            boolean isDiff = (currentRow != null && currentRow.isDifference());

            if (isGap) {
                // Proper Gap: diagonal hatch stripes
                Color gapBg = isDarkTheme ? DARK_GAP_BG : LIGHT_GAP_BG;
                Color stripeColor = isDarkTheme ? DARK_GAP_STRIPE : LIGHT_GAP_STRIPE;
                paintHatchPattern(g2, w, h, gapBg, stripeColor);
            } else if (currentRow != null && currentRow.type() == DiffType.SPACER) {
                // Collapsed spacer row
                Color spacerBg = isDarkTheme ? DARK_GUTTER_BG : LIGHT_GUTTER_BG;
                g2.setColor(spacerBg);
                g2.fillRect(0, 0, w, h);

                Font font = getAppropriateFont().deriveFont(Font.ITALIC, 12f);
                g2.setFont(font);
                g2.setColor(isDarkTheme ? DARK_GUTTER_TEXT : LIGHT_GUTTER_TEXT);
                FontMetrics fm = g2.getFontMetrics(font);
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(currentText, 16, textY);
            } else {
                // Background
                Color bg;
                if (!isDiff || currentRow.type() == DiffType.EQUAL) {
                    // "if match leave it" -> Clean normal editor background
                    bg = isDarkTheme ? DARK_BG : LIGHT_BG;
                } else if (isLeft) {
                    // Left side diff (Delete or Change) -> Red
                    bg = isDarkTheme ? DARK_DELETE_BG : LIGHT_DELETE_BG;
                } else {
                    // Right side diff (Insert or Change) -> Green
                    bg = isDarkTheme ? DARK_INSERT_BG : LIGHT_INSERT_BG;
                }

                g2.setColor(bg);
                g2.fillRect(0, 0, w, h);

                // Subtle selection overlay
                if (isSelected) {
                    g2.setColor(isDarkTheme ? new Color(56, 139, 253, 50) : new Color(9, 105, 218, 30));
                    g2.fillRect(0, 0, w, h);
                }

                // Render Text and Word Badges
                if (!currentText.isEmpty()) {
                    Font font = getAppropriateFont();
                    g2.setFont(font);
                    FontMetrics fm = g2.getFontMetrics(font);

                    int textX = 10;
                    int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

                    String displayText = currentText.replace("\t", "    ");

                    // Intra-line word highlight badges for modified lines
                    if (currentRow.type() == DiffType.CHANGE) {
                        List<TextSpan> spans = isLeft ? currentRow.leftSpans() : currentRow.rightSpans();
                        if (spans != null && !spans.isEmpty()) {
                            Color badgeColor = isLeft
                                    ? (isDarkTheme ? DARK_DELETE_BADGE : LIGHT_DELETE_BADGE)
                                    : (isDarkTheme ? DARK_INSERT_BADGE : LIGHT_INSERT_BADGE);
                            g2.setColor(badgeColor);

                            for (TextSpan span : spans) {
                                int s = Math.min(span.start(), displayText.length());
                                int e = Math.min(span.end(), displayText.length());
                                if (s < e) {
                                    int spanX = textX + fm.stringWidth(displayText.substring(0, s));
                                    int spanW = fm.stringWidth(displayText.substring(s, e));
                                    g2.fillRoundRect(spanX, 3, spanW, h - 6, 4, 4);
                                }
                            }
                        }
                    }

                    // Text Foreground
                    Color fg;
                    if (isDiff) {
                        if (isLeft) {
                            fg = isDarkTheme ? DARK_DELETE_TEXT : LIGHT_DELETE_TEXT;
                        } else {
                            fg = isDarkTheme ? DARK_INSERT_TEXT : LIGHT_INSERT_TEXT;
                        }
                    } else {
                        fg = isDarkTheme ? DARK_TEXT : LIGHT_TEXT;
                    }

                    g2.setColor(fg);
                    g2.drawString(displayText, textX, textY);
                }
            }

            g2.dispose();
        }
    }

    private static DiffRow getDiffRow(JTable table, int row) {
        if (row < 0 || row >= table.getRowCount()) return null;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        return (DiffRow) model.getValueAt(row, 2);
    }
}
