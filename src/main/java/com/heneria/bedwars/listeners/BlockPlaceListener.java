package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.block.Block;
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
        if (!arena.isWithinBoundaries(block.getX(), block.getY(), block.getZ())) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "errors.out-of-bounds");
            return;
        }
        arena.getPlacedBlocks().add(block);
    }
}
