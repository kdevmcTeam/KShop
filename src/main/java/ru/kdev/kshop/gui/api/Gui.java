package ru.kdev.kshop.gui.api;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.kdev.kshop.util.ItemBuilder;
import ru.kdev.kshop.util.SlotUtil;

import java.util.function.Consumer;

public class Gui implements InventoryHolder {

    protected final Player player;

    private final Inventory inventory;
    private final GuiIcon[] icons;

    public Gui(Player player, String title, int rows) {
        Preconditions.checkNotNull(player, "player is null");
        Preconditions.checkNotNull(title, "title is null");

        this.player = player;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        this.icons = new GuiIcon[inventory.getSize()];

        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Отрисовать инвентарь
     */
    public void draw() {

    }

    /**
     * Установить предмет в определённый слот
     *
     * @param slot Слот
     * @param item Предмет
     */
    public void set(int slot, ItemStack item) {
        set(slot, item, null);
    }

    /**
     * Установить предмет в определённый слот
     *
     * @param slot Слот
     * @param item Предмет
     * @param onClickHandler Обработчик клика
     */
    public void set(int slot, ItemStack item, Consumer<Player> onClickHandler) {
        set(slot, new GuiIcon(item, onClickHandler));
    }

    /**
     * Установить иконку в определённый слот
     *
     * @param slot Слот
     * @param icon Иконка
     */
    public void set(int slot, GuiIcon icon) {
        icons[slot] = icon;

        icon.drawIcon(inventory, slot);
    }

    /**
     * Удалить предмет из определённого слота
     *
     * @param slot Слот
     */
    public void remove(int slot) {
        GuiIcon oldIcon = icons[slot];

        if (oldIcon == null) {
            return;
        }

        oldIcon.removeIcon(inventory, slot);
        icons[slot] = null;
    }

    public void handleClick(Player player, int slot) {
        GuiIcon icon = icons[slot];

        if (icon == null) {
            return;
        }

        icon.handleClick(player);
    }

    public GuiDynamicIcon set(ConfigurationSection section) {
        return set(section, null);
    }

    public GuiDynamicIcon set(ConfigurationSection section, Consumer<Player> onClickHandler) {
        String location = section.getString("location");

        if (location == null) {
            throw new IllegalStateException("Location is required");
        }

        ItemBuilder builder = ItemBuilder.parseItem(section);
        int slot = SlotUtil.parseSlot(location);

        set(slot, builder.build(), onClickHandler);

        return new GuiDynamicIcon(slot, builder);
    }

}
