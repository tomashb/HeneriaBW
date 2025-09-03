package com.heneria.bedwars.gui.shop;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.PlayerProgressionManager;
import com.heneria.bedwars.managers.ResourceManager;
import com.heneria.bedwars.managers.ResourceType;
import com.heneria.bedwars.managers.ShopManager;
import com.heneria.bedwars.utils.GameUtils;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * Unified shop menu with category tabs and quick buy section.
 */
public class ShopMenu extends Menu {

    private final ShopManager shopManager;
    private final PlayerProgressionManager progressionManager;
    private final Player player;

    private final Map<Integer, ShopManager.ShopItem> slotItems = new HashMap<>();
    private final Map<Integer, ShopManager.CategoryTab> slotTabs = new HashMap<>();

    private ShopManager.ShopCategory activeCategory;

    public ShopMenu(ShopManager shopManager, PlayerProgressionManager progressionManager, Player player) {
        this.shopManager = shopManager;
        this.progressionManager = progressionManager;
        this.player = player;
    }

    @Override
    public String getTitle() {
        return activeCategory != null ? ChatColor.translateAlternateColorCodes('&', activeCategory.title())
                : shopManager.getMainMenuTitle();
    }

    @Override
    public int getSize() {
        return 54;
    }

    private Material getFillerMaterial() {
        if (activeCategory == null) {
            return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
        }
        return switch (activeCategory.id().toLowerCase()) {
            case "blocks_category" -> Material.LIME_STAINED_GLASS_PANE;
            case "melee_category" -> Material.RED_STAINED_GLASS_PANE;
            case "armors_category" -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            case "tools_category" -> Material.YELLOW_STAINED_GLASS_PANE;
            case "ranged_category" -> Material.ORANGE_STAINED_GLASS_PANE;
            case "potions_category" -> Material.MAGENTA_STAINED_GLASS_PANE;
            case "utilities_category" -> Material.GREEN_STAINED_GLASS_PANE;
            default -> Material.GRAY_STAINED_GLASS_PANE;
        };
    }

