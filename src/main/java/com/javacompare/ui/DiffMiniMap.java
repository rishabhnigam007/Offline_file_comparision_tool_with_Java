package com.javacompare.ui;

import com.javacompare.model.DiffResult;
import com.javacompare.model.DiffRow;
import com.javacompare.model.DiffType;
import com.javacompare.ui.neobrutalism.NeoBrutalistTheme;
import com.javacompare.ui.neobrutalism.NeoBrutalistUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Overview mini-map bar for Neo-Brutalist diff viewer.
 * Thick 2px border, hard drop shadow, and punchy block markers for changes.
 */
public class DiffMiniMap extends JComponent {

    private DiffResult diffResult = DiffResult.empty();
    private double viewportStartFraction = 0.0;
    private double viewportExtentFraction = 1.0;
    private Consumer<Double> onJumpRequested;
    private boolean isDarkTheme = false;

    public DiffMiniMap() {
        setPreferredSize(new Dimension(20, 0));
        setMinimumSize(new Dimension(20, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Diff Overview Mini-Map (Click or drag to jump)");

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

        NeoBrutalistTheme.addThemeChangeListener(this::repaint);
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
        NeoBrutalistUtils.applyQualityHints(g2);

        int width = getWidth();
        int height = getHeight();

        // 1. Fill MiniMap track background
        Color trackBg = NeoBrutalistTheme.isDark() ? new Color(28, 28, 34) : new Color(248, 246, 240);
        g2.setColor(trackBg);
        g2.fillRect(0, 0, width, height);

        // 2. Bold Left Border Outline
        g2.setColor(NeoBrutalistTheme.border());
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(0, 0, 0, height);

        // 3. Render Diff Markers
        if (diffResult != null && !diffResult.rows().isEmpty() && height > 0) {
            int totalRows = diffResult.rows().size();
            for (int i = 0; i < totalRows; i++) {
                DiffRow row = diffResult.rows().get(i);
                if (row.isDifference()) {
                    int y = (int) Math.round(((double) i / (double) totalRows) * (height - 6)) + 2;
                    int tickHeight = Math.max(3, height / totalRows);

                    Color tickColor;
                    if (row.type() == DiffType.INSERT) {
                        tickColor = NeoBrutalistTheme.accentGreen();
                    } else if (row.type() == DiffType.DELETE) {
                        tickColor = NeoBrutalistTheme.accentRed();
                    } else {
                        tickColor = NeoBrutalistTheme.accentBlue();
                    }

                    g2.setColor(tickColor);
                    g2.fillRect(2, y, width - 4, tickHeight);
                }
            }
        }

        // 4. Viewport Indicator (Neo-Brutalist Slider)
        if (viewportExtentFraction < 1.0) {
            int usableHeight = Math.max(10, height - 6);
            int vpY = (int) Math.round(viewportStartFraction * usableHeight) + 2;
            int vpH = Math.max(16, (int) Math.round(viewportExtentFraction * usableHeight));

            // Hard shadow
            g2.setColor(Color.BLACK);
            g2.fillRect(3, vpY + 2, width - 5, vpH);

            // Viewport block
            g2.setColor(NeoBrutalistTheme.accentYellow());
            g2.fillRect(2, vpY, width - 5, vpH);

            // Bold border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(2, vpY, width - 5, vpH);
        }

        g2.dispose();
    }
}