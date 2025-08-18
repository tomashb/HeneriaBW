package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

/**
 * Handles loading of team upgrades and applying their effects.
 */
public class UpgradeManager {

    private final HeneriaBedwars plugin;
    private YamlConfiguration config;
    private final Map<String, Upgrade> upgrades = new HashMap<>();

    public UpgradeManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        loadConfiguration();
    }

    private void loadConfiguration() {
        File file = new File(plugin.getDataFolder(), "upgrades.yml");
        if (!file.exists()) {
            plugin.saveResource("upgrades.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        upgrades.clear();
        for (String id : config.getKeys(false)) {
            String base = id + ".";
            String name = config.getString(base + "name", id);
            Material item = Material.valueOf(config.getString(base + "item", "STONE"));
            Map<Integer, UpgradeTier> tiers = new HashMap<>();
            ConfigurationSection sec = config.getConfigurationSection(base + "tiers");
            if (sec != null) {
                for (String tierKey : sec.getKeys(false)) {
                    try {
                        int tier = Integer.parseInt(tierKey);
                        int cost = sec.getInt(tierKey + ".cost", 1);
                        String desc = sec.getString(tierKey + ".description", "");
                        tiers.put(tier, new UpgradeTier(cost, desc));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            upgrades.put(id, new Upgrade(id, name, item, tiers));
        }
    }

    public Collection<Upgrade> getUpgrades() {
        return upgrades.values();
    }

    public Upgrade getUpgrade(String id) {
        return upgrades.get(id);
    }

    /**
     * Adds a sharpness enchantment to the provided item.
     *
     * @param item  the item to enchant
     * @param level the level of the enchantment
     */
    public void applySharpness(ItemStack item, int level) {
        item.addUnsafeEnchantment(Enchantment.SHARPNESS, level);
    }

    /**
     * Adds a protection enchantment to the provided item.
     *
     * @param item  the item to enchant
     * @param level the level of the enchantment
     */
    public void applyProtection(ItemStack item, int level) {
        item.addUnsafeEnchantment(Enchantment.PROTECTION, level);
    }

    /**
     * Applies the haste potion effect to the player.
     *
     * @param player    the player receiving the effect
     * @param amplifier the amplifier of the effect
     * @param duration  the duration of the effect in ticks
     */
    public void applyHaste(Player player, int amplifier, int duration) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, amplifier, true, true, true));
    }

    public record Upgrade(String id, String name, Material item, Map<Integer, UpgradeTier> tiers) {
    }

    public record UpgradeTier(int cost, String description) {
    }
}
