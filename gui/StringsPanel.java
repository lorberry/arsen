package de.lorberry.arsen.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StringsPanel extends JPanel {
    private JTable stringsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public StringsPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        String[] columns = {"Index", "String", "Length"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stringsTable = new JTable(tableModel);
        stringsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(stringsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
    }

    public void setStrings(List<String> strings) {
        tableModel.setRowCount(0);

        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);
            Object[] row = {i, str, str.length()};
            tableModel.addRow(row);
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            return;
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String str = tableModel.getValueAt(i, 1).toString().toLowerCase();
            if (str.contains(searchText)) {
                stringsTable.setRowSelectionInterval(i, i);
                stringsTable.scrollRectToVisible(stringsTable.getCellRect(i, 0, true));
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "String not found", "Search Result", JOptionPane.INFORMATION_MESSAGE);
    }
}
