package de.lorberry.arsen.gui;

import de.lorberry.arsen.util.HexUtil;

import javax.swing.*;
import java.awt.*;

public class HexViewPanel extends JPanel {
    private JTextArea hexTextArea;
    private byte[] data;
    private long baseAddress;

    public HexViewPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        hexTextArea = new JTextArea();
        hexTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        hexTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(hexTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField addressField = new JTextField(15);
        JButton goButton = new JButton("Go to Address");

        goButton.addActionListener(e -> {
            try {
                String addrText = addressField.getText().trim();
                if (addrText.startsWith("0x")) {
                    addrText = addrText.substring(2);
                }
                long address = Long.parseLong(addrText, 16);
                scrollToAddress(address);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid address format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        controlPanel.add(new JLabel("Address:"));
        controlPanel.add(addressField);
        controlPanel.add(goButton);

        add(controlPanel, BorderLayout.NORTH);
    }

    public void setData(byte[] data, long baseAddress) {
        this.data = data;
        this.baseAddress = baseAddress;
        displayHex();
    }

    private void displayHex() {
        if (data == null) {
            hexTextArea.setText("");
            return;
        }

        String hexDump = HexUtil.formatHexDump(data, baseAddress);
        hexTextArea.setText(hexDump);
        hexTextArea.setCaretPosition(0);
    }

    private void scrollToAddress(long address) {
        if (data == null) {
            return;
        }

        long offset = address - baseAddress;
        if (offset < 0 || offset >= data.length) {
            JOptionPane.showMessageDialog(this, "Address out of range", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int lineNumber = (int) (offset / 16);
        try {
            int position = hexTextArea.getLineStartOffset(lineNumber);
            hexTextArea.setCaretPosition(position);
            Rectangle viewRect = hexTextArea.modelToView(position);
            hexTextArea.scrollRectToVisible(viewRect);
        } catch (Exception e) {
        }
    }
}
