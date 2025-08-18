package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles the "/bedwars join" sub-command allowing players to join an arena.
 */
public class JoinCommand implements SubCommand {

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("heneriabw.player.join")) {
            MessageManager.sendMessage(player, "errors.no-permission");
            return;
        }
        if (args.length < 1) {
            MessageManager.sendMessage(player, "commands.join-usage");
            return;
        }
        ArenaManager manager = HeneriaBedwars.getInstance().getArenaManager();
        Arena arena = manager.getArena(args[0]);
        if (arena == null) {
            MessageManager.sendMessage(player, "errors.arena-not-found");
            return;
        }
        if (!arena.isEnabled() || arena.getState() != GameState.WAITING) {
            MessageManager.sendMessage(player, "errors.arena-not-available");
            return;
        }
        if (manager.getArenaByPlayer(player.getUniqueId()) != null) {
            MessageManager.sendMessage(player, "errors.already-in-arena");
            return;
        }
        arena.addPlayer(player);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            ArenaManager manager = HeneriaBedwars.getInstance().getArenaManager();
            for (Arena arena : manager.getAllArenas()) {
                if (arena.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(arena.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
