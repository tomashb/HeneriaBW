package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.GameUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Iterator;

/**
 * Handles special shop items such as fireballs, instant TNT and bridge eggs.
 */
public class SpecialItemListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }
        ItemStack stack = event.getItem();
        if (stack == null) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        String action = meta.getPersistentDataContainer()
                .get(GameUtils.SPECIAL_ITEM_KEY, PersistentDataType.STRING);
        if (action == null) {
            return;
        }
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }

        switch (action) {
            case "FIREBALL" -> {
                event.setCancelled(true);
                player.launchProjectile(Fireball.class);
                consumeItem(player, event.getHand());
            }
            case "BRIDGE_EGG" -> {
                event.setCancelled(true);
                Egg egg = player.launchProjectile(Egg.class);
                egg.addScoreboardTag("bridge_egg");
                consumeItem(player, event.getHand());
            }
            default -> {
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        if (stack == null || stack.getType() != Material.TNT) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        String action = meta.getPersistentDataContainer()
                .get(GameUtils.SPECIAL_ITEM_KEY, PersistentDataType.STRING);
        if (action == null || !action.equals("BEDWARS_TNT")) {
            return;
        }
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        event.setCancelled(true);
        consumeItem(player, event.getHand());
        TNTPrimed tnt = event.getBlock().getWorld()
                .spawn(event.getBlock().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
        tnt.setFuseTicks(60);
        tnt.addScoreboardTag("bedwars_tnt");
        tnt.setSource(player);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        Player source = null;
        boolean special = false;

        if (entity instanceof Fireball fireball) {
            ProjectileSource shooter = fireball.getShooter();
            if (shooter instanceof Player player) {
                source = player;
                special = true;
            }
        } else if (entity instanceof TNTPrimed tnt && entity.getScoreboardTags().contains("bedwars_tnt")) {
            if (tnt.getSource() instanceof Player player) {
                source = player;
            }
            special = true;
        }

        if (!special) {
            return;
        }

        Arena arena = source != null ? arenaManager.getArena(source) : null;
        if (arena == null) {
            event.blockList().clear();
            return;
        }
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (!arena.getPlacedBlocks().remove(block)) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg egg)) {
            return;
        }
        if (!egg.getScoreboardTags().contains("bridge_egg")) {
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
        Vector direction = egg.getVelocity().clone();
        direction.setY(0);
        if (direction.lengthSquared() == 0) {
            direction = player.getLocation().getDirection().setY(0);
        }
        direction.normalize();
        Location loc = egg.getLocation().getBlock().getLocation();
        Material wool = team.getColor().getWoolMaterial();
        for (int i = 0; i < 12; i++) {
            loc = loc.add(direction);
            Block block = loc.getBlock();
            if (block.getType() == Material.AIR) {
                block.setType(wool);
                arena.getPlacedBlocks().add(block);
            }
        }
    }

    private void consumeItem(Player player, EquipmentSlot slot) {
        ItemStack current = slot == EquipmentSlot.HAND
                ? player.getInventory().getItemInMainHand()
                : player.getInventory().getItemInOffHand();
        if (current == null) {
            return;
        }
        if (current.getAmount() <= 1) {
            if (slot == EquipmentSlot.HAND) {
                player.getInventory().setItemInMainHand(null);
            } else {
                player.getInventory().setItemInOffHand(null);
            }
        } else {
            current.setAmount(current.getAmount() - 1);
        }
    }
}

