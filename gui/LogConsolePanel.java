package de.lorberry.arsen.gui;

import de.lorberry.arsen.util.Logger;

import javax.swing.*;
import java.awt.*;

public class LogConsolePanel extends JPanel {
    private JTextArea logArea;

    public LogConsolePanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearButton = new JButton("Clear");

        clearButton.addActionListener(e -> {
            logArea.setText("");
            Logger.clear();
        });

        controlPanel.add(clearButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    public void addLog(Logger.LogEntry entry) {
        logArea.append(entry.toString() + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
