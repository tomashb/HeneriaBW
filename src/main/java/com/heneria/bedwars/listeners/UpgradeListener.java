package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.gui.upgrades.TeamUpgradesMenu;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import com.heneria.bedwars.managers.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Handles interaction with upgrade shop NPCs.
 */
public class UpgradeListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        HeneriaBedwars plugin = HeneriaBedwars.getInstance();
        NpcManager.ShopInfo info = plugin.getNpcManager().getShopInfo(event.getRightClicked());
        if (info != null && "upgrade".equalsIgnoreCase(info.type())) {
            event.setCancelled(true);
        } else {
            if (!(event.getRightClicked() instanceof Villager villager)) {
                return;
            }
            if (!villager.getScoreboardTags().contains("upgrade_npc")) {
                return;
            }
            event.setCancelled(true);
        }
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
