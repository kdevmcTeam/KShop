package ru.kdev.kshop.gui;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.gui.api.Gui;
import ru.kdev.kshop.gui.api.GuiDynamicIcon;
import ru.kdev.kshop.item.CartItem;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class ItemsGui extends Gui {

    private final KShop plugin;

    private final List<CartItem> items;
    private final int[] itemSlots;

    private int page;
    private int pages;

    public ItemsGui(KShop plugin, Player player, List<CartItem> items) {
        super(player, plugin.getMessage("menu.title"), plugin.getConfig().getInt("menu.rows"));

        Preconditions.checkNotNull(plugin, "plugin is null");
        Preconditions.checkNotNull(items, "items is null");

        this.plugin = plugin;
        this.items = items;

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("menu.item-container");

        if (section == null) {
            this.itemSlots = IntStream.range(0, 45).toArray();
        } else {
            IntStream.Builder builder = IntStream.builder();

            int sx = section.getInt("x");
            int sy = section.getInt("y");
            int w = section.getInt("w");
            int h = section.getInt("h");

            for (int y = sy; y < sy + h; y++) {
                for (int x = sx; x < sx + w; x++) {
                    builder.accept(y * 9 + x);
                }
            }

            this.itemSlots = builder.build().toArray();
        }

        this.pages = (int) Math.ceil((double) items.size() / itemSlots.length);

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

    private GuiDynamicIcon prevPage;
    private GuiDynamicIcon nextPage;

    public void drawScrollingButtons() {
        Object[] replacements = new Object[]{
                "%total_pages%", pages, "%next_page%", page + 1, "%prev_page%", page - 1
        };

        if (page > 0) {
            prevPage = set(getNotNullSection("previous-button"), player -> previousPage());
            prevPage.getBuilder().applyReplacements(false, replacements);
            prevPage.getBuilder().update();
        } else {
            if (prevPage != null) {
                remove(prevPage.getSlot());
            }
        }

        if (page != pages - 1) {
            nextPage = set(getNotNullSection("next-button"), player -> nextPage());
            nextPage.getBuilder().applyReplacements(false, replacements);
            nextPage.getBuilder().update();
        } else {
            if (nextPage != null) {
                remove(nextPage.getSlot());
            }
        }
    }

    @Override
    public void draw() {
        if (items.isEmpty()) {
            set(getNotNullSection("no-items-icon"));
        } else {
            int i = page * itemSlots.length;

            Iterator<CartItem> itr = skip(i);

            for (int slot : itemSlots) {
                if (itr.hasNext()) {
                    CartItem cartItem = itr.next();
                    ItemStack item = cartItem.getItem();

                    set(slot, item, player -> {
                        player.getInventory().addItem(item.clone());
                        plugin.getDatabase().removeItem(cartItem);

                        player.closeInventory();
                    });
                } else {
                    remove(slot);
                }
            }

            drawScrollingButtons();
        }

        set(getNotNullSection("close-button"), HumanEntity::closeInventory);
    }

    private ConfigurationSection getNotNullSection(String name) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("menu." + name);

        if (section == null) {
            player.closeInventory();

            throw new IllegalStateException("Section " + name + " not found in config.yml");
        }

        return section;
    }

    private Iterator<CartItem> skip(int skip) {
        if (skip >= items.size()) {
            return Collections.emptyIterator();
        }

        return items.subList(skip, items.size()).iterator();
    }

}
