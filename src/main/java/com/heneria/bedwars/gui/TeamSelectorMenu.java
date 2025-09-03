package com.heneria.bedwars.gui;

import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Menu allowing players to choose their team before the game starts.
 */
public class TeamSelectorMenu extends Menu {

    private final Arena arena;
    private final Map<Integer, TeamColor> slotTeam = new HashMap<>();

    public TeamSelectorMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        // Use localized title from the message manager
        return MessageManager.getFormattedMessage("menus.team-selector-title");
    }

    @Override
    public int getSize() {
        // Fixed 3-row inventory
        return 27;
    }

    @Override
    public void setupItems() {
        slotTeam.clear();
        int teams = arena.getTeams().size();
        int maxPerTeam = arena.getMaxPlayers() / teams;

        // Fill background with grey glass panes
        ItemStack background = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, background);
        }

        // Center team icons on the middle row
        int start = 13 - teams / 2;
        int index = 0;
        for (Map.Entry<TeamColor, Team> entry : arena.getTeams().entrySet()) {
            TeamColor color = entry.getKey();
            Team team = entry.getValue();
            int slot = start + index;
            int size = team.getMembers().size();
            ItemBuilder builder = new ItemBuilder(color.getWoolMaterial())
                    .setName(color.getChatColor() + "Équipe " + color.getDisplayName())
                    .addLore("&7Joueurs: &a" + size + "/" + maxPerTeam)
                    .addLore(" ")
                    .addLore("&e► Cliquez pour rejoindre");
            inventory.setItem(slot, builder.build());
            slotTeam.put(slot, color);
            index++;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (handleBack(event)) {
            return;
        }
        int slot = event.getRawSlot();
        TeamColor color = slotTeam.get(slot);
        if (color == null) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Team target = arena.getTeams().get(color);
        int maxPerTeam = arena.getMaxPlayers() / arena.getTeams().size();
        if (target.getMembers().size() >= maxPerTeam) {
            MessageManager.sendMessage(player, "errors.team-full");
            return;
        }
        Team current = arena.getTeam(player);
        if (current != null) {
            current.removeMember(player.getUniqueId());
        }
        target.addMember(player.getUniqueId());
        MessageManager.sendMessage(player, "game.team-joined", "team", target.getColor().getDisplayName());
        // Refresh menus for all players in lobby
        for (UUID id : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.getOpenInventory().getTopInventory().getHolder() instanceof TeamSelectorMenu) {
                new TeamSelectorMenu(arena).open(p);
            }
        }
    }
}
