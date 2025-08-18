package com.heneria.bedwars.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Utility methods to interact with player resources.
 */
public final class ResourceManager {

    private ResourceManager() {
    }

    /**
     * Checks if a player has at least the required amount of a resource.
     *
     * @param player the player
     * @param type   the resource type
     * @param amount the amount required
     * @return {@code true} if the player has enough resources
     */
    public static boolean hasResources(Player player, ResourceType type, int amount) {
        int total = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == type.getMaterial()) {
                total += stack.getAmount();
                if (total >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes the specified amount of a resource from a player's inventory.
     *
     * @param player the player
     * @param type   the resource type
     * @param amount the amount to remove
     */
    public static void takeResources(Player player, ResourceType type, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType() != type.getMaterial()) {
                continue;
            }
            int stackAmount = stack.getAmount();
            if (stackAmount <= remaining) {
                player.getInventory().setItem(i, null);
                remaining -= stackAmount;
            } else {
                stack.setAmount(stackAmount - remaining);
                player.getInventory().setItem(i, stack);
                remaining = 0;
            }
        }
    }
}
