package de.lorberry.arsen.gui;

import javax.swing.*;

public class ToolBarComponent extends JToolBar {
    private final MainFrame mainFrame;

    public ToolBarComponent(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setFloatable(false);
        initButtons();
    }

    private void initButtons() {
        JButton openButton = createButton("Open", "Open file");
        JButton saveButton = createButton("Save", "Save project");
        addSeparator();
        JButton backButton = createButton("←", "Go back");
        JButton forwardButton = createButton("→", "Go forward");
        addSeparator();
        JButton searchButton = createButton("Search", "Search");
        JButton analyzeButton = createButton("Analyze", "Run analysis");
        addSeparator();
        JButton themeButton = createButton("Theme", "Toggle theme");

        openButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                mainFrame.openFile(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        backButton.addActionListener(e -> {
            if (mainFrame.getNavigationHistory().canGoBack()) {
                long address = mainFrame.getNavigationHistory().goBack();
            }
        });

        forwardButton.addActionListener(e -> {
            if (mainFrame.getNavigationHistory().canGoForward()) {
                long address = mainFrame.getNavigationHistory().goForward();
            }
        });

        themeButton.addActionListener(e -> {
            ThemeManager.toggleTheme();
            SwingUtilities.updateComponentTreeUI(mainFrame);
        });

        add(openButton);
        add(saveButton);
        add(backButton);
        add(forwardButton);
        add(searchButton);
        add(analyzeButton);
        add(themeButton);
    }

    private JButton createButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        return button;
    }
}
