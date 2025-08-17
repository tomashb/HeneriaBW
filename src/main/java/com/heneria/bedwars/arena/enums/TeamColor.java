package com.heneria.bedwars.arena.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Represents the available team colors with useful metadata.
 */
public enum TeamColor {
    RED("Rouge", ChatColor.RED, Material.RED_WOOL),
    BLUE("Bleu", ChatColor.BLUE, Material.BLUE_WOOL),
    GREEN("Vert", ChatColor.GREEN, Material.LIME_WOOL),
    YELLOW("Jaune", ChatColor.YELLOW, Material.YELLOW_WOOL),
    AQUA("Cyan", ChatColor.AQUA, Material.CYAN_WOOL),
    WHITE("Blanc", ChatColor.WHITE, Material.WHITE_WOOL),
    PINK("Rose", ChatColor.LIGHT_PURPLE, Material.PINK_WOOL),
    GRAY("Gris", ChatColor.GRAY, Material.GRAY_WOOL);

    private final String displayName;
    private final ChatColor chatColor;
    private final Material woolMaterial;

    TeamColor(String displayName, ChatColor chatColor, Material woolMaterial) {
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.woolMaterial = woolMaterial;
    }

    /**
     * Gets the localized display name of the team color.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the chat color associated with the team.
     *
     * @return the chat color
     */
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * Gets the wool material representing the team color.
     *
     * @return the wool material
     */
    public Material getWoolMaterial() {
        return woolMaterial;
    }
}
