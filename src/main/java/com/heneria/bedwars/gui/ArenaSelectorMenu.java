package com.heneria.bedwars.gui;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu for selecting an arena based on game mode.
 */
public class ArenaSelectorMenu extends Menu {

    private final String mode;
    private final Map<Integer, Arena> arenaSlots = new HashMap<>();

    public ArenaSelectorMenu(String mode) {
        this.mode = mode.toLowerCase();
    }

    @Override
    public String getTitle() {
        return "Arènes " + mode;
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void setupItems() {
        arenaSlots.clear();
        int slot = 0;
        for (Arena arena : HeneriaBedwars.getInstance().getArenaManager().getAllArenas()) {
            if (!matchesMode(arena)) {
                continue;
            }
            String state = (arena.getState() == GameState.PLAYING || arena.getState() == GameState.ENDING) ? "&cEN COURS" : "&aEN ATTENTE";
            ItemStack item = new ItemBuilder(Material.PAPER)
                    .setName("&e" + arena.getName())
                    .addLore("&7Statut: " + state)
                    .addLore("&7Joueurs: &b" + arena.getPlayers().size() + "&7/&b" + arena.getMaxPlayers())
                    .build();
            inventory.setItem(slot, item);
            arenaSlots.put(slot, arena);
            slot++;
            if (slot >= getSize()) {
                break;
            }
        }
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private boolean matchesMode(Arena arena) {
        int teams = arena.getTeams().size();
        if (teams == 0) {
            return false;
        }
        int teamSize = arena.getMaxPlayers() / teams;
        return switch (mode) {
            case "solos" -> teamSize == 1;
            case "duos" -> teamSize == 2;
            case "trios" -> teamSize == 3;
            case "quads", "squads" -> teamSize == 4;
            default -> true;
        };
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
        Arena arena = arenaSlots.get(event.getRawSlot());
        if (arena == null) {
            return;
        }
        if (arena.canJoin()) {
            player.closeInventory();
            arena.addPlayer(player);
        } else {
            MessageManager.sendMessage(player, "errors.arena-full-or-started");
        }
    }
}
