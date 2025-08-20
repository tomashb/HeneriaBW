package com.heneria.bedwars.gui;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ReconnectManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Small hub menu offering quick join, stats and reconnect options.
 */
public class GameHubMenu extends Menu {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ReconnectManager reconnectManager = plugin.getReconnectManager();
    private Player viewer;

    @Override
    public void open(Player player, Menu previousMenu) {
        this.viewer = player;
        super.open(player, previousMenu);
    }

    @Override
    public String getTitle() {
        return "Hub de Jeu";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, filler);
        }
        inventory.setItem(13, new ItemBuilder(Material.RED_BED)
                .setName("&aJouer")
                .addLore("&7Trouver une partie rapidement")
                .build());
        inventory.setItem(11, new ItemBuilder(Material.PAPER)
                .setName("&eStatistiques")
                .addLore("&7Voir vos statistiques")
                .build());
        if (viewer != null && reconnectManager.hasPending(viewer.getUniqueId())) {
            Arena arena = reconnectManager.getArena(viewer.getUniqueId());
            inventory.setItem(15, new ItemBuilder(Material.ENDER_PEARL)
                    .setName("&dReconnexion")
                    .addLore("&7Retourner dans l'arène &b" + arena.getName())
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (handleBack(event)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == 13) {
            quickJoin(player);
            player.closeInventory();
        } else if (slot == 11) {
            new PlayerStatsMenu().open(player, this);
        } else if (slot == 15 && reconnectManager.hasPending(player.getUniqueId())) {
            reconnectManager.reconnect(player);
            player.closeInventory();
        }
    }

    private void quickJoin(Player player) {
        Arena best = null;
        for (Arena arena : plugin.getArenaManager().getAllArenas()) {
            if (arena.getState() == GameState.WAITING || arena.getState() == GameState.STARTING) {
                if (best == null || arena.getPlayers().size() > best.getPlayers().size()) {
                    best = arena;
                }
            }
        }
        if (best != null) {
            best.addPlayer(player);
        } else {
            player.sendMessage("Aucune partie disponible pour le moment");
        }
    }
}
