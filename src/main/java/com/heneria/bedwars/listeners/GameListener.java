package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.GameUtils;
import org.bukkit.Bukkit;
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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Arena arena = arenaManager.getArena(player);

        System.out.println("=============================================");
        System.out.println("[HENERIA DEBUG - LIT] " + player.getName() + " a cassé un bloc de type " + block.getType());
        if (arena == null) {
            System.out.println("[HENERIA DEBUG - LIT] Le joueur n'est pas dans une arène. On ne fait rien.");
            System.out.println("=============================================");
            return;
        }
        System.out.println("[HENERIA DEBUG - LIT] Le joueur est dans l'arène: " + arena.getName());
        if (!block.getType().name().endsWith("_BED")) {
            System.out.println("[HENERIA DEBUG - LIT] Le bloc n'est pas un lit. On ne fait rien.");
            System.out.println("=============================================");
            return;
        }
        System.out.println("[HENERIA DEBUG - LIT] Le bloc EST un lit.");
        Team playerTeam = arena.getTeam(player);
        Team bedTeam = arena.getTeamFromBedLocation(block.getLocation());
        if (playerTeam == null || bedTeam == null) {
            System.out.println("[HENERIA DEBUG - LIT] ÉQUIPE INTROUVABLE. Annulation.");
            event.setCancelled(true);
            System.out.println("=============================================");
            return;
        }
        System.out.println("[HENERIA DEBUG - LIT] Équipe du joueur: " + playerTeam.getColor().name());
        System.out.println("[HENERIA DEBUG - LIT] Équipe du lit: " + bedTeam.getColor().name());
        if (playerTeam.equals(bedTeam)) {
            System.out.println("[HENERIA DEBUG - LIT] Le joueur essaie de casser son propre lit. Annulation.");
            player.sendMessage("§cVous ne pouvez pas détruire le lit de votre propre équipe !");
            event.setCancelled(true);
        } else {
            System.out.println("[HENERIA DEBUG - LIT] C'est un lit ennemi ! Destruction autorisée.");
            event.setDropItems(false);
            bedTeam.setHasBed(false);
            arena.broadcastTitle("§cDESTRUCTION DE LIT !", "§fLe lit de l'équipe " + bedTeam.getColor().getChatColor() + bedTeam.getColor().getDisplayName() + "§f a été détruit par §e" + player.getName() + "§f!", 10, 70, 20);
        }
        System.out.println("=============================================");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = arenaManager.getArena(player);

        System.out.println("=============================================");
        System.out.println("[HENERIA DEBUG - MORT] Le joueur " + player.getName() + " est mort.");
        if (arena == null) {
            System.out.println("[HENERIA DEBUG - MORT] Le joueur n'est pas dans une arène. On ne fait rien.");
            System.out.println("=============================================");
            return;
        }
        System.out.println("[HENERIA DEBUG - MORT] Le joueur est dans l'arène: " + arena.getName());
        event.setDroppedExp(0);
        event.getDrops().clear();
        event.setDeathMessage(null);
        Team playerTeam = arena.getTeam(player);
        if (playerTeam == null) {
            System.out.println("[HENERIA DEBUG - MORT] ÉQUIPE INTROUVABLE. Comportement par défaut.");
            System.out.println("=============================================");
            return;
        }
        System.out.println("[HENERIA DEBUG - MORT] Statut du lit de l'équipe " + playerTeam.getColor().name() + ": " + playerTeam.hasBed());
        if (playerTeam.hasBed()) {
            System.out.println("[HENERIA DEBUG - MORT] Le lit est intact. Lancement de la réapparition.");
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.spigot().respawn();
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(playerTeam.getSpawnLocation());
                new BukkitRunnable() {
                    int countdown = 5;

                    @Override
                    public void run() {
                        if (countdown > 0) {
                            player.sendTitle("§cVOUS ÊTES MORT !", "§fRéapparition dans §e" + countdown + "s", 0, 25, 0);
                            countdown--;
                        } else {
                            this.cancel();
                            player.setGameMode(GameMode.SURVIVAL);
                            player.teleport(playerTeam.getSpawnLocation());
                            GameUtils.giveDefaultKit(player);
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            });
        } else {
            System.out.println("[HENERIA DEBUG - MORT] Le lit est détruit. Élimination finale.");

            // DOIT ÊTRE APPELÉ IMMÉDIATEMENT POUR SUPPRIMER L'ÉCRAN DE MORT
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.spigot().respawn(), 1L);

            // Le reste de la logique peut suivre...
            arena.eliminatePlayer(player);
            player.setGameMode(GameMode.SPECTATOR); // Spectateur permanent
            player.teleport(playerTeam.getSpawnLocation());
            arena.broadcastTitle("§cÉLIMINATION !", "§e" + player.getName() + "§f a été éliminé.", 10, 70, 20);
            arena.checkForWinner();
        }
        System.out.println("=============================================");
    }
}
