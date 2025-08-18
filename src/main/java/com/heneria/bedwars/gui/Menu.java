package com.heneria.bedwars.gui;

import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Base class for simple inventory based GUIs.
 */
public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;
    protected Menu previousMenu;

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
     * @param player       the player
     * @param previousMenu the previous menu, or {@code null} if none
     */
    public void open(Player player, Menu previousMenu) {
        this.previousMenu = previousMenu;
        inventory = Bukkit.createInventory(this, getSize(), getTitle());
        setupItems();
        if (previousMenu != null) {
            inventory.setItem(getBackButtonSlot(), backButton());
        }
        player.openInventory(inventory);
    }

    /**
     * Opens the menu without a previous reference.
     *
     * @param player the player
     */
    public void open(Player player) {
        open(player, null);
    }

    protected int getBackButtonSlot() {
        return getSize() - 9;
    }

    protected ItemStack backButton() {
        return new ItemBuilder(Material.BARRIER).setName("&cRetour").build();
    }

    protected boolean handleBack(InventoryClickEvent event) {
        if (previousMenu != null && event.getRawSlot() == getBackButtonSlot()) {
            previousMenu.open((Player) event.getWhoClicked(), previousMenu.previousMenu);
            return true;
        }
        return false;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
