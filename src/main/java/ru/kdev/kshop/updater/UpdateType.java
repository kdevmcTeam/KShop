package ru.kdev.kshop.updater;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author artem
 */
public enum UpdateType {

    NEW_FEATURE("feature", ChatColor.AQUA),
    BUG_FIX("fix", ChatColor.GREEN),
    UNKNOWN("", ChatColor.RESET);

    private final String name;
    private final ChatColor color;

    private static final Map<String, UpdateType> BY_NAME = new HashMap<>();

    static {
        for (UpdateType type : UpdateType.values()) {
            BY_NAME.put(type.name.toUpperCase(), type);
        }
    }

    UpdateType(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public static UpdateType getByName(String name) {
        return BY_NAME.getOrDefault(name.toUpperCase(), UNKNOWN);
    }

}
