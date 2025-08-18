package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

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
                String title = config.getString("shop-categories." + id + ".title", id);
                int rows = config.getInt("shop-categories." + id + ".rows", 3);
                categories.put(id, new ShopCategory(id, title, rows));
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

    public record ShopCategory(String id, String title, int rows) {
    }
}
