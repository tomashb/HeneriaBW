package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Handles the "Lait du Guérisseur" item which heals nearby teammates.
 */
public class HealerMilkListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String special = container.get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
        if (!"HEALER_MILK".equals(special)) {
            return;
        }
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        Team team = arena.getTeam(player);
        if (team == null) {
            return;
        }
        for (UUID id : team.getMembers()) {
            Player mate = Bukkit.getPlayer(id); // Correct API usage
            if (mate != null && mate.isOnline() && mate.getWorld().equals(player.getWorld())) {
                if (mate.getLocation().distanceSquared(player.getLocation()) <= 64) {
                    mate.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                }
            }
        }
    }
}
