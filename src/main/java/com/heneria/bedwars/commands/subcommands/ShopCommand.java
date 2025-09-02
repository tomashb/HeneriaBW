package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.gui.LobbyShopMenu;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Handles the "/bw shop" sub-command to open the cosmetic shop.
 */
public class ShopCommand implements SubCommand {

    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("heneriabw.player.shop")) {
            MessageManager.sendMessage(player, "errors.no-permission");
            return;
        }
        new LobbyShopMenu().open(player);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }
}
