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
 * Handles interaction with upgrade shop NPCs.
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
        Arena arena = plugin.getArenaManager().getArenaByPlayer(player.getUniqueId());
        if (arena == null) {
            return;
        }
        Team team = arena.getTeam(player);
        if (team == null) {
            player.sendMessage("§cVous n'avez pas d'équipe.");
            return;
        }
        new TeamUpgradesMenu(plugin, arena, team).open(player);
    }
}
