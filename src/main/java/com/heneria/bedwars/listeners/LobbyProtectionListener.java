package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.Projectile;

/**
 * Protects the lobby from PvP before the game starts.
 */
public class LobbyProtectionListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        if (!(victim instanceof Player player)) {
            return;
        }
        Player damagerPlayer = null;
        Entity damager = event.getDamager();
        if (damager instanceof Player p) {
            damagerPlayer = p;
        } else if (damager instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player p) {
                damagerPlayer = p;
            }
        }
        if (damagerPlayer == null) {
            return;
        }
        Arena arena = arenaManager.getArena(player);
        if (arena == null) {
            return;
        }
        if (arena.getState() == GameState.WAITING || arena.getState() == GameState.STARTING) {
            event.setCancelled(true);
        }
    }
}
