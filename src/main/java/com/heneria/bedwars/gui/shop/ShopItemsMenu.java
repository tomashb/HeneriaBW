package com.heneria.bedwars.gui.shop;

import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ResourceManager;
import com.heneria.bedwars.managers.ResourceType;
import com.heneria.bedwars.managers.ShopManager;
import com.heneria.bedwars.managers.PlayerProgressionManager;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.GameUtils;
import com.heneria.bedwars.utils.MessageManager;
import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Menu displaying all items available for a given shop category.
 */
public class ShopItemsMenu extends Menu {

    private final ShopManager shopManager;
    private final ShopManager.ShopCategory category;
    private final Player player;
    private final PlayerProgressionManager progressionManager;
    private final Map<Integer, ShopManager.ShopItem> slotItems = new HashMap<>();

    public ShopItemsMenu(ShopManager shopManager, ShopManager.ShopCategory category,
                         PlayerProgressionManager progressionManager, Player player) {
        this.shopManager = shopManager;
        this.category = category;
        this.progressionManager = progressionManager;
        this.player = player;
    }

    @Override
    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', category.title());
    }

    @Override
    public int getSize() {
        return category.rows() * 9;
    }

    @Override
    public void setupItems() {
        slotItems.clear();
        for (Map.Entry<Integer, List<ShopManager.ShopItem>> entry : category.items().entrySet()) {
            int slot = entry.getKey();
            List<ShopManager.ShopItem> items = entry.getValue();
            ShopManager.ShopItem display = items.get(0);
            if (items.size() > 1 && items.get(0).upgradeType() != null) {
                items.sort(Comparator.comparingInt(ShopManager.ShopItem::upgradeLevel));
                int current = getTier(items.get(0).upgradeType());
                int max = items.get(items.size() - 1).upgradeLevel();
                if (current >= max) {
                    ItemStack owned = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                            .setName("&cDéjà possédé").build();
                    inventory.setItem(slot, owned);
                    continue;
                }
                int next = current + 1;
                display = null;
                for (ShopManager.ShopItem candidate : items) {
                    if (candidate.upgradeLevel() == next) {
                        display = candidate;
                        break;
                    }
                }
                if (display == null) {
                    ItemStack placeholder = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
                    inventory.setItem(slot, placeholder);
                    continue;
                }
            }
            ItemBuilder builder = new ItemBuilder(display.material())
                    .setName(display.name())
                    .addLore("&7Quantité: &f" + display.amount())
                    .addLore("&7Coût: &f" + display.costAmount() + " " + display.costResource().getDisplayName());
            ItemStack stack = builder.build();
            stack.setAmount(display.amount());
            inventory.setItem(slot, stack);
            slotItems.put(slot, display);
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
        if (!(event.getWhoClicked() instanceof Player clicker)) {
            return;
        }
        ShopManager.ShopItem item = slotItems.get(event.getRawSlot());
        if (item == null) {
            return;
        }
        ResourceType type = item.costResource();
        int price = item.costAmount();
        if (ResourceManager.hasResources(clicker, type, price)) {
            ResourceManager.takeResources(clicker, type, price);
            Material material = item.material();
            Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(clicker);
            Team team = arena != null ? arena.getTeam(clicker) : null;
            if (material.toString().endsWith("_WOOL") && team != null) {
                material = team.getColor().getWoolMaterial();
            }
            boolean isSword = material.name().endsWith("_SWORD");
            if (isSword) {
                for (int i = 0; i < clicker.getInventory().getSize(); i++) {
                    ItemStack invItem = clicker.getInventory().getItem(i);
                    if (invItem != null && invItem.getType().name().endsWith("_SWORD")) {
                        clicker.getInventory().setItem(i, null);
                    }
                }
            }
            ItemStack give = new ItemStack(material, item.amount());
            ItemMeta meta = give.getItemMeta();
            if (meta != null) {
                if (isSword) {
                    meta.getPersistentDataContainer().set(GameUtils.STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
                }
                if (item.action() != null) {
                    meta.getPersistentDataContainer()
                            .set(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING, item.action());
                }
                give.setItemMeta(meta);
            }
            handleUpgrade(clicker, item, give);
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            setupItems();
            if (previousMenu != null) {
                inventory.setItem(getBackButtonSlot(), backButton());
            }
            clicker.updateInventory();
        } else {
            MessageManager.sendMessage(clicker, "errors.not-enough-resource", "resource", type.getDisplayName().toLowerCase());
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    private int getTier(String type) {
        UUID uuid = player.getUniqueId();
        return switch (type.toUpperCase()) {
            case "PICKAXE" -> progressionManager.getPickaxeTier(uuid);
            case "AXE" -> progressionManager.getAxeTier(uuid);
            case "ARMOR" -> progressionManager.getArmorTier(uuid);
            default -> 0;
        };
    }

    private void handleUpgrade(Player clicker, ShopManager.ShopItem item, ItemStack give) {
        String type = item.upgradeType();
        if (type == null) {
            clicker.getInventory().addItem(give);
            return;
        }
        UUID uuid = clicker.getUniqueId();
        switch (type.toUpperCase()) {
            case "PICKAXE" -> {
                removeTool(clicker, "_PICKAXE");
                ItemMeta meta = give.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                    give.setItemMeta(meta);
                }
                clicker.getInventory().addItem(give);
                progressionManager.setPickaxeTier(uuid, item.upgradeLevel());
            }
            case "AXE" -> {
                removeTool(clicker, "_AXE");
                ItemMeta meta = give.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.EFFICIENCY, 1, true);
                    give.setItemMeta(meta);
                }
                clicker.getInventory().addItem(give);
                progressionManager.setAxeTier(uuid, item.upgradeLevel());
            }
            case "ARMOR" -> {
                clicker.getInventory().setBoots(give);
                progressionManager.setArmorTier(uuid, item.upgradeLevel());
            }
            default -> clicker.getInventory().addItem(give);
        }
    }

    private void removeTool(Player player, String suffix) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack invItem = player.getInventory().getItem(i);
            if (invItem != null && invItem.getType().name().endsWith(suffix)) {
                player.getInventory().setItem(i, null);
            }
        }
    }
}
