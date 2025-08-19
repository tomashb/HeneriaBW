package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.ArenaSelectorMenu;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Opens the arena selector menu when clicking a join NPC.
 */
public class JoinNpcListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            return;
        }
        if (!CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked())) {
            return;
        }
        String mode = HeneriaBedwars.getInstance().getNpcManager().getMode(event.getRightClicked());
        if (mode == null) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        new ArenaSelectorMenu(mode).open(player);
    }
}
