package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles broadcasting of custom join and quit messages.
 */
public class JoinQuitMessageListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        String name = event.getPlayer().getName();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (HeneriaBedwars.getInstance().getArenaManager().getArena(player) == null) {
                MessageManager.sendRawMessage(player, "server.join-message", "player", name);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        String name = event.getPlayer().getName();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (HeneriaBedwars.getInstance().getArenaManager().getArena(player) == null) {
                MessageManager.sendRawMessage(player, "server.leave-message", "player", name);
            }
        }
    }
}
