package de.heil_privat.keepasstool;

import com.famfamfam.silk.SilkIcons;
import de.heil_privat.keepasstool.actions.EntrySelectionAction;
import de.heil_privat.keepasstool.actions.OpenAboutDialogAction;
import de.heil_privat.keepasstool.actions.OpenFileAction;
import org.linguafranca.pwdb.Entry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KeepassToolGui extends JFrame {
    public static KeepassToolGui gui;
    private final OpenFileAction openFileAction;

    public KeepassToolGui() {
        setTitle("KeepassTool");
        ApplicationModel model = ApplicationModel.getInstance();
        JPanel content = new JPanel(new BorderLayout());
        setContentPane(content);

        openFileAction = new OpenFileAction(content);
        EntrySelectionAction copyUserNameToClipboard = new EntrySelectionAction("Copy username",
                SilkIcons.ICON_USER, Entry::getUsername);
        model.addSelectedEntryListener(copyUserNameToClipboard::setEntry);
        EntrySelectionAction copyPasswordToClipboard = new EntrySelectionAction("Copy password", SilkIcons.ICON_KEY, Entry::getPassword);
        model.addSelectedEntryListener(copyPasswordToClipboard::setEntry);
        EntrySelectionAction copyUrlToClipboard = new EntrySelectionAction("Copy url", SilkIcons.ICON_WORLD_LINK, Entry::getUrl);
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

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem(new OpenAboutDialogAction(this)));
        menubar.add(helpMenu);

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
                public void windowOpened(WindowEvent e) {
                    gui.openFileAction.actionPerformed(new ActionEvent(gui, 0, null));
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    model.saveSettings();
                }
            });
            gui.setVisible(true);
        });
    }
}
