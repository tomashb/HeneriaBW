package com.heneria.bedwars.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Stores the state of a player before joining an arena so it can be restored when leaving.
 */
public class PlayerData {

    private final ItemStack[] inventoryContents;
    private final ItemStack[] armorContents;
    private final Location location;
    private final float exp;
    private final int level;

    /**
     * Captures the current state of the given player.
     *
     * @param player the player
     */
    public PlayerData(Player player) {
        this.inventoryContents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.location = player.getLocation();
        this.exp = player.getExp();
        this.level = player.getLevel();
    }

    /**
     * Restores the saved state to the player.
     *
     * @param player the player to restore
     */
    public void restore(Player player) {
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);
        player.teleport(location);
        player.setExp(exp);
        player.setLevel(level);
    }
}

