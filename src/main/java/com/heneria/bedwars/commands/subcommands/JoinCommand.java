package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageUtils;
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
            MessageUtils.sendMessage(player, "&cVous n'avez pas la permission.");
            return;
        }
        if (args.length < 1) {
            MessageUtils.sendMessage(player, "&eUsage: /bw join <arène>");
            return;
        }
        ArenaManager manager = HeneriaBedwars.getInstance().getArenaManager();
        Arena arena = manager.getArena(args[0]);
        if (arena == null) {
            MessageUtils.sendMessage(player, "&cArène introuvable.");
            return;
        }
        if (!arena.isEnabled() || arena.getState() != GameState.WAITING) {
            MessageUtils.sendMessage(player, "&cCette arène n'est pas disponible.");
            return;
        }
        if (manager.getArenaByPlayer(player.getUniqueId()) != null) {
            MessageUtils.sendMessage(player, "&cVous êtes déjà dans une arène.");
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
