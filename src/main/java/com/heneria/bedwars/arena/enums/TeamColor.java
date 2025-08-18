package com.heneria.bedwars.arena.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Color;

/**
 * Represents the available team colors with useful metadata.
 */
public enum TeamColor {
    RED("Rouge", ChatColor.RED, Material.RED_WOOL, Color.RED),
    BLUE("Bleu", ChatColor.BLUE, Material.BLUE_WOOL, Color.BLUE),
    GREEN("Vert", ChatColor.GREEN, Material.LIME_WOOL, Color.LIME),
    YELLOW("Jaune", ChatColor.YELLOW, Material.YELLOW_WOOL, Color.YELLOW),
    AQUA("Cyan", ChatColor.AQUA, Material.CYAN_WOOL, Color.AQUA),
    WHITE("Blanc", ChatColor.WHITE, Material.WHITE_WOOL, Color.WHITE),
    PINK("Rose", ChatColor.LIGHT_PURPLE, Material.PINK_WOOL, Color.FUCHSIA),
    GRAY("Gris", ChatColor.GRAY, Material.GRAY_WOOL, Color.GRAY);

    private final String displayName;
    private final ChatColor chatColor;
    private final Material woolMaterial;
    private final Color leatherColor;

    TeamColor(String displayName, ChatColor chatColor, Material woolMaterial, Color leatherColor) {
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.woolMaterial = woolMaterial;
        this.leatherColor = leatherColor;
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

    /**
     * Gets the leather armor color associated with the team.
     *
     * @return the armor color
     */
    public Color getLeatherColor() {
        return leatherColor;
    }
}
