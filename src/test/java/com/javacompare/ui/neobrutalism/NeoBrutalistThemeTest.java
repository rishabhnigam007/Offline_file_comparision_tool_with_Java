package com.javacompare.ui.neobrutalism;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class NeoBrutalistThemeTest {

    @AfterEach
    void resetTheme() {
        NeoBrutalistTheme.setDark(false);
    }

    @Test
    void testInitialThemeIsLight() {
        NeoBrutalistTheme.setDark(false);
        assertThat(NeoBrutalistTheme.isDark()).isFalse();
        assertThat(NeoBrutalistTheme.canvas()).isEqualTo(NeoBrutalistTheme.LIGHT_CANVAS);
        assertThat(NeoBrutalistTheme.surface()).isEqualTo(NeoBrutalistTheme.LIGHT_SURFACE);
        assertThat(NeoBrutalistTheme.border()).isEqualTo(NeoBrutalistTheme.LIGHT_BORDER);
    }

    @Test
    void testThemeToggleAndListenerNotification() {
        AtomicBoolean notified = new AtomicBoolean(false);
        Runnable listener = () -> notified.set(true);

        NeoBrutalistTheme.addThemeChangeListener(listener);
        NeoBrutalistTheme.setDark(true);

        assertThat(NeoBrutalistTheme.isDark()).isTrue();
        assertThat(notified.get()).isTrue();
        assertThat(NeoBrutalistTheme.canvas()).isEqualTo(NeoBrutalistTheme.DARK_CANVAS);
        assertThat(NeoBrutalistTheme.surface()).isEqualTo(NeoBrutalistTheme.DARK_SURFACE);

        NeoBrutalistTheme.removeThemeChangeListener(listener);
    }

    @Test
    void testDiffColorsExistAndAreDistinct() {
        NeoBrutalistTheme.setDark(false);
        Color lightInsert = NeoBrutalistTheme.diffInsertBg();
        Color lightDelete = NeoBrutalistTheme.diffDeleteBg();
        Color lightGap = NeoBrutalistTheme.diffGapBg();

        assertThat(lightInsert).isNotNull();
        assertThat(lightDelete).isNotNull();
        assertThat(lightGap).isNotNull();
        assertThat(lightInsert).isNotEqualTo(lightDelete);

        NeoBrutalistTheme.setDark(true);
        Color darkInsert = NeoBrutalistTheme.diffInsertBg();
        Color darkDelete = NeoBrutalistTheme.diffDeleteBg();

        assertThat(darkInsert).isNotNull();
        assertThat(darkDelete).isNotNull();
        assertThat(darkInsert).isNotEqualTo(darkDelete);
    }
}
