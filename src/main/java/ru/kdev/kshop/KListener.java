package ru.kdev.kshop;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.kdev.kshop.database.MySQL;
import ru.kdev.kshop.util.MessageGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KListener implements Listener {
    private final KShop plugin;
    private final MySQL database;

    KListener(KShop plugin) {
        Preconditions.checkNotNull(plugin, "plugin is null");

        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    @EventHandler // todo async
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Player player = e.getPlayer();
        ResultSet resultSet = database.getGroups(player);

        while (resultSet.next()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("give-commands.give-group")
                    .replace("%player%", player.getName())
                    .replace("%group%", resultSet.getString("groupName")));
            player.sendMessage(MessageGetter.getMessage("locale.success-group").replace("%group%", resultSet.getString("groupName")));
            database.removeGroup(player, resultSet.getString("groupName"));
        }
    }

}
