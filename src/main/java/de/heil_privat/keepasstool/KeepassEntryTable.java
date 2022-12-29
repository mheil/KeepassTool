package de.heil_privat.keepasstool;

import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Group;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeepassEntryTable extends JPanel {
    private final JTable table;
    private final ApplicationModel model = ApplicationModel.getInstance();
    private String filter = "";
    private Group<?, ?, ?, ?> group = null;
    private List<Entry<?, ?, ?, ?>> entries = Collections.emptyList();

    @SuppressWarnings("unchecked")
    public KeepassEntryTable() {
        setLayout(new BorderLayout());
        table = new JTable(new KeepassEntryTableModel());
        add(new JScrollPane(table), BorderLayout.CENTER);
        model.addDatabaseOpenListener(db -> {
            entries = (List<Entry<?, ?, ?, ?>>) db.findEntries(e -> true);
            updateTable();
        });
        table.getSelectionModel().addListSelectionListener(this::onSelection);
    }

    public void setFilter(String filter) {
        this.filter = filter;
        updateTable();
    }

    public void setGroup(Group<?, ?, ?, ?> group) {
        this.group = group;
        updateTable();
    }

    private void updateTable() {
        Predicate<Entry<?, ?, ?, ?>> matcher = null;
        if (filter != null && !filter.isEmpty()) {
            matcher = e -> e.getTitle().toLowerCase().contains(filter.toLowerCase());
        } else if (group != null) {
            matcher = e -> Objects.equals(e.getParent(), group);
        }

        List<Entry<?, ?, ?, ?>> filteredList;
        if (matcher == null) {
            filteredList = entries;
        } else {
            filteredList = entries.stream().filter(matcher).collect(Collectors.toList());
        }

        ((KeepassEntryTableModel)table.getModel()).setEntries(filteredList);
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
        private List<Entry<?, ?, ?, ?>> entries = Collections.emptyList();

        public KeepassEntryTableModel() {
        }

        public void setEntries(List<Entry<?, ?, ?, ?>> entries) {
            this.entries = entries;
            fireTableDataChanged();
        }

        public Entry<?, ?, ?, ?> getEntry(int row) {
            return row < 0 || row >= entries.size() ? null : entries.get(row);
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
