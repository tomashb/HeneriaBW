package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.admin.creation.ArenaSettingsMenu;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.managers.SetupManager;
import com.heneria.bedwars.setup.NpcCreationProcess;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        SetupManager setupManager = HeneriaBedwars.getInstance().getSetupManager();
        NpcCreationProcess process = setupManager.getNpcCreation(player.getUniqueId());
        if (process != null) {
            event.setCancelled(true);
            String msg = event.getMessage();
            if (msg.equalsIgnoreCase("annuler")) {
                setupManager.clearNpcCreation(player);
                player.sendMessage(ChatColor.RED + "Création de PNJ annulée.");
                return;
            }
            switch (process.getStep()) {
                case SKIN -> {
                    // Player heads rely on the underlying game profile name
                    // which is limited to 16 characters. Reject longer skin
                    // names to avoid packet encoding errors during NPC
                    // creation.
                    if (msg.length() > 16) {
                        player.sendMessage(ChatColor.RED + "Nom de skin trop long (16 caractères max).");
                        return;
                    }
                    process.setSkin(msg);
                    process.setStep(NpcCreationProcess.Step.MODE);
                    player.sendMessage(ChatColor.YELLOW + "Pour quel mode ce PNJ doit-il servir ?");
                }
                case MODE -> {
                    process.setMode(msg);
                    process.setStep(NpcCreationProcess.Step.NAME);
                    player.sendMessage(ChatColor.YELLOW + "Quel nom doit afficher le PNJ ?");
                }
                case NAME -> {
                    process.setName(msg);
                    process.setStep(NpcCreationProcess.Step.ITEM);
                    player.sendMessage(ChatColor.YELLOW + "Quel objet doit-il tenir ? (aucun)");
                }
                case ITEM -> {
                    if (!msg.equalsIgnoreCase("aucun") && !msg.equalsIgnoreCase("aucune")) {
                        Material mat = Material.matchMaterial(msg.toUpperCase());
                        if (mat == null) {
                            player.sendMessage(ChatColor.RED + "Objet invalide, réessayez.");
                            return;
                        }
                        process.setItem(mat);
                    }
                    process.setStep(NpcCreationProcess.Step.CHESTPLATE);
                    player.sendMessage(ChatColor.YELLOW + "Quel plastron ? (aucun)");
                }
                case CHESTPLATE -> {
                    if (!msg.equalsIgnoreCase("aucun") && !msg.equalsIgnoreCase("aucune")) {
                        Material mat = Material.matchMaterial(msg.toUpperCase());
                        if (mat == null) {
                            player.sendMessage(ChatColor.RED + "Plastron invalide, réessayez.");
                            return;
                        }
                        process.setChestplate(mat);
                    }
                    process.setStep(NpcCreationProcess.Step.LEGGINGS);
                    player.sendMessage(ChatColor.YELLOW + "Quel pantalon ? (aucun)");
                }
                case LEGGINGS -> {
                    if (!msg.equalsIgnoreCase("aucun") && !msg.equalsIgnoreCase("aucune")) {
                        Material mat = Material.matchMaterial(msg.toUpperCase());
                        if (mat == null) {
                            player.sendMessage(ChatColor.RED + "Pantalon invalide, réessayez.");
                            return;
                        }
                        process.setLeggings(mat);
                    }
                    process.setStep(NpcCreationProcess.Step.BOOTS);
                    player.sendMessage(ChatColor.YELLOW + "Quelles bottes ? (aucune)");
                }
                case BOOTS -> {
                    if (!msg.equalsIgnoreCase("aucun") && !msg.equalsIgnoreCase("aucune")) {
                        Material mat = Material.matchMaterial(msg.toUpperCase());
                        if (mat == null) {
                            player.sendMessage(ChatColor.RED + "Bottes invalides, réessayez.");
                            return;
                        }
                        process.setBoots(mat);
                    }
                    process.setStep(NpcCreationProcess.Step.WAITING_CONFIRM);
                    player.sendMessage(ChatColor.GREEN + "Placez-vous et tapez /bw admin confirmnpc pour valider.");
                }
                case WAITING_CONFIRM -> player.sendMessage(ChatColor.YELLOW + "Tapez /bw admin confirmnpc pour finaliser ou 'annuler' pour annuler.");
            }
            return;
        }

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
