package de.heil_privat.keepasstool;

import com.famfamfam.silk.SilkIcons;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.stream.Stream;

import static de.heil_privat.keepasstool.ApplicationModel.Settting.LAST_KEEPASSFILE;

public class OpenDatabaseDialog extends JDialog {
    private final ApplicationModel model = ApplicationModel.getInstance();
    private final JTextField databaseFile;
    private final JPasswordField passwordField;
    private final JComboBox<DbImplementation> typeChooser;

    public OpenDatabaseDialog(final JComponent parent) {
        super((JFrame) SwingUtilities.getRoot(parent), "Open Keepass database file", true);

        setModalityType(ModalityType.APPLICATION_MODAL);

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);

        gc.gridx = 0;
        gc.gridy = 0;
        add(new JLabel("Database:"), gc);

        gc.gridx++;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        databaseFile = new JTextField(20);
        databaseFile.setText(model.get(ApplicationModel.Settting.LAST_KEEPASSFILE, ""));
        add(databaseFile, gc);

        gc.gridx++;
        gc.weightx = 0.0;
        gc.fill = GridBagConstraints.NONE;
        JButton selectFile = new JButton(SilkIcons.ICON_FOLDER);
        selectFile.addActionListener(e -> selectFile());
        add(selectFile, gc);

        gc.gridx = 0;
        gc.gridy++;
        add(new JLabel("Password:"), gc);

        gc.gridx++;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        passwordField = new JPasswordField(20);
        add(passwordField, gc);

        gc.gridx = 0;
        gc.gridy++;
        gc.weightx = 0.0;
        gc.gridwidth = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Type:"), gc);

        gc.gridx++;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        typeChooser = new JComboBox<>(DbImplementation.values());
        typeChooser.setSelectedItem(model.get(ApplicationModel.Settting.DB_TYPE, DbImplementation.JaxB));
        typeChooser.addItemListener(e -> model.set(ApplicationModel.Settting.DB_TYPE, (DbImplementation) typeChooser.getSelectedItem()));
        add(typeChooser, gc);

        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 3;
        gc.weightx = 1.0;
        gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gc);

        JPanel okCancel = new JPanel();
        okCancel.setLayout(new BoxLayout(okCancel, BoxLayout.X_AXIS));
        okCancel.add(Box.createGlue());
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> openFile());
        okCancel.add(ok);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        okCancel.add(cancel);

        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 3;
        gc.weightx = 0.0;
        gc.weighty = 0.0;
        add(okCancel, gc);

        Stream.of(databaseFile, selectFile, passwordField, ok).forEach(c -> c.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case KeyEvent.VK_ENTER:
                        openFile();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        dispose();
                        break;
                }
            }
        }));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                if (databaseFile.getText().isEmpty()) {
                    databaseFile.requestFocusInWindow();
                } else {
                    passwordField.requestFocusInWindow();
                }
            }
        });

        pack();
        setLocationRelativeTo(parent);
    }

    private void selectFile() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setSelectedFile(new File(ApplicationModel.getInstance().get(LAST_KEEPASSFILE, "")));
        fileDialog.setMultiSelectionEnabled(false);
        fileDialog.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".kdbx");
            }

            @Override
            public String getDescription() {
                return "Keepass Database (*.kdbx)";
            }
        });
        if (fileDialog.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileDialog.getSelectedFile();
            databaseFile.setText(selectedFile.getPath());
            model.set(LAST_KEEPASSFILE, selectedFile.getPath());
        }
    }

    private void openFile() {
        File dbFile = new File(databaseFile.getText());
        char[] password = passwordField.getPassword();
        DbImplementation dbType = (DbImplementation) typeChooser.getSelectedItem();
        byte[] passwordBytes = new byte[password.length];
        for (int i = 0; i < password.length; i++) {
            passwordBytes[i] = (byte) password[i];
        }

        new Thread(() -> model.openKeepassFile(dbFile, dbType, passwordBytes)).start();
        dispose();
    }
}
