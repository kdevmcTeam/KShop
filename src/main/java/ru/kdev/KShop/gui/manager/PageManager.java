package ru.kdev.KShop.gui.manager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.inventory.Inventory;

public class PageManager {

    public Set<PageGUI> w = new HashSet<PageGUI>();

    public PageManager() {

    }

    public PageGUI getInventory(Inventory inv) {
        return w.stream().filter(wname -> wname.getInventory().equals(inv)).findFirst().orElse(null);
    }

    public Set<PageGUI> getInventoryList() {
        return Collections.unmodifiableSet(w);
    }
    public void addInventory(PageGUI w) {
        this.w.add(w);
    }

}
