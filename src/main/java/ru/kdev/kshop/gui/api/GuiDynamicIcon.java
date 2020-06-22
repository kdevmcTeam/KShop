package ru.kdev.kshop.gui.api;

import ru.kdev.kshop.util.ItemBuilder;

/**
 * @author artem
 */
public class GuiDynamicIcon {

    private final int slot;
    private final ItemBuilder builder;

    public GuiDynamicIcon(int slot, ItemBuilder builder) {
        this.slot = slot;
        this.builder = builder;
    }

    public int getSlot() {
        return slot;
    }

    public ItemBuilder getBuilder() {
        return builder;
    }
}
