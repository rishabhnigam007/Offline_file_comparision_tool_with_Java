package com.javacompare.ui.neobrutalism;

import javax.swing.*;
import java.awt.*;

/**
 * Neo-Brutalist status and count badge with bold border and hard drop shadow.
 */
public class NeoBrutalistBadge extends JComponent {

    public enum BadgeType {
        NEUTRAL,
        SUCCESS,
        DANGER,
        INFO,
        YELLOW
    }

    private String text;
    private BadgeType type = BadgeType.NEUTRAL;
    private int arc = 6;
    private int shadowOffset = 2;

    public NeoBrutalistBadge(String text) {
        this(text, BadgeType.NEUTRAL);
    }

    public NeoBrutalistBadge(String text, BadgeType type) {
        this.text = text;
        this.type = type;
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setOpaque(false);
        NeoBrutalistTheme.addThemeChangeListener(this::repaint);
    }

    public void setText(String text) {
        this.text = text;
        revalidate();
        repaint();
    }

    public String getText() {
        return text;
    }

    public void setBadgeType(BadgeType type) {
        this.type = type;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        NeoBrutalistUtils.applyQualityHints(g2);

        int margin = 2;
        int x = margin;
        int y = margin;
        int w = getWidth() - shadowOffset - (margin * 2);
        int h = getHeight() - shadowOffset - (margin * 2);

        Color fill = determineFillColor();
        Color fg = determineTextColor();
        Color border = NeoBrutalistTheme.border();
        Color shadow = NeoBrutalistTheme.shadow();

        if (w > 0 && h > 0) {
            NeoBrutalistUtils.paintCard(g2, x, y, w, h, arc, shadowOffset, 2.0f, fill, border, shadow);
        }

        if (text != null && !text.isEmpty()) {
            g2.setFont(getFont());
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (w - fm.stringWidth(text)) / 2;
            int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, tx, ty);
        }

        g2.dispose();
    }

    private Color determineFillColor() {
        switch (type) {
            case SUCCESS:
                return NeoBrutalistTheme.accentGreen();
            case DANGER:
                return NeoBrutalistTheme.accentRed();
            case INFO:
                return NeoBrutalistTheme.accentBlue();
            case YELLOW:
                return NeoBrutalistTheme.accentYellow();
            case NEUTRAL:
            default:
                return NeoBrutalistTheme.surface();
        }
    }

    private Color determineTextColor() {
        switch (type) {
            case DANGER:
                return Color.WHITE;
            case SUCCESS:
            case INFO:
            case YELLOW:
            case NEUTRAL:
            default:
                return Color.BLACK;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int tw = (text != null) ? fm.stringWidth(text) : 0;
        return new Dimension(tw + 24 + shadowOffset, Math.max(fm.getHeight() + 10, 26) + shadowOffset);
    }
}
