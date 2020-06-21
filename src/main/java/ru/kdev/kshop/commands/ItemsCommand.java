package ru.kdev.kshop.commands;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.database.MySQL;
import ru.kdev.kshop.gui.EmptyGui;
import ru.kdev.kshop.gui.PageGUI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemsCommand implements CommandExecutor {

    private final KShop plugin;
    private final MySQL mysql;

    public ItemsCommand(KShop plugin) {
        this.plugin = plugin;
        this.mysql = plugin.getDatabase();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            try {
                ResultSet resultSet = mysql.getItems(player);

                if(resultSet.next()) {
                    resultSet.previous();

                    ArrayList<ItemStack> list = new ArrayList<>();

                    while (resultSet.next()) {
                        ItemStack item = new ItemStack(Material.getMaterial(resultSet.getString("pattern").toUpperCase()), resultSet.getInt("quantity"), (byte)resultSet.getInt("data"));

                        if(!resultSet.getString("nbt").isEmpty()) {
                            NBTItem nbti = new NBTItem(item);
                            nbti.mergeCompound(new NBTContainer(resultSet.getString("nbt")));
                            list.add(nbti.getItem());
                        } else {
                            list.add(item);
                        }
                    }

                    new PageGUI(plugin, player, list);
                } else {
                    player.openInventory(EmptyGui.getEmptyInventory(player));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage("This command only for player!");
        }
        return false;
    }
}
