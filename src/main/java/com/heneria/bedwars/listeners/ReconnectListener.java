package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.managers.ReconnectManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Captures disconnects to offer a reconnection window.
 */
public class ReconnectListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
    private final ReconnectManager reconnectManager = HeneriaBedwars.getInstance().getReconnectManager();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena != null && arena.getState() == GameState.PLAYING) {
            reconnectManager.markDisconnected(player, arena);
            arena.removePlayer(player);
            arena.checkForWinner();
        }
    }
}
