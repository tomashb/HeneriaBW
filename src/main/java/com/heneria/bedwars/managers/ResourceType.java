package com.heneria.bedwars.managers;

import org.bukkit.Material;
import org.bukkit.ChatColor;

/**
 * Types of resources used in the item shop.
 */
public enum ResourceType {
    /** Iron ingot resource. */
    IRON(Material.IRON_INGOT, "Fer", ChatColor.GRAY),
    /** Gold ingot resource. */
    GOLD(Material.GOLD_INGOT, "Or", ChatColor.GOLD),
    /** Diamond resource. */
    DIAMOND(Material.DIAMOND, "Diamant", ChatColor.AQUA),
    /** Emerald resource. */
    EMERALD(Material.EMERALD, "Émeraude", ChatColor.GREEN);

    private final Material material;
    private final String displayName;
    private final ChatColor color;

    ResourceType(Material material, String displayName, ChatColor color) {
        this.material = material;
        this.displayName = displayName;
        this.color = color;
    }

    /**
     * Gets the Bukkit material representing this resource.
     *
     * @return corresponding material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the human readable name of this resource.
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the color associated with this resource for display purposes.
     *
     * @return chat color linked to the resource
     */
    public ChatColor getColor() {
        return color;
    }
}
