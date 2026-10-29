package com.javacompare.ui.neobrutalism;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Neo-Brutalist container panel with bold outlines and hard offset shadow.
 */
public class NeoBrutalistPanel extends JPanel {

    private int arc = 8;
    private int shadowOffset = 4;
    private float borderWidth = 2.0f;
    private Color customFill = null;
    private Color customBorder = null;
    private Color customShadow = null;

    public NeoBrutalistPanel() {
        this(new BorderLayout(), 8, 4, 2.0f);
    }

    public NeoBrutalistPanel(LayoutManager layout) {
        this(layout, 8, 4, 2.0f);
    }

    public NeoBrutalistPanel(LayoutManager layout, int arc, int shadowOffset, float borderWidth) {
        super(layout);
        this.arc = arc;
        this.shadowOffset = shadowOffset;
        this.borderWidth = borderWidth;
        setOpaque(false);

        NeoBrutalistTheme.addThemeChangeListener(this::repaint);
    }

    public void setCustomFill(Color fill) {
        this.customFill = fill;
        repaint();
    }

    public Color getCustomFill() {
        return customFill;
    }

    public void setCustomBorder(Color border) {
        this.customBorder = border;
        repaint();
    }

    public void setArc(int arc) {
        this.arc = arc;
        repaint();
    }

    public void setShadowOffset(int offset) {
        this.shadowOffset = offset;
        repaint();
    }

    public int getShadowOffset() {
        return shadowOffset;
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

        if (w > 0 && h > 0) {
            Color fill = (customFill != null) ? customFill : NeoBrutalistTheme.surface();
            Color border = (customBorder != null) ? customBorder : NeoBrutalistTheme.border();
            Color shadow = (customShadow != null) ? customShadow : NeoBrutalistTheme.shadow();

            NeoBrutalistUtils.paintCard(g2, x, y, w, h, arc, shadowOffset, borderWidth, fill, border, shadow);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
