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
import ru.kdev.kshop.updater.UpdateChecker;
import ru.kdev.kshop.util.Patterns;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

public class KShop extends JavaPlugin {

    private final MySQL database = new MySQL(this);
    private final Map<String, String> messages = new HashMap<>();

    private final UpdateChecker updateChecker = new UpdateChecker(this);

    private String updateBranch;
    private String revision;

    @Override
    public void onEnable() {
        revision = parseRevision();

        saveDefaultConfig();
        reloadConfig();

        Objects.requireNonNull(getCommand("items"), "Command /items not found in plugin.yml")
                .setExecutor(new ItemsCommand(this));

        Objects.requireNonNull(getCommand("shopadmin"), "Command /shopadmin not found in plugin.yml")
                .setExecutor(new ShopAdminCommand(this));

        getServer().getPluginManager().registerEvents(new KListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(), this);

        new MetricsLite(this, 7103);
    }

    public MySQL getDatabase() {
        return database;
    }

    // когда перезагружается конфиг, то мы кешируем сообщения и коннектимся к базе данных
    // конфиг перезагружается при загрузке и командой /shopadmin reload
    @Override
    public void reloadConfig() {
        super.reloadConfig();

        FileConfiguration config = getConfig();

        ConfigurationSection localeSection = config.getConfigurationSection("locale");

        if (localeSection == null) {
            throw new IllegalStateException("No locale configuration found in config.yml");
        }

        // очищаем кеш (хз зачем, пусть будет)
        messages.clear();

        // Предварительно кешируем все сообщения
        for (String localeKey : localeSection.getKeys(false)) {
            String message = localeSection.getString(localeKey);

            // Такого быть не может, но IDE говорит, что может, ну ок
            if (message == null) {
                continue;
            }

            cacheMessage(localeKey, message);
        }

        // заодно кешируем заголовок окна
        cacheMessage("menu.title", config.getString("menu.title"));

        ConfigurationSection connectionSection = config.getConfigurationSection("connection");

        if (connectionSection == null) {
            throw new IllegalStateException("No connection configuration found in config.yml");
        }

        // Подключаемся к базе данных
        database.connect(connectionSection);

        // Останавливаем проверку обновлений
        updateChecker.stopUpdateChecker();

        ConfigurationSection updateCheckerSection
                = config.getConfigurationSection("update-checker");

        if (updateCheckerSection != null) {
            boolean enabled = updateCheckerSection.getBoolean("enabled", true);
            String branch = updateCheckerSection.getString("branch", "master");

            if (enabled) {
                getServer().getScheduler().runTaskLater(this, () -> {
                    updateChecker.runUpdateChecker(branch);
                }, 20L);
            }

            updateBranch = branch;
        } else {
            updateBranch = "master";
        }

        updateChecker.stopUpdateChecker();
    }

    private String parseRevision() {
        String line = getDescription().getVersion();
        Matcher matcher = Patterns.PLUGIN_VERSION.matcher(line);

        if (!matcher.matches()) {
            getLogger().warning("Cannot determine plugin revision.");

            return "Unknown";
        }

        return matcher.group(2);
    }

    private void cacheMessage(String key, String message) {
        messages.put(key.toLowerCase(), ChatColor.translateAlternateColorCodes('&', message));
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public String getUpdateBranch() {
        return updateBranch;
    }

    public String getRevision() {
        return revision;
    }

    public String getMessage(String path) {
        return messages.computeIfAbsent(path.toLowerCase(), x -> x);
    }

}
