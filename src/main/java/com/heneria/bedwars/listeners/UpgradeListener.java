package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.gui.upgrades.TeamUpgradesMenu;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Gère l'ouverture du menu des améliorations lors de l'interaction avec le PNJ dédié.
 */
public class UpgradeListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) {
            return;
        }
        if (!villager.getScoreboardTags().contains("upgrade_npc")) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        HeneriaBedwars plugin = HeneriaBedwars.getInstance();
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) {
            return;
        }
        Team team = arena.getTeam(player);
        if (team == null) {
            return;
        }
        new TeamUpgradesMenu(plugin.getUpgradeManager(), arena, team).open(player);
    }
}
