package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GeneratorType;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.managers.SetupManager;
import com.heneria.bedwars.setup.SetupAction;
import com.heneria.bedwars.setup.SetupType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for player interactions with the setup tool and records locations accordingly.
 */
public class SetupListener implements Listener {

    private final SetupManager setupManager;

    public SetupListener(SetupManager setupManager) {
        this.setupManager = setupManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.BLAZE_ROD) {
            return;
        }
        SetupAction action = setupManager.getSetupAction(player.getUniqueId());
        if (action == null) {
            return;
        }

        Arena arena = action.getArena();
        Location loc = player.getLocation();

        if (action.getType() == SetupType.LOBBY) {
            arena.setLobbyLocation(loc);
            player.sendMessage("Lobby défini.");
        } else if (action.getType() == SetupType.TEAM_SPAWN && action.getTeamColor() != null) {
            Team team = arena.getTeams().computeIfAbsent(action.getTeamColor(), Team::new);
            team.setSpawnLocation(loc);
            player.sendMessage("Spawn de l'équipe " + action.getTeamColor().getDisplayName() + " défini.");
        } else if (action.getType() == SetupType.TEAM_BED && action.getTeamColor() != null) {
            Team team = arena.getTeams().computeIfAbsent(action.getTeamColor(), Team::new);
            team.setBedLocation(loc);
            player.sendMessage("Lit de l'équipe " + action.getTeamColor().getDisplayName() + " défini.");
        } else if (action.getType() == SetupType.GENERATOR && action.getGeneratorType() != null) {
            arena.getGenerators().add(new Generator(loc, action.getGeneratorType(), 1));
            player.sendMessage("Générateur " + action.getGeneratorType().name() + " ajouté.");
        } else if (action.getType() == SetupType.NPC_SHOP) {
            arena.setShopNpcLocation(loc);
            player.sendMessage("PNJ Boutique défini.");
        } else if (action.getType() == SetupType.NPC_UPGRADE) {
            arena.setUpgradeNpcLocation(loc);
            player.sendMessage("PNJ Améliorations défini.");
        }

        HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
        player.getInventory().remove(item);
        setupManager.clear(player);
        event.setCancelled(true);
    }
}
