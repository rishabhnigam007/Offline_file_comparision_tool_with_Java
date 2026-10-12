package com.javacompare.ui;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Clean Git/IDE-style line number gutter component for JTextArea in JScrollPane.
 * Displays line numbers in normal text mode matching the visual diff mode's aesthetic.
 * Automatically handles font matching, dynamic width resizing, soft word-wrapping,
 * mouse-click cursor jumping, and high-contrast Light/Dark theme rendering.
 */
public class TextLineNumberPanel extends JComponent implements DocumentListener, CaretListener, PropertyChangeListener {

    public static final Color LIGHT_GUTTER_BG = new Color(246, 248, 250);
    public static final Color LIGHT_GUTTER_TEXT = new Color(110, 119, 129);
    public static final Color LIGHT_CURRENT_TEXT = new Color(31, 35, 40);
    public static final Color LIGHT_BORDER = new Color(208, 215, 222);

    public static final Color DARK_GUTTER_BG = new Color(22, 27, 34);
    public static final Color DARK_GUTTER_TEXT = new Color(139, 148, 158);
    public static final Color DARK_CURRENT_TEXT = new Color(240, 246, 252);
    public static final Color DARK_BORDER = new Color(48, 54, 61);

    private final JTextArea textArea;
    private boolean isDarkTheme = false;
    private int lastDigits = 0;

    public TextLineNumberPanel(JTextArea textArea) {
        this.textArea = textArea;

        setOpaque(true);
        setFocusable(false);
        setCursor(Cursor.getDefaultCursor());

        Font textFont = textArea.getFont();
        setFont(textFont != null ? textFont : new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // Listen for document changes to update line count and width
        textArea.getDocument().addDocumentListener(this);
        textArea.addCaretListener(this);
        textArea.addPropertyChangeListener("font", this);
        textArea.addPropertyChangeListener("document", this);

        // Listen for resizing (important for line wrapping)
        textArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                revalidate();
                repaint();
            }
        });

        // Click on a line number to jump caret to that line
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    int offset = textArea.viewToModel2D(new Point(0, e.getY()));
                    if (offset >= 0) {
                        int line = textArea.getLineOfOffset(offset);
                        int lineStart = textArea.getLineStartOffset(line);
                        textArea.setCaretPosition(lineStart);
                        textArea.requestFocusInWindow();
                    }
                } catch (Exception ignored) {}
            }
        });

        updateWidth();
    }

    public void setDarkTheme(boolean darkTheme) {
        this.isDarkTheme = darkTheme;
        repaint();
    }

    private void updateWidth() {
        int lines = Math.max(1, textArea.getLineCount());
        int digits = Math.max(3, String.valueOf(lines).length());
        if (digits != lastDigits) {
            lastDigits = digits;
            revalidate();
        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int lines = Math.max(1, textArea.getLineCount());
        int digits = Math.max(3, String.valueOf(lines).length());

        Font font = getFont();
        FontMetrics fm = getFontMetrics(font != null ? font : textArea.getFont());
        int charW = fm.charWidth('9');
        int width = Math.max(48, digits * charW + 24);
        int height = Math.max(textArea.getHeight(), 200);

        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Fill background
        Color bg = isDarkTheme ? DARK_GUTTER_BG : LIGHT_GUTTER_BG;
        g2.setColor(bg);
        g2.fillRect(0, 0, w, h);

        // 2. Draw 1px vertical border separator on right edge
        Color borderCol = isDarkTheme ? DARK_BORDER : LIGHT_BORDER;
        g2.setColor(borderCol);
        g2.drawLine(w - 1, 0, w - 1, h);

        // 3. Determine visible clip
        Rectangle clip = g2.getClipBounds();
        if (clip == null) {
            clip = new Rectangle(0, 0, w, h);
        }

        Font font = textArea.getFont();
        if (font == null) {
            font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics(font);

        int totalLines = textArea.getLineCount();
        if (totalLines <= 0) {
            totalLines = 1;
        }

        int caretLine = -1;
        try {
            caretLine = textArea.getLineOfOffset(textArea.getCaretPosition());
        } catch (Exception ignored) {}

        // Find visible line range in clip
        int startLine = 0;
        int endLine = totalLines - 1;

        try {
            int startOffset = textArea.viewToModel2D(new Point(0, Math.max(0, clip.y)));
            if (startOffset >= 0) {
                startLine = Math.max(0, textArea.getLineOfOffset(startOffset));
            }
            int endOffset = textArea.viewToModel2D(new Point(0, Math.max(0, clip.y + clip.height)));
            if (endOffset >= 0) {
                endLine = Math.min(totalLines - 1, textArea.getLineOfOffset(endOffset) + 1);
            }
        } catch (Exception e) {
            startLine = 0;
            endLine = totalLines - 1;
        }

        Color normalFg = isDarkTheme ? DARK_GUTTER_TEXT : LIGHT_GUTTER_TEXT;
        Color currentLineFg = isDarkTheme ? DARK_CURRENT_TEXT : LIGHT_CURRENT_TEXT;

        Insets insets = textArea.getInsets();
        int topInset = (insets != null) ? insets.top : 0;
        int lineHeight = fm.getHeight();

        for (int line = startLine; line <= endLine; line++) {
            try {
                int offset = textArea.getLineStartOffset(line);
                Rectangle2D r = textArea.modelToView2D(offset);

                int y;
                if (r != null) {
                    y = (int) r.getY();
                } else {
                    y = topInset + line * lineHeight;
                }

                int textY = y + fm.getAscent();
                String numStr = String.valueOf(line + 1);
                int numX = w - 12 - fm.stringWidth(numStr);

                if (line == caretLine) {
                    g2.setColor(currentLineFg);
                    g2.setFont(font.deriveFont(Font.BOLD));
                } else {
                    g2.setColor(normalFg);
                    g2.setFont(font);
                }

                g2.drawString(numStr, numX, textY);
            } catch (BadLocationException ignored) {
            }
        }

        g2.dispose();
    }

    // --- DocumentListener & CaretListener ---

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateWidth();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateWidth();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateWidth();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("font".equals(evt.getPropertyName())) {
            Font newFont = (Font) evt.getNewValue();
            setFont(newFont != null ? newFont : new Font(Font.MONOSPACED, Font.PLAIN, 12));
            updateWidth();
        } else if ("document".equals(evt.getPropertyName())) {
            if (evt.getOldValue() instanceof javax.swing.text.Document oldDoc) {
                oldDoc.removeDocumentListener(this);
            }
            if (evt.getNewValue() instanceof javax.swing.text.Document newDoc) {
                newDoc.addDocumentListener(this);
            }
            updateWidth();
        }
    }
}
