package de.lorberry.arsen.gui;

import de.lorberry.arsen.disasm.Architecture;
import de.lorberry.arsen.disasm.CapstoneDisassembler;
import de.lorberry.arsen.disasm.Disassembler;
import de.lorberry.arsen.disasm.DisassemblyLine;
import de.lorberry.arsen.format.ExecutableFile;
import de.lorberry.arsen.util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DisassemblyPanel extends JPanel {
    private JTable disassemblyTable;
    private DefaultTableModel tableModel;
    private final MainFrame mainFrame;
    private ExecutableFile currentFile;
    private final Disassembler disassembler;

    public DisassemblyPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.disassembler = new CapstoneDisassembler();
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        String[] columns = {"Address", "Bytes", "Mnemonic", "Operands"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        disassemblyTable = new JTable(tableModel);
        disassemblyTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        disassemblyTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        disassemblyTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        disassemblyTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        disassemblyTable.getColumnModel().getColumn(3).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(disassemblyTable);
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
                disassembleAt(address);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid address format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        controlPanel.add(new JLabel("Address:"));
        controlPanel.add(addressField);
        controlPanel.add(goButton);

        add(controlPanel, BorderLayout.NORTH);
    }

    public void setExecutableFile(ExecutableFile file) {
        this.currentFile = file;
    }

    public void disassembleAt(long address) {
        if (currentFile == null) {
            return;
        }

        Logger.info("Disassembling at address: 0x" + Long.toHexString(address));

        tableModel.setRowCount(0);

        byte[] code = currentFile.getDataAtVirtualAddress(address, 1024);
        if (code == null) {
            Logger.error("Failed to read data at address: 0x" + Long.toHexString(address));
            return;
        }

        Architecture arch = Architecture.X86_64;
        List<DisassemblyLine> lines = disassembler.disassemble(code, address, arch);

        for (DisassemblyLine line : lines) {
            Object[] row = {String.format("0x%08X", line.address()), line.getBytesHex(), line.mnemonic(), line.operands()};
            tableModel.addRow(row);
        }

        mainFrame.getNavigationHistory().navigate(address);
    }

    public void clear() {
        tableModel.setRowCount(0);
    }
}
