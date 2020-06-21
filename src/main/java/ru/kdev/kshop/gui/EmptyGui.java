package ru.kdev.kshop.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.kdev.kshop.util.MessageGetter;

import java.util.Arrays;

public class EmptyGui { // fixme
    public static Inventory getEmptyInventory(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, MessageGetter.getMessage("locale.menu-title"));
        ItemStack close = new ItemStack(Material.ARROW, 1);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(MessageGetter.getMessage("locale.close"));
        closeMeta.setLore(Arrays.asList(" ", MessageGetter.getMessage("locale.close-lore")));
        close.setItemMeta(closeMeta);
        ItemStack empty = new ItemStack(Material.BUCKET, 1);
        ItemMeta emptyMeta = empty.getItemMeta();
        emptyMeta.setDisplayName(MessageGetter.getMessage("locale.no-items"));
        empty.setItemMeta(emptyMeta);
        inv.setItem(49, close);
        inv.setItem(22, empty);
        return inv;
    }
}
