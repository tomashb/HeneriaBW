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
        System.out.println("[LobbyVoidListener] Checking player " + player.getName());
        if (arenaManager.getArena(player) != null) {
            System.out.println("[LobbyVoidListener] Player is in an arena, ignoring.");
            return;
        }
        Location lobby = plugin.getMainLobby();
        if (lobby == null) {
            System.out.println("[LobbyVoidListener] Main lobby is not set.");
            return;
        }
        if (!player.getWorld().equals(lobby.getWorld())) {
            System.out.println("[LobbyVoidListener] Player is not in the lobby world.");
            return;
        }
        if (player.getLocation().getY() < voidTeleportHeight) {
            System.out.println("[LobbyVoidListener] Teleporting " + player.getName() + " to lobby.");
            player.teleport(lobby);
        }
    }
}
