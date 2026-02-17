package de.lorberry.arsen;

import de.lorberry.arsen.gui.MainFrame;
import de.lorberry.arsen.gui.ThemeManager;
import de.lorberry.arsen.util.Logger;

import javax.swing.*;

public class ArsenApplication {
    static void main(String[] args) {
        Logger.init();
        Logger.info("Starting Arsen");

        SwingUtilities.invokeLater(() -> {
            try {
                ThemeManager.applyLightTheme();
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                Logger.info("Application started successfully");
            } catch (Exception e) {
                Logger.error("Failed to start application: " + e.getMessage());
                System.exit(1);
            }
        });
    }
}
