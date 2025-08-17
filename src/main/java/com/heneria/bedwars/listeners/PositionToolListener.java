package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GeneratorType;
import com.heneria.bedwars.arena.enums.TeamColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles clicks with the positioning tool to record locations.
 */
public class PositionToolListener implements Listener {

    private final NamespacedKey typeKey;
    private final NamespacedKey arenaKey;

    public PositionToolListener(HeneriaBedwars plugin) {
        this.typeKey = new NamespacedKey(plugin, "pos-type");
        this.arenaKey = new NamespacedKey(plugin, "arena");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(typeKey, PersistentDataType.STRING)) {
            return;
        }
        String type = data.get(typeKey, PersistentDataType.STRING);
        String arenaName = data.get(arenaKey, PersistentDataType.STRING);
        if (arenaName == null) {
            return;
        }
        Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(arenaName);
        if (arena == null) {
            return;
        }

        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if ("lobby".equals(type)) {
            arena.setLobbyLocation(loc);
            player.sendMessage("Lobby défini.");
        } else if (type != null && type.startsWith("team_spawn_")) {
            TeamColor color = TeamColor.valueOf(type.substring("team_spawn_".length()).toUpperCase());
            Team team = arena.getTeams().computeIfAbsent(color, Team::new);
            team.setSpawnLocation(loc);
            player.sendMessage("Spawn de l'équipe " + color.getDisplayName() + " défini.");
        } else if (type != null && type.startsWith("team_bed_")) {
            TeamColor color = TeamColor.valueOf(type.substring("team_bed_".length()).toUpperCase());
            Team team = arena.getTeams().computeIfAbsent(color, Team::new);
            team.setBedLocation(loc);
            player.sendMessage("Lit de l'équipe " + color.getDisplayName() + " défini.");
        } else if (type != null && type.startsWith("gen_")) {
            GeneratorType gType = GeneratorType.valueOf(type.substring("gen_".length()).toUpperCase());
            arena.getGenerators().add(new Generator(loc, gType, 1));
            player.sendMessage("Générateur " + gType.name() + " ajouté.");
        } else if ("npc_shop".equals(type)) {
            arena.setShopNpcLocation(loc);
            player.sendMessage("PNJ Boutique défini.");
        } else if ("npc_upgrade".equals(type)) {
            arena.setUpgradeNpcLocation(loc);
            player.sendMessage("PNJ Améliorations défini.");
        }

        HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
        player.getInventory().remove(item);
        event.setCancelled(true);
    }
}
