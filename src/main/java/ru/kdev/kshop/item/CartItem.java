package ru.kdev.kshop.item;

import org.bukkit.inventory.ItemStack;

/**
 * @author artem
 */
public class CartItem {

    private final int databaseIndex;
    private final ItemStack item;

    public CartItem(int index, ItemStack item) {
        this.databaseIndex = index;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getDatabaseIndex() {
        return databaseIndex;
    }

}
