package com.heneria.bedwars.gui.upgrades;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ResourceManager;
import com.heneria.bedwars.managers.ResourceType;
import com.heneria.bedwars.managers.UpgradeManager;
import com.heneria.bedwars.managers.UpgradeManager.Trap;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Menu allowing players to purchase team upgrades with diamonds.
 */
public class TeamUpgradesMenu extends Menu {

    private final HeneriaBedwars plugin;
    private final Arena arena;
    private final Team team;
    private final UpgradeManager upgradeManager;
    private final Map<Integer, UpgradeManager.Upgrade> slotUpgrades = new HashMap<>();
    private final Map<Integer, Trap> slotTraps = new HashMap<>();

    public TeamUpgradesMenu(HeneriaBedwars plugin, Arena arena, Team team) {
        this.plugin = plugin;
        this.arena = arena;
        this.team = team;
        this.upgradeManager = plugin.getUpgradeManager();
    }

    @Override
    public String getTitle() {
        return "§aAméliorations";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        slotUpgrades.clear();
        slotTraps.clear();
        int slot = 10;
        for (UpgradeManager.Upgrade upgrade : upgradeManager.getUpgrades()) {
            int current = team.getUpgradeLevel(upgrade.id());
            UpgradeManager.UpgradeTier tier = upgrade.tiers().get(current + 1);
            ItemBuilder builder = new ItemBuilder(upgrade.item()).setName(upgrade.name());
            if (tier != null) {
                builder.addLore(tier.description())
                        .addLore("&7Coût: &b" + tier.cost() + " Diamants");
            } else {
                builder.addLore("&7Amélioration maximale atteinte");
            }
            inventory.setItem(slot, builder.build());
            slotUpgrades.put(slot, upgrade);
            slot++;
        }

        for (Trap trap : upgradeManager.getTraps()) {
            ItemBuilder builder = new ItemBuilder(trap.item()).setName(trap.name()).setLore(trap.description());
            if (!team.isTrapActive(trap.id())) {
                builder.addLore("&7Coût: &b" + trap.cost() + " Diamants");
            } else {
                builder.addLore("&7Piège acheté");
            }
            inventory.setItem(slot, builder.build());
            slotTraps.put(slot, trap);
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
        UpgradeManager.Upgrade upgrade = slotUpgrades.get(event.getRawSlot());
        if (upgrade != null) {
            int current = team.getUpgradeLevel(upgrade.id());
            UpgradeManager.UpgradeTier tier = upgrade.tiers().get(current + 1);
            if (tier == null) {
                player.sendMessage("§cCette amélioration est déjà au maximum.");
                return;
            }
            int cost = tier.cost();
            if (!ResourceManager.hasResources(player, ResourceType.DIAMOND, cost)) {
                player.sendMessage("§cVous n'avez pas assez de diamants !");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }
            ResourceManager.takeResources(player, ResourceType.DIAMOND, cost);
            team.setUpgradeLevel(upgrade.id(), current + 1);
            applyUpgradeEffect(upgrade.id(), current + 1);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            new TeamUpgradesMenu(plugin, arena, team).open(player, previousMenu);
            return;
        }

        Trap trap = slotTraps.get(event.getRawSlot());
        if (trap == null) {
            return;
        }
        if (team.isTrapActive(trap.id())) {
            player.sendMessage("§cCe piège a déjà été acheté.");
            return;
        }
        int cost = trap.cost();
        if (!ResourceManager.hasResources(player, ResourceType.DIAMOND, cost)) {
            player.sendMessage("§cVous n'avez pas assez de diamants !");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }
        ResourceManager.takeResources(player, ResourceType.DIAMOND, cost);
        team.setTrapActive(trap.id(), true);
        for (UUID uuid : team.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                MessageUtils.sendMessage(p, "Votre équipe a acheté " + trap.name());
            }
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        new TeamUpgradesMenu(plugin, arena, team).open(player, previousMenu);
    }

    private void applyUpgradeEffect(String id, int level) {
        switch (id.toLowerCase()) {
            case "sharpness" -> {
                for (UUID uuid : team.getMembers()) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p == null) continue;
                    for (ItemStack item : p.getInventory().getContents()) {
                        if (item != null && item.getType().name().endsWith("SWORD")) {
                            upgradeManager.applySharpness(item, level);
                        }
                    }
                }
            }
            case "protection" -> {
                for (UUID uuid : team.getMembers()) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p == null) continue;
                    for (ItemStack item : p.getInventory().getArmorContents()) {
                        if (item != null) {
                            upgradeManager.applyProtection(item, level);
                        }
                    }
                }
            }
            case "haste" -> {
                for (UUID uuid : team.getMembers()) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p == null) continue;
                    upgradeManager.applyHaste(p, level - 1, 20 * 60 * 60);
                }
            }
            case "forge" -> plugin.getGeneratorManager().upgradeTeamGenerators(arena, team, level + 1);
        }
    }
}
