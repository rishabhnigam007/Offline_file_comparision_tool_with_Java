package com.javacompare.ui.neobrutalism;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Neo-Brutalist tactile button.
 * Bold solid black border outline, hard offset black drop shadow (zero blur),
 * and a physical mechanical displacement when pressed (shifts into its shadow).
 */
public class NeoBrutalistButton extends JButton {

    public enum ButtonStyle {
        DEFAULT,
        PRIMARY,
        SUCCESS,
        DANGER,
        WARNING
    }

    private ButtonStyle buttonStyle = ButtonStyle.DEFAULT;
    private int arc = 6;
    private int shadowOffset = 3;
    private float borderWidth = 2.0f;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public NeoBrutalistButton(String text) {
        this(text, ButtonStyle.DEFAULT);
    }

    public NeoBrutalistButton(String text, ButtonStyle style) {
        super(text);
        this.buttonStyle = style;
        initButton();
    }

    private void initButton() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setMargin(new Insets(6, 14, 6, 14));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });

        NeoBrutalistTheme.addThemeChangeListener(this::repaint);
    }

    public void setButtonStyle(ButtonStyle style) {
        this.buttonStyle = style;
        repaint();
    }

    public ButtonStyle getButtonStyle() {
        return buttonStyle;
    }

    public void setArc(int arc) {
        this.arc = arc;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        NeoBrutalistUtils.applyQualityHints(g2);

        int margin = 2;
        int activeOffset = (isPressed || isSelected()) ? 0 : (isHovered ? shadowOffset + 1 : shadowOffset);
        int push = (isPressed || isSelected()) ? shadowOffset : 0;

        int x = margin + push;
        int y = margin + push;
        int w = getWidth() - shadowOffset - (margin * 2);
        int h = getHeight() - shadowOffset - (margin * 2);

        if (w > 0 && h > 0) {
            Color fill = determineFillColor();
            Color border = NeoBrutalistTheme.border();
            Color shadow = NeoBrutalistTheme.shadow();

            if (!isEnabled()) {
                fill = NeoBrutalistTheme.isDark() ? new Color(45, 45, 50) : new Color(225, 225, 225);
                activeOffset = 1;
            }

            NeoBrutalistUtils.paintCard(g2, x, y, w, h, arc, activeOffset, borderWidth, fill, border, shadow);
        }

        // Draw text and icon
        FontMetrics fm = g2.getFontMetrics(getFont());
        String text = getText();
        Icon icon = getIcon();

        Color fg = determineTextColor();
        g2.setColor(fg);
        g2.setFont(getFont());

        int textWidth = (text != null && !text.isEmpty()) ? fm.stringWidth(text) : 0;
        int iconWidth = (icon != null) ? icon.getIconWidth() : 0;
        int iconGap = (icon != null && textWidth > 0) ? getIconTextGap() : 0;
        int contentWidth = iconWidth + iconGap + textWidth;

        int startX = x + (w - contentWidth) / 2;
        int centerY = y + (h / 2);

        if (icon != null) {
            int iconY = centerY - (icon.getIconHeight() / 2);
            icon.paintIcon(this, g2, startX, iconY);
            startX += iconWidth + iconGap;
        }

        if (text != null && !text.isEmpty()) {
            int textY = centerY + (fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, startX, textY);
        }

        g2.dispose();
    }

    private Color determineFillColor() {
        if (!isEnabled()) {
            return NeoBrutalistTheme.isDark() ? new Color(40, 40, 45) : new Color(230, 230, 230);
        }

        switch (buttonStyle) {
            case PRIMARY:
                return NeoBrutalistTheme.accentBlue();
            case SUCCESS:
                return NeoBrutalistTheme.accentGreen();
            case DANGER:
                return NeoBrutalistTheme.accentRed();
            case WARNING:
                return NeoBrutalistTheme.accentYellow();
            case DEFAULT:
            default:
                if (isHovered) {
                    return NeoBrutalistTheme.isDark() ? new Color(50, 50, 56) : new Color(245, 245, 245);
                }
                return NeoBrutalistTheme.surface();
        }
    }

    private Color determineTextColor() {
        if (!isEnabled()) {
            return NeoBrutalistTheme.textMuted();
        }

        switch (buttonStyle) {
            case PRIMARY:
            case SUCCESS:
            case WARNING:
                return Color.BLACK; // High-contrast black on bright vibrant color blocks
            case DANGER:
                return Color.WHITE;
            case DEFAULT:
            default:
                return NeoBrutalistTheme.textPrimary();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension s = super.getPreferredSize();
        int padH = shadowOffset + 12;
        int padW = shadowOffset + 20;
        return new Dimension(Math.max(s.width + padW, 84), Math.max(s.height + padH, 36));
    }
}
