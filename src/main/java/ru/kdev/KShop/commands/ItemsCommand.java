package ru.kdev.KShop.commands;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.kdev.KShop.KShop;
import ru.kdev.KShop.database.MySQL;
import ru.kdev.KShop.gui.manager.EmptyGui;
import ru.kdev.KShop.gui.manager.PageGUI;
import ru.kdev.KShop.gui.manager.PageManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemsCommand implements CommandExecutor {
    private MySQL mysql;
    private KShop plugin = KShop.getPlugin(KShop.class);
    public ItemsCommand(MySQL db) {
        this.mysql = db;
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
                    PageGUI paged = new PageGUI(player, null, list, 1);
                    PageManager pageManager = new PageManager();
                    pageManager.addInventory(paged);
                    KShop.inv.put(player, paged);
                    paged.open();
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
