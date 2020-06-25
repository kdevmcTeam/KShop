package ru.kdev.kshop.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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

    public static ItemBuilder parseItem(ConfigurationSection section, Object... replacements) {
        String id = section.getString("id");

        if (id == null) {
            throw new IllegalStateException("ID is required");
        }

        Material material = Material.matchMaterial(id);

        if (material == null) {
            throw new IllegalStateException("Unknown material: " + id);
        }

        int amount = section.getInt("amount", 1);
        int durability = section.getInt("damage", 0);

        String name = section.getString("name");
        List<String> lore = section.getStringList("lore");

        ItemBuilder builder = getBuilder(material, amount, (short) durability);

        if (name != null) {
            builder.setName(name);
        }

        if (!lore.isEmpty()) {
            builder.setLore(lore);
        }

        return builder.applyReplacements(true, replacements);
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

    private String applyReplacements(String line, boolean colors, Object... replacements) {
        for (int i = 0; i < replacements.length; i += 2) {
            String key = replacements[i].toString();
            String value = replacements[i + 1].toString();

            line = line.replace(key, value);
        }

        if (colors) {
            line = ChatColor.translateAlternateColorCodes('&', line);
        }

        return line;
    }

    public ItemBuilder applyReplacements(boolean colors, Object... replacements) {
        String name = meta.getDisplayName();
        List<String> lore = meta.getLore();

        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, applyReplacements(lore.get(i), colors, replacements));
            }

            setLore(lore);
        }

        setName(applyReplacements(name, colors, replacements));

        return this;
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);

        return this;
    }

    public void update() {
        item.setItemMeta(meta);
    }

    public ItemStack build() {
        update();

        return item;
    }

}
