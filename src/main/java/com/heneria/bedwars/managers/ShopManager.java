package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

/**
 * Manages shop configuration loaded from {@code shop.yml}.
 */
public class ShopManager {

    private final HeneriaBedwars plugin;
    private YamlConfiguration config;
    private final List<MainMenuItem> mainMenuItems = new ArrayList<>();
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
        mainMenuItems.clear();
        categories.clear();

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
                    mainMenuItems.add(new MainMenuItem(material, name, lore, slot, category));
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().warning("Invalid material for main menu item " + key);
                }
            }
        }

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
                            Material material = Material.valueOf(config.getString(path + ".material", "STONE"));
                            String name = config.getString(path + ".name", itemKey);
                            int amount = config.getInt(path + ".amount", 1);
                            ResourceType resource = ResourceType.valueOf(config.getString(path + ".cost.resource", "IRON"));
                            int cost = config.getInt(path + ".cost.amount", 1);
                            int slot = config.getInt(path + ".slot", 0);
                            String action = config.getString(path + ".action");
                            ConfigurationSection tierSec = config.getConfigurationSection(path + ".upgrade_tier");
                            String type = tierSec != null ? tierSec.getString("type") : null;
                            int level = tierSec != null ? tierSec.getInt("level", 0) : 0;

                            List<PotionEffectData> effects = new ArrayList<>();
                            for (Map<String, Object> map : config.getMapList(path + ".potion-effects")) {
                                try {
                                    PotionEffectType potion = PotionEffectType.valueOf(((String) map.get("type")).toUpperCase());
                                    int duration = ((Number) map.getOrDefault("duration", 1)).intValue();
                                    int amplifier = ((Number) map.getOrDefault("amplifier", 0)).intValue();
                                    effects.add(new PotionEffectData(potion, duration, amplifier));
                                } catch (Exception e) {
                                    plugin.getLogger().warning("Invalid potion effect for item " + itemKey + " in category " + id);
                                }
                            }

                            Map<Enchantment, Integer> enchants = new HashMap<>();
                            for (Map<String, Object> map : config.getMapList(path + ".enchantments")) {
                                try {
                                    Enchantment enchantment = Enchantment.getByName(((String) map.get("type")).toUpperCase());
                                    int lvl = ((Number) map.getOrDefault("level", 1)).intValue();
                                    if (enchantment != null) {
                                        enchants.put(enchantment, lvl);
                                    }
                                } catch (Exception e) {
                                    plugin.getLogger().warning("Invalid enchantment for item " + itemKey + " in category " + id);
                                }
                            }

                            items.computeIfAbsent(slot, k -> new ArrayList<>())
                                    .add(new ShopItem(material, name, amount, resource, cost, slot, action, type, level, effects, enchants));
                        } catch (IllegalArgumentException ex) {
                            plugin.getLogger().warning("Invalid item configuration for category " + id + ": " + itemKey);
                        }
                    }
                }
                categories.put(id, new ShopCategory(id, title, rows, items));
            }
        }
    }

    public String getMainMenuTitle() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("main-menu.title", "Boutique"));
    }

    public int getMainMenuRows() {
        return config.getInt("main-menu.rows", 3);
    }

    public List<MainMenuItem> getMainMenuItems() {
        return mainMenuItems;
    }

    public ShopCategory getCategory(String id) {
        return categories.get(id);
    }

    public record MainMenuItem(Material material, String name, List<String> lore, int slot, String category) {
    }

    public record ShopItem(Material material, String name, int amount, ResourceType costResource,
                           int costAmount, int slot, String action, String upgradeType, int upgradeLevel,
                           List<PotionEffectData> potionEffects, Map<Enchantment, Integer> enchantments) {
    }

    public record PotionEffectData(PotionEffectType type, int duration, int amplifier) {
    }

    public record ShopCategory(String id, String title, int rows, Map<Integer, List<ShopItem>> items) {
    }
}
