package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Teleports players back to the lobby if they fall into the void.
 */
public class LobbyVoidListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();
    private final int voidTeleportHeight;

    public LobbyVoidListener() {
        this.voidTeleportHeight = plugin.getConfig().getInt("void-teleport-height", 0);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (arenaManager.getArena(player) != null) {
            return;
        }
        if (event.getTo() == null) {
            return;
        }
        if (event.getTo().getY() >= event.getFrom().getY()) {
            return;
        }
        if (event.getTo().getY() < voidTeleportHeight) {
            Location lobby = plugin.getMainLobby();
            if (lobby != null) {
                player.teleport(lobby);
            }
        }
    }
}
