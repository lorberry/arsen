package de.lorberry.arsen.gui;

import de.lorberry.arsen.format.Section;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SectionsPanel extends JPanel {
    private JTable sectionsTable;
    private DefaultTableModel tableModel;

    public SectionsPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        String[] columns = {"Name", "Virtual Address", "Virtual Size", "Raw Address", "Raw Size", "Characteristics"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sectionsTable = new JTable(tableModel);
        sectionsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setSections(List<Section> sections) {
        tableModel.setRowCount(0);

        for (Section section : sections) {
            Object[] row = {section.name(), String.format("0x%08X", section.virtualAddress()), String.format("0x%08X", section.virtualSize()), String.format("0x%08X", section.rawAddress()), String.format("0x%08X", section.rawSize()), getCharacteristicsString(section)};
            tableModel.addRow(row);
        }
    }

    private String getCharacteristicsString(Section section) {
        StringBuilder sb = new StringBuilder();
        if (section.isReadable()) sb.append("R");
        if (section.isWritable()) sb.append("W");
        if (section.isExecutable()) sb.append("X");
        return sb.toString();
    }
}
