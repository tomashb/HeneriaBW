package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

/**
 * Places a small wool platform beneath the player to save them from falling.
 */
public class SafetyPlatformListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String special = meta.getPersistentDataContainer()
                .get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
        if (!"SAFETY_PLATFORM".equals(special)) {
            return;
        }
        Player player = event.getPlayer();
        if (player.isOnGround()) {
            return;
        }
        if (player.getVelocity().getY() >= 0) {
            return;
        }
        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);
        Arena arena = arenaManager.getArena(player);
        Team team = arena != null ? arena.getTeam(player) : null;
        Material wool = team != null ? team.getColor().getWoolMaterial() : Material.WHITE_WOOL;
        Location center = player.getLocation().clone().add(0, -1, 0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block b = center.clone().add(x, 0, z).getBlock();
                if (b.getType() == Material.AIR) {
                    b.setType(wool);
                    if (arena != null) {
                        arena.getPlacedBlocks().add(b);
                    }
                }
            }
        }
        player.setVelocity(new Vector(0, 0.5, 0));
    }
}
