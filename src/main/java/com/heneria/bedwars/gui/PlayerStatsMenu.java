package com.heneria.bedwars.gui;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.stats.PlayerStats;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Displays a player's statistics.
 */
public class PlayerStatsMenu extends Menu {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private Player viewer;

    @Override
    public void open(Player player, Menu previousMenu) {
        this.viewer = player;
        super.open(player, previousMenu);
    }

    @Override
    public String getTitle() {
        return "Statistiques";
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
        PlayerStats stats = plugin.getStatsManager().getStats(viewer);
        ItemBuilder builder = new ItemBuilder(Material.BOOK).setName("&eVos Statistiques");
        if (stats != null) {
            builder
                    .addLore("&7Kills: &b" + stats.getKills())
                    .addLore("&7Morts: &b" + stats.getDeaths())
                    .addLore("&7Victoires: &b" + stats.getWins())
                    .addLore("&7Défaites: &b" + stats.getLosses())
                    .addLore("&7Lits détruits: &b" + stats.getBedsBroken())
                    .addLore("&7Parties jouées: &b" + stats.getGamesPlayed());
        }
        inventory.setItem(13, builder.build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        handleBack(event);
    }
}
