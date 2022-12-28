package de.heil_privat.keepasstool;

import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.kdb.KdbDatabase;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.dom.DomDatabaseWrapper;
import org.linguafranca.pwdb.kdbx.jaxb.JaxbDatabase;
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class ApplicationModel {
    private static final File SETTINGS_FILE = new File(System.getProperty("user.home", ""), ".keepasstool.properties");
    private static ApplicationModel INSTANCE;
    private final Properties settings = new Properties();
    private final List<Consumer<Database<?, ?, ?, ?>>> onDatabaseOpen = new LinkedList<>();
    private final List<Consumer<Entry<?, ?, ?, ?>>> onEntrySelection = new LinkedList<>();
    private boolean dirty = false;
    private Database<?, ?, ?, ?> keepassDB;

    private ApplicationModel() {
        //prevent instantiation from outside to force usage of factory method
    }

    public static ApplicationModel getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (ApplicationModel.class) {
            if (INSTANCE == null) {
                INSTANCE = new ApplicationModel();
            }
            INSTANCE.loadSettings();
        }
        return INSTANCE;
    }

    public void setSelectedEntry(Entry<?, ?, ?, ?> selectedEntry) {
        onEntrySelection.forEach(l -> SwingUtilities.invokeLater(() -> l.accept(selectedEntry)));
    }

    public void addDatabaseOpenListener(Consumer<Database<?, ?, ?, ?>> listener) {
        onDatabaseOpen.add(listener);
    }

    public void addSelectedEntryListener(Consumer<Entry<?, ?, ?, ?>> listener) {
        onEntrySelection.add(listener);
    }

    public void set(ApplicationModel.Settting name, String value) {
        settings.setProperty(name.name(), value);
        dirty = true;
    }

    public String get(ApplicationModel.Settting setting, String defaultValue) {
        return settings.getProperty(setting.name(), defaultValue);
    }

    private void loadSettings() {
        synchronized (this) {
            if (!SETTINGS_FILE.exists()) {
                System.out.println("No settings file to load");
                return;
            }
            try (InputStream in = Files.newInputStream(SETTINGS_FILE.toPath())) {
                settings.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveSettings() {
        synchronized (this) {
            if (!dirty) {
                return;
            }

            System.out.println("Saving settings to " + SETTINGS_FILE);
            try (OutputStream out = Files.newOutputStream(SETTINGS_FILE.toPath())) {
                settings.store(out, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            dirty = false;
        }
    }

    public void openKeepassFile(final File f, String dbImplType, byte[] credentialData) {
        try {
            KdbxCreds creds = new KdbxCreds(credentialData);
            try (InputStream dbFile = Files.newInputStream(f.toPath())) {
                switch (dbImplType) {
                    case "Simple":
                        keepassDB = SimpleDatabase.load(creds, dbFile);
                        break;
                    case "Dom":
                        keepassDB = DomDatabaseWrapper.load(creds, dbFile);
                        break;
                    case "JaxB":
                        keepassDB = JaxbDatabase.load(creds, dbFile);
                        break;
                    default:
                        throw new IllegalStateException("Illegal Database type '" + dbImplType + " selected");
                }

                System.out.println("Loaded keepassDB from " + f);
                onDatabaseOpen.forEach(l -> SwingUtilities.invokeLater(() -> l.accept(keepassDB)));
            }
        } catch (Exception ex) {
            StringWriter str = new StringWriter();
            str.append("Error opening database.\n").append(ex.getMessage());
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(KeepassToolGui.gui, str.toString()));
            ex.printStackTrace();
        }
    }

    public enum Settting {
        LAST_KEEPASSFILE,
        DB_TYPE
    }
}
