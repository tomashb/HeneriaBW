package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

/**
 * Gestionnaire des améliorations d'équipe chargées depuis {@code upgrades.yml}.
 */
public class UpgradeManager {

    private final HeneriaBedwars plugin;
    private final Map<UpgradeType, Upgrade> upgrades = new EnumMap<>(UpgradeType.class);

    public UpgradeManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        loadConfiguration();
    }

    private void loadConfiguration() {
        File file = new File(plugin.getDataFolder(), "upgrades.yml");
        if (!file.exists()) {
            plugin.saveResource("upgrades.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        upgrades.clear();
        for (String key : config.getKeys(false)) {
            try {
                UpgradeType type = UpgradeType.valueOf(key.toUpperCase());
                String name = config.getString(key + ".name", key);
                Material item = Material.valueOf(config.getString(key + ".item", "STONE"));
                Map<Integer, UpgradeTier> tiers = new HashMap<>();
                ConfigurationSection tierSec = config.getConfigurationSection(key + ".tiers");
                if (tierSec != null) {
                    for (String tierKey : tierSec.getKeys(false)) {
                        int level = Integer.parseInt(tierKey);
                        int cost = tierSec.getInt(tierKey + ".cost", 1);
                        String desc = tierSec.getString(tierKey + ".description", "");
                        tiers.put(level, new UpgradeTier(cost, desc));
                    }
                }
                upgrades.put(type, new Upgrade(type, name, item, tiers));
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("Invalid upgrade configuration: " + key);
            }
        }
    }

    public Upgrade getUpgrade(UpgradeType type) {
        return upgrades.get(type);
    }

    public void applyUpgrade(Arena arena, Team team, UpgradeType type, int level) {
        switch (type) {
            case SHARPNESS -> applySharpness(team, level);
            case PROTECTION -> applyProtection(team, level);
            case HASTE -> applyHaste(team, level);
            case FORGE -> applyForge(arena, team, level);
            default -> {
            }
        }
    }

    private void applySharpness(Team team, int level) {
        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack != null && stack.getType().name().endsWith("_SWORD")) {
                    stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level);
                }
            }
        }
    }

    private void applyProtection(Team team, int level) {
        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            ItemStack[] armor = player.getInventory().getArmorContents();
            for (int i = 0; i < armor.length; i++) {
                ItemStack piece = armor[i];
                if (piece != null) {
                    piece.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
                    armor[i] = piece;
                }
            }
            player.getInventory().setArmorContents(armor);
        }
    }

    private void applyHaste(Team team, int level) {
        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level - 1, false, false));
            }
        }
    }

    private void applyForge(Arena arena, Team team, int level) {
        if (arena == null || team.getSpawnLocation() == null) {
            return;
        }
        for (Generator gen : arena.getGenerators()) {
            if (gen.getLocation() != null && gen.getLocation().getWorld() == team.getSpawnLocation().getWorld()
                    && gen.getLocation().distanceSquared(team.getSpawnLocation()) <= 36) {
                gen.setTier(level + 1);
            }
        }
    }

    public record Upgrade(UpgradeType type, String name, Material item, Map<Integer, UpgradeTier> tiers) {
    }

    public record UpgradeTier(int cost, String description) {
    }
}
