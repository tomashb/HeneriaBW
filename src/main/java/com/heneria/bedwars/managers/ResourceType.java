package com.heneria.bedwars.managers;

import org.bukkit.Material;

/**
 * Types of resources used in the item shop.
 */
public enum ResourceType {
    /** Iron ingot resource. */
    IRON(Material.IRON_INGOT, "Fer"),
    /** Gold ingot resource. */
    GOLD(Material.GOLD_INGOT, "Or"),
    /** Diamond resource. */
    DIAMOND(Material.DIAMOND, "Diamant"),
    /** Emerald resource. */
    EMERALD(Material.EMERALD, "Émeraude");

    private final Material material;
    private final String displayName;

    ResourceType(Material material, String displayName) {
        this.material = material;
        this.displayName = displayName;
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
}
