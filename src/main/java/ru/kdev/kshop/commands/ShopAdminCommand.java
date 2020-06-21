package ru.kdev.kshop.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.database.MySQL;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ShopAdminCommand implements CommandExecutor {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("^\\d+$");

    private final MySQL mysql;
    private final KShop plugin;

    public ShopAdminCommand(KShop plugin) {
        this.plugin = plugin;
        this.mysql = plugin.getDatabase();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("kshop.admin")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        String subcommand = args.length == 0 ? "help" : args[0];

        switch (subcommand) {
            case "reload": {
                plugin.reloadConfig();

                sender.sendMessage(plugin.getMessage("reload"));
                break;
            }
            case "add": {
                if (args.length < 4) {
                    sender.sendMessage(plugin.getMessage("no-args"));
                    break;
                }

                String playerName = args[1];
                Material material = Material.matchMaterial(args[2].toUpperCase());

                if (material == null) {
                    sender.sendMessage(plugin.getMessage("wrong-material"));
                    break;
                }

                String amountString = args[3];

                if (!DIGIT_PATTERN.matcher(amountString).matches()) {
                    sender.sendMessage(plugin.getMessage("wrong-quantity"));
                    break;
                }

                int amount = Integer.parseInt(args[3]);

                if (amount > material.getMaxStackSize() || amount <= 0) {
                    sender.sendMessage(plugin.getMessage("wrong-quantity"));
                    break;
                }

                int data = 0;
                String nbt = "";

                if (args.length >= 5) {
                    String dataString = args[4];

                    if (!DIGIT_PATTERN.matcher(dataString).matches()) {
                        sender.sendMessage(plugin.getMessage("wrong-data"));
                        break;
                    }

                    data = Integer.parseInt(dataString);
                }

                if (args.length >= 6) {
                    String[] nbtData = Arrays.copyOfRange(args, 5, args.length);

                    nbt = String.join(" ", nbtData);
                }

                mysql.addItem(playerName, args[2].toUpperCase(), amount, data, nbt);
                sender.sendMessage(plugin.getMessage("gived").replace("%player%", playerName));
                break;
            }
            case "clear": {
                if (args.length < 2) {
                    sender.sendMessage(plugin.getMessage("no-args"));
                    break;
                }

                mysql.removeItems(args[1]);
                sender.sendMessage(plugin.getMessage("cleared").replace("%player%", args[1]));
                break;
            }
            default: {
                sender.sendMessage(plugin.getMessage("admin-help"));
                break;
            }
        }

        return true;
    }
}