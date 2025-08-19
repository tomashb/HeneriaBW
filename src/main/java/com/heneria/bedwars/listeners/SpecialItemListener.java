package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles special gameplay items like fireballs, instant TNT and bridge eggs.
 */
public class SpecialItemListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();
    private final Map<Egg, Location> eggStarts = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        Material type = item.getType();
        if (type == Material.FIRE_CHARGE) {
            event.setCancelled(true);
            item.setAmount(item.getAmount() - 1);
            player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), org.bukkit.entity.Fireball.class, fireball -> {
                fireball.setVelocity(player.getLocation().getDirection().multiply(1.5));
                fireball.setShooter(player);
                fireball.setIsIncendiary(false);
                fireball.setYield(0f);
            });
        } else if (type == Material.EGG) {
            event.setCancelled(true);
            item.setAmount(item.getAmount() - 1);
            Egg egg = player.launchProjectile(Egg.class);
            egg.setVelocity(player.getLocation().getDirection().multiply(1.5));
            eggStarts.put(egg, player.getLocation());
        } else if (type == Material.CHEST) {
            event.setCancelled(true);
            Team team = arena.getTeam(player);
            if (team == null) {
                return;
            }
            Block currentBlock = player.getLocation().getBlock();
            for (int i = 0; i < 4; i++) {
                Block blockToPlace = currentBlock.getRelative(BlockFace.DOWN, i);
                if (blockToPlace.getType() != Material.AIR) {
                    return;
                }
            }
            item.setAmount(item.getAmount() - 1);
            Material teamWoolMaterial = team.getColor().getWoolMaterial();
            for (int i = 0; i < 4; i++) {
                Block blockToPlace = currentBlock.getRelative(BlockFace.DOWN, i);
                blockToPlace.setType(teamWoolMaterial);
                arena.getPlacedBlocks().add(blockToPlace);
            }
            player.teleport(currentBlock.getRelative(BlockFace.UP, 1).getLocation().add(0.5, 0, 0.5));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.TNT) {
            return;
        }
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        event.setCancelled(true);
        Block block = event.getBlockPlaced();
        block.setType(Material.AIR);
        TNTPrimed tnt = player.getWorld().spawn(block.getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
        tnt.setFuseTicks(40);
        tnt.setSource(player);
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        itemInHand.setAmount(itemInHand.getAmount() - 1);
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed tnt)) {
            return;
        }
        if (!(tnt.getSource() instanceof Player player)) {
            return;
        }
        Arena arena = arenaManager.getArena(player);
        if (arena == null) {
            return;
        }
        event.blockList().removeIf(b -> !arena.getPlacedBlocks().contains(b));
        arena.getPlacedBlocks().removeAll(event.blockList());
    }

    @EventHandler
    public void onEggHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg egg)) {
            return;
        }
        Location start = eggStarts.remove(egg);
        if (start == null) {
            return;
        }
        if (!(egg.getShooter() instanceof Player player)) {
            return;
        }
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        Team team = arena.getTeam(player);
        if (team == null) {
            return;
        }
        Location end = egg.getLocation();
        double startY = start.getBlockY();
        Vector direction = end.toVector().subtract(start.toVector());
        direction.setY(0);
        double length = direction.length();
        if (length == 0) {
            return;
        }
        Vector step = direction.normalize();
        Location current = start.clone();
        for (double i = 0; i < length; i += 1) {
            current.add(step);
            current.setY(startY);
            Block block = current.getBlock();
            if (block.getType() == Material.AIR) {
                block.setType(team.getColor().getWoolMaterial());
                arena.getPlacedBlocks().add(block);
            }
        }
    }
}
