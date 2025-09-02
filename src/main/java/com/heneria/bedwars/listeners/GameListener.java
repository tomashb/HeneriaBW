package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.events.GameStateChangeEvent;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.managers.StatsManager;
import com.heneria.bedwars.stats.PlayerStats;
import com.heneria.bedwars.utils.MessageManager;
import com.heneria.bedwars.managers.CosmeticManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import com.heneria.bedwars.utils.GameUtils;

public class GameListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();
    private final StatsManager statsManager = plugin.getStatsManager();
    private final CosmeticManager cosmeticManager = plugin.getCosmeticManager();

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
    public void onBedInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null || !(block.getBlockData() instanceof Bed)) {
            return;
        }
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getSetupManager().isBypassing(player.getUniqueId())) {
            return;
        }
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }

        Team bedTeam = arena.getTeamOfBed(block);

        if (bedTeam != null) {
            Team playerTeam = arena.getTeam(player);
                if (playerTeam != null && playerTeam.equals(bedTeam)) {
                    MessageManager.sendMessage(player, "errors.break-own-bed");
                    event.setCancelled(true);
                } else {
                    event.setDropItems(false);
                    bedTeam.setHasBed(false);
                    arena.broadcastTitleExcept(player, "game.bed-destroyed-title", "game.bed-destroyed-subtitle", 10, 70, 20, "team", bedTeam.getColor().getDisplayName(), "player", player.getName());
                    String attackerColor = playerTeam != null ? playerTeam.getColor().getChatColor().toString() : "";
                    String custom = cosmeticManager.getBedBreakMessage(player.getUniqueId());
                    if (custom != null) {
                        arena.broadcastRaw(custom.replace("{victim_team}", bedTeam.getColor().getDisplayName())
                                .replace("{player}", player.getName()));
                    } else {
                        arena.broadcast("game.bed-destroyed-chat",
                                "victim_team_color", bedTeam.getColor().getChatColor().toString(),
                                "victim_team_name", bedTeam.getColor().getDisplayName(),
                                "attacker_color", attackerColor,
                                "attacker_name", player.getName());
                    }
                    CosmeticManager.BedBreakEffect effect = cosmeticManager.getBedBreakEffect(player.getUniqueId());
                    if (effect != null) {
                        effect.play(block.getLocation());
                    }
                    PlayerStats stats = statsManager.getStats(player);
                    if (stats != null) {
                        stats.incrementBedsBroken();
                    }
                }
                return;
            }

        if (!arena.getPlacedBlocks().remove(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
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
        
        Team playerTeam = arena.getTeam(player);
        if (playerTeam == null) return;

        // Point de vue du lobby utilisé pour les spectateurs
        Location lobbyView = arena.getLobbyLocation() != null
                ? arena.getLobbyLocation().clone().add(0, 20, 0)
                : playerTeam.getSpawnLocation();

        // Forcer la réapparition et téléporter le joueur en spectateur au-dessus du lobby
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.spigot().respawn();
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(lobbyView);
        }, 1L);

        Player killer = player.getKiller();
        if (killer != null) {
            PlayerStats killerStats = statsManager.getStats(killer);
            if (killerStats != null) {
                killerStats.incrementKills();
            }
            plugin.getBountyManager().handleKill(killer, player, arena);
        }
        PlayerStats victimStats = statsManager.getStats(player);
        if (victimStats != null) {
            victimStats.incrementDeaths();
        }

        String victimColor = playerTeam.getColor().getChatColor().toString();
        if (killer != null) {
            Team killerTeam = arena.getTeam(killer);
            String attackerColor = killerTeam != null ? killerTeam.getColor().getChatColor().toString() : "";
            String custom = cosmeticManager.getKillMessage(killer.getUniqueId());
            if (custom != null) {
                arena.broadcastRaw(custom.replace("{victim}", player.getName()).replace("{killer}", killer.getName()));
            } else {
                arena.broadcast("game.player-kill-by-player",
                        "victim_color", victimColor,
                        "victim_name", player.getName(),
                        "attacker_color", attackerColor,
                        "attacker_name", killer.getName());
            }
            CosmeticManager.KillEffect effect = cosmeticManager.getKillEffect(killer.getUniqueId());
            if (effect != null) {
                effect.play(player.getLocation());
            }
        } else {
            arena.broadcast("game.player-kill-void",
                    "victim_color", victimColor,
                    "victim_name", player.getName());
        }

        if (playerTeam.hasBed()) {
            GameUtils.removeUpgradedToolsAndSwords(player);
            new BukkitRunnable() {
                int countdown = 5;
                public void run() {
                    if (countdown > 0) {
                        MessageManager.sendTitle(player, "game.respawn-title", "game.respawn-subtitle", 0, 25, 0, "seconds", String.valueOf(countdown));
                        countdown--;
                    } else {
                        this.cancel();
                        player.setGameMode(GameMode.SURVIVAL);
                        player.teleport(playerTeam.getSpawnLocation());
                        GameUtils.giveDefaultKit(player, playerTeam);
                        plugin.getUpgradeManager().applyTeamUpgrades(player);
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        } else {
            arena.eliminatePlayer(player);
            player.getInventory().clear();
            arena.broadcastTitle("game.elimination-title", "game.elimination-subtitle", 10, 70, 20, "player", player.getName());
            plugin.getPlayerProgressionManager().removePlayer(player.getUniqueId());
            arena.checkForWinner();
        }
    }
}
