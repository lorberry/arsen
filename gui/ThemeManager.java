package de.lorberry.arsen.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import de.lorberry.arsen.util.Logger;

import javax.swing.*;

public class ThemeManager {
    private static boolean isDarkMode = true;

    public static void applyDarkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            isDarkMode = true;
            Logger.info("Dark theme applied");
        } catch (Exception e) {
            Logger.error("Failed to apply dark theme: " + e.getMessage());
        }
    }

    public static void applyLightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            isDarkMode = false;
            Logger.info("Light theme applied");
        } catch (Exception e) {
            Logger.error("Failed to apply light theme: " + e.getMessage());
        }
    }

    public static void toggleTheme() {
        if (isDarkMode) {
            applyLightTheme();
        } else {
            applyDarkTheme();
        }
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }
}
