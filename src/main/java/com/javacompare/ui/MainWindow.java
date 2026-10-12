package com.javacompare.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.javacompare.diff.DiffService;
import com.javacompare.model.DiffOptions;
import com.javacompare.model.DiffResult;
import com.javacompare.util.FileLoader;
import com.javacompare.util.ReportExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;

/**
 * Main application window for Offline Java Compare Tool desktop tool.
 * High-contrast Pure White / Pure Black design with clear icons and logging.
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
    private final JLabel leftFileLabel = new JLabel("📄 Original (Left) - Paste text or drop file");
    private final JLabel rightFileLabel = new JLabel("📄 Modified (Right) - Paste text or drop file");
    private final JScrollPane leftEditorScrollPane;
    private final JScrollPane rightEditorScrollPane;
    private final TextLineNumberPanel leftLineNumberGutter;
    private final TextLineNumberPanel rightLineNumberGutter;

    // Diff View Component
    private final DiffViewPanel diffViewPanel = new DiffViewPanel();

    // Option Controls (Word wrap and Trim Lines checked by default)
    private final JCheckBox wordWrapToggle = new JCheckBox("Word wrap", true);
    private final JCheckBox hideUnchangedToggle = new JCheckBox("Hide Unchanged", false);
    private final JCheckBox trimLinesToggle = new JCheckBox("Trim Lines", true);
    private final JCheckBox ignoreCaseToggle = new JCheckBox("Ignore Case", false);

    // Navigation & Status
    private final JButton prevButton = new JButton("◀ PREVIOUS");
    private final JButton nextButton = new JButton("NEXT ▶");
    private final JLabel diffStatusLabel = new JLabel("0 diffs");
    private final JButton modeToggleButton = new JButton("🔍 Visual Diff View");

    private DiffResult currentDiffResult = DiffResult.empty();
    private int currentDiffNavigationIndex = -1;
    private boolean isDarkMode = false;
    private boolean isDiffViewActive = false;

    private String leftFileName = "Original";
    private String rightFileName = "Modified";

    public MainWindow() {
        super("Offline Java Compare Tool");
        log.info("Initializing MainWindow UI components...");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 800);
        setMinimumSize(new Dimension(920, 620));
        setLocationRelativeTo(null);

        Font codeFont = new Font("JetBrains Mono", Font.PLAIN, 13);
        if (!codeFont.getFamily().equals("JetBrains Mono")) {
            codeFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);
        }

        leftTextArea.setFont(codeFont);
        leftTextArea.setTabSize(4);
        leftTextArea.setLineWrap(true);
        leftTextArea.setWrapStyleWord(true);
        leftTextArea.setMargin(new Insets(4, 6, 4, 6));

        rightTextArea.setFont(codeFont);
        rightTextArea.setTabSize(4);
        rightTextArea.setLineWrap(true);
        rightTextArea.setWrapStyleWord(true);
        rightTextArea.setMargin(new Insets(4, 6, 4, 6));

        leftLineNumberGutter = new TextLineNumberPanel(leftTextArea);
        rightLineNumberGutter = new TextLineNumberPanel(rightTextArea);

        leftEditorScrollPane = new JScrollPane(leftTextArea);
        leftEditorScrollPane.setRowHeaderView(leftLineNumberGutter);

        rightEditorScrollPane = new JScrollPane(rightTextArea);
        rightEditorScrollPane.setRowHeaderView(rightLineNumberGutter);

        initUI();
        setupEvents();

        applyThemeStyles();
        log.info("MainWindow initialized.");
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        rootPanel.setBackground(Color.WHITE);

        // 1. Top Header
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Container
        JPanel editorPanel = createEditorPanel();
        centerContainer.add(editorPanel, "EDITOR");
        centerContainer.add(diffViewPanel, "DIFF");
        rootPanel.add(centerContainer, BorderLayout.CENTER);

        // 3. Bottom Toolbar
        JPanel bottomToolbar = createBottomToolbar();
        rootPanel.add(bottomToolbar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(10, 8));
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        header.setOpaque(false);

        // Title Box
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);

        JLabel titleLabel = new JLabel("⚡ Offline Java Compare Tool");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(9, 105, 218));

        JLabel subtitleLabel = new JLabel("Find difference between 2 text files. Pure offline comparison with synchronized parallel diffing.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(110, 110, 110));

        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);
        header.add(titleBox, BorderLayout.WEST);

        // Action Buttons with Icons
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionBox.setOpaque(false);

        JButton resetBtn = createActionButton("🔄 Reset", "Clear both text panes", e -> resetInputs());
        JButton exportBtn = createActionButton("💾 Export Report", "Export standalone HTML diff report", e -> exportHtmlReport());
        JButton themeBtn = createActionButton("🌓 Theme", "Switch between pure White and pure Black modes", e -> toggleTheme());

        actionBox.add(resetBtn);
        actionBox.add(exportBtn);
        actionBox.add(themeBtn);

        header.add(actionBox, BorderLayout.EAST);
        return header;
    }

    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setOpaque(false);

        // Left Area
        JPanel leftBox = new JPanel(new BorderLayout(0, 6));
        leftBox.setOpaque(false);
        leftFileLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        leftEditorScrollPane.setBorder(new LineBorder(new Color(210, 210, 210), 1));
        leftBox.add(leftFileLabel, BorderLayout.NORTH);
        leftBox.add(leftEditorScrollPane, BorderLayout.CENTER);

        // Right Area
        JPanel rightBox = new JPanel(new BorderLayout(0, 6));
        rightBox.setOpaque(false);
        rightFileLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rightEditorScrollPane.setBorder(new LineBorder(new Color(210, 210, 210), 1));
        rightBox.add(rightFileLabel, BorderLayout.NORTH);
        rightBox.add(rightEditorScrollPane, BorderLayout.CENTER);

        panel.add(leftBox);
        panel.add(rightBox);
        return panel;
    }

    private JPanel createBottomToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 8));
        toolbar.setBorder(new EmptyBorder(10, 0, 0, 0));
        toolbar.setOpaque(false);

        // Filter Toggles
        JPanel optionsBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
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

        // Navigation & Trigger
        JPanel navBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        navBox.setOpaque(false);

        diffStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        diffStatusLabel.setForeground(new Color(207, 34, 46));

        modeToggleButton.setBackground(new Color(9, 105, 218));
        modeToggleButton.setForeground(Color.WHITE);
        modeToggleButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        modeToggleButton.setFocusPainted(false);
        modeToggleButton.addActionListener(e -> toggleViewMode());

        JButton compareBtn = new JButton("⚡ COMPARE");
        compareBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        compareBtn.setBackground(new Color(26, 127, 55));
        compareBtn.setForeground(Color.WHITE);
        compareBtn.setFocusPainted(false);
        compareBtn.addActionListener(e -> {
            performDiff();
            showDiffView();
        });

        prevButton.setFocusPainted(false);
        nextButton.setFocusPainted(false);

        navBox.add(prevButton);
        navBox.add(nextButton);
        navBox.add(diffStatusLabel);
        navBox.add(modeToggleButton);
        navBox.add(compareBtn);

        toolbar.add(navBox, BorderLayout.EAST);
        return toolbar;
    }

    private JButton createActionButton(String text, String tooltip, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.addActionListener(action);
        return button;
    }

    private void setupEvents() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { performDiff(); }
            @Override
            public void removeUpdate(DocumentEvent e) { performDiff(); }
            @Override
            public void changedUpdate(DocumentEvent e) { performDiff(); }
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
                file -> loadFileIntoPane(file, false)
        );
    }

    private void performDiff() {
        String left = leftTextArea.getText();
        String right = rightTextArea.getText();

        DiffOptions options = new DiffOptions(
                ignoreCaseToggle.isSelected(),
                trimLinesToggle.isSelected(),
                false,
                hideUnchangedToggle.isSelected(),
                wordWrapToggle.isSelected()
        );

        currentDiffResult = diffService.compare(left, right, options);
        diffViewPanel.displayDiff(currentDiffResult);

        int total = currentDiffResult.totalDifferences();
        if (total == 0) {
            diffStatusLabel.setText("✅ Identical (0 diffs)");
            diffStatusLabel.setForeground(isDarkMode ? new Color(63, 185, 80) : new Color(26, 127, 55));
        } else {
            diffStatusLabel.setText("⚠️ " + total + " diff" + (total > 1 ? "s" : ""));
            diffStatusLabel.setForeground(isDarkMode ? new Color(248, 81, 73) : new Color(207, 34, 46));
        }

        prevButton.setEnabled(total > 0);
        nextButton.setEnabled(total > 0);
        currentDiffNavigationIndex = -1;
    }

    private void navigateDiff(int direction) {
        if (currentDiffResult == null || currentDiffResult.diffRowIndices().isEmpty()) return;

        int total = currentDiffResult.diffRowIndices().size();
        currentDiffNavigationIndex += direction;

        if (currentDiffNavigationIndex >= total) {
            currentDiffNavigationIndex = 0;
        } else if (currentDiffNavigationIndex < 0) {
            currentDiffNavigationIndex = total - 1;
        }

        diffStatusLabel.setText("Diff " + (currentDiffNavigationIndex + 1) + " of " + total);
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
        modeToggleButton.setText("✏️ Edit Text Mode");
    }

    private void showEditorView() {
        isDiffViewActive = false;
        cardLayout.show(centerContainer, "EDITOR");
        modeToggleButton.setText("🔍 Visual Diff View");
    }

    private void loadFileIntoPane(File file, boolean isLeft) {
        try {
            log.info("Loading file into {} pane: {}", isLeft ? "Left" : "Right", file.getAbsolutePath());
            var loaded = FileLoader.loadFile(file);
            if (isLeft) {
                leftFileName = loaded.fileName();
                leftFileLabel.setText("📄 Original: " + leftFileName + " (" + loaded.charset().name() + ")");
                leftTextArea.setText(loaded.content());
            } else {
                rightFileName = loaded.fileName();
                rightFileLabel.setText("📄 Modified: " + rightFileName + " (" + loaded.charset().name() + ")");
                rightTextArea.setText(loaded.content());
            }
            performDiff();
            showDiffView();
            log.info("File loaded successfully ({} chars).", loaded.content().length());
        } catch (Exception ex) {
            log.error("Failed to load file: {}", file.getAbsolutePath(), ex);
            JOptionPane.showMessageDialog(this, "Failed to load file: " + ex.getMessage(), "File Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetInputs() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear both inputs?", "Reset", JOptionPane.YES_NO_OPTION);
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
            leftFileLabel.setText("📄 Original (Left) - Paste text or drop file");
            rightFileLabel.setText("📄 Modified (Right) - Paste text or drop file");
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
                ReportExporter.exportHtmlReport(currentDiffResult, leftFileName, rightFileName, chooser.getSelectedFile());
                log.info("Report exported to: {}", chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Report saved to:\n" + chooser.getSelectedFile().getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                log.error("Failed to export report", ex);
                JOptionPane.showMessageDialog(this, "Failed to export report: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        log.info("Switching theme to: {}", isDarkMode ? "Pure Black" : "Pure White");
        try {
            if (isDarkMode) {
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
        Color bg = isDarkMode ? Color.BLACK : Color.WHITE;
        Color fg = isDarkMode ? Color.WHITE : Color.BLACK;
        Color borderCol = isDarkMode ? new Color(50, 50, 50) : new Color(210, 210, 210);

        getContentPane().setBackground(bg);
        centerContainer.setBackground(bg);

        leftTextArea.setBackground(bg);
        leftTextArea.setForeground(fg);
        leftTextArea.setCaretColor(fg);

        rightTextArea.setBackground(bg);
        rightTextArea.setForeground(fg);
        rightTextArea.setCaretColor(fg);

        leftEditorScrollPane.getViewport().setBackground(bg);
        leftEditorScrollPane.setBorder(new LineBorder(borderCol, 1));
        rightEditorScrollPane.getViewport().setBackground(bg);
        rightEditorScrollPane.setBorder(new LineBorder(borderCol, 1));

        leftFileLabel.setForeground(fg);
        rightFileLabel.setForeground(fg);

        wordWrapToggle.setForeground(fg);
        hideUnchangedToggle.setForeground(fg);
        trimLinesToggle.setForeground(fg);
        ignoreCaseToggle.setForeground(fg);

        Color gutterBg = isDarkMode ? TextLineNumberPanel.DARK_GUTTER_BG : TextLineNumberPanel.LIGHT_GUTTER_BG;
        leftLineNumberGutter.setDarkTheme(isDarkMode);
        rightLineNumberGutter.setDarkTheme(isDarkMode);
        if (leftEditorScrollPane.getRowHeader() != null) {
            leftEditorScrollPane.getRowHeader().setBackground(gutterBg);
        }
        if (rightEditorScrollPane.getRowHeader() != null) {
            rightEditorScrollPane.getRowHeader().setBackground(gutterBg);
        }

        diffViewPanel.setDarkTheme(isDarkMode);
        performDiff();
    }
}
