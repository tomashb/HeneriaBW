package com.heneria.bedwars.gui;

import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.utils.MessageManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        return MessageManager.get("menus.team-selector-title");
    }

    @Override
    public int getSize() {
        int teams = arena.getTeams().size();
        int innerRows = (int) Math.ceil(teams / 7.0);
        int rows = Math.max(3, innerRows + 2);
        return rows * 9;
    }

    @Override
    public void setupItems() {
        slotTeam.clear();
        int maxPerTeam = arena.getMaxPlayers() / arena.getTeams().size();
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, filler);
        }
        int index = 0;
        for (Map.Entry<TeamColor, Team> entry : arena.getTeams().entrySet()) {
            TeamColor color = entry.getKey();
            Team team = entry.getValue();
            int row = index / 7;
            int col = index % 7;
            int slot = 10 + row * 9 + col;
            ItemBuilder builder = new ItemBuilder(color.getWoolMaterial())
                    .setName(color.getChatColor() + team.getColor().getDisplayName()
                            + (team.getMembers().size() >= maxPerTeam ? " &c(PLEIN)" : ""))
                    .addLore(team.getMembers().size() + "/" + maxPerTeam + " Joueurs");
            for (UUID id : team.getMembers()) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) {
                    builder.addLore(" &7- " + p.getName());
                }
            }
            ItemStack item = builder.build();
            inventory.setItem(slot, item);
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
