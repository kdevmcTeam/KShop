package ru.kdev.kshop.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public static ItemBuilder getBuilder(Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    public static ItemBuilder getBuilder(Material material, int amount) {
        return new ItemBuilder(new ItemStack(material, amount));
    }

    public static ItemBuilder getBuilder(Material material, int amount, short damage) {
        return new ItemBuilder(new ItemStack(material, amount, damage));
    }

    public static ItemBuilder getBuilder(ItemStack item) {
        return new ItemBuilder(item.clone());
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);

        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);

        return item;
    }

}
