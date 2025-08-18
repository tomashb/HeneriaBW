package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.events.GameStateChangeEvent;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();

    // Quand le jeu démarre, on doit enregistrer les lits
    // CECI EST UN NOUVEL ÉVÉNEMENT A AJOUTER
    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getNewState() == GameState.PLAYING) {
            Arena arena = event.getArena();
            arena.registerBeds();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }

        Team bedTeam = arena.getTeamOfBed(block);

        if (bedTeam != null) {
            Team playerTeam = arena.getTeam(player);
            if (playerTeam != null && playerTeam.equals(bedTeam)) {
                player.sendMessage("§cVous ne pouvez pas casser votre propre lit.");
                event.setCancelled(true);
            } else {
                event.setDropItems(false);
                bedTeam.setHasBed(false);
                arena.broadcastTitle("§cDESTRUCTION DE LIT !", "Lit de l'équipe " + bedTeam.getColor().getDisplayName() + " détruit par " + player.getName(), 10, 70, 20);
            }
            return;
        }

        if (!arena.getPlacedBlocks().remove(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) return;
        
        event.setDroppedExp(0);
        event.getDrops().clear();
        event.setDeathMessage(null);
        
        // On force la réapparition pour éviter l'écran de mort
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.spigot().respawn(), 1L);

        Team playerTeam = arena.getTeam(player);
        if (playerTeam == null) return;

        if (playerTeam.hasBed()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(playerTeam.getSpawnLocation());
            new BukkitRunnable() {
                int countdown = 5;
                public void run() {
                    if (countdown > 0) {
                        player.sendTitle("§cVOUS ÊTES MORT !", "Réapparition dans §e" + countdown + "s", 0, 25, 0);
                        countdown--;
                    } else {
                        this.cancel();
                        player.setGameMode(GameMode.SURVIVAL);
                        player.teleport(playerTeam.getSpawnLocation());
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        } else {
            arena.eliminatePlayer(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(playerTeam.getSpawnLocation());
            arena.broadcastTitle("§cÉLIMINATION !", "§e" + player.getName() + "§f a été éliminé.", 10, 70, 20);
            arena.checkForWinner();
        }
    }
}