    @Override
    public void setupItems() {
        inventory.clear();
        slotItems.clear();
        slotTabs.clear();

        ItemStack filler = new ItemBuilder(getFillerMaterial()).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, filler);
        }

        for (ShopManager.CategoryTab tab : shopManager.getCategoryTabs()) {
            ItemStack stack = new ItemBuilder(tab.material())
                    .setName(tab.name())
                    .setLore(tab.lore())
                    .build();
            inventory.setItem(tab.slot(), stack);
            slotTabs.put(tab.slot(), tab);
        }

        Map<Integer, List<ShopManager.ShopItem>> items = activeCategory != null
                ? activeCategory.items()
                : shopManager.getQuickBuyItems();

        Team team = getTeam();

        for (Map.Entry<Integer, List<ShopManager.ShopItem>> entry : items.entrySet()) {
            int slot = entry.getKey();
            List<ShopManager.ShopItem> list = entry.getValue();
            ShopManager.ShopItem display = list.get(0);
            if (list.size() > 1 && list.get(0).upgradeType() != null) {
                list.sort(Comparator.comparingInt(ShopManager.ShopItem::upgradeLevel));
                int current = getTier(list.get(0).upgradeType());
                int max = list.get(list.size() - 1).upgradeLevel();
                if (current >= max) {
                    ItemStack owned = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                            .setName("&cDéjà possédé").build();
                    inventory.setItem(slot, owned);
                    continue;
                }
                int next = current + 1;
                display = null;
                for (ShopManager.ShopItem candidate : list) {
                    if (candidate.upgradeLevel() == next) {
                        display = candidate;
                        break;
                    }
                }
                if (display == null) {
                    inventory.setItem(slot, filler);
                    continue;
                }
            }
            int price = getDiscountedPrice(team, display.costAmount());
            ItemBuilder builder = new ItemBuilder(display.material())
                    .setName(display.name())
                    .addLore("&7Quantité: &f" + display.amount())
                    .addLore("&7Coût: " + display.costResource().getColor() + price + " " + display.costResource().getDisplayName());
            ItemStack stack = builder.build();
            stack.setAmount(display.amount());
            inventory.setItem(slot, stack);
            slotItems.put(slot, display);
        }

        // Fill remaining slots in the quick buy menu with decorative panes
        if (activeCategory == null) {
            ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
            for (int i = 0; i < getSize(); i++) {
                if (!slotItems.containsKey(i) && !slotTabs.containsKey(i)) {
                    inventory.setItem(i, pane);
                }
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player clicker)) {
            return;
        }
        int slot = event.getRawSlot();
        ShopManager.CategoryTab tab = slotTabs.get(slot);
        if (tab != null) {
            String cat = tab.category();
            if (cat == null || (activeCategory != null && activeCategory.id().equalsIgnoreCase(cat))) {
                activeCategory = null;
            } else {
                activeCategory = shopManager.getCategory(cat);
            }
            setupItems();
            clicker.updateInventory();
            return;
        }

        ShopManager.ShopItem item = slotItems.get(slot);
        if (item == null) {
            return;
        }

        Team team = getTeam();
        ResourceType type = item.costResource();
        int price = getDiscountedPrice(team, item.costAmount());
        if (ResourceManager.hasResources(clicker, type, price)) {
            String coloredName = ChatColor.translateAlternateColorCodes('&', item.name());
            ResourceManager.takeResources(clicker, type, price);
            MessageManager.sendMessage(clicker, "shop.purchase-success",
                    "item", coloredName,
                    "price", String.valueOf(price),
                    "resource", type.getColor() + type.getDisplayName());
            Material material = item.material();
            Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(clicker);
            Team team = arena != null ? arena.getTeam(clicker) : null;
            if (material.toString().endsWith("_WOOL") && team != null) {
                material = team.getColor().getWoolMaterial();
            }
            boolean isSword = material.name().endsWith("_SWORD");
            boolean isPickaxe = material.name().endsWith("_PICKAXE");
            boolean isAxe = material.name().endsWith("_AXE");
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
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', item.name()));
                if (isSword || isPickaxe || isAxe) {
                    meta.getPersistentDataContainer().set(GameUtils.STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
                }
                if (item.action() != null) {
                    meta.getPersistentDataContainer()
                            .set(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING, item.action());
                }
                for (Map.Entry<Enchantment, Integer> entry : item.enchantments().entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
                if (meta instanceof org.bukkit.inventory.meta.PotionMeta potionMeta) {
                    for (PotionEffect effect : item.potionEffects()) {
                        potionMeta.addCustomEffect(effect, true);
                    }
                }
                if (give.getType().getMaxDurability() > 0) {
                    meta.setUnbreakable(true);
                }
                give.setItemMeta(meta);
            }
            HeneriaBedwars.getInstance().getUpgradeManager().applyTeamUpgrades(clicker, give);
            handleUpgrade(clicker, item, give);
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            setupItems();
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
                    meta.setUnbreakable(true);
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
                    meta.setUnbreakable(true);
                    give.setItemMeta(meta);
                }
                clicker.getInventory().addItem(give);
                progressionManager.setAxeTier(uuid, item.upgradeLevel());
            }
            case "ARMOR" -> {
                Material bootsType = give.getType();
                clicker.getInventory().setBoots(GameUtils.createBoundArmor(bootsType));
                Material leggingsType = switch (bootsType) {
                    case CHAINMAIL_BOOTS -> Material.CHAINMAIL_LEGGINGS;
                    case IRON_BOOTS -> Material.IRON_LEGGINGS;
                    case DIAMOND_BOOTS -> Material.DIAMOND_LEGGINGS;
                    default -> Material.LEATHER_LEGGINGS;
                };
                clicker.getInventory().setLeggings(GameUtils.createBoundArmor(leggingsType));
                HeneriaBedwars.getInstance().getUpgradeManager()
                        .applyTeamUpgrades(clicker, clicker.getInventory().getBoots());
                HeneriaBedwars.getInstance().getUpgradeManager()
                        .applyTeamUpgrades(clicker, clicker.getInventory().getLeggings());
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

    private Team getTeam() {
        Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(player);
        return arena != null ? arena.getTeam(player) : null;
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

