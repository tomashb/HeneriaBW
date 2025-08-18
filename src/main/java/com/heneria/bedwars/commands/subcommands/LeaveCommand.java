package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Handles the "/bedwars leave" sub-command to quit the current arena.
 */
public class LeaveCommand implements SubCommand {

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public void execute(Player player, String[] args) {
        ArenaManager manager = HeneriaBedwars.getInstance().getArenaManager();
        Arena arena = manager.getArenaByPlayer(player.getUniqueId());
        if (arena == null) {
            MessageManager.sendMessage(player, "errors.not-in-arena");
            return;
        }
        arena.removePlayer(player);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
