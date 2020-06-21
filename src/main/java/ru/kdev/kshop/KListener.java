package ru.kdev.kshop;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.kdev.kshop.database.MySQL;

public class KListener implements Listener {
    private final KShop plugin;
    private final MySQL database;

    KListener(KShop plugin) {
        Preconditions.checkNotNull(plugin, "plugin is null");

        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        database.getGroups(player, groups -> {
            for (String group : groups) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("give-commands.give-group")
                        .replace("%player%", player.getName())
                        .replace("%group%", group));

                player.sendMessage(plugin.getMessage("success-group")
                        .replace("%group%", group));

                database.removeGroup(player, group);
            }
        });
    }

}
