package com.javacompare.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.javacompare.diff.DiffService;
import com.javacompare.model.DiffOptions;
import com.javacompare.model.DiffResult;
import com.javacompare.ui.neobrutalism.NeoBrutalistBadge;
import com.javacompare.ui.neobrutalism.NeoBrutalistButton;
import com.javacompare.ui.neobrutalism.NeoBrutalistPanel;
import com.javacompare.ui.neobrutalism.NeoBrutalistScrollBarUI;
import com.javacompare.ui.neobrutalism.NeoBrutalistTheme;
import com.javacompare.ui.neobrutalism.NeoBrutalistToggle;
import com.javacompare.util.FileLoader;
import com.javacompare.util.ReportExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;

/**
 * Main application window for Offline Java Compare Tool.
 * Engineered with the Neo-Brutalist design system: bold solid black borders,
 * hard offset drop shadows (zero blur), punchy saturated color blocks, and
 * mechanical tactile buttons.
 */
public class MainWindow extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);

    private final DiffService diffService = new DiffService();

    // UI View Cards
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel centerContainer = new JPanel(cardLayout);

    // Edit Mode Components
    private final JTextArea leftTextArea = new JTextArea();
    private final JTextArea rightTextArea = new JTextArea();
    private final JLabel leftFileLabel = new JLabel("🫲 ORIGINAL (LEFT) - Paste text or drop file");
    private final JLabel rightFileLabel = new JLabel("🫱 MODIFIED (RIGHT) - Paste text or drop file");
    private final JScrollPane leftEditorScrollPane;
    private final JScrollPane rightEditorScrollPane;
    private final TextLineNumberPanel leftLineNumberGutter;
    private final TextLineNumberPanel rightLineNumberGutter;

    private NeoBrutalistPanel leftEditorCard;
    private NeoBrutalistPanel rightEditorCard;
    private NeoBrutalistPanel headerPanel;
    private NeoBrutalistPanel bottomToolbar;

    // Diff View Component
    private final DiffViewPanel diffViewPanel = new DiffViewPanel();

    // Neo-Brutalist Option Toggles
    private final NeoBrutalistToggle wordWrapToggle = new NeoBrutalistToggle("Word wrap", true);
    private final NeoBrutalistToggle hideUnchangedToggle = new NeoBrutalistToggle("Hide Unchanged", false);
    private final NeoBrutalistToggle trimLinesToggle = new NeoBrutalistToggle("Trim Lines", true);
    private final NeoBrutalistToggle ignoreCaseToggle = new NeoBrutalistToggle("Ignore Case", false);

    // Navigation & Action Controls
    private final NeoBrutalistButton prevButton = new NeoBrutalistButton("⏮️ PREV",
            NeoBrutalistButton.ButtonStyle.DEFAULT);
    private final NeoBrutalistButton nextButton = new NeoBrutalistButton("NEXT ⏭️",
            NeoBrutalistButton.ButtonStyle.DEFAULT);
    private final NeoBrutalistBadge diffStatusBadge = new NeoBrutalistBadge("0 diffs",
            NeoBrutalistBadge.BadgeType.NEUTRAL);
    private final NeoBrutalistButton modeToggleButton = new NeoBrutalistButton("🔍 Visual Diff View",
            NeoBrutalistButton.ButtonStyle.PRIMARY);
    private final NeoBrutalistButton compareBtn = new NeoBrutalistButton("⚡ COMPARE",
            NeoBrutalistButton.ButtonStyle.SUCCESS);

    private DiffResult currentDiffResult = DiffResult.empty();
    private int currentDiffNavigationIndex = -1;
    private boolean isDiffViewActive = false;

    private String leftFileName = "Original";
    private String rightFileName = "Modified";

    public MainWindow() {
        super("Offline Java Compare Tool");
        log.info("Initializing Neo-Brutalist MainWindow UI...");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 820);
        setMinimumSize(new Dimension(940, 640));
        setLocationRelativeTo(null);

        Font codeFont = new Font("JetBrains Mono", Font.PLAIN, 13);
        if (!codeFont.getFamily().equals("JetBrains Mono")) {
            codeFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);
        }

        leftTextArea.setFont(codeFont);
        leftTextArea.setTabSize(4);
        leftTextArea.setLineWrap(true);
        leftTextArea.setWrapStyleWord(true);
        leftTextArea.setMargin(new Insets(6, 8, 6, 8));
        leftTextArea.setBorder(BorderFactory.createEmptyBorder());

        rightTextArea.setFont(codeFont);
        rightTextArea.setTabSize(4);
        rightTextArea.setLineWrap(true);
        rightTextArea.setWrapStyleWord(true);
        rightTextArea.setMargin(new Insets(6, 8, 6, 8));
        rightTextArea.setBorder(BorderFactory.createEmptyBorder());

        leftLineNumberGutter = new TextLineNumberPanel(leftTextArea);
        rightLineNumberGutter = new TextLineNumberPanel(rightTextArea);

        leftEditorScrollPane = new JScrollPane(leftTextArea);
        leftEditorScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftEditorScrollPane.setRowHeaderView(leftLineNumberGutter);
        leftEditorScrollPane.getVerticalScrollBar().setUI(new NeoBrutalistScrollBarUI());
        leftEditorScrollPane.getHorizontalScrollBar().setUI(new NeoBrutalistScrollBarUI());

        rightEditorScrollPane = new JScrollPane(rightTextArea);
        rightEditorScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightEditorScrollPane.setRowHeaderView(rightLineNumberGutter);
        rightEditorScrollPane.getVerticalScrollBar().setUI(new NeoBrutalistScrollBarUI());
        rightEditorScrollPane.getHorizontalScrollBar().setUI(new NeoBrutalistScrollBarUI());

        initUI();
        setupEvents();

        applyThemeStyles();
        NeoBrutalistTheme.addThemeChangeListener(this::applyThemeStyles);
        log.info("MainWindow initialized with Neo-Brutalism.");
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 10));
        rootPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        rootPanel.setBackground(NeoBrutalistTheme.canvas());

        // 1. Top Header Card (Neo-Brutalist)
        headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Container
        centerContainer.setOpaque(false);
        JPanel editorPanel = createEditorPanel();
        centerContainer.add(editorPanel, "EDITOR");
        centerContainer.add(diffViewPanel, "DIFF");
        rootPanel.add(centerContainer, BorderLayout.CENTER);

        // 3. Bottom Toolbar Dock (Neo-Brutalist)
        bottomToolbar = createBottomToolbar();
        rootPanel.add(bottomToolbar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private NeoBrutalistPanel createHeaderPanel() {
        NeoBrutalistPanel header = new NeoBrutalistPanel(new BorderLayout(12, 0), 8, 4, 2.0f);
        header.setBorder(new EmptyBorder(10, 16, 10, 16));

        // Title and Subtitle Box
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("OFFLINE JAVA COMPARE TOOL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        JLabel subtitleLabel = new JLabel(
                "High-contrast Neo-Brutalist parallel file diffing. 100% offline, zero cloud leaks.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subtitleLabel.setForeground(new Color(60, 60, 60));

        titleBox.add(titleLabel);
        // titleBox.add(subtitleLabel);
        header.add(titleBox, BorderLayout.WEST);

        // Action Buttons
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        NeoBrutalistButton resetBtn = new NeoBrutalistButton("🔄 RESET", NeoBrutalistButton.ButtonStyle.DANGER);
        resetBtn.setToolTipText("Clear both comparison panes");
        resetBtn.addActionListener(e -> resetInputs());

        NeoBrutalistButton exportBtn = new NeoBrutalistButton("💾 EXPORT REPORT",
                NeoBrutalistButton.ButtonStyle.DEFAULT);
        exportBtn.setToolTipText("Export standalone Neo-Brutalist HTML diff report");
        exportBtn.addActionListener(e -> exportHtmlReport());

        NeoBrutalistButton themeBtn = new NeoBrutalistButton("🌓 THEME", NeoBrutalistButton.ButtonStyle.WARNING);
        themeBtn.setToolTipText("Toggle between Neo-Brutalist Light and Cyber-Brutalist Dark");
        themeBtn.addActionListener(e -> toggleTheme());

        actionBox.add(resetBtn);
        actionBox.add(exportBtn);
        actionBox.add(themeBtn);

        header.add(actionBox, BorderLayout.EAST);
        return header;
    }

    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setOpaque(false);

        // Left Area (Neo-Brutalist Card)
        leftEditorCard = new NeoBrutalistPanel(new BorderLayout(0, 0), 8, 4, 2.0f);
        leftEditorCard.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
                new EmptyBorder(8, 12, 8, 12)));
        leftHeader.setBackground(NeoBrutalistTheme.surfaceAlt());
        leftFileLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        leftFileLabel.setForeground(Color.BLACK);
        leftHeader.add(leftFileLabel, BorderLayout.WEST);

        leftEditorCard.add(leftHeader, BorderLayout.NORTH);
        leftEditorCard.add(leftEditorScrollPane, BorderLayout.CENTER);

        // Right Area (Neo-Brutalist Card)
        rightEditorCard = new NeoBrutalistPanel(new BorderLayout(0, 0), 8, 4, 2.0f);
        rightEditorCard.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
                new EmptyBorder(8, 12, 8, 12)));
        rightHeader.setBackground(NeoBrutalistTheme.surfaceAlt());
        rightFileLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rightFileLabel.setForeground(Color.BLACK);
        rightHeader.add(rightFileLabel, BorderLayout.WEST);

        rightEditorCard.add(rightHeader, BorderLayout.NORTH);
        rightEditorCard.add(rightEditorScrollPane, BorderLayout.CENTER);

        panel.add(leftEditorCard);
        panel.add(rightEditorCard);
        return panel;
    }

    private NeoBrutalistPanel createBottomToolbar() {
        NeoBrutalistPanel toolbar = new NeoBrutalistPanel(new BorderLayout(12, 0), 8, 4, 2.0f);
        toolbar.setBorder(new EmptyBorder(8, 14, 8, 14));

        // Filter Toggles
        JPanel optionsBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        optionsBox.setOpaque(false);

        wordWrapToggle.setToolTipText("Toggle soft line wrapping");
        hideUnchangedToggle.setToolTipText("Collapse unchanged code blocks");
        trimLinesToggle.setToolTipText("Ignore leading and trailing whitespace");
        ignoreCaseToggle.setToolTipText("Ignore character casing");

        optionsBox.add(wordWrapToggle);
        optionsBox.add(hideUnchangedToggle);
        optionsBox.add(trimLinesToggle);
        optionsBox.add(ignoreCaseToggle);

        toolbar.add(optionsBox, BorderLayout.WEST);

        // Navigation & Trigger Controls
        JPanel navBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navBox.setOpaque(false);

        modeToggleButton.addActionListener(e -> toggleViewMode());

        compareBtn.addActionListener(e -> {
            performDiff();
            showDiffView();
        });

        navBox.add(prevButton);
        navBox.add(nextButton);
        navBox.add(diffStatusBadge);
        navBox.add(modeToggleButton);
        navBox.add(compareBtn);

        toolbar.add(navBox, BorderLayout.EAST);
        return toolbar;
    }

    private void setupEvents() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performDiff();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performDiff();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performDiff();
            }
        };
        leftTextArea.getDocument().addDocumentListener(docListener);
        rightTextArea.getDocument().addDocumentListener(docListener);

        wordWrapToggle.addActionListener(e -> {
            boolean wrap = wordWrapToggle.isSelected();
            leftTextArea.setLineWrap(wrap);
            leftTextArea.setWrapStyleWord(wrap);
            rightTextArea.setLineWrap(wrap);
            rightTextArea.setWrapStyleWord(wrap);
            leftLineNumberGutter.revalidate();
            leftLineNumberGutter.repaint();
            rightLineNumberGutter.revalidate();
            rightLineNumberGutter.repaint();
            performDiff();
        });
        hideUnchangedToggle.addActionListener(e -> performDiff());
        trimLinesToggle.addActionListener(e -> performDiff());
        ignoreCaseToggle.addActionListener(e -> performDiff());

        prevButton.addActionListener(e -> navigateDiff(-1));
        nextButton.addActionListener(e -> navigateDiff(1));

        diffViewPanel.setOnFileDroppedListeners(
                file -> loadFileIntoPane(file, true),
                file -> loadFileIntoPane(file, false));
    }

    private void performDiff() {
        String left = leftTextArea.getText();
        String right = rightTextArea.getText();

        DiffOptions options = new DiffOptions(
                ignoreCaseToggle.isSelected(),
                trimLinesToggle.isSelected(),
                false,
                hideUnchangedToggle.isSelected(),
                wordWrapToggle.isSelected());

        currentDiffResult = diffService.compare(left, right, options);
        diffViewPanel.displayDiff(currentDiffResult);

        int total = currentDiffResult.totalDifferences();
        if (total == 0) {
            diffStatusBadge.setText("✅ IDENTICAL (0 DIFFS)");
            diffStatusBadge.setBadgeType(NeoBrutalistBadge.BadgeType.SUCCESS);
        } else {
            diffStatusBadge.setText("⚠️ " + total + " DIFF" + (total > 1 ? "S" : ""));
            diffStatusBadge.setBadgeType(NeoBrutalistBadge.BadgeType.DANGER);
        }

        prevButton.setEnabled(total > 0);
        nextButton.setEnabled(total > 0);
        currentDiffNavigationIndex = -1;
    }

    private void navigateDiff(int direction) {
        if (currentDiffResult == null || currentDiffResult.diffRowIndices().isEmpty())
            return;

        int total = currentDiffResult.diffRowIndices().size();
        currentDiffNavigationIndex += direction;

        if (currentDiffNavigationIndex >= total) {
            currentDiffNavigationIndex = 0;
        } else if (currentDiffNavigationIndex < 0) {
            currentDiffNavigationIndex = total - 1;
        }

        diffStatusBadge.setText("DIFF " + (currentDiffNavigationIndex + 1) + " OF " + total);
        diffStatusBadge.setBadgeType(NeoBrutalistBadge.BadgeType.INFO);
        showDiffView();
        diffViewPanel.scrollToDiffIndex(currentDiffNavigationIndex);
    }

    private void toggleViewMode() {
        if (isDiffViewActive) {
            showEditorView();
        } else {
            showDiffView();
        }
    }

    private void showDiffView() {
        isDiffViewActive = true;
        cardLayout.show(centerContainer, "DIFF");
        modeToggleButton.setText("✏️ EDIT TEXT MODE");
    }

    private void showEditorView() {
        isDiffViewActive = false;
        cardLayout.show(centerContainer, "EDITOR");
        modeToggleButton.setText("🔍 VISUAL DIFF VIEW");
    }

    private void loadFileIntoPane(File file, boolean isLeft) {
        try {
            log.info("Loading file into {} pane: {}", isLeft ? "Left" : "Right", file.getAbsolutePath());
            var loaded = FileLoader.loadFile(file);
            if (isLeft) {
                leftFileName = loaded.fileName();
                leftFileLabel.setText("📄 ORIGINAL: " + leftFileName + " (" + loaded.charset().name() + ")");
                leftTextArea.setText(loaded.content());
            } else {
                rightFileName = loaded.fileName();
                rightFileLabel.setText("📄 MODIFIED: " + rightFileName + " (" + loaded.charset().name() + ")");
                rightTextArea.setText(loaded.content());
            }
            performDiff();
            showDiffView();
            log.info("File loaded successfully ({} chars).", loaded.content().length());
        } catch (Exception ex) {
            log.error("Failed to load file: {}", file.getAbsolutePath(), ex);
            JOptionPane.showMessageDialog(this, "Failed to load file: " + ex.getMessage(), "File Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetInputs() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear both inputs?", "Reset",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            log.info("Resetting comparison inputs.");
            leftTextArea.setText("");
            rightTextArea.setText("");
            leftLineNumberGutter.revalidate();
            leftLineNumberGutter.repaint();
            rightLineNumberGutter.revalidate();
            rightLineNumberGutter.repaint();
            leftFileName = "Original";
            rightFileName = "Modified";
            leftFileLabel.setText("📄 ORIGINAL (LEFT) - Paste text or drop file");
            rightFileLabel.setText("📄 MODIFIED (RIGHT) - Paste text or drop file");
            performDiff();
            showEditorView();
        }
    }

    private void exportHtmlReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("comparison_report.html"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                ReportExporter.exportHtmlReport(currentDiffResult, leftFileName, rightFileName,
                        chooser.getSelectedFile());
                log.info("Report exported to: {}", chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Report saved to:\n" + chooser.getSelectedFile().getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                log.error("Failed to export report", ex);
                JOptionPane.showMessageDialog(this, "Failed to export report: " + ex.getMessage(), "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleTheme() {
        boolean newDark = !NeoBrutalistTheme.isDark();
        log.info("Switching Neo-Brutalist theme to: {}", newDark ? "Cyber Dark" : "Retro Light");
        NeoBrutalistTheme.setDark(newDark);

        try {
            if (newDark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            FlatLaf.updateUI();
            applyThemeStyles();
        } catch (Exception ex) {
            log.error("Failed to toggle theme", ex);
        }
    }

    private void applyThemeStyles() {
        boolean dark = NeoBrutalistTheme.isDark();
        Color canvas = NeoBrutalistTheme.canvas();
        Color surface = NeoBrutalistTheme.surface();
        Color fg = NeoBrutalistTheme.textPrimary();

        getContentPane().setBackground(canvas);
        centerContainer.setBackground(canvas);

        leftTextArea.setBackground(surface);
        leftTextArea.setForeground(fg);
        leftTextArea.setCaretColor(dark ? Color.WHITE : Color.BLACK);
        leftTextArea.setSelectionColor(new Color(255, 230, 0, 100)); // Electric yellow selection

        rightTextArea.setBackground(surface);
        rightTextArea.setForeground(fg);
        rightTextArea.setCaretColor(dark ? Color.WHITE : Color.BLACK);
        rightTextArea.setSelectionColor(new Color(255, 230, 0, 100));

        leftEditorScrollPane.getViewport().setBackground(surface);
        rightEditorScrollPane.getViewport().setBackground(surface);

        leftFileLabel.setForeground(fg);
        rightFileLabel.setForeground(fg);

        leftLineNumberGutter.setDarkTheme(dark);
        rightLineNumberGutter.setDarkTheme(dark);

        if (leftEditorScrollPane.getRowHeader() != null) {
            leftEditorScrollPane.getRowHeader().setBackground(NeoBrutalistTheme.surfaceAlt());
        }
        if (rightEditorScrollPane.getRowHeader() != null) {
            rightEditorScrollPane.getRowHeader().setBackground(NeoBrutalistTheme.surfaceAlt());
        }

        diffViewPanel.setDarkTheme(dark);
        performDiff();
        repaint();
    }
}