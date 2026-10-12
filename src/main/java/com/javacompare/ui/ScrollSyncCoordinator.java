package com.javacompare.ui;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * Coordinates synchronized scrolling across dual editor panes without recursive event loops.
 */
public class ScrollSyncCoordinator {

    private final JScrollPane leftScrollPane;
    private final JScrollPane rightScrollPane;
    private boolean isUpdating = false;

    public ScrollSyncCoordinator(JScrollPane leftScrollPane, JScrollPane rightScrollPane) {
        this.leftScrollPane = leftScrollPane;
        this.rightScrollPane = rightScrollPane;
        setupSync();
    }

    private void setupSync() {
        JScrollBar leftVBar = leftScrollPane.getVerticalScrollBar();
        JScrollBar rightVBar = rightScrollPane.getVerticalScrollBar();

        AdjustmentListener leftListener = (AdjustmentEvent e) -> {
            if (isUpdating) return;
            isUpdating = true;
            try {
                rightVBar.setValue(e.getValue());
            } finally {
                isUpdating = false;
            }
        };

        AdjustmentListener rightListener = (AdjustmentEvent e) -> {
            if (isUpdating) return;
            isUpdating = true;
            try {
                leftVBar.setValue(e.getValue());
            } finally {
                isUpdating = false;
            }
        };

        leftVBar.addAdjustmentListener(leftListener);
        rightVBar.addAdjustmentListener(rightListener);

        // Horizontal sync
        JScrollBar leftHBar = leftScrollPane.getHorizontalScrollBar();
        JScrollBar rightHBar = rightScrollPane.getHorizontalScrollBar();

        leftHBar.addAdjustmentListener(e -> {
            if (isUpdating) return;
            isUpdating = true;
            try {
                rightHBar.setValue(e.getValue());
            } finally {
                isUpdating = false;
            }
        });

        rightHBar.addAdjustmentListener(e -> {
            if (isUpdating) return;
            isUpdating = true;
            try {
                leftHBar.setValue(e.getValue());
            } finally {
                isUpdating = false;
            }
        });
    }

    public void scrollToRow(int rowIndex, int totalRows) {
        if (totalRows <= 0) return;
        JScrollBar vBar = leftScrollPane.getVerticalScrollBar();
        int max = vBar.getMaximum() - vBar.getVisibleAmount();
        int targetVal = (int) Math.round(((double) rowIndex / (double) totalRows) * max);
        vBar.setValue(Math.max(0, Math.min(max, targetVal)));
    }

    public void scrollToFraction(double fraction) {
        JScrollBar vBar = leftScrollPane.getVerticalScrollBar();
        int max = vBar.getMaximum() - vBar.getVisibleAmount();
        int targetVal = (int) Math.round(fraction * max);
        vBar.setValue(Math.max(0, Math.min(max, targetVal)));
    }
}
