package ru.kdev.kshop.gui;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.gui.api.Gui;
import ru.kdev.kshop.util.ItemBuilder;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ItemsGui extends Gui {

    private final KShop plugin;

    private final List<ItemStack> items;
    private int page;

    public ItemsGui(KShop plugin, Player player, List<ItemStack> items) {
        super(player, plugin.getMessage("menu-title"), 6);

        Preconditions.checkNotNull(plugin, "plugin is null");
        Preconditions.checkNotNull(items, "items is null");

        this.plugin = plugin;
        this.items = items;

        draw();
    }

    public void nextPage() {
        page++;

        draw();
    }

    public void previousPage() {
        page--;

        draw();
    }

    public void drawScrollingButtons() {
        if (page > 0) {
            set(45, ItemBuilder.getBuilder(Material.SPECTRAL_ARROW)
                    .setName(plugin.getMessage("previous"))
                    .setLore(plugin.getMessageList("previous-lore"))
                    .build(), player -> previousPage());
        } else {
            remove(45);
        }

        if (page != getPages() - 1) {
            set(53, ItemBuilder.getBuilder(Material.SPECTRAL_ARROW)
                    .setName(plugin.getMessage("next"))
                    .setLore(plugin.getMessageList("next-lore"))
                    .build(), player -> nextPage());
        } else {
            remove(53);
        }
    }

    public int getPages() {
        return (int) Math.ceil(items.size() / 45D);
    }

    @Override
    public void draw() {
        if (items.isEmpty()) {
            set(22, ItemBuilder.getBuilder(Material.BUCKET)
                    .setName(plugin.getMessage("no-items"))
                    .build());
        } else {
            int i = page * 45;

            Iterator<ItemStack> itr = skip(i);

            for (int slot = 0; slot < 45; slot++) {
                if (itr.hasNext()) {
                    ItemStack item = itr.next();
                    int index = slot + i;

                    set(slot, item, player -> {
                        try {
                            player.getInventory().addItem(item.clone());
                            plugin.getDatabase().removeItem(player, index);

                            player.closeInventory();
                        } catch (SQLException e) {
                            // fixme
                        }
                    });
                } else {
                    remove(slot);
                }
            }

            drawScrollingButtons();
        }

        set(49, ItemBuilder.getBuilder(Material.ARROW)
                .setName(plugin.getMessage("close"))
                .setLore(plugin.getMessageList("close-lore"))
                .build(), HumanEntity::closeInventory);
    }

    private Iterator<ItemStack> skip(int skip) {
        if (skip >= items.size()) {
            return Collections.emptyIterator();
        }

        return items.subList(skip, items.size()).iterator();
    }

}
