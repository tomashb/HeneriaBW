package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.gui.upgrades.TeamUpgradesMenu;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Handles interaction with upgrade shop NPCs.
 */
public class UpgradeListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!entity.getScoreboardTags().contains("upgrade_npc")) {
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
            MessageManager.sendMessage(player, "errors.no-team");
            return;
        }
        new TeamUpgradesMenu(plugin, arena, team).open(player);
    }
}
