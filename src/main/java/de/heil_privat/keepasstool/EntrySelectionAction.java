package de.heil_privat.keepasstool;

import org.linguafranca.pwdb.Entry;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.function.Function;

public class EntrySelectionAction extends AbstractAction {
    private final Function<Entry<?, ?, ?, ?>, String> clipBoardValueExtractor;
    private Entry<?, ?, ?, ?> entry;

    public EntrySelectionAction(String name, Function<Entry<?, ?, ?, ?>, String> clipBoardValueExtractor) {
        super(name);
        this.clipBoardValueExtractor = clipBoardValueExtractor;
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String clipBoardValue = clipBoardValueExtractor.apply(entry);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(clipBoardValue), null);
    }

    public void setEntry(Entry<?, ?, ?, ?> entry) {
        this.entry = entry;
        setEnabled(entry != null && clipBoardValueExtractor.apply(entry) != null);
    }
}
