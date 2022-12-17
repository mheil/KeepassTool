package de.heil_privat.keepasstool;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OpenFileAction extends AbstractAction {
    private final JComponent parent;

    public OpenFileAction(JComponent parent) {
        super("Open File");
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new OpenDatabaseDialog(parent).setVisible(true);
    }
}
