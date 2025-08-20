package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.admin.AdminMainMenu;
import com.heneria.bedwars.gui.admin.LobbyNPCManagerMenu;
import com.heneria.bedwars.managers.SetupManager;
import com.heneria.bedwars.setup.NpcCreationProcess;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import com.heneria.bedwars.arena.enums.TeamColor;

import java.util.*;

/**
 * Handles the "/bedwars admin" sub-command and its administrative operations.
 */
public class AdminCommand implements SubCommand {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final Map<UUID, PendingDelete> pendingDeletes = new HashMap<>();

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length > 0) {
            String sub = args[0].toLowerCase();
            if (sub.equals("delete") && args.length >= 2) {
                if (!player.hasPermission("heneriabw.admin.delete")) {
                    MessageManager.sendMessage(player, "errors.no-permission");
                    return;
                }
                String arenaName = args[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageManager.sendMessage(player, "errors.arena-not-found");
                    return;
                }
                pendingDeletes.put(player.getUniqueId(), new PendingDelete(arenaName, System.currentTimeMillis() + 30000));
                MessageManager.sendMessage(player, "admin.delete-pending", "arena", arenaName);
                return;
            } else if (sub.equals("confirmdelete") && args.length >= 2) {
                String arenaName = args[1];
                PendingDelete pending = pendingDeletes.get(player.getUniqueId());
                if (pending == null || !pending.arenaName.equalsIgnoreCase(arenaName) || System.currentTimeMillis() > pending.expireAt) {
                    MessageManager.sendMessage(player, "admin.delete-expired", "arena", arenaName);
                    pendingDeletes.remove(player.getUniqueId());
                    return;
                }
                boolean success = plugin.getArenaManager().deleteArena(arenaName);
                pendingDeletes.remove(player.getUniqueId());
                if (success) {
                    MessageManager.sendMessage(player, "admin.delete-confirmed", "arena", arenaName);
                } else {
                    MessageManager.sendMessage(player, "errors.arena-not-found");
                }
                return;
            } else if (sub.equals("lobby")) {
                if (!player.hasPermission("heneriabw.admin.lobby")) {
                    MessageManager.sendMessage(player, "errors.no-permission");
                    return;
                }
                new LobbyNPCManagerMenu().open(player);
                return;
            } else if (sub.equals("setmainlobby")) {
                if (!player.hasPermission("heneriabw.admin.setmainlobby")) {
                    MessageManager.sendMessage(player, "errors.no-permission");
                    return;
                }
                plugin.setMainLobby(player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Lobby principal défini.");
                return;
            } else if (sub.equals("setshopnpc") && args.length >= 3) {
                if (!player.hasPermission("heneriabw.admin.setshopnpc")) {
                    MessageManager.sendMessage(player, "errors.no-permission");
                    return;
                }
                String team = args[1];
                String type = args[2].toLowerCase();
                Material chestplate = args.length > 3 ? Material.matchMaterial(args[3].toUpperCase()) : null;
                Material leggings = args.length > 4 ? Material.matchMaterial(args[4].toUpperCase()) : null;
                Material boots = args.length > 5 ? Material.matchMaterial(args[5].toUpperCase()) : null;
                ArmorStand npc = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
                npc.setInvisible(true);
                npc.setInvulnerable(true);
                npc.setGravity(false);
                npc.setBasePlate(false);
                npc.setArms(true);
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Villager"));
                head.setItemMeta(meta);
                npc.getEquipment().setHelmet(head);
                npc.getPersistentDataContainer().set(HeneriaBedwars.getNpcKey(), PersistentDataType.STRING, type.equals("upgrade") ? "upgrade" : "shop");
                if (type.equals("upgrade")) {
                    npc.setCustomName(MessageManager.get("game.upgrade-npc-name"));
                    npc.getEquipment().setItemInMainHand(new ItemStack(Material.NETHER_STAR));
                } else {
                    npc.setCustomName(MessageManager.get("game.shop-npc-name"));
                    npc.getEquipment().setItemInMainHand(new ItemStack(Material.EMERALD));
                }
                TeamColor teamColor = null;
                try {
                    teamColor = TeamColor.valueOf(team.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
                if (chestplate != null) {
                    ItemStack item = new ItemStack(chestplate);
                    if (teamColor != null && item.getType().name().startsWith("LEATHER_")) {
                        LeatherArmorMeta am = (LeatherArmorMeta) item.getItemMeta();
                        am.setColor(teamColor.getLeatherColor());
                        item.setItemMeta(am);
                    }
                    npc.getEquipment().setChestplate(item);
                }
                if (leggings != null) {
                    ItemStack item = new ItemStack(leggings);
                    if (teamColor != null && item.getType().name().startsWith("LEATHER_")) {
                        LeatherArmorMeta am = (LeatherArmorMeta) item.getItemMeta();
                        am.setColor(teamColor.getLeatherColor());
                        item.setItemMeta(am);
                    }
                    npc.getEquipment().setLeggings(item);
                }
                if (boots != null) {
                    ItemStack item = new ItemStack(boots);
                    if (teamColor != null && item.getType().name().startsWith("LEATHER_")) {
                        LeatherArmorMeta am = (LeatherArmorMeta) item.getItemMeta();
                        am.setColor(teamColor.getLeatherColor());
                        item.setItemMeta(am);
                    }
                    npc.getEquipment().setBoots(item);
                }
                npc.setCustomNameVisible(true);
                player.sendMessage(ChatColor.GREEN + "PNJ de boutique " + type + " pour l'équipe " + team + " placé.");
                return;
            } else if (sub.equals("confirmnpc")) {
                if (!player.hasPermission("heneriabw.admin.lobby")) {
                    MessageManager.sendMessage(player, "errors.no-permission");
                    return;
                }
                SetupManager setupManager = plugin.getSetupManager();
                NpcCreationProcess process = setupManager.getNpcCreation(player.getUniqueId());
                if (process == null || process.getStep() != NpcCreationProcess.Step.WAITING_CONFIRM) {
                    player.sendMessage(ChatColor.RED + "Aucune création de PNJ en cours.");
                    return;
                }
                plugin.getNpcManager().addNpc(player.getLocation(), process.getMode(), process.getSkin(), process.getItem(), process.getName(), process.getChestplate(), process.getLeggings(), process.getBoots());
                setupManager.clearNpcCreation(player);
                player.sendMessage(ChatColor.GREEN + "PNJ créé.");
                return;
            }
        }

        if (!player.hasPermission("heneriabw.admin")) {
            MessageManager.sendMessage(player, "errors.no-permission");
            return;
        }
        new AdminMainMenu().open(player);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("delete", "confirmdelete", "setmainlobby", "setshopnpc", "lobby", "confirmnpc");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("confirmdelete"))) {
            List<String> names = new ArrayList<>();
            for (Arena arena : plugin.getArenaManager().getAllArenas()) {
                if (arena.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    names.add(arena.getName());
                }
            }
            return names;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setshopnpc")) {
            return Collections.singletonList("<team>");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("setshopnpc")) {
            return Arrays.asList("item", "upgrade");
        }
        return Collections.emptyList();
    }

    private static class PendingDelete {
        final String arenaName;
        final long expireAt;

        PendingDelete(String arenaName, long expireAt) {
            this.arenaName = arenaName;
            this.expireAt = expireAt;
        }
    }
}

