package de.heil_privat.keepasstool.actions;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URISyntaxException;

public class OpenAboutDialogAction extends AbstractAction {
    private final Component parent;

    public OpenAboutDialogAction(final Component parent) {
        super("About");
        putValue(Action.SHORT_DESCRIPTION, "Opens About Dialog");
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AboutDialog dlg = new AboutDialog(parent);
        dlg.setMinimumSize(new Dimension(480, 360));
        dlg.setLocationRelativeTo(parent);
        dlg.pack();
        dlg.setVisible(true);
    }

    private static class AboutDialog extends JDialog {
        public AboutDialog(final Component parent) {
            super((JFrame) SwingUtilities.getRoot(parent), "Open Keepass database file", true);
            JPanel content = new JPanel(new GridBagLayout());
            setContentPane(content);

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5);

            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1.0;
            c.weighty = 1.0;
            c.fill = GridBagConstraints.BOTH;
            JTextPane aboutText = new JTextPane();
            aboutText.setEditable(false);
            aboutText.setMinimumSize(new Dimension(480, 360));
            try {
                aboutText.setPage(getClass().getResource("about.html"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            aboutText.addHyperlinkListener(e -> {
                if (HyperlinkEvent.EventType.ACTIVATED != e.getEventType()) {
                    return;
                }

                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            content.add(new JScrollPane(aboutText), c);

            c.gridy++;
            c.weightx = 0.0;
            c.weighty = 0.0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.SOUTHEAST;
            JButton ok = new JButton("Ok");
            ok.addActionListener(e -> dispose());
            content.add(ok, c);
            KeyListener pushOk = new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    switch (e.getKeyChar()) {
                        case KeyEvent.VK_ENTER:
                        case KeyEvent.VK_ESCAPE:
                            ok.doClick();
                    }
                }
            };
            ok.addKeyListener(pushOk);
            aboutText.addKeyListener(pushOk);
            content.addKeyListener(pushOk);
            ok.requestFocus();
        }
    }
}
