package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;


import java.io.File;
import java.util.*;

/**
 * Loads and provides access to the special mid-game shop configuration
 * defined in {@code special_shop.yml}.
 */
public class SpecialShopManager {

    private final HeneriaBedwars plugin;
    private String title = "Special Shop";
    private int rows = 3;
    private final Map<Integer, SpecialItem> items = new HashMap<>();

    public SpecialShopManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        File file = new File(plugin.getDataFolder(), "special_shop.yml");
        if (!file.exists()) {
            plugin.saveResource("special_shop.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.title = ChatColor.translateAlternateColorCodes('&', config.getString("title", title));
        this.rows = config.getInt("rows", rows);
        items.clear();
        ConfigurationSection sec = config.getConfigurationSection("items");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                String base = "items." + key + ".";
                try {
                    Material material = Material.valueOf(config.getString(base + "material", "STONE"));
                    String name = config.getString(base + "name", key);
                    List<String> lore = config.getStringList(base + "lore");
                    ResourceType resource = ResourceType.valueOf(config.getString(base + "cost.resource", "DIAMOND"));
                    int cost = config.getInt(base + "cost.amount", 1);
                    int slot = config.getInt(base + "slot", 0);
                    String action = config.getString(base + "action");
                    items.put(slot, new SpecialItem(material, name, lore, resource, cost, slot, action));
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().warning("Invalid special shop item: " + key);
                }
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public Map<Integer, SpecialItem> getItems() {
        return items;
    }

    /**
     * Represents an item sold in the special shop.
     */
    public record SpecialItem(Material material, String name, List<String> lore,
                              ResourceType costResource, int costAmount, int slot, String action) {
    }
}

