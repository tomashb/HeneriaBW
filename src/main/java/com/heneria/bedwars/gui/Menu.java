package com.heneria.bedwars.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Base class for simple inventory based GUIs.
 */
public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    /**
     * Gets the title of the menu.
     *
     * @return menu title
     */
    public abstract String getTitle();

    /**
     * Gets the size of the inventory.
     *
     * @return inventory size
     */
    public abstract int getSize();

    /**
     * Fills the inventory with the necessary items.
     */
    public abstract void setupItems();

    /**
     * Handles a click inside this menu.
     *
     * @param event click event
     */
    public abstract void handleClick(InventoryClickEvent event);

    /**
     * Opens the menu for the given player.
     *
     * @param player the player
     */
    public void open(Player player) {
        inventory = Bukkit.createInventory(this, getSize(), getTitle());
        setupItems();
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
