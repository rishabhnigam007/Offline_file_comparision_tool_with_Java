package com.javacompare.ui.neobrutalism;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Rendering utility for Neo-Brutalist graphics:
 * Hard solid drop shadows (zero blur), bold black border outlines, and flat color-blocking.
 */
public final class NeoBrutalistUtils {

    private NeoBrutalistUtils() {}

    /**
     * Applies high-contrast anti-aliasing hints for crisp vector outlines.
     */
    public static void applyQualityHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * Renders a classic Neo-Brutalist card with solid hard drop shadow and thick solid border.
     *
     * @param g2 Graphics context
     * @param x Inner X
     * @param y Inner Y
     * @param w Inner Width
     * @param h Inner Height
     * @param arc Corner radius (e.g. 6-8px, or 0 for stark sharp)
     * @param shadowOffset Hard shadow translation (e.g. 3-5px)
     * @param borderWidth Border line thickness (e.g. 2.0f)
     * @param fillColor Body fill color
     * @param borderColor Outline border color (usually pure black)
     * @param shadowColor Hard drop shadow color (pure black)
     */
    public static void paintCard(Graphics2D g2, int x, int y, int w, int h, int arc,
                                 int shadowOffset, float borderWidth,
                                 Color fillColor, Color borderColor, Color shadowColor) {
        if (w <= 0 || h <= 0) return;

        // 1. Hard Offset Drop Shadow (Zero Blur)
        if (shadowOffset > 0) {
            g2.setColor(shadowColor != null ? shadowColor : Color.BLACK);
            g2.fill(new RoundRectangle2D.Float(
                    x + shadowOffset,
                    y + shadowOffset,
                    w,
                    h,
                    arc,
                    arc
            ));
        }

        // 2. Main Card Body
        RoundRectangle2D.Float bodyShape = new RoundRectangle2D.Float(x, y, w, h, arc, arc);
        g2.setColor(fillColor != null ? fillColor : Color.WHITE);
        g2.fill(bodyShape);

        // 3. Bold Solid Border Outline
        if (borderWidth > 0.0f) {
            g2.setColor(borderColor != null ? borderColor : Color.BLACK);
            g2.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(bodyShape);
        }
    }
}
