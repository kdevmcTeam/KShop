package ru.kdev.kshop;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kdev.kshop.commands.ItemsCommand;
import ru.kdev.kshop.commands.ShopAdminCommand;
import ru.kdev.kshop.database.MySQL;
import ru.kdev.kshop.gui.api.GuiListener;
import ru.kdev.kshop.metrics.MetricsLite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KShop extends JavaPlugin {

    private static final Pattern NEW_LINE = Pattern.compile("\n");

    private final MySQL database = new MySQL(this);
    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        FileConfiguration config = getConfig();

        ConfigurationSection localeSection = config.getConfigurationSection("locale");

        if (localeSection == null) {
            throw new IllegalStateException("No locale configuration found in config.yml");
        }

        // Предварительно кешируем все сообщения
        for (String localeKey : localeSection.getKeys(false)) {
            String message = localeSection.getString(localeKey);

            // Такого быть не может, но IDE говорит, что может, ну ок
            if (message == null) {
                continue;
            }

            messages.put(
                    localeKey.toLowerCase(),
                    ChatColor.translateAlternateColorCodes('&', message)
            );
        }

        ConfigurationSection connectionSection = config.getConfigurationSection("connection");

        if (connectionSection == null) {
            throw new IllegalStateException("No connection configuration found in config.yml");
        }

        database.connect(connectionSection);

        getCommand("items").setExecutor(new ItemsCommand(this));
        getCommand("shopadmin").setExecutor(new ShopAdminCommand(this));

        getServer().getPluginManager().registerEvents(new KListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(), this);

        MetricsLite metrics = new MetricsLite(this, 7103);
    }

    public MySQL getDatabase() {
        return database;
    }

    public List<String> getMessageList(String path) {
        return NEW_LINE.splitAsStream(messages.get(path.toLowerCase())).collect(Collectors.toList());
    }

    public String getMessage(String path) {
        return messages.get(path.toLowerCase());
    }

}
