package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class BedBreakListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArenaByPlayer(player.getUniqueId());
        if (arena == null) {
            return;
        }

        Block block = event.getBlock();
        if (!Tag.BEDS.isTagged(block.getType())) {
            return;
        }

        Team targetTeam = null;
        for (Team team : arena.getTeams().values()) {
            Location loc = team.getBedLocation();
            if (loc != null && loc.getWorld().equals(block.getWorld())
                    && loc.getBlockX() == block.getX()
                    && loc.getBlockY() == block.getY()
                    && loc.getBlockZ() == block.getZ()) {
                targetTeam = team;
                break;
            }
        }

        if (targetTeam == null) {
            event.setCancelled(true);
            return;
        }

        Team playerTeam = arena.getTeam(player.getUniqueId());
        if (playerTeam != null && playerTeam == targetTeam) {
            event.setCancelled(true);
            MessageUtils.sendMessage(player, "&cVous ne pouvez pas détruire votre propre lit !");
            return;
        }

        if (!targetTeam.hasBed()) {
            event.setCancelled(true);
            return;
        }

        event.setDropItems(false);
        block.setType(Material.AIR);
        if (block.getBlockData() instanceof Bed bed) {
            Block other = block.getRelative(bed.getFacing().getOppositeFace());
            if (Tag.BEDS.isTagged(other.getType())) {
                other.setType(Material.AIR);
            }
        }

        targetTeam.setHasBed(false);

        for (UUID id : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                MessageUtils.sendMessage(p, "&cLe lit de l'équipe " + targetTeam.getColor().getDisplayName() + " a été détruit par " + player.getName() + " !");
                p.sendTitle("§cLit détruit", "§fLe lit de l'équipe " + targetTeam.getColor().getDisplayName() + " a été détruit par " + player.getName() + " !", 10, 60, 10);
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 1f);
            }
        }
    }
}
