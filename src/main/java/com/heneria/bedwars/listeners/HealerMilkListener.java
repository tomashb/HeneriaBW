package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

/**
 * Healer milk removes negative potion effects from the player and nearby teammates.
 */
public class HealerMilkListener implements Listener {

    private static final Set<PotionEffectType> DEBUFFS = Set.of(
            PotionEffectType.SLOWNESS,
            PotionEffectType.MINING_FATIGUE,
            PotionEffectType.NAUSEA,
            PotionEffectType.BLINDNESS,
            PotionEffectType.HUNGER,
            PotionEffectType.WEAKNESS,
            PotionEffectType.POISON,
            PotionEffectType.WITHER
    );

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String special = meta.getPersistentDataContainer()
                .get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
        if (!"HEALER_MILK".equals(special)) {
            return;
        }
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        cleanse(player);
        Team team = arena.getTeam(player);
        if (team != null) {
            for (java.util.UUID id : team.getMembers()) {
                if (id.equals(player.getUniqueId())) {
                    continue;
                }
                Player mate = player.getWorld().getPlayer(id);
                if (mate != null && mate.getLocation().distanceSquared(player.getLocation()) <= 25) {
                    cleanse(mate);
                }
            }
        }
    }

    private void cleanse(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (DEBUFFS.contains(effect.getType())) {
                player.removePotionEffect(effect.getType());
            }
        }
    }
}
