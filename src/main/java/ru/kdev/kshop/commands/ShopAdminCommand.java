package ru.kdev.kshop.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.database.MySQL;
import ru.kdev.kshop.util.MessageGetter;

public class ShopAdminCommand implements CommandExecutor {
    private final MySQL mysql;
    private final KShop plugin;

    public ShopAdminCommand(KShop plugin) {
        this.plugin = plugin;
        this.mysql = plugin.getDatabase();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("kshop.admin")) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "reload":
                        plugin.reloadConfig();
                        sender.sendMessage(MessageGetter.getMessage("locale.reload"));
                        break;
                    case "add": {
                        if (args.length < 4) {
                            sender.sendMessage(MessageGetter.getMessage("locale.no-args"));
                            return true;
                        }
                        Player giving = Bukkit.getPlayer(args[1]);
                        if (Material.getMaterial(args[2].toUpperCase()) == null) {
                            sender.sendMessage(MessageGetter.getMessage("locale.wrong-material"));
                            return true;
                        }
                        if (Integer.parseInt(args[3]) > 64 || Integer.parseInt(args[3]) == 0) {
                            sender.sendMessage(MessageGetter.getMessage("locale.wrong-quantity"));
                            return true;
                        }
                        if (args.length == 4) {
                            mysql.addItem(giving, args[2].toUpperCase(), Integer.parseInt(args[3]), 0, "");
                            sender.sendMessage(MessageGetter.getMessage("locale.gived").replace("%player%", giving.getName()));
                        } else if (args.length == 5) {
                            mysql.addItem(giving, args[2].toUpperCase(), Integer.parseInt(args[3]), Integer.parseInt(args[4]), "");
                            sender.sendMessage(MessageGetter.getMessage("locale.gived").replace("%player%", giving.getName()));
                        } else {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 5; i < args.length; i++) {
                                builder.append(args[i]).append(" ");
                            }
                            String msg = builder.toString();
                            mysql.addItem(giving, args[2].toUpperCase(), Integer.parseInt(args[3]), Integer.parseInt(args[4]), msg);
                            sender.sendMessage(MessageGetter.getMessage("locale.gived").replace("%player%", giving.getName()));
                        }
                        break;
                    }
                    case "clear": {
                        if (args.length == 2) {
                            Player giving = Bukkit.getPlayer(args[1]);
                            mysql.removeItems(giving);
                            sender.sendMessage(MessageGetter.getMessage("locale.cleared").replace("%player%", giving.getName()));
                            break;
                        }
                    }
                }
            } else {
                for (String str : plugin.getConfig().getStringList("locale.admin-help")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
                }
            }
        } else {
            sender.sendMessage(MessageGetter.getMessage("locale.no-permission"));
        }
        return false;
    }
}
