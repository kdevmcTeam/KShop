package ru.kdev.KShop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kdev.KShop.commands.ItemsCommand;
import ru.kdev.KShop.commands.ShopAdminCommand;
import ru.kdev.KShop.database.MySQL;
import ru.kdev.KShop.gui.manager.PageGUI;
import ru.kdev.KShop.metrics.MetricsLite;

import java.sql.SQLException;
import java.util.HashMap;

public class KShop extends JavaPlugin {
    public static HashMap<Player, PageGUI> inv = new HashMap<>();

    @Override
    public void onEnable() {
        MySQL mysql = new MySQL(this);
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        try {
            mysql.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.getCommand("items").setExecutor(new ItemsCommand(mysql));
        this.getCommand("shopadmin").setExecutor(new ShopAdminCommand(mysql));
        MetricsLite metrics = new MetricsLite(this, 7103);
        Bukkit.getPluginManager().registerEvents(new KListener(mysql), this);
    }
}
