package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.gui.admin.AdminMainMenu;
import com.heneria.bedwars.utils.MessageManager;
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
            MessageManager.sendMessage(player, "errors.no-permission");
            return;
        }
        new AdminMainMenu().open(player);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
