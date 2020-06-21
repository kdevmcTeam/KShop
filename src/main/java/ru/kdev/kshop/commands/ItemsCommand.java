package ru.kdev.kshop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.database.MySQL;
import ru.kdev.kshop.gui.ItemsGui;

public class ItemsCommand implements CommandExecutor {

    private final KShop plugin;
    private final MySQL mysql;

    public ItemsCommand(KShop plugin) {
        this.plugin = plugin;
        this.mysql = plugin.getDatabase();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            mysql.getItems(player, items -> new ItemsGui(plugin, player, items));
        } else {
            sender.sendMessage("This command only for player!");
        }
        return false;
    }
}
