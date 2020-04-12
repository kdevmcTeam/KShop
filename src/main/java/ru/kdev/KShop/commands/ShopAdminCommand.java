package ru.kdev.KShop.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kdev.KShop.KShop;
import ru.kdev.KShop.database.MySQL;
import ru.kdev.KShop.utils.MessageGetter;

import java.sql.SQLException;

public class ShopAdminCommand implements CommandExecutor {
    private MySQL mysql;
    private KShop plugin = KShop.getPlugin(KShop.class);
    public ShopAdminCommand(MySQL db) { this.mysql = db; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("kshop.admin")) {
            if(args.length > 0) {
                if(args[0].equals("reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage(MessageGetter.getMessage("locale.reload"));
                } else if(args[0].equals("add")) {
                    if(args.length < 4) {
                        sender.sendMessage(MessageGetter.getMessage("locale.no-args"));
                        return true;
                    }
                    Player giving = Bukkit.getPlayer(args[1]);
                    if(Material.getMaterial(args[2].toUpperCase()) == null) {
                        sender.sendMessage(MessageGetter.getMessage("locale.wrong-material"));
                        return true;
                    }
                    if(Integer.parseInt(args[3]) > 64 || Integer.parseInt(args[3]) == 0) {
                        sender.sendMessage(MessageGetter.getMessage("locale.wrong-quantity"));
                        return true;
                    }
                    if(args.length == 4) {
                        try {
                            mysql.addItem(giving, args[2].toUpperCase(), Integer.parseInt(args[3]), 0, "");
                            sender.sendMessage(MessageGetter.getMessage("locale.gived").replace("%player%", giving.getName()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if(args.length == 5) {
                        try {
                            mysql.addItem(giving, args[2].toUpperCase(), Integer.parseInt(args[3]), Integer.parseInt(args[4]), "");
                            sender.sendMessage(MessageGetter.getMessage("locale.gived").replace("%player%", giving.getName()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 5; i < args.length; i++) {
                                builder.append(args[i]).append(" ");
                            }
                            String msg = builder.toString();
                            mysql.addItem(giving, args[2].toUpperCase(), Integer.parseInt(args[3]), Integer.parseInt(args[4]), msg);
                            sender.sendMessage(MessageGetter.getMessage("locale.gived").replace("%player%", giving.getName()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else if(args[0].equals("clear")) {
                    if(args.length == 2) {
                        Player giving = Bukkit.getPlayer(args[1]);
                        try {
                            mysql.removeItems(giving);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        sender.sendMessage(MessageGetter.getMessage("locale.cleared").replace("%player%", giving.getName()));
                    } else {
                    }
                }
            } else {
                for(String str : plugin.getConfig().getStringList("locale.admin-help")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
                }
            }
        } else {
            sender.sendMessage(MessageGetter.getMessage("locale.no-permission"));
        }
        return false;
    }
}
