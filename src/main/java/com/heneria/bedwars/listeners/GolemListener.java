package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

/**
 * Prevents team-owned golems from targeting their owners.
 */
public class GolemListener implements Listener {

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof IronGolem golem)) {
            return;
        }
        if (!(event.getTarget() instanceof Player player)) {
            return;
        }
        HeneriaBedwars plugin = HeneriaBedwars.getInstance();
        Arena arena = plugin.getArenaManager().getArenaByPlayer(player.getUniqueId());
        if (arena == null) {
            return;
        }
        Team team = arena.getTeam(player);
        if (team == null) {
            return;
        }
        String tag = "team_" + team.getColor().name();
        if (golem.getScoreboardTags().contains(tag)) {
            event.setCancelled(true);
        }
    }
}

