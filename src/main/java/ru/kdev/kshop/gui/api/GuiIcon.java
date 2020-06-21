package ru.kdev.kshop.gui.api;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class GuiIcon {

    private final ItemStack item;
    private final Consumer<Player> onClickHandler;

    public GuiIcon(ItemStack item, Consumer<Player> onClickHandler) {
        Preconditions.checkNotNull(item, "item is null");

        this.item = item;
        this.onClickHandler = onClickHandler;
    }

    /**
     * Отрисовать иконку в инвентаре
     *
     * @param inventory Инвентарь
     * @param slot Слот
     */
    public void drawIcon(Inventory inventory, int slot) {
        inventory.setItem(slot, item);
    }

    /**
     * Удалить иконку в инвентаре
     *
     * @param inventory Инвентарь
     * @param slot Слот
     */
    public void removeIcon(Inventory inventory, int slot) {
        inventory.setItem(slot, null);
    }

    /**
     * Обработать клик игрока
     *
     * @param player Игрок
     */
    public void handleClick(Player player) {
        if (onClickHandler != null) {
            onClickHandler.accept(player);
        }
    }

}
