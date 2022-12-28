package com.famfamfam.silk;

import javax.swing.*;
import java.net.URL;

public class SilkIcons {
    public static final Icon ICON_FOLDER = loadIcon("folder.png");
    public static final Icon ICON_KEY = loadIcon("key.png");
    public static final Icon ICON_USER = loadIcon("user.png");
    public static final Icon ICON_WORLD_LINK = loadIcon("world_link.png");

    private static Icon loadIcon(final String name) {
        URL resource = SilkIcons.class.getResource(name);
        if (resource == null) {
            throw new IllegalArgumentException("Unknown icon " + name);
        }
        return new ImageIcon(resource);
    }
}
