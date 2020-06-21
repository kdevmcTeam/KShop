package ru.kdev.kshop.gui;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.util.ItemBuilder;

import java.sql.SQLException;
import java.util.List;

public class PageGUI extends Gui {

    private final KShop plugin;

    private final List<ItemStack> items;
    private int page;

    public PageGUI(KShop plugin, Player player, List<ItemStack> items) {
        super(player, plugin.getMessage("menu-title"), 6);

        this.plugin = plugin;
        this.items = items;
    }

    public void nextPage() {
        page++;

        draw();
    }

    public void previousPage() {
        page--;

        draw();
    }

    public List<ItemStack> getItems() {
        return items;
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
        int i = page * 45;

        for (int x = 0; x < 45 && x < items.size(); x++) {
            if (i + x + 1 > items.size()) break;

            int slot = i + x;
            ItemStack item = items.get(slot);

            set(x, item, player -> {
                try {
                    player.getInventory().addItem(item.clone());
                    plugin.getDatabase().removeItem(player, slot);

                    player.closeInventory();
                } catch (SQLException e) {
                    // fixme
                }
            });
        }

        drawScrollingButtons();

        set(49, ItemBuilder.getBuilder(Material.ARROW)
                .setName(plugin.getMessage("close"))
                .setLore(plugin.getMessageList("close-lore"))
                .build(), HumanEntity::closeInventory);
    }

}
