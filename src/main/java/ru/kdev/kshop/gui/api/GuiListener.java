package ru.kdev.kshop.gui.api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * @author artem
 */
public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof Gui) {
            event.setCancelled(true);

            Gui gui = (Gui) inventory.getHolder();
            gui.handleClick((Player) event.getWhoClicked(), event.getSlot());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof Gui) {
            event.setCancelled(true);
        }
    }

}

