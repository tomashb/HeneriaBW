package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.admin.creation.ArenaSettingsMenu;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageUtils;
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
                MessageUtils.sendMessage(player, "&cCréation d'arène annulée.");
                return;
            }

            if (arenaName.trim().isEmpty() || arenaName.length() > 16) {
                MessageUtils.sendMessage(player, "&cNom invalide (1-16 caractères). Veuillez réessayer ou tapez 'annuler'.");
                return;
            }
            if (arenaManager.getArena(arenaName) != null) {
                MessageUtils.sendMessage(player, "&cUne arène avec ce nom existe déjà. Veuillez réessayer ou tapez 'annuler'.");
                return;
            }

            arenaManager.removePlayerFromCreationMode(player);
            MessageUtils.sendMessage(player, "&aNom '&e" + arenaName + "&a' validé !");

            Bukkit.getScheduler().runTask(HeneriaBedwars.getInstance(), () -> {
                new ArenaSettingsMenu(arenaName).open(player);
            });
        }
    }
}
