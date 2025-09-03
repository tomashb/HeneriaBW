package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Registry;
import org.bukkit.scheduler.BukkitRunnable;

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
    private final Map<String, UpgradeCategory> categories = new HashMap<>();
    private String mainMenuTitle = "Upgrades";
    private int mainMenuRows = 3;
    private final java.util.List<MainMenuItem> mainMenuItems = new java.util.ArrayList<>();
    private double healPoolRadius = 8;
    private int healPoolAmplifier = 0;
    private String trapAlarmSound = "ENTITY_ENDER_DRAGON_GROWL";

    public UpgradeManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        loadConfiguration();
        startHealPoolTask();
    }

    private void loadConfiguration() {
        File file = new File(plugin.getDataFolder(), "upgrades.yml");
        if (!file.exists()) {
            plugin.saveResource("upgrades.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        upgrades.clear();
        traps.clear();
        categories.clear();
        mainMenuItems.clear();

        ConfigurationSection main = config.getConfigurationSection("main-menu");
        if (main != null) {
            mainMenuTitle = main.getString("title", mainMenuTitle);
            mainMenuRows = main.getInt("rows", mainMenuRows);
            ConfigurationSection items = main.getConfigurationSection("items");
            if (items != null) {
                for (String key : items.getKeys(false)) {
                    ConfigurationSection itemSec = items.getConfigurationSection(key);
                    Material mat = Material.valueOf(itemSec.getString("material", "STONE"));
                    String name = itemSec.getString("name", key);
                    java.util.List<String> lore = itemSec.getStringList("lore");
                    int slot = itemSec.getInt("slot", 0);
                    String category = itemSec.getString("category");
                    mainMenuItems.add(new MainMenuItem(mat, name, lore, slot, category));
                }
            }
        }

        ConfigurationSection catSec = config.getConfigurationSection("upgrade-categories");
        if (catSec != null) {
            for (String catId : catSec.getKeys(false)) {
                ConfigurationSection c = catSec.getConfigurationSection(catId);
                String title = c.getString("title", catId);
                int rows = c.getInt("rows", 3);
                Map<String, Upgrade> catUpgrades = new LinkedHashMap<>();
                ConfigurationSection ups = c.getConfigurationSection("upgrades");
                if (ups != null) {
                    for (String id : ups.getKeys(false)) {
                        String base = id + ".";
                        String name = ups.getString(base + "name", id);
                        Material item = Material.valueOf(ups.getString(base + "item", "STONE"));
                        Map<Integer, UpgradeTier> tiers = new HashMap<>();
                        ConfigurationSection sec = ups.getConfigurationSection(base + "tiers");
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
                        Upgrade up = new Upgrade(id, name, item, tiers);
                        catUpgrades.put(id, up);
                        upgrades.put(id, up);

                        if (id.equals("heal-pool")) {
                            ConfigurationSection params = ups.getConfigurationSection(base + "tiers.1.parameters");
                            if (params != null) {
                                healPoolRadius = params.getDouble("radius", 8);
                                healPoolAmplifier = params.getInt("amplifier", 0);
                            }
                        } else if (id.equals("trap-alarm")) {
                            ConfigurationSection params = ups.getConfigurationSection(base + "tiers.1.parameters");
                            if (params != null) {
                                trapAlarmSound = params.getString("sound", "ENTITY_ENDER_DRAGON_GROWL");
                            }
                        }
                    }
                }

                Map<String, Trap> catTraps = new LinkedHashMap<>();
                ConfigurationSection trs = c.getConfigurationSection("traps");
                if (trs != null) {
                    for (String id : trs.getKeys(false)) {
                        String base = id + ".";
                        String name = trs.getString(base + "name", id);
                        Material item = Material.valueOf(trs.getString(base + "item", "STONE"));
                        int cost = trs.getInt(base + "cost", 1);
                        List<String> description = trs.getStringList(base + "description");
                        ConfigurationSection effectSec = trs.getConfigurationSection(base + "effect");
                        PotionEffectType type = Registry.EFFECT.get(
                                NamespacedKey.minecraft(effectSec.getString("type", "BLINDNESS").toLowerCase(Locale.ROOT)));
                        int duration = effectSec.getInt("duration", 5);
                        int amplifier = effectSec.getInt("amplifier", 0);
                        Trap trap = new Trap(id, name, item, cost, description, type, duration, amplifier);
                        catTraps.put(id, trap);
                        traps.put(id, trap);
                    }
                }

                categories.put(catId, new UpgradeCategory(catId, title, rows, catUpgrades, catTraps));
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

    public String getMainMenuTitle() {
        return mainMenuTitle;
    }

    public int getMainMenuRows() {
        return mainMenuRows;
    }

    public java.util.List<MainMenuItem> getMainMenuItems() {
        return mainMenuItems;
    }

    public UpgradeCategory getCategory(String id) {
        return categories.get(id);
    }

    /**
     * Gets the configured sound name for the trap alarm.
     *
     * @return sound identifier
     */
    public String getTrapAlarmSound() {
        return trapAlarmSound;
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
     * Applies the speed potion effect to the player.
     *
     * @param player    the player receiving the effect
     * @param amplifier the amplifier of the effect
     * @param duration  the duration of the effect in ticks
     */
    public void applySpeed(Player player, int amplifier, int duration) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier, true, true, true));
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

    private void startHealPoolTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Arena arena = plugin.getArenaManager().getArena(player);
                    if (arena == null) continue;
                    Team team = arena.getTeam(player);
                    if (team == null || !team.hasHealPool()) continue;
                    Location bed = team.getBedLocation();
                    if (bed == null) continue;
                    if (player.getLocation().distanceSquared(bed) <= healPoolRadius * healPoolRadius) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, healPoolAmplifier, true, true, true));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 40L);
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

        int speed = team.getUpgradeLevel("speed");
        if (speed > 0) {
            applySpeed(player, speed - 1, 20 * 60 * 60);
        }
    }

    /**
     * Applies relevant team upgrades to a freshly obtained item.
     *
     * @param player the owner of the item
     * @param item   the item to update
     */
    public void applyTeamUpgrades(Player player, ItemStack item) {
        if (item == null) return;
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        Team team = arena.getTeam(player);
        if (team == null) return;
        String name = item.getType().name();
        if (name.endsWith("SWORD")) {
            int sharpness = team.getUpgradeLevel("sharpness");
            if (sharpness > 0) {
                applySharpness(item, sharpness);
            }
        } else if (name.endsWith("HELMET") || name.endsWith("CHESTPLATE")
                || name.endsWith("LEGGINGS") || name.endsWith("BOOTS")) {
            int protection = team.getUpgradeLevel("protection");
            if (protection > 0) {
                applyProtection(item, protection);
            }
        }
    }

    public record Upgrade(String id, String name, Material item, Map<Integer, UpgradeTier> tiers) {
    }

    public record UpgradeTier(int cost, String description) {
    }

    public record Trap(String id, String name, Material item, int cost, List<String> description,
                        PotionEffectType effectType, int duration, int amplifier) {
    }

    public record MainMenuItem(Material material, String name, java.util.List<String> lore, int slot, String category) {
    }

    public record UpgradeCategory(String id, String title, int rows, Map<String, Upgrade> upgrades, Map<String, Trap> traps) {
    }
}
