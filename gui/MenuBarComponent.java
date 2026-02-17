package de.lorberry.arsen.gui;

import javax.swing.*;
import java.io.File;

public class MenuBarComponent extends JMenuBar {
    private final MainFrame mainFrame;

    public MenuBarComponent(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initMenus();
    }

    private void initMenus() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save Project");
        JMenuItem loadProjectItem = new JMenuItem("Load Project");
        JMenuItem exitItem = new JMenuItem("Exit");

        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveProject());
        loadProjectItem.addActionListener(e -> loadProject());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(loadProjectItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem searchItem = new JMenuItem("Search");
        JMenuItem renameItem = new JMenuItem("Rename Symbol");

        editMenu.add(searchItem);
        editMenu.add(renameItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem toggleThemeItem = new JMenuItem("Toggle Theme");

        toggleThemeItem.addActionListener(e -> {
            ThemeManager.toggleTheme();
            SwingUtilities.updateComponentTreeUI(mainFrame);
        });

        viewMenu.add(toggleThemeItem);

        JMenu analysisMenu = new JMenu("Analysis");
        JMenuItem stringsItem = new JMenuItem("Extract Strings");
        JMenuItem functionsItem = new JMenuItem("Detect Functions");
        JMenuItem cfgItem = new JMenuItem("Build CFG");

        analysisMenu.add(stringsItem);
        analysisMenu.add(functionsItem);
        analysisMenu.add(cfgItem);

        JMenu pluginsMenu = new JMenu("Plugins");
        JMenuItem manageItem = new JMenuItem("Manage Plugins");

        pluginsMenu.add(manageItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> showAbout());

        helpMenu.add(aboutItem);

        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(analysisMenu);
        add(pluginsMenu);
        add(helpMenu);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            mainFrame.openFile(selectedFile.getAbsolutePath());
        }
    }

    private void saveProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Project");

        int result = fileChooser.showSaveDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            if (!path.endsWith(".arsen")) {
                path += ".arsen";
            }
            mainFrame.saveProject(path);
        }
    }

    private void loadProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Project");

        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            mainFrame.loadProject(selectedFile.getAbsolutePath());
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(mainFrame, "Arsen\nVersion 1.0\n\nJava 25", "About Arsen", JOptionPane.INFORMATION_MESSAGE);
    }
}
