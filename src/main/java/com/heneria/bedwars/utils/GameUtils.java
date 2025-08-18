package com.heneria.bedwars.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Utility methods for common game actions.
 */
public final class GameUtils {

    private GameUtils() {
    }

    /**
     * Gives the default starting kit to the specified player.
     *
     * @param player the player to equip
     */
    public static void giveDefaultKit(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
        player.setLevel(0);
        player.setExp(0f);
    }
}
