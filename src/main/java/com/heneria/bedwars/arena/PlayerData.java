package com.heneria.bedwars.arena;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
    private final GameMode gameMode;
    private final double attackSpeed;

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
        this.gameMode = player.getGameMode();
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        this.attackSpeed = attr != null ? attr.getBaseValue() : 4.0D;
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
        player.setGameMode(gameMode);
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attr != null) {
            attr.setBaseValue(attackSpeed);
        }
    }
}

