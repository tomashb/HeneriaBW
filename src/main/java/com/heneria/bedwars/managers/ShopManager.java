package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

/**
 * Manages shop configuration loaded from {@code shop.yml}.
 */
public class ShopManager {

    private final HeneriaBedwars plugin;
    private YamlConfiguration config;

    private final List<CategoryTab> categoryTabs = new ArrayList<>();
    private final Map<Integer, List<ShopItem>> quickBuyItems = new HashMap<>();
    private final Map<String, ShopCategory> categories = new HashMap<>();

    public ShopManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        loadConfiguration();
    }

    private void loadConfiguration() {
        File file = new File(plugin.getDataFolder(), "shop.yml");
        if (!file.exists()) {
            plugin.saveResource("shop.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        categoryTabs.clear();
        quickBuyItems.clear();
        categories.clear();

        // New configuration section for category tabs. Fallback to legacy main-menu.items
        ConfigurationSection tabSection = config.getConfigurationSection("category-tabs");
        if (tabSection != null) {
            for (String key : tabSection.getKeys(false)) {
                String base = "category-tabs." + key;
                try {
                    Material material = Material.valueOf(config.getString(base + ".material", "STONE"));
                    String name = config.getString(base + ".name", key);
                    List<String> lore = config.getStringList(base + ".lore");
                    int slot = config.getInt(base + ".slot", 0);
                    String category = config.getString(base + ".category");
                    categoryTabs.add(new CategoryTab(material, name, lore, slot, category));
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().warning("Invalid material for category tab " + key);
                }
            }
        } else {
            ConfigurationSection mainSection = config.getConfigurationSection("main-menu.items");
            if (mainSection != null) {
                for (String key : mainSection.getKeys(false)) {
                    String base = "main-menu.items." + key;
                    try {
                        Material material = Material.valueOf(config.getString(base + ".material", "STONE"));
                        String name = config.getString(base + ".name", key);
                        List<String> lore = config.getStringList(base + ".lore");
                        int slot = config.getInt(base + ".slot", 0);
                        String category = config.getString(base + ".category");
                        categoryTabs.add(new CategoryTab(material, name, lore, slot, category));
                    } catch (IllegalArgumentException ex) {
                        plugin.getLogger().warning("Invalid material for main menu item " + key);
                    }
                }
            }
        }

        // Load shop categories
        ConfigurationSection catSection = config.getConfigurationSection("shop-categories");
        if (catSection != null) {
            for (String id : catSection.getKeys(false)) {
                String base = "shop-categories." + id;
                String title = config.getString(base + ".title", id);
                int rows = config.getInt(base + ".rows", 3);
                Map<Integer, List<ShopItem>> items = new HashMap<>();
                ConfigurationSection itemsSec = config.getConfigurationSection(base + ".items");
                if (itemsSec != null) {
                    for (String itemKey : itemsSec.getKeys(false)) {
                        String path = base + ".items." + itemKey;
                        try {
                            ShopItem item = parseItem(path, itemKey);
                            items.computeIfAbsent(item.slot(), k -> new ArrayList<>()).add(item);
                        } catch (IllegalArgumentException ex) {
                            plugin.getLogger().warning("Invalid item configuration for category " + id + ": " + itemKey);
                        }
                    }
                }
                categories.put(id, new ShopCategory(id, title, rows, items));
            }
        }

        // Load quick buy items or migrate legacy quick_buy_category
        ConfigurationSection qbSection = config.getConfigurationSection("quick-buy-items");
        if (qbSection != null) {
            for (String key : qbSection.getKeys(false)) {
                String path = "quick-buy-items." + key;
                try {
                    ShopItem item = parseItem(path, key);
                    quickBuyItems.computeIfAbsent(item.slot(), k -> new ArrayList<>()).add(item);
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().warning("Invalid quick buy item " + key);
                }
            }
        } else {
            ShopCategory legacy = categories.remove("quick_buy_category");
            if (legacy != null) {
                quickBuyItems.putAll(legacy.items());
            }
        }
    }

    private ShopItem parseItem(String path, String key) {
        Material material = Material.valueOf(config.getString(path + ".material", "STONE"));
        String name = config.getString(path + ".name", key);
        int amount = config.getInt(path + ".amount", 1);
        ResourceType resource = ResourceType.valueOf(config.getString(path + ".cost.resource", "IRON"));
        int cost = config.getInt(path + ".cost.amount", 1);
        int slot = config.getInt(path + ".slot", 0);
        String action = config.getString(path + ".action");
        ConfigurationSection tierSec = config.getConfigurationSection(path + ".upgrade_tier");
        String type = tierSec != null ? tierSec.getString("type") : null;
        int level = tierSec != null ? tierSec.getInt("level", 0) : 0;

        List<PotionEffect> potionEffects = new ArrayList<>();
        for (Map<?, ?> map : config.getMapList(path + ".potion-effects")) {
            PotionEffectType pet = Registry.POTION_EFFECT_TYPE.get(
                    NamespacedKey.minecraft(String.valueOf(map.get("type")).toLowerCase(Locale.ROOT)));
            if (pet != null) {
                int duration = map.get("duration") instanceof Number d ? d.intValue() * 20 : 0;
                int amplifier = map.get("amplifier") instanceof Number a ? a.intValue() : 0;
                potionEffects.add(new PotionEffect(pet, duration, amplifier));
            }
        }

        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (Map<?, ?> map : config.getMapList(path + ".enchantments")) {
            Object typeObj = map.get("type");
            NamespacedKey enchKey = typeObj != null
                    ? NamespacedKey.minecraft(String.valueOf(typeObj).toLowerCase(Locale.ROOT))
                    : null;
            Enchantment ench = enchKey != null ? Registry.ENCHANTMENT.get(enchKey) : null;
            if (ench != null) {
                int lvl = map.get("level") instanceof Number n ? n.intValue() : 1;
                enchantments.put(ench, lvl);
            }
        }

        return new ShopItem(material, name, amount, resource, cost, slot, action, type, level, potionEffects, enchantments);
    }

    public String getMainMenuTitle() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("main-menu.title", "Boutique"));
    }

    public List<CategoryTab> getCategoryTabs() {
        return categoryTabs;
    }

    public Map<Integer, List<ShopItem>> getQuickBuyItems() {
        return quickBuyItems;
    }

    public ShopCategory getCategory(String id) {
        return categories.get(id);
    }

    public record CategoryTab(Material material, String name, List<String> lore, int slot, String category) {
    }

    public record ShopItem(Material material, String name, int amount, ResourceType costResource,
                           int costAmount, int slot, String action, String upgradeType, int upgradeLevel,
                           List<PotionEffect> potionEffects, Map<Enchantment, Integer> enchantments) {
    }

    public record ShopCategory(String id, String title, int rows, Map<Integer, List<ShopItem>> items) {
    }
}

