package de.heil_privat.keepasstool;

import org.linguafranca.pwdb.Entry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class KeepassToolGui extends JFrame {
    public static KeepassToolGui gui;

    public KeepassToolGui() {
        ApplicationModel model = ApplicationModel.getInstance();
        JPanel content = new JPanel(new BorderLayout());
        setContentPane(content);

        Action openFileAction = new OpenFileAction(content);
        EntrySelectionAction copyUserNameToClipboard = new EntrySelectionAction("Copy username", Entry::getUsername);
        model.addSelectedEntryListener(copyUserNameToClipboard::setEntry);
        EntrySelectionAction copyPasswordToClipboard = new EntrySelectionAction("Copy password", Entry::getPassword);
        model.addSelectedEntryListener(copyPasswordToClipboard::setEntry);
        EntrySelectionAction copyUrlToClipboard = new EntrySelectionAction("Copy url", Entry::getUrl);
        model.addSelectedEntryListener(copyUrlToClipboard::setEntry);

        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(openFileAction));
        menubar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem(copyUserNameToClipboard));
        editMenu.add(new JMenuItem(copyPasswordToClipboard));
        editMenu.add(new JMenuItem(copyUrlToClipboard));
        menubar.add(editMenu);

        JToolBar toolbar = new JToolBar();
        toolbar.add(new JButton(openFileAction));
        toolbar.addSeparator();
        toolbar.add(new JButton(copyUserNameToClipboard));
        toolbar.add(new JButton(copyPasswordToClipboard));
        toolbar.add(new JButton(copyUrlToClipboard));
        content.add(toolbar, BorderLayout.NORTH);
        toolbar.addSeparator();

        toolbar.add(new JLabel("search entries by title"));
        JTextField textField = new JTextField();
        toolbar.add(textField);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        DatabaseTreeView databaseTreeView = new DatabaseTreeView();
        split.add(databaseTreeView);

        KeepassEntryTable entryTable = new KeepassEntryTable();

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                entryTable.setFilter(textField.getText());
            }
        });
        split.add(entryTable);

        content.add(split, BorderLayout.CENTER);
    }

    public static void main(String... args) {
        ApplicationModel model = ApplicationModel.getInstance();
        SwingUtilities.invokeLater(() -> {
            gui = new KeepassToolGui();
            gui.setSize(1024, 768);
            gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            gui.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    model.saveSettings();
                }
            });
            gui.setVisible(true);
        });
    }
}