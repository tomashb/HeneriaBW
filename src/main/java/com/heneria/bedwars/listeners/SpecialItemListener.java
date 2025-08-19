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
import org.bukkit.entity.IronGolem;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String special = container.get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
            if ("POPUP_TOWER".equals(special)) {
                System.out.println("SpecialItemListener: Pop-up Tower item detected for " + player.getName());
                event.setCancelled(true);
                int towerHeight = 4; // Hauteur de la tour en blocs
                Block baseBlock = player.getLocation().getBlock(); // Le bloc où sont les pieds du joueur

                // --- 1. Vérification de l'espace ---
                // Vérifier que l'espace pour la tour est libre
                for (int i = 0; i < towerHeight; i++) {
                    Block blockToCheck = baseBlock.getRelative(BlockFace.DOWN, i);
                    // On autorise la construction sur de l'air ou des plantes, mais pas des blocs solides (sauf le premier)
                    if (i > 0 && !blockToCheck.isPassable() && blockToCheck.getType() != Material.AIR) {
                        player.sendMessage("§cEspace obstrué en dessous !");
                        return; // On ne consomme pas l'item
                    }
                }
                // Vérifier que le joueur a 2 blocs d'air au-dessus de lui pour être surélevé
                if (baseBlock.getRelative(BlockFace.UP, 1).getType() != Material.AIR || baseBlock.getRelative(BlockFace.UP, 2).getType() != Material.AIR) {
                    player.sendMessage("§cPas assez d'espace au-dessus de votre tête !");
                    return; // On ne consomme pas l'item
                }

                // --- 2. Si l'espace est libre, on consomme l'item ---
                event.getItem().setAmount(event.getItem().getAmount() - 1);

                // --- 3. Construction de la tour ---
                Team team = arena.getTeam(player);
                if (team == null) {
                    return;
                }
                Material teamWool = team.getColor().getWoolMaterial();

                // On construit de haut en bas, en partant du bloc sous les pieds du joueur
                for (int i = 0; i < towerHeight; i++) {
                    Block blockToPlace = baseBlock.getRelative(BlockFace.DOWN, i);
                    blockToPlace.setType(teamWool);
                    arena.getPlacedBlocks().add(blockToPlace);
                }

                // --- 4. Téléportation du joueur ---
                // On téléporte le joueur 1 bloc au-dessus de la base de la tour qu'on vient de créer
                Location topOfTower = baseBlock.getLocation().add(0.5, 1.0, 0.5);
                topOfTower.setYaw(player.getLocation().getYaw());
                topOfTower.setPitch(player.getLocation().getPitch());
                player.teleport(topOfTower);
                System.out.println("SpecialItemListener: Pop-up Tower built for " + player.getName());
                return;
            }
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
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }

        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                String special = container.get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
                if ("SPAWN_IRON_GOLEM".equals(special)) {
                    event.setCancelled(true);
                    event.getBlock().setType(Material.AIR);
                    item.setAmount(item.getAmount() - 1);
                    Location loc = event.getBlock().getLocation().add(0.5, 0, 0.5);
                    IronGolem golem = loc.getWorld().spawn(loc, IronGolem.class);
                    golem.setPlayerCreated(true);
                    Team team = arena.getTeam(player);
                    if (team != null) {
                        golem.addScoreboardTag("team_" + team.getColor().name());
                    }
                    arena.addNpc(golem);
                    return;
                }
            }
        }

        if (event.getBlockPlaced().getType() != Material.TNT) {
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
