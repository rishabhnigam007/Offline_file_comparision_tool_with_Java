package com.javacompare.ui.neobrutalism;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Neo-Brutalist Design System Theme Manager.
 * Characterized by bold solid black borders, hard offset drop shadows (zero blur),
 * punchy saturated color-blocking (Electric Yellow, Lime Green, Coral Pink, Cyan),
 * and high-contrast typography.
 */
public final class NeoBrutalistTheme {

    private static boolean dark = false;
    private static final List<Runnable> listeners = new ArrayList<>();

    // --- Neo-Brutalist Light Palette ---
    public static final Color LIGHT_CANVAS = new Color(253, 250, 243);         // #FDFAF3 Warm retro cream
    public static final Color LIGHT_SURFACE = new Color(255, 255, 255);        // #FFFFFF Crisp stark white
    public static final Color LIGHT_SURFACE_ALT = new Color(245, 241, 233);    // #F5F1E9
    public static final Color LIGHT_BORDER = new Color(0, 0, 0);               // #000000 Solid thick black
    public static final Color LIGHT_SHADOW = new Color(0, 0, 0);               // #000000 Hard offset shadow (0 blur)

    public static final Color LIGHT_TEXT_PRIMARY = new Color(0, 0, 0);         // #000000 Pure Black
    public static final Color LIGHT_TEXT_SECONDARY = new Color(60, 60, 60);    // #3C3C3C
    public static final Color LIGHT_TEXT_MUTED = new Color(110, 110, 110);     // #6E6E6E

    // Vibrant Neo-Brutalist Saturated Accents
    public static final Color ACCENT_YELLOW = new Color(255, 230, 0);          // #FFE600 Electric Yellow
    public static final Color ACCENT_GREEN = new Color(0, 240, 118);           // #00F076 Vibrant Lime
    public static final Color ACCENT_RED = new Color(255, 67, 101);            // #FF4365 Electric Pink-Red
    public static final Color ACCENT_BLUE = new Color(56, 182, 255);           // #38B6FF Electric Sky Blue
    public static final Color ACCENT_PURPLE = new Color(162, 89, 255);         // #A259FF Retro Purple
    public static final Color ACCENT_ORANGE = new Color(255, 145, 77);         // #FF914D Punchy Orange

    // Diff states for Light Neo-Brutalism
    public static final Color LIGHT_DIFF_INSERT_BG = new Color(230, 252, 232); // #E6FCE8 High-contrast light mint
    public static final Color LIGHT_DIFF_INSERT_BORDER = new Color(0, 200, 80);
    public static final Color LIGHT_DIFF_INSERT_TEXT = new Color(0, 120, 45);
    public static final Color LIGHT_DIFF_INSERT_BADGE = new Color(0, 240, 118);

    public static final Color LIGHT_DIFF_DELETE_BG = new Color(255, 235, 239); // #FFEBEF High-contrast light rose
    public static final Color LIGHT_DIFF_DELETE_BORDER = new Color(255, 67, 101);
    public static final Color LIGHT_DIFF_DELETE_TEXT = new Color(196, 0, 38);
    public static final Color LIGHT_DIFF_DELETE_BADGE = new Color(255, 67, 101);

    public static final Color LIGHT_DIFF_GAP_BG = new Color(242, 242, 242);
    public static final Color LIGHT_DIFF_GAP_STRIPE = new Color(190, 190, 190);

    // --- Cyber Neo-Brutalist Dark Palette ---
    public static final Color DARK_CANVAS = new Color(20, 20, 22);             // #141416 Deep obsidian
    public static final Color DARK_SURFACE = new Color(30, 30, 34);            // #1E1E22
    public static final Color DARK_SURFACE_ALT = new Color(40, 40, 46);
    public static final Color DARK_BORDER = new Color(0, 0, 0);                // #000000 Solid heavy border
    public static final Color DARK_BORDER_OUTLINE = new Color(255, 255, 255);  // #FFFFFF High-contrast perimeter
    public static final Color DARK_SHADOW = new Color(0, 0, 0);                // Hard black drop shadow

    public static final Color DARK_TEXT_PRIMARY = new Color(255, 255, 255);    // #FFFFFF Pure White
    public static final Color DARK_TEXT_SECONDARY = new Color(210, 210, 210);
    public static final Color DARK_TEXT_MUTED = new Color(150, 150, 150);

