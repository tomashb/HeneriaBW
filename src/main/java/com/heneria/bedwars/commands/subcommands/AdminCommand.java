package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.admin.AdminMainMenu;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

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
            } else if (sub.equals("setmainlobby")) {
                if (!player.hasPermission("heneriabw.admin.setmainlobby")) {
                    MessageManager.sendMessage(player, "errors.no-permission");
                    return;
                }
                plugin.setMainLobby(player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Lobby principal défini.");
                return;
            } else if (sub.equals("setjoinnpc") && args.length >= 2) {
                if (!player.hasPermission("heneriabw.admin.setjoinnpc")) {
                    MessageManager.sendMessage(player, "errors.no-permission");
                    return;
                }
                String mode = args[1].toLowerCase();
                plugin.getNpcManager().addNpc(player.getLocation(), mode);
                player.sendMessage(ChatColor.GREEN + "PNJ de jonction " + mode + " placé.");
                return;
            } else if (sub.equals("setshopnpc") && args.length >= 3) {
                if (!player.hasPermission("heneriabw.admin.setshopnpc")) {
                    MessageManager.sendMessage(player, "errors.no-permission");
                    return;
                }
                String team = args[1];
                String type = args[2].toLowerCase();
                Villager npc = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
                npc.setAI(false);
                npc.setInvulnerable(true);
                npc.setSilent(true);
                npc.setCollidable(false);
                npc.addScoreboardTag(type.equals("upgrade") ? "upgrade_npc" : "shop_npc");
                if (type.equals("upgrade")) {
                    npc.setCustomName(MessageManager.get("game.upgrade-npc-name"));
                } else {
                    npc.setCustomName(MessageManager.get("game.shop-npc-name"));
                }
                npc.setCustomNameVisible(true);
                player.sendMessage(ChatColor.GREEN + "PNJ de boutique " + type + " pour l'équipe " + team + " placé.");
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
            return Arrays.asList("delete", "confirmdelete", "setmainlobby", "setjoinnpc", "setshopnpc");
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
        if (args.length == 2 && args[0].equalsIgnoreCase("setjoinnpc")) {
            return Arrays.asList("solos", "duos", "trios", "quads");
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

