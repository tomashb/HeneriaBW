package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.gui.admin.AdminMainMenu;
import com.heneria.bedwars.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Handles the "/bedwars admin" sub-command.
 */
public class AdminCommand implements SubCommand {

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("heneriabw.admin")) {
            MessageUtils.sendMessage(player, "&cVous n'avez pas la permission.");
            return;
        }
        new AdminMainMenu().open(player);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
