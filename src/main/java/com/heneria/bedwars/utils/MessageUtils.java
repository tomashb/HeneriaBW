package com.heneria.bedwars.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Utility for sending formatted plugin messages.
 */
public final class MessageUtils {

    private static final String PREFIX = "§8[§bHeneriaBedwars§8]§r ";

    private MessageUtils() {
    }

    /**
     * Sends a formatted message to a player.
     *
     * @param player the player
     * @param message the message
     */
    public static void sendMessage(Player player, String message) {
        sendMessage((CommandSender) player, message);
    }

    /**
     * Sends a formatted message to any command sender.
     *
     * @param sender the command sender
     * @param message the message
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', message));
    }
}
