package de.heil_privat.keepasstool;

import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.Entry;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class KeepassEntryTable extends JPanel {
    private final JTable table;
    private final ApplicationModel model = ApplicationModel.getInstance();
    private String filter = "";
    private Database<?, ?, ?, ?> db;

    public KeepassEntryTable() {
        setLayout(new BorderLayout());
        table = new JTable(new KeepassEntryTableModel());
        add(new JScrollPane(table), BorderLayout.CENTER);
        model.addDatabaseOpenListener(db -> {
            this.db = db;
            updateTable();
        });
        table.getSelectionModel().addListSelectionListener(this::onSelection);
    }

    public void setFilter(String filter) {
        this.filter = filter;
        updateTable();
    }

    private void updateTable() {
        @SuppressWarnings("unchecked")
        List<Entry<?, ?, ?, ?>> entries = db == null ? Collections.emptyList() : (List<Entry<?, ?, ?, ?>>) db.findEntries(filter);
        table.setModel(new KeepassEntryTableModel(entries));
    }

    private void onSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int selectedRow = table.getSelectedRow();
        KeepassEntryTableModel tableModel = (KeepassEntryTableModel) table.getModel();
        model.setSelectedEntry(tableModel.getEntry(selectedRow));
    }

    private static class KeepassEntryTableModel extends AbstractTableModel {
        private final String[] columnNames = new String[]{"group", "name", "user", "url"};
        private final List<Entry<?, ?, ?, ?>> entries;

        public KeepassEntryTableModel() {
            this(Collections.emptyList());
        }

        public KeepassEntryTableModel(List<Entry<?, ?, ?, ?>> entries) {
            this.entries = entries;
        }

        public Entry<?, ?, ?, ?> getEntry(int row) {
            return (row < 0 || row >= entries.size()) ? null : entries.get(row);
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public int getRowCount() {
            return entries.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Entry<?, ?, ?, ?> entry = entries.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return entry.getParent().getPath();
                case 1:
                    return entry.getTitle();
                case 2:
                    return entry.getUsername();
                case 3:
                    return entry.getUrl();
            }
            return null;
        }
    }
}
