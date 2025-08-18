package com.heneria.bedwars.gui.upgrades;

import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ResourceManager;
import com.heneria.bedwars.managers.ResourceType;
import com.heneria.bedwars.managers.UpgradeManager;
import com.heneria.bedwars.managers.UpgradeType;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu permettant l'achat des améliorations d'équipe.
 */
public class TeamUpgradesMenu extends Menu {

    private final UpgradeManager upgradeManager;
    private final Arena arena;
    private final Team team;
    private final Map<Integer, UpgradeType> slotTypes = new HashMap<>();

    public TeamUpgradesMenu(UpgradeManager upgradeManager, Arena arena, Team team) {
        this.upgradeManager = upgradeManager;
        this.arena = arena;
        this.team = team;
    }

    @Override
    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', "&aAméliorations d'équipe");
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        int slot = 10;
        for (UpgradeType type : UpgradeType.values()) {
            UpgradeManager.Upgrade upgrade = upgradeManager.getUpgrade(type);
            if (upgrade == null) {
                continue;
            }
            int level = team.getUpgradeLevel(type);
            UpgradeManager.UpgradeTier next = upgrade.tiers().get(level + 1);
            ItemBuilder builder = new ItemBuilder(upgrade.item()).setName(upgrade.name());
            if (next != null) {
                builder.addLore(next.description())
                        .addLore("&7Coût: &b" + next.cost() + " Diamants");
            } else {
                builder.addLore("&aAmélioration maximale");
            }
            inventory.setItem(slot, builder.build());
            slotTypes.put(slot, type);
            slot++;
        }
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
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
        UpgradeType type = slotTypes.get(event.getRawSlot());
        if (type == null) {
            return;
        }
        UpgradeManager.Upgrade upgrade = upgradeManager.getUpgrade(type);
        int level = team.getUpgradeLevel(type);
        UpgradeManager.UpgradeTier tier = upgrade.tiers().get(level + 1);
        if (tier == null) {
            player.sendMessage("\u00a7cCette amélioration est déj\u00e0 au niveau maximum.");
            return;
        }
        int cost = tier.cost();
        if (ResourceManager.hasResources(player, ResourceType.DIAMOND, cost)) {
            ResourceManager.takeResources(player, ResourceType.DIAMOND, cost);
            team.setUpgradeLevel(type, level + 1);
            upgradeManager.applyUpgrade(arena, team, type, level + 1);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            player.sendMessage("\u00a7aAmélioration achetée !");
            open(player, this.previousMenu);
        } else {
            player.sendMessage("\u00a7cVous n'avez pas assez de diamants !");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }
}
