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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

/**
 * Handles the healer milk item which removes negative effects from teammates.
 */
public class HealerMilkListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();
    private final int radius;

    public HealerMilkListener() {
        this.radius = plugin.getConfig().getInt("healer-milk.radius", 10);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta == null) {
            return;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String type = container.get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
        if (!"HEALER_MILK".equals(type)) {
            return;
        }
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        cleanEffects(player);
        Team team = arena.getTeam(player);
        if (team == null) {
            return;
        }
        for (UUID id : team.getMembers()) {
            Player mate = Bukkit.getPlayer(id);
            if (mate == null || mate.equals(player)) {
                continue;
            }
            if (mate.getWorld() != player.getWorld()) {
                continue;
            }
            if (mate.getLocation().distanceSquared(player.getLocation()) <= radius * radius) {
                cleanEffects(mate);
            }
        }
    }

    private void cleanEffects(Player p) {
        for (PotionEffect effect : p.getActivePotionEffects()) {
            if (effect.getType().isBad()) {
                p.removePotionEffect(effect.getType());
            }
        }
    }
}
