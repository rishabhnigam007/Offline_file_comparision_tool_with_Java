package com.javacompare.ui.neobrutalism;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Neo-Brutalist chunky switch toggle.
 * 2px solid black borders, hard 2px black drop shadow, and punchy electric color fills.
 */
public class NeoBrutalistToggle extends JCheckBox {

    private static final int TRACK_WIDTH = 36;
    private static final int TRACK_HEIGHT = 18;
    private static final int THUMB_SIZE = 14;

    private boolean isHovered = false;

    public NeoBrutalistToggle(String text) {
        this(text, false);
    }

    public NeoBrutalistToggle(String text, boolean selected) {
        super(text, selected);
        initToggle();
    }

    private void initToggle() {
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 12));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });

        NeoBrutalistTheme.addThemeChangeListener(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        NeoBrutalistUtils.applyQualityHints(g2);

        int h = getHeight();
        int trackY = (h - TRACK_HEIGHT) / 2;
        int trackX = 4;

        boolean on = isSelected();
        Color trackFill = on ? NeoBrutalistTheme.accentYellow() : (NeoBrutalistTheme.isDark() ? new Color(45, 45, 52) : Color.WHITE);

        // 1. Hard Shadow
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(trackX + 2, trackY + 2, TRACK_WIDTH, TRACK_HEIGHT, 6, 6);

        // 2. Track Body
        g2.setColor(trackFill);
        g2.fillRoundRect(trackX, trackY, TRACK_WIDTH, TRACK_HEIGHT, 6, 6);

        // 3. 2px Solid Black Border
        g2.setColor(NeoBrutalistTheme.border());
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(trackX, trackY, TRACK_WIDTH, TRACK_HEIGHT, 6, 6);

        // 4. Chunky Sliding Thumb
        int thumbX = on ? (trackX + TRACK_WIDTH - THUMB_SIZE - 2) : (trackX + 2);
        int thumbY = trackY + (TRACK_HEIGHT - THUMB_SIZE) / 2;

        Color thumbFill = on ? Color.BLACK : (NeoBrutalistTheme.isDark() ? Color.WHITE : Color.BLACK);
        g2.setColor(thumbFill);
        g2.fillRoundRect(thumbX, thumbY, THUMB_SIZE, THUMB_SIZE, 4, 4);

        // 5. Text Label
        String text = getText();
        if (text != null && !text.isEmpty()) {
            g2.setFont(getFont());
            g2.setColor(NeoBrutalistTheme.textPrimary());
            FontMetrics fm = g2.getFontMetrics();
            int textX = trackX + TRACK_WIDTH + 10;
            int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, textX, textY);
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        String text = getText();
        int textW = (text != null) ? fm.stringWidth(text) : 0;
        int w = 4 + TRACK_WIDTH + 10 + textW + 8;
        int h = Math.max(TRACK_HEIGHT + 10, fm.getHeight() + 8);
        return new Dimension(w, h);
    }
}
