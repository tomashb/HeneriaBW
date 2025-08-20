package com.heneria.bedwars.gui.upgrades;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.UpgradeManager;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays upgrade categories before diving into specific upgrades or traps.
 */
public class TeamUpgradeCategoryMenu extends Menu {

    private final HeneriaBedwars plugin;
    private final Arena arena;
    private final Team team;
    private final UpgradeManager upgradeManager;
    private final Map<Integer, String> slotCategories = new HashMap<>();

    public TeamUpgradeCategoryMenu(HeneriaBedwars plugin, Arena arena, Team team) {
        this.plugin = plugin;
        this.arena = arena;
        this.team = team;
        this.upgradeManager = plugin.getUpgradeManager();
    }

    @Override
    public String getTitle() {
        return upgradeManager.getMainMenuTitle();
    }

    @Override
    public int getSize() {
        return upgradeManager.getMainMenuRows() * 9;
    }

    @Override
    public void setupItems() {
        slotCategories.clear();
        for (UpgradeManager.MainMenuItem item : upgradeManager.getMainMenuItems()) {
            ItemStack stack = new ItemBuilder(item.material())
                    .setName(item.name())
                    .setLore(item.lore())
                    .build();
            inventory.setItem(item.slot(), stack);
            if (item.category() != null) {
                slotCategories.put(item.slot(), item.category());
            }
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
        String categoryId = slotCategories.get(event.getRawSlot());
        if (categoryId != null) {
            UpgradeManager.UpgradeCategory category = upgradeManager.getCategory(categoryId);
            if (category != null) {
                new TeamUpgradesMenu(plugin, arena, team, category).open(player, this);
            } else {
                MessageManager.sendMessage(player, "errors.category-not-found");
            }
        }
    }
}

