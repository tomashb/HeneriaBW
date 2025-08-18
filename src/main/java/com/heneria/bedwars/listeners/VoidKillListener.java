package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Kills players who fall below a configured Y level in active arenas.
 */
public class VoidKillListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
    private final int voidKillHeight;

    public VoidKillListener() {
        this.voidKillHeight = HeneriaBedwars.getInstance().getConfig().getInt("void-kill-height", 0);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArenaByPlayer(player.getUniqueId());
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        if (player.getLocation().getY() < voidKillHeight) {
            player.setHealth(0.0);
        }
    }
}
