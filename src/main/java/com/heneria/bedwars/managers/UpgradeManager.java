package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
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
    private final Map<String, Trap> traps = new HashMap<>();

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
            if (id.equalsIgnoreCase("traps")) continue;
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

        traps.clear();
        ConfigurationSection trapSection = config.getConfigurationSection("traps");
        if (trapSection != null) {
            for (String id : trapSection.getKeys(false)) {
                String base = id + ".";
                String name = trapSection.getString(base + "name", id);
                Material item = Material.valueOf(trapSection.getString(base + "item", "STONE"));
                int cost = trapSection.getInt(base + "cost", 1);
                List<String> description = trapSection.getStringList(base + "description");
                ConfigurationSection effectSec = trapSection.getConfigurationSection(base + "effect");
                PotionEffectType type = PotionEffectType.getByName(effectSec.getString("type", "BLINDNESS"));
                int duration = effectSec.getInt("duration", 5);
                int amplifier = effectSec.getInt("amplifier", 0);
                traps.put(id, new Trap(id, name, item, cost, description, type, duration, amplifier));
            }
        }
    }

    public Collection<Upgrade> getUpgrades() {
        return upgrades.values();
    }

    public Upgrade getUpgrade(String id) {
        return upgrades.get(id);
    }

    public Collection<Trap> getTraps() {
        return traps.values();
    }

    public Trap getTrap(String id) {
        return traps.get(id);
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

    /**
     * Applies the trap's potion effect to the player.
     *
     * @param player the player affected
     * @param trap   the trap definition
     */
    public void applyTrapEffect(Player player, Trap trap) {
        player.addPotionEffect(new PotionEffect(trap.effectType(), trap.duration() * 20, trap.amplifier(), true, true, true));
    }

    /**
     * Reapplies all team upgrades to a player's equipment after respawn.
     *
     * @param player the player to update
     */
    public void applyTeamUpgrades(Player player) {
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        Team team = arena.getTeam(player);
        if (team == null) return;

        int sharpness = team.getUpgradeLevel("sharpness");
        if (sharpness > 0) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType().name().endsWith("SWORD")) {
                    applySharpness(item, sharpness);
                }
            }
        }

        int protection = team.getUpgradeLevel("protection");
        if (protection > 0) {
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor != null) {
                    applyProtection(armor, protection);
                }
            }
        }

        int haste = team.getUpgradeLevel("haste");
        if (haste > 0) {
            applyHaste(player, haste - 1, 20 * 60 * 60);
        }
    }

    public record Upgrade(String id, String name, Material item, Map<Integer, UpgradeTier> tiers) {
    }

    public record UpgradeTier(int cost, String description) {
    }

    public record Trap(String id, String name, Material item, int cost, List<String> description,
                        PotionEffectType effectType, int duration, int amplifier) {
    }
}
