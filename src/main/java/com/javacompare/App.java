package com.javacompare;

import com.formdev.flatlaf.FlatLightLaf;
import com.javacompare.ui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for Offline Java Compare Tool.
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        log.info("Starting Offline Java Compare Tool (Java 21)...");

        // Enable high-DPI scaling and crisp font anti-aliasing
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Initialize pure clean White theme by default
        try {
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("Viewport.background", Color.WHITE);
            UIManager.setLookAndFeel(new FlatLightLaf());
            log.info("FlatLaf theme initialized successfully.");
        } catch (Exception ex) {
            log.warn("Failed to initialize FlatLaf look and feel: {}", ex.getMessage());
        }

        // Launch UI safely on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow window = new MainWindow();
                window.setVisible(true);
                log.info("Main application window displayed.");
            } catch (Exception ex) {
                log.error("Failed to display main application window", ex);
            }
        });
    }
}
