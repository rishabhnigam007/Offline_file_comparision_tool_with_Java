package com.javacompare.ui.neobrutalism;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

class NeoBrutalistComponentsTest {

    @Test
    void testNeoBrutalistPanel() {
        NeoBrutalistPanel panel = new NeoBrutalistPanel(new BorderLayout(), 8, 4, 2.0f);
        assertThat(panel.getShadowOffset()).isEqualTo(4);

        panel.setCustomFill(Color.YELLOW);
        assertThat(panel.getCustomFill()).isEqualTo(Color.YELLOW);
        assertThat(panel.isOpaque()).isFalse();
    }

    @Test
    void testNeoBrutalistButton() {
        NeoBrutalistButton button = new NeoBrutalistButton("⚡ COMPARE", NeoBrutalistButton.ButtonStyle.SUCCESS);
        assertThat(button.getText()).isEqualTo("⚡ COMPARE");
        assertThat(button.getButtonStyle()).isEqualTo(NeoBrutalistButton.ButtonStyle.SUCCESS);
        assertThat(button.isFocusPainted()).isFalse();
        assertThat(button.getPreferredSize().width).isGreaterThan(50);
        assertThat(button.getPreferredSize().height).isGreaterThan(20);

        button.setButtonStyle(NeoBrutalistButton.ButtonStyle.PRIMARY);
        assertThat(button.getButtonStyle()).isEqualTo(NeoBrutalistButton.ButtonStyle.PRIMARY);
    }

    @Test
    void testNeoBrutalistToggle() {
        NeoBrutalistToggle toggle = new NeoBrutalistToggle("Word wrap", true);
        assertThat(toggle.isSelected()).isTrue();
        assertThat(toggle.getText()).isEqualTo("Word wrap");
        assertThat(toggle.getPreferredSize().width).isGreaterThan(40);

        toggle.setSelected(false);
        assertThat(toggle.isSelected()).isFalse();
    }

    @Test
    void testNeoBrutalistBadge() {
        NeoBrutalistBadge badge = new NeoBrutalistBadge("✅ IDENTICAL", NeoBrutalistBadge.BadgeType.SUCCESS);
        assertThat(badge.getText()).isEqualTo("✅ IDENTICAL");

        badge.setText("⚠️ 3 DIFFS");
        badge.setBadgeType(NeoBrutalistBadge.BadgeType.DANGER);
        assertThat(badge.getText()).isEqualTo("⚠️ 3 DIFFS");
        assertThat(badge.getPreferredSize().width).isGreaterThan(40);
    }
}
