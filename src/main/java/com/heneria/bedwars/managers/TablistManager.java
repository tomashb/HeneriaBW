package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * Handles lobby tablist header and footer updates.
 */
public class TablistManager {

    private final HeneriaBedwars plugin;
    private final ArenaManager arenaManager;
    private String header;
    private String footer;

    public TablistManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
        loadConfig();
        startTask();
    }

    private void loadConfig() {
        File file = new File(plugin.getDataFolder(), "tablist.yml");
        if (!file.exists()) {
            plugin.saveResource("tablist.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.header = config.getString("header", "");
        this.footer = config.getString("footer", "");
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAll();
            }
        }.runTaskTimer(plugin, 0L, 100L);
    }

    /**
     * Updates the tablist for a specific player based on their location.
     *
     * @param player target player
     */
    public void updatePlayer(Player player) {
        Arena arena = arenaManager.getArena(player);
        if (arena == null) {
            player.setPlayerListHeaderFooter(format(header, player), format(footer, player));
        } else {
            player.setPlayerListHeaderFooter(null, null);
        }
    }

    private void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    private String format(String text, Player player) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        text = text.replace("\\n", "\n");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
