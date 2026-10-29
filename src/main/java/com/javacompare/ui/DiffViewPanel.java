package com.javacompare.ui;

import com.javacompare.model.DiffResult;
import com.javacompare.model.DiffRow;
import com.javacompare.model.DiffType;
import com.javacompare.model.TextSpan;
import com.javacompare.ui.neobrutalism.NeoBrutalistPanel;
import com.javacompare.ui.neobrutalism.NeoBrutalistScrollBarUI;
import com.javacompare.ui.neobrutalism.NeoBrutalistTheme;
import com.javacompare.ui.neobrutalism.NeoBrutalistUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * Git-style dual-pane comparison viewer engineered with Neo-Brutalist design.
 * Chunky 2px solid black borders, hard offset shadows, high-contrast typography,
 * and bold saturated diff indicators.
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

    private final NeoBrutalistPanel leftCard;
    private final NeoBrutalistPanel rightCard;

    private DiffResult currentResult = DiffResult.empty();
    private boolean isDarkTheme = false;
    private Consumer<File> onLeftFileDropped;
    private Consumer<File> onRightFileDropped;

    private static final Font CODE_FONT = new Font("JetBrains Mono", Font.PLAIN, 13);
    private static final Font FALLBACK_CODE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    public DiffViewPanel() {
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);

        // Left Table (Original)
        leftTable = createDiffTable(true);
        leftScrollPane = new JScrollPane(leftTable);
        leftScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftScrollPane.getViewport().setBackground(NeoBrutalistTheme.surface());
        leftScrollPane.getVerticalScrollBar().setUI(new NeoBrutalistScrollBarUI());
        leftScrollPane.getHorizontalScrollBar().setUI(new NeoBrutalistScrollBarUI());

        // Right Table (Modified)
        rightTable = createDiffTable(false);
        rightScrollPane = new JScrollPane(rightTable);
        rightScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightScrollPane.getViewport().setBackground(NeoBrutalistTheme.surface());
        rightScrollPane.getVerticalScrollBar().setUI(new NeoBrutalistScrollBarUI());
        rightScrollPane.getHorizontalScrollBar().setUI(new NeoBrutalistScrollBarUI());

        // Header Panels with bold bottom borders
        leftHeaderPanel = new JPanel(new BorderLayout());
        leftHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
                new EmptyBorder(8, 12, 8, 12)
        ));
        leftHeaderPanel.setBackground(NeoBrutalistTheme.surfaceAlt());
        leftHeaderLabel = new JLabel("📄 ORIGINAL (LEFT)");
        leftHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        leftHeaderLabel.setForeground(Color.BLACK);
        leftHeaderPanel.add(leftHeaderLabel, BorderLayout.WEST);

        rightHeaderPanel = new JPanel(new BorderLayout());
        rightHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
                new EmptyBorder(8, 12, 8, 12)
        ));
        rightHeaderPanel.setBackground(NeoBrutalistTheme.surfaceAlt());
        rightHeaderLabel = new JLabel("📄 MODIFIED (RIGHT)");
        rightHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rightHeaderLabel.setForeground(Color.BLACK);
        rightHeaderPanel.add(rightHeaderLabel, BorderLayout.WEST);

        // Wrap left & right inside Neo-Brutalist Cards (2px border, 4px hard shadow)
        leftCard = new NeoBrutalistPanel(new BorderLayout(), 8, 4, 2.0f);
        leftCard.setBorder(new EmptyBorder(2, 2, 2, 2));
        leftCard.add(leftHeaderPanel, BorderLayout.NORTH);
        leftCard.add(leftScrollPane, BorderLayout.CENTER);

        rightCard = new NeoBrutalistPanel(new BorderLayout(), 8, 4, 2.0f);
        rightCard.setBorder(new EmptyBorder(2, 2, 2, 2));
        rightCard.add(rightHeaderPanel, BorderLayout.NORTH);
        rightCard.add(rightScrollPane, BorderLayout.CENTER);

        // MiniMap on right edge
        miniMap = new DiffMiniMap();

        // Scroll Synchronization
        scrollCoordinator = new ScrollSyncCoordinator(leftScrollPane, rightScrollPane);
        miniMap.setOnJumpRequested(fraction -> scrollCoordinator.scrollToFraction(fraction));
        leftScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> updateMiniMapViewport());

        // Split Pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCard, rightCard);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(8);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOpaque(false);

        add(splitPane, BorderLayout.CENTER);
        add(miniMap, BorderLayout.EAST);

        setupDragAndDrop();
        NeoBrutalistTheme.addThemeChangeListener(this::applyThemeStyles);
        applyThemeStyles();
    }

    public void setDarkTheme(boolean darkTheme) {
        this.isDarkTheme = darkTheme;
        applyThemeStyles();
    }

    private void applyThemeStyles() {
        Color surface = NeoBrutalistTheme.surface();
        Color alt = NeoBrutalistTheme.surfaceAlt();
        Color border = NeoBrutalistTheme.border();
        Color fg = NeoBrutalistTheme.textPrimary();

        leftScrollPane.getViewport().setBackground(surface);
        rightScrollPane.getViewport().setBackground(surface);
        leftTable.setBackground(surface);
        rightTable.setBackground(surface);

        leftHeaderPanel.setBackground(alt);
        leftHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, border),
                new EmptyBorder(8, 12, 8, 12)
        ));
        leftHeaderLabel.setForeground(fg);

        rightHeaderPanel.setBackground(alt);
        rightHeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, border),
                new EmptyBorder(8, 12, 8, 12)
        ));
        rightHeaderLabel.setForeground(fg);

        miniMap.setDarkTheme(NeoBrutalistTheme.isDark());
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

        FontMetrics fm = leftTable.getFontMetrics(leftTable.getFont());
        int charW = fm.charWidth('m');
        int contentWidth = Math.max(700, maxLen * charW + 100);
        leftTable.getColumnModel().getColumn(1).setPreferredWidth(contentWidth);
        rightTable.getColumnModel().getColumn(1).setPreferredWidth(contentWidth);

        int maxLine = Math.max(currentResult.originalLineCount(), currentResult.modifiedLineCount());
        int digits = Math.max(3, String.valueOf(maxLine).length());
        int gutterW = Math.max(64, digits * fm.charWidth('9') + 34);
        leftTable.getColumnModel().getColumn(0).setPreferredWidth(gutterW);
        leftTable.getColumnModel().getColumn(0).setMinWidth(gutterW);
        leftTable.getColumnModel().getColumn(0).setMaxWidth(gutterW);
        rightTable.getColumnModel().getColumn(0).setPreferredWidth(gutterW);
        rightTable.getColumnModel().getColumn(0).setMinWidth(gutterW);
        rightTable.getColumnModel().getColumn(0).setMaxWidth(gutterW);

        // Header labels with punchy counts
        int del = currentResult.deletionsCount();
        int ins = currentResult.insertionsCount();
        int mod = currentResult.modificationsCount();
        leftHeaderLabel.setText("📄 ORIGINAL (LEFT)" + (del + mod > 0 ? "  [-" + (del + mod) + " lines]" : ""));
        rightHeaderLabel.setText("📄 MODIFIED (RIGHT)" + (ins + mod > 0 ? "  [+" + (ins + mod) + " lines]" : ""));

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
        table.setTableHeader(null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.removeColumn(table.getColumnModel().getColumn(2));

        table.getColumnModel().getColumn(0).setMaxWidth(64);
        table.getColumnModel().getColumn(0).setMinWidth(64);
        table.getColumnModel().getColumn(0).setPreferredWidth(64);
        table.getColumnModel().getColumn(0).setCellRenderer(new GutterCellRenderer(isLeft));

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
        g2.setStroke(new BasicStroke(1.2f));
        int step = 10;
        for (int x = -height; x < width; x += step) {
            g2.drawLine(x, height, x + height, 0);
        }
    }

    // --- Neo-Brutalist Gutter Cell Renderer ---

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
            NeoBrutalistUtils.applyQualityHints(g2);

            int w = getWidth();
            int h = getHeight();

            int lineNum = (currentRow != null) ? (isLeft ? currentRow.leftLineNum() : currentRow.rightLineNum()) : -1;
            boolean isGap = (lineNum <= 0);
            boolean isDiff = (currentRow != null && currentRow.isDifference());

            Color borderCol = NeoBrutalistTheme.border();

            if (isGap) {
                Color gapBg = NeoBrutalistTheme.diffGapBg();
                Color stripeColor = NeoBrutalistTheme.diffGapStripe();
                paintHatchPattern(g2, w, h, gapBg, stripeColor);
            } else {
                Color bg;
                if (!isDiff || currentRow.type() == DiffType.EQUAL) {
                    bg = NeoBrutalistTheme.surfaceAlt();
                } else if (isLeft) {
                    bg = NeoBrutalistTheme.diffDeleteBg();
                } else {
                    bg = NeoBrutalistTheme.diffInsertBg();
                }

                g2.setColor(bg);
                g2.fillRect(0, 0, w, h);

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
                        fg = NeoBrutalistTheme.diffDeleteText();
                    } else {
                        sym = "+";
                        fg = NeoBrutalistTheme.diffInsertText();
                    }
                } else {
                    fg = NeoBrutalistTheme.textMuted();
                }

                g2.setColor(fg);

                int numX = w - 24 - fm.stringWidth(numStr);
                g2.drawString(numStr, numX, textY);

                if (!sym.isEmpty()) {
                    g2.setFont(font.deriveFont(Font.BOLD));
                    g2.drawString(sym, w - 16, textY);
                }
            }

            // Bold vertical divider line
            g2.setColor(borderCol);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawLine(w - 1, 0, w - 1, h);

            g2.dispose();
        }
    }

    // --- Neo-Brutalist Content Cell Renderer ---

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
            NeoBrutalistUtils.applyQualityHints(g2);

            int w = getWidth();
            int h = getHeight();

            boolean isGap = (currentRow != null && (isLeft ? currentRow.leftLineNum() <= 0 : currentRow.rightLineNum() <= 0));
            boolean isDiff = (currentRow != null && currentRow.isDifference());

            if (isGap) {
                Color gapBg = NeoBrutalistTheme.diffGapBg();
                Color stripeColor = NeoBrutalistTheme.diffGapStripe();
                paintHatchPattern(g2, w, h, gapBg, stripeColor);
            } else if (currentRow != null && currentRow.type() == DiffType.SPACER) {
                Color spacerBg = NeoBrutalistTheme.surfaceAlt();
                g2.setColor(spacerBg);
                g2.fillRect(0, 0, w, h);

                Font font = getAppropriateFont().deriveFont(Font.BOLD, 12f);
                g2.setFont(font);
                g2.setColor(NeoBrutalistTheme.textMuted());
                FontMetrics fm = g2.getFontMetrics(font);
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(currentText, 16, textY);
            } else {
                Color bg;
                if (!isDiff || currentRow.type() == DiffType.EQUAL) {
                    bg = NeoBrutalistTheme.surface();
                } else if (isLeft) {
                    bg = NeoBrutalistTheme.diffDeleteBg();
                } else {
                    bg = NeoBrutalistTheme.diffInsertBg();
                }

                g2.setColor(bg);
                g2.fillRect(0, 0, w, h);

                if (isSelected) {
                    g2.setColor(new Color(255, 230, 0, 70)); // Electric yellow selection
                    g2.fillRect(0, 0, w, h);
                }

                if (!currentText.isEmpty()) {
                    Font font = getAppropriateFont();
                    g2.setFont(font);
                    FontMetrics fm = g2.getFontMetrics(font);

                    int textX = 10;
                    int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

                    String displayText = currentText.replace("\t", "    ");

                    // Intra-line word highlight badges (bold solid badges with 1px black outline)
                    if (currentRow.type() == DiffType.CHANGE) {
                        List<TextSpan> spans = isLeft ? currentRow.leftSpans() : currentRow.rightSpans();
                        if (spans != null && !spans.isEmpty()) {
                            Color badgeColor = isLeft
                                    ? NeoBrutalistTheme.diffDeleteBadge()
                                    : NeoBrutalistTheme.diffInsertBadge();

                            for (TextSpan span : spans) {
                                int s = Math.min(span.start(), displayText.length());
                                int e = Math.min(span.end(), displayText.length());
                                if (s < e) {
                                    int spanX = textX + fm.stringWidth(displayText.substring(0, s));
                                    int spanW = fm.stringWidth(displayText.substring(s, e));

                                    // Badge fill
                                    g2.setColor(badgeColor);
                                    g2.fillRect(spanX, 3, spanW, h - 6);

                                    // Badge 1px solid black outline
                                    g2.setColor(Color.BLACK);
                                    g2.setStroke(new BasicStroke(1.0f));
                                    g2.drawRect(spanX, 3, spanW, h - 6);
                                }
                            }
                        }
                    }

                    Color fg;
                    if (isDiff) {
                        if (isLeft) {
                            fg = NeoBrutalistTheme.diffDeleteText();
                        } else {
                            fg = NeoBrutalistTheme.diffInsertText();
                        }
                    } else {
                        fg = NeoBrutalistTheme.textPrimary();
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