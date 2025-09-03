package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.utils.MessageManager;
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
    private String mainLobbyHeader;
    private String mainLobbyFooter;
    private String waitingLobbyHeader;
    private String waitingLobbyFooter;
    private String gameHeader;
    private String gameFooter;

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
        this.mainLobbyHeader = config.getString("main-lobby.header", "");
        this.mainLobbyFooter = config.getString("main-lobby.footer", "");
        this.waitingLobbyHeader = config.getString("waiting-lobby.header", "");
        this.waitingLobbyFooter = config.getString("waiting-lobby.footer", "");
        this.gameHeader = config.getString("game.header", "");
        this.gameFooter = config.getString("game.footer", "");
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAll();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Updates the tablist for a specific player based on their location.
     *
     * @param player target player
     */
    public void updatePlayer(Player player) {
        Arena arena = arenaManager.getArena(player);
        if (arena == null) {
            player.setPlayerListName(player.getName());
            player.setPlayerListHeaderFooter(format(mainLobbyHeader, player, null), format(mainLobbyFooter, player, null));
        } else if (arena.getState() == GameState.WAITING || arena.getState() == GameState.STARTING) {
            player.setPlayerListName(player.getName());
            player.setPlayerListHeaderFooter(format(waitingLobbyHeader, player, arena), format(waitingLobbyFooter, player, arena));
        } else {
            Team team = arena.getTeam(player);
            if (team != null) {
                String bed = team.hasBed() ? MessageManager.get("scoreboard.bed-alive") : MessageManager.get("scoreboard.bed-destroyed");
                ChatColor teamColor = team.getColor().getChatColor();
                player.setPlayerListName(teamColor + bed + " " + teamColor + player.getName());
            } else {
                player.setPlayerListName(player.getName());
            }
            player.setPlayerListHeaderFooter(format(gameHeader, player, arena), format(gameFooter, player, arena));
        }
    }

    private void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    private String format(String text, Player player, Arena arena) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        text = text.replace("\\n", "\n");
        if (arena != null) {
            text = text.replace("{map_name}", arena.getName())
                    .replace("{current_players}", String.valueOf(arena.getPlayers().size()))
                    .replace("{max_players}", String.valueOf(arena.getMaxPlayers()))
                    .replace("{status}", getLobbyStatus(arena));
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private String getLobbyStatus(Arena arena) {
        GameState state = arena.getState();
        if (state == GameState.STARTING) {
            return MessageManager.get("scoreboard.lobby-starting", "time", String.valueOf(arena.getCountdownTime()));
        }
        if (state == GameState.WAITING) {
            return MessageManager.get("scoreboard.lobby-waiting");
        }
        return "";
    }
}
