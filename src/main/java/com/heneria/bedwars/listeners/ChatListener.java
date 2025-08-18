package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.admin.creation.ArenaSettingsMenu;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

        if (arenaManager.isPlayerInCreationMode(player)) {
            event.setCancelled(true);
            String arenaName = event.getMessage();

            if (arenaName.equalsIgnoreCase("annuler")) {
                arenaManager.removePlayerFromCreationMode(player);
                MessageManager.sendMessage(player, "admin.creation-cancelled");
                return;
            }

            if (arenaName.trim().isEmpty() || arenaName.length() > 16) {
                MessageManager.sendMessage(player, "admin.invalid-name");
                return;
            }
            if (arenaManager.getArena(arenaName) != null) {
                MessageManager.sendMessage(player, "admin.name-exists");
                return;
            }

            arenaManager.removePlayerFromCreationMode(player);
            MessageManager.sendMessage(player, "admin.name-validated", "arena", arenaName);

            Bukkit.getScheduler().runTask(HeneriaBedwars.getInstance(), () -> {
                new ArenaSettingsMenu(arenaName).open(player);
            });
        }
    }
}
