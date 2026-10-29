package com.javacompare.ui.neobrutalism;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Neo-Brutalist scrollbar UI with solid black outlines and high-contrast thumb.
 */
public class NeoBrutalistScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {}

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setVisible(false);
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        NeoBrutalistUtils.applyQualityHints(g2);

        Color trackColor = NeoBrutalistTheme.isDark() ? new Color(28, 28, 32) : new Color(245, 243, 238);
        g2.setColor(trackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

        // Subtle divider on left/top of track
        g2.setColor(NeoBrutalistTheme.isDark() ? new Color(50, 50, 56) : new Color(220, 218, 212));
        g2.drawLine(trackBounds.x, trackBounds.y, trackBounds.x, trackBounds.y + trackBounds.height);

        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        NeoBrutalistUtils.applyQualityHints(g2);

        int inset = 2;
        int x = thumbBounds.x + inset;
        int y = thumbBounds.y + inset;
        int w = thumbBounds.width - (inset * 2);
        int h = thumbBounds.height - (inset * 2);

        if (w > 0 && h > 0) {
            Color fill = isDragging
                    ? NeoBrutalistTheme.accentYellow()
                    : isThumbRollover()
                    ? (NeoBrutalistTheme.isDark() ? new Color(80, 80, 90) : new Color(200, 200, 200))
                    : (NeoBrutalistTheme.isDark() ? new Color(60, 60, 68) : Color.BLACK);

            g2.setColor(fill);
            g2.fillRoundRect(x, y, w, h, 4, 4);

            g2.setColor(NeoBrutalistTheme.border());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w, h, 4, 4);
        }

        g2.dispose();
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(8, 28);
    }
}
