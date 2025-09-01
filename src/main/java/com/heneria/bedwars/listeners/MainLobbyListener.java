package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import com.heneria.bedwars.utils.MessageManager;

/**
 * Handles player spawning and protection in the main lobby.
 */
public class MainLobbyListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joining = event.getPlayer();
        Location lobby = plugin.getMainLobby();
        if (lobby != null) {
            joining.teleport(lobby);
            joining.setGameMode(GameMode.ADVENTURE);
            joining.setFoodLevel(20);
        }
        plugin.getScoreboardManager().setScoreboard(joining);
        plugin.getTablistManager().updatePlayer(joining);
        for (String line : MessageManager.getList("on-join.welcome-message")) {
            joining.sendMessage(line.replace("{player}", joining.getName()));
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(joining)) {
                continue;
            }
            if (arenaManager.getArena(online) != null) {
                online.hidePlayer(plugin, joining);
                joining.hidePlayer(plugin, online);
            } else {
                online.showPlayer(plugin, joining);
                joining.showPlayer(plugin, online);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (arenaManager.getArena(player) == null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (HeneriaBedwars.getInstance().getSetupManager().isBypassing(player.getUniqueId())) {
            return;
        }
        if (arenaManager.getArena(player) == null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (HeneriaBedwars.getInstance().getSetupManager().isBypassing(player.getUniqueId())) {
            return;
        }
        if (arenaManager.getArena(player) == null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (arenaManager.getArena(player) == null) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }
}
