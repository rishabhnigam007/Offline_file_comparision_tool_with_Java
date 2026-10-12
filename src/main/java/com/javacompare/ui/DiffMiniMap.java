package com.javacompare.ui;

import com.javacompare.model.DiffResult;
import com.javacompare.model.DiffRow;
import com.javacompare.model.DiffType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Overview mini-map bar rendered on the right margin of the diff viewer.
 * High-contrast rendering for pure Black & White themes.
 */
public class DiffMiniMap extends JComponent {

    private DiffResult diffResult = DiffResult.empty();
    private double viewportStartFraction = 0.0;
    private double viewportExtentFraction = 1.0;
    private Consumer<Double> onJumpRequested;
    private boolean isDarkTheme = false;

    // Crisp Diff Indicator Colors
    private static final Color COLOR_INSERT = new Color(26, 127, 55);
    private static final Color COLOR_DELETE = new Color(207, 34, 46);
    private static final Color COLOR_CHANGE = new Color(9, 105, 218);

    private static final Color COLOR_VIEWPORT_LIGHT = new Color(0, 0, 0, 35);
    private static final Color COLOR_VIEWPORT_DARK = new Color(255, 255, 255, 45);

    public DiffMiniMap() {
        setPreferredSize(new Dimension(16, 0));
        setMinimumSize(new Dimension(16, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Diff Mini-Map (Click to jump)");

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseJump(e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseJump(e.getY());
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setDarkTheme(boolean darkTheme) {
        this.isDarkTheme = darkTheme;
        repaint();
    }

    public void setOnJumpRequested(Consumer<Double> onJumpRequested) {
        this.onJumpRequested = onJumpRequested;
    }

    public void updateDiffResult(DiffResult diffResult) {
        this.diffResult = (diffResult != null) ? diffResult : DiffResult.empty();
        repaint();
    }

    public void updateViewport(double startFraction, double extentFraction) {
        this.viewportStartFraction = Math.max(0.0, Math.min(1.0, startFraction));
        this.viewportExtentFraction = Math.max(0.01, Math.min(1.0, extentFraction));
        repaint();
    }

    private void handleMouseJump(int y) {
        if (getHeight() <= 0 || onJumpRequested == null) return;
        double targetFraction = (double) y / (double) getHeight();
        onJumpRequested.accept(Math.max(0.0, Math.min(1.0, targetFraction)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Crisp White or Jet Black Background
        Color bgColor = isDarkTheme ? Color.BLACK : Color.WHITE;
        Color borderColor = isDarkTheme ? new Color(40, 40, 40) : new Color(220, 220, 220);

        g2.setColor(bgColor);
        g2.fillRect(0, 0, width, height);

        g2.setColor(borderColor);
        g2.drawLine(0, 0, 0, height);

        if (diffResult != null && !diffResult.rows().isEmpty() && height > 0) {
            int totalRows = diffResult.rows().size();
            for (int i = 0; i < totalRows; i++) {
                DiffRow row = diffResult.rows().get(i);
                if (row.isDifference()) {
                    int y = (int) Math.round(((double) i / (double) totalRows) * height);
                    int tickHeight = Math.max(3, height / totalRows);

                    if (row.type() == DiffType.INSERT) {
                        g2.setColor(COLOR_INSERT);
                    } else if (row.type() == DiffType.DELETE) {
                        g2.setColor(COLOR_DELETE);
                    } else {
                        g2.setColor(COLOR_CHANGE);
                    }
                    g2.fillRect(2, y, width - 4, tickHeight);
                }
            }
        }

        // Render viewport indicator rect
        if (viewportExtentFraction < 1.0) {
            int vpY = (int) Math.round(viewportStartFraction * height);
            int vpH = Math.max(14, (int) Math.round(viewportExtentFraction * height));
            g2.setColor(isDarkTheme ? COLOR_VIEWPORT_DARK : COLOR_VIEWPORT_LIGHT);
            g2.fillRect(1, vpY, width - 2, vpH);
            g2.setColor(isDarkTheme ? Color.WHITE : Color.BLACK);
            g2.drawRect(1, vpY, width - 3, vpH - 1);
        }

        g2.dispose();
    }
}
