package ru.kdev.KShop.gui.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.kdev.KShop.utils.MessageGetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageGUI {

    private Player p;
    private ArrayList<ItemStack> items;
    private Inventory inv;
    private int paged;

    public PageGUI(Player p, Inventory inv, ArrayList<ItemStack> items, int page) {
        this.p = p;
        this.items = items;
        this.inv = Bukkit.createInventory(p, 54, MessageGetter.getMessage("locale.menu-title"));
        ;
        this.paged = page;
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }
    public void setPage(Integer page) {
        paged = page;
    }
    public int getPage() {
        return paged;
    }

    public Inventory getInventory() {
        return inv;
    }
    public void open() {
        inv = Bukkit.createInventory(p, 54, MessageGetter.getMessage("locale.menu-title"));

        int i = paged *45 - 45;

        for (int x = 0; x < 45 && x < items.size(); x++) {
            if (i + x + 1 > items.size()) break;
            inv.setItem(x, items.get(i + x));
        }
        if (paged != 1) {
            createItem(45, Material.SPECTRAL_ARROW, MessageGetter.getMessage("locale.previous"), new ArrayList<String>(Arrays.asList(MessageGetter.getMessage("locale.previous-lore"))));
        }
        createItem(53, Material.SPECTRAL_ARROW, MessageGetter.getMessage("locale.next"), new ArrayList<String>(Arrays.asList(MessageGetter.getMessage("locale.next-lore"))));
        createItem(49, Material.ARROW, MessageGetter.getMessage("locale.close"), new ArrayList<String>(Arrays.asList(" ", MessageGetter.getMessage("locale.close-lore"))));

        p.closeInventory();
        p.openInventory(inv);
    }
    private void createItem(int slot, Material mat, String name, ArrayList<String> lore) {
        if (inv == null) {
            return;
        }
        List<String> lore2 = new ArrayList<String>();
        for (String s : lore) {
            lore2.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(lore2);
        is.setItemMeta(meta);
        inv.setItem(slot, is);
    }
}
