package de.heil_privat.keepasstool.actions;

import com.famfamfam.silk.SilkIcons;
import de.heil_privat.keepasstool.OpenDatabaseDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OpenFileAction extends AbstractAction {
    private final JComponent parent;

    public OpenFileAction(JComponent parent) {
        super("Open File", SilkIcons.ICON_FOLDER);
        putValue(Action.SHORT_DESCRIPTION, "Open Database file");
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new OpenDatabaseDialog(parent).setVisible(true);
    }
}
