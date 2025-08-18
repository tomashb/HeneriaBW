package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
    private static final int RESPAWN_DELAY_SECONDS = 5;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = arenaManager.getArenaByPlayer(player.getUniqueId());
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }

        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepInventory(true);

        Bukkit.getScheduler().runTask(HeneriaBedwars.getInstance(), () -> {
            player.spigot().respawn();
            Team team = arena.getTeam(player.getUniqueId());
            if (team == null) {
                return;
            }
            if (team.hasBed()) {
                player.setGameMode(GameMode.SPECTATOR);
                if (team.getSpawnLocation() != null) {
                    player.teleport(team.getSpawnLocation());
                }
                Bukkit.getScheduler().runTaskLater(HeneriaBedwars.getInstance(), () -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    if (team.getSpawnLocation() != null) {
                        player.teleport(team.getSpawnLocation());
                    }
                    player.getInventory().clear();
                    player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                }, RESPAWN_DELAY_SECONDS * 20L);
            } else {
                arena.removeAlivePlayer(player.getUniqueId());
                arena.addSpectator(player.getUniqueId());
                player.setGameMode(GameMode.SPECTATOR);
                for (UUID id : arena.getPlayers()) {
                    Player p = Bukkit.getPlayer(id);
                    if (p != null) {
                        MessageUtils.sendMessage(p, "&c" + player.getName() + " a été éliminé !");
                        p.sendTitle("§c" + player.getName(), "§7a été éliminé !", 10, 40, 10);
                    }
                }
            }
        });
    }
}
