package ru.kdev.KShop.utils;

import org.bukkit.ChatColor;
import ru.kdev.KShop.KShop;

public class MessageGetter {
    public static String getMessage(String path) {
        KShop plugin = KShop.getPlugin(KShop.class);
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path));
    }
}
