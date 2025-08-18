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
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
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
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                MessageManager.sendMessage(player, "setup.lobby-click-block");
                return;
            }
            double x = clickedBlock.getX() + 0.5;
            double y = clickedBlock.getY() + 1.0;
            double z = clickedBlock.getZ() + 0.5;
            float yaw = player.getLocation().getYaw();
            float pitch = 0.0f;
            Location lobbyLocation = new Location(player.getWorld(), x, y, z, yaw, pitch);
            arena.setLobbyLocation(lobbyLocation);
            MessageManager.sendMessage(player, "setup.lobby-set");
        } else if (action.getType() == SetupType.TEAM_SPAWN && action.getTeamColor() != null) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                MessageManager.sendMessage(player, "setup.spawn-click-block");
                return;
            }
            double x = clickedBlock.getX() + 0.5;
            double y = clickedBlock.getY() + 1.0;
            double z = clickedBlock.getZ() + 0.5;
            float yaw = player.getLocation().getYaw();
            float pitch = 0.0f;
            Location spawnLocation = new Location(player.getWorld(), x, y, z, yaw, pitch);

            Team team = arena.getTeams().computeIfAbsent(action.getTeamColor(), Team::new);
            team.setSpawnLocation(spawnLocation);
            MessageManager.sendMessage(player, "setup.spawn-set", "team", action.getTeamColor().getDisplayName());
        } else if (action.getType() == SetupType.TEAM_BED && action.getTeamColor() != null) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || !(clickedBlock.getBlockData() instanceof Bed)) {
                MessageManager.sendMessage(player, "setup.bed-click-block");
                return;
            }

            Bed bedData = (Bed) clickedBlock.getBlockData();
            Block headBlock = clickedBlock;
            if (bedData.getPart() == Bed.Part.FOOT) {
                headBlock = clickedBlock.getRelative(bedData.getFacing());
            }

            Location bedHeadLocation = headBlock.getLocation().add(0.5, 0, 0.5);

            Team team = arena.getTeams().computeIfAbsent(action.getTeamColor(), Team::new);
            team.setBedLocation(bedHeadLocation);
            MessageManager.sendMessage(player, "setup.bed-set", "team", action.getTeamColor().getDisplayName());
        } else if (action.getType() == SetupType.GENERATOR && action.getGeneratorType() != null) {
            arena.getGenerators().add(new Generator(loc, action.getGeneratorType(), 1));
            MessageManager.sendMessage(player, "setup.generator-added", "generator", action.getGeneratorType().name());
        } else if (action.getType() == SetupType.NPC_SHOP && action.getTeamColor() != null) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                MessageManager.sendMessage(player, "setup.npc-click-block");
                return;
            }
            double x = clickedBlock.getX() + 0.5;
            double y = clickedBlock.getY() + 1.0;
            double z = clickedBlock.getZ() + 0.5;
            float yaw = player.getLocation().getYaw();
            float pitch = 0.0f;
            Location npcLocation = new Location(player.getWorld(), x, y, z, yaw, pitch);
            Team team = arena.getTeams().computeIfAbsent(action.getTeamColor(), Team::new);
            team.setItemShopNpcLocation(npcLocation);
            MessageManager.sendMessage(player, "setup.shop-npc-set", "team", action.getTeamColor().getDisplayName());
        } else if (action.getType() == SetupType.NPC_UPGRADE && action.getTeamColor() != null) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                MessageManager.sendMessage(player, "setup.npc-click-block");
                return;
            }
            double x = clickedBlock.getX() + 0.5;
            double y = clickedBlock.getY() + 1.0;
            double z = clickedBlock.getZ() + 0.5;
            float yaw = player.getLocation().getYaw();
            float pitch = 0.0f;
            Location npcLocation = new Location(player.getWorld(), x, y, z, yaw, pitch);
            Team team = arena.getTeams().computeIfAbsent(action.getTeamColor(), Team::new);
            team.setUpgradeShopNpcLocation(npcLocation);
            MessageManager.sendMessage(player, "setup.upgrade-npc-set", "team", action.getTeamColor().getDisplayName());
        }

        HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
        player.getInventory().remove(item);
        setupManager.clear(player);
        event.setCancelled(true);
    }
}
