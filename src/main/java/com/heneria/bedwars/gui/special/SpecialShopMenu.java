package com.heneria.bedwars.gui.special;

import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ResourceManager;
import com.heneria.bedwars.managers.ResourceType;
import com.heneria.bedwars.managers.SpecialShopManager;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * Inventory GUI for the special mid-game merchant.
 */
public class SpecialShopMenu extends Menu {

    private final SpecialShopManager manager;
    private final Map<Integer, SpecialShopManager.SpecialItem> slotItems = new HashMap<>();
    private Player viewer;

    public SpecialShopMenu(SpecialShopManager manager) {
        this.manager = manager;
    }

    @Override
    public String getTitle() {
        return manager.getTitle();
    }

    @Override
    public int getSize() {
        return manager.getRows() * 9;
    }

    @Override
    public void open(Player player, Menu previousMenu) {
        this.viewer = player;
        super.open(player, previousMenu);
    }

    @Override
    public void setupItems() {
        Team team = getTeam();
        for (SpecialShopManager.SpecialItem item : manager.getItems().values()) {
            int price = getDiscountedPrice(team, item.costAmount());
            ItemBuilder builder = new ItemBuilder(item.material())
                    .setName(item.name());
            for (String line : item.lore()) {
                builder.addLore(line);
            }
            builder.addLore("&7Coût: " + item.costResource().getColor() + price + " " + item.costResource().getDisplayName());
            if (item.purchaseLimit() > 0) {
                builder.addLore("&7Limite: &f" + item.purchaseLimit());
            }
            ItemStack stack = builder.build();
            inventory.setItem(item.slot(), stack);
            slotItems.put(item.slot(), item);
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
        SpecialShopManager.SpecialItem item = slotItems.get(event.getRawSlot());
        if (item == null) {
            return;
        }
        Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(player);
        if (arena != null && item.purchaseLimit() > 0) {
            if (!arena.canPurchase(player.getUniqueId(), item.id(), item.purchaseLimit())) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }
        }
        Team team = getTeam();
        ResourceType type = item.costResource();
        int price = getDiscountedPrice(team, item.costAmount());
        if (ResourceManager.hasResources(player, type, price)) {
            ResourceManager.takeResources(player, type, price);
            ItemStack give = new ItemStack(item.material(), 1);
            ItemMeta meta = give.getItemMeta();
            if (meta != null) {
                if (item.action() != null) {
                    meta.getPersistentDataContainer().set(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING, item.action());
                }
                if (give.getType().getMaxDurability() > 0) {
                    meta.setUnbreakable(true);
                }
                give.setItemMeta(meta);
            }
            HeneriaBedwars.getInstance().getUpgradeManager().applyTeamUpgrades(player, give);
            player.getInventory().addItem(give);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    private Team getTeam() {
        if (viewer == null) {
            return null;
        }
        Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(viewer);
        return arena != null ? arena.getTeam(viewer) : null;
    }

    private int getDiscountedPrice(Team team, int basePrice) {
        if (team == null) {
            return basePrice;
        }
        int level = team.getUpgradeLevel("team-discount");
        double multiplier = switch (level) {
            case 1 -> 0.9;
            case 2 -> 0.8;
            default -> 1.0;
        };
        int price = (int) Math.ceil(basePrice * multiplier);
        return Math.max(1, price);
    }
}

