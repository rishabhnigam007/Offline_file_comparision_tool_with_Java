package com.javacompare;

import com.formdev.flatlaf.FlatLightLaf;
import com.javacompare.ui.MainWindow;
import com.javacompare.ui.neobrutalism.NeoBrutalistTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for Offline Java Compare Tool - Neo-Brutalist Edition.
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        log.info("Starting Offline Java Compare Tool (Java 21) - Neo-Brutalist Design System...");

        // Enable high-DPI scaling and crisp font anti-aliasing
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Initialize Neo-Brutalist Light theme by default
        try {
            UIManager.put("Panel.background", NeoBrutalistTheme.LIGHT_CANVAS);
            UIManager.put("Viewport.background", NeoBrutalistTheme.LIGHT_SURFACE);
            UIManager.put("Component.arc", 6);
            UIManager.put("Button.arc", 6);
            UIManager.setLookAndFeel(new FlatLightLaf());
            log.info("FlatLaf and Neo-Brutalist Theme initialized successfully.");
        } catch (Exception ex) {
            log.warn("Failed to initialize FlatLaf look and feel: {}", ex.getMessage());
        }

        // Launch UI safely on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow window = new MainWindow();
                window.setVisible(true);
                log.info("Neo-Brutalist main application window displayed.");
            } catch (Exception ex) {
                log.error("Failed to display main application window", ex);
            }
        });
    }
}