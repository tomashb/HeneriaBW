package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Tracks blocks placed by players during a game.
 */
public class BlockPlaceListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        Block block = event.getBlockPlaced();
        if (arena.getMaxBuildY() > 0 && block.getY() > arena.getMaxBuildY()) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "errors.build-outside-boundaries");
            return;
        }
        if (arena.getMaxBuildDistance() > 0) {
            Location center = arena.getCenterLocation();
            if (center != null) {
                double dx = block.getX() - center.getX();
                double dz = block.getZ() - center.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > arena.getMaxBuildDistance()) {
                    event.setCancelled(true);
                    MessageManager.sendMessage(player, "errors.build-outside-boundaries");
                    return;
                }
            }
        }
        arena.getPlacedBlocks().add(block);
        if (block.getType() == Material.BLACK_STAINED_GLASS) {
            arena.getTemperedGlassBlocks().add(block);
        }
    }
}