    // Diff states for Dark Neo-Brutalism
    public static final Color DARK_DIFF_INSERT_BG = new Color(12, 44, 22);
    public static final Color DARK_DIFF_INSERT_BORDER = new Color(0, 240, 118);
    public static final Color DARK_DIFF_INSERT_TEXT = new Color(130, 255, 170);
    public static final Color DARK_DIFF_INSERT_BADGE = new Color(0, 180, 85);

    public static final Color DARK_DIFF_DELETE_BG = new Color(55, 14, 22);
    public static final Color DARK_DIFF_DELETE_BORDER = new Color(255, 67, 101);
    public static final Color DARK_DIFF_DELETE_TEXT = new Color(255, 160, 175);
    public static final Color DARK_DIFF_DELETE_BADGE = new Color(190, 30, 60);

    public static final Color DARK_DIFF_GAP_BG = new Color(24, 24, 28);
    public static final Color DARK_DIFF_GAP_STRIPE = new Color(50, 50, 56);

    private NeoBrutalistTheme() {}

    public static boolean isDark() {
        return dark;
    }

    public static void setDark(boolean isDark) {
        dark = isDark;
        for (Runnable listener : new ArrayList<>(listeners)) {
            try {
                listener.run();
            } catch (Exception ignored) {}
        }
    }

    public static void addThemeChangeListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeThemeChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    // --- Dynamic Color Getters ---
    public static Color canvas() {
        return dark ? DARK_CANVAS : LIGHT_CANVAS;
    }

    public static Color surface() {
        return dark ? DARK_SURFACE : LIGHT_SURFACE;
    }

    public static Color surfaceAlt() {
        return dark ? DARK_SURFACE_ALT : LIGHT_SURFACE_ALT;
    }

    public static Color border() {
        return dark ? DARK_BORDER_OUTLINE : LIGHT_BORDER;
    }

    public static Color shadow() {
        return dark ? DARK_SHADOW : LIGHT_SHADOW;
    }

    public static Color textPrimary() {
        return dark ? DARK_TEXT_PRIMARY : LIGHT_TEXT_PRIMARY;
    }

    public static Color textSecondary() {
        return dark ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;
    }

    public static Color textMuted() {
        return dark ? DARK_TEXT_MUTED : LIGHT_TEXT_MUTED;
    }

    public static Color accentYellow() {
        return ACCENT_YELLOW;
    }

    public static Color accentGreen() {
        return ACCENT_GREEN;
    }

    public static Color accentRed() {
        return ACCENT_RED;
    }

    public static Color accentBlue() {
        return ACCENT_BLUE;
    }

    public static Color accentPurple() {
        return ACCENT_PURPLE;
    }

    public static Color accentOrange() {
        return ACCENT_ORANGE;
    }

    // Diff colors
    public static Color diffInsertBg() {
        return dark ? DARK_DIFF_INSERT_BG : LIGHT_DIFF_INSERT_BG;
    }

    public static Color diffInsertBorder() {
        return dark ? DARK_DIFF_INSERT_BORDER : LIGHT_DIFF_INSERT_BORDER;
    }

    public static Color diffInsertText() {
        return dark ? DARK_DIFF_INSERT_TEXT : LIGHT_DIFF_INSERT_TEXT;
    }

    public static Color diffInsertBadge() {
        return dark ? DARK_DIFF_INSERT_BADGE : LIGHT_DIFF_INSERT_BADGE;
    }

    public static Color diffDeleteBg() {
        return dark ? DARK_DIFF_DELETE_BG : LIGHT_DIFF_DELETE_BG;
    }

    public static Color diffDeleteBorder() {
        return dark ? DARK_DIFF_DELETE_BORDER : LIGHT_DIFF_DELETE_BORDER;
    }

    public static Color diffDeleteText() {
        return dark ? DARK_DIFF_DELETE_TEXT : LIGHT_DIFF_DELETE_TEXT;
    }

    public static Color diffDeleteBadge() {
        return dark ? DARK_DIFF_DELETE_BADGE : LIGHT_DIFF_DELETE_BADGE;
    }

    public static Color diffGapBg() {
        return dark ? DARK_DIFF_GAP_BG : LIGHT_DIFF_GAP_BG;
    }

    public static Color diffGapStripe() {
        return dark ? DARK_DIFF_GAP_STRIPE : LIGHT_DIFF_GAP_STRIPE;
    }
}
