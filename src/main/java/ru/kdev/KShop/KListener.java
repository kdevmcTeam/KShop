package ru.kdev.KShop;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.kdev.KShop.database.MySQL;
import ru.kdev.KShop.gui.manager.PageGUI;
import ru.kdev.KShop.gui.manager.PageManager;
import ru.kdev.KShop.utils.MessageGetter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KListener implements Listener {
    private KShop plugin = KShop.getPlugin(KShop.class);
    private MySQL mysql;

    KListener(MySQL db) {
        this.mysql = db;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Player player = e.getPlayer();
        ResultSet resultSet = mysql.getGroups(player);
        while (resultSet.next()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("give-commands.give-group")
                    .replace("%player%", player.getName())
                    .replace("%group%", resultSet.getString("groupName")));
            player.sendMessage(MessageGetter.getMessage("locale.success-group").replace("%group%", resultSet.getString("groupName")));
            mysql.removeGroup(player, resultSet.getString("groupName"));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) throws SQLException {
        if(e.getView().getTitle().equals(MessageGetter.getMessage("locale.menu-title"))) {
            e.setCancelled(true);
            if(e.getCurrentItem().getItemMeta().hasDisplayName() && e.getCurrentItem().getItemMeta().getDisplayName().equals(MessageGetter.getMessage("locale.next"))) {
                PageGUI paged = KShop.inv.get((Player) e.getWhoClicked());
                paged.setPage(paged.getPage() + 1);
                paged.open();
            } else if(e.getCurrentItem().getItemMeta().hasDisplayName() && e.getCurrentItem().getItemMeta().getDisplayName().equals(MessageGetter.getMessage("locale.previous"))) {
                PageGUI paged = KShop.inv.get((Player) e.getWhoClicked());
                paged.setPage(paged.getPage() - 1);
                paged.open();
            } else if(e.getCurrentItem().getItemMeta().hasDisplayName() && e.getCurrentItem().getItemMeta().getDisplayName().equals(MessageGetter.getMessage("locale.close"))) {
                e.getWhoClicked().closeInventory();
            } else if(e.getCurrentItem().getType() != Material.AIR) {
                int index = e.getSlot();
                ResultSet resultSet;
                PageGUI paged = KShop.inv.get((Player) e.getWhoClicked());
                if(paged.getPage() == 1) resultSet = mysql.getItem((Player) e.getWhoClicked(), index);
                else resultSet = mysql.getItem((Player) e.getWhoClicked(), ((paged.getPage()-1) * 45) + index);
                if(resultSet == null) {
                    return;
                }
                while (resultSet.next()) {
                    ItemStack item = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount());
                    if(!resultSet.getString("nbt").isEmpty()) {
                        NBTItem nbti = new NBTItem(item);
                        nbti.mergeCompound(new NBTContainer(resultSet.getString("nbt")));
                        e.getWhoClicked().getInventory().addItem(nbti.getItem());
                    } else {
                        e.getWhoClicked().getInventory().addItem(item);
                    }
                    if(paged.getPage() == 1) mysql.removeItem((Player) e.getWhoClicked(), index);
                    else mysql.removeItem((Player) e.getWhoClicked(), ((paged.getPage()-1) * 45) + index);
                }
                e.getWhoClicked().closeInventory();
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if(e.getInventory().getTitle().equals(MessageGetter.getMessage("locale.menu-title"))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(e.getInventory().getTitle().equals(MessageGetter.getMessage("locale.menu-title"))) {
            ((Player) e.getPlayer()).updateInventory();
        }
    }
}
