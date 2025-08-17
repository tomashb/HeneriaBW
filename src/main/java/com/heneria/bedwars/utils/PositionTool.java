package com.heneria.bedwars.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Gives players a special tool to select positions in the world.
 */
public final class PositionTool {

    private PositionTool() {}

    private static final String TOOL_NAME = "Outil de Positionnement";
    private static final Map<UUID, Consumer<Location>> pending = new HashMap<>();

    /**
     * Gives the player the position tool and registers a callback to be executed
     * once the player selects a position.
     *
     * @param player   player receiving the tool
     * @param callback action executed with the selected location
     */
    public static void request(Player player, Consumer<Location> callback) {
        pending.put(player.getUniqueId(), callback);
        ItemStack rod = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = rod.getItemMeta();
        meta.setDisplayName(TOOL_NAME);
        rod.setItemMeta(meta);
        player.getInventory().addItem(rod);
        player.sendMessage("Faites un clic droit pour définir la position.");
    }

    /**
     * Handles a selection made by the player.
     *
     * @param player  the player
     * @param location selected location
     */
    public static void handleSelection(Player player, Location location) {
        Consumer<Location> callback = pending.remove(player.getUniqueId());
        if (callback != null) {
            callback.accept(location);
        }
    }

    /**
     * Checks whether the given item is the position tool.
     *
     * @param item item to check
     * @return true if it is the tool
     */
    public static boolean isTool(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_ROD) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && TOOL_NAME.equals(meta.getDisplayName());
    }
}
