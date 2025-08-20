package com.heneria.bedwars.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Central manager for handling translatable messages.
 */
public final class MessageManager {

    private static FileConfiguration messages;
    private static String prefix = "";

    private MessageManager() {
    }

    /**
     * Initializes the message manager and loads the language file.
     *
     * @param plugin the plugin instance
     */
    public static void init(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
        prefix = ChatColor.translateAlternateColorCodes('&', messages.getString("prefix", ""));
    }

    private static String format(String path, String... placeholders) {
        String message = messages.getString(path, path);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Gets a formatted message without prefix.
     *
     * @param path the config path
     * @param placeholders placeholder replacements
     * @return formatted message
     */
    public static String get(String path, String... placeholders) {
        return format(path, placeholders);
    }

    /**
     * Convenience alias for {@link #get(String, String...)} to mirror plugin API usage.
     *
     * @param path the config path
     * @param placeholders placeholder replacements
     * @return formatted message
     */
    public static String getFormattedMessage(String path, String... placeholders) {
        return get(path, placeholders);
    }

    /**
     * Sends a message with the configured prefix.
     *
     * @param sender the receiver
     * @param path the config path
     * @param placeholders placeholder replacements
     */
    public static void sendMessage(CommandSender sender, String path, String... placeholders) {
        sender.sendMessage(prefix + format(path, placeholders));
    }

    /**
     * Sends a message without prefix.
     *
     * @param sender the receiver
     * @param path the config path
     * @param placeholders placeholder replacements
     */
    public static void sendRawMessage(CommandSender sender, String path, String... placeholders) {
        sender.sendMessage(format(path, placeholders));
    }

    /**
     * Retrieves a list of formatted messages.
     *
     * @param path config path
     * @return list of formatted strings
     */
    public static java.util.List<String> getList(String path) {
        java.util.List<String> list = messages.getStringList(path);
        java.util.List<String> formatted = new java.util.ArrayList<>();
        for (String line : list) {
            formatted.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return formatted;
    }

    /**
     * Sends a title to the player using messages from the file.
     *
     * @param player the player
     * @param titlePath path for title text
     * @param subtitlePath path for subtitle text
     * @param fadeIn fade in time
     * @param stay stay time
     * @param fadeOut fade out time
     * @param placeholders placeholder replacements
     */
    public static void sendTitle(Player player, String titlePath, String subtitlePath, int fadeIn, int stay, int fadeOut, String... placeholders) {
        String title = format(titlePath, placeholders);
        String subtitle = format(subtitlePath, placeholders);
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
