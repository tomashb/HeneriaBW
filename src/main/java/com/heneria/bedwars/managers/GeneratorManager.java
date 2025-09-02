package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GeneratorType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import java.io.File;
import java.util.*;

/**
 * Centralized manager handling resource generators for all arenas.
 */
public class GeneratorManager {

    private static final int TICK_RATE = 10; // 0.5 seconds

    private final HeneriaBedwars plugin;
    private final Map<Generator, Integer> counters = new HashMap<>();
    private final Map<GeneratorType, Map<Integer, GeneratorSettings>> settings = new EnumMap<>(GeneratorType.class);
    private final Map<GeneratorType, List<String>> hologramFormats = new EnumMap<>(GeneratorType.class);
    private boolean hologramsEnabled = true;
    private double hologramOffsetY = 2.5;

    public GeneratorManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        loadConfiguration();
        loadHologramSettings();
        startTask();
    }

    private void loadConfiguration() {
        File file = new File(plugin.getDataFolder(), "generators.yml");
        if (!file.exists()) {
            plugin.saveResource("generators.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (GeneratorType type : GeneratorType.values()) {
            Map<Integer, GeneratorSettings> tierMap = new HashMap<>();
            if (config.contains(type.name())) {
                for (String key : Objects.requireNonNull(config.getConfigurationSection(type.name())).getKeys(false)) {
                    if (!key.startsWith("tier-")) {
                        continue;
                    }
                    int tier = Integer.parseInt(key.substring(5));
                    double delay = config.getDouble(type.name() + "." + key + ".delay", 1.0);
                    int amount = config.getInt(type.name() + "." + key + ".amount", 1);
                    tierMap.put(tier, new GeneratorSettings(delay, amount));
                }
            }
            settings.put(type, tierMap);
            if (type == GeneratorType.GOLD) {
                plugin.getLogger().info("[DEBUG] Loaded " + tierMap.size() + " gold generator tiers");
            }
        }
    }

    private void loadHologramSettings() {
        FileConfiguration cfg = plugin.getConfig();
        hologramsEnabled = cfg.getBoolean("generator-holograms.enabled", true);
        hologramOffsetY = cfg.getDouble("generator-holograms.offset-y", 2.5);
        ConfigurationSection formats = cfg.getConfigurationSection("generator-holograms.formats");
        if (formats != null) {
            for (String key : formats.getKeys(false)) {
                try {
                    GeneratorType type = GeneratorType.valueOf(key.toUpperCase());
                    hologramFormats.put(type, formats.getStringList(key));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    private List<String> formatLines(Generator gen, int seconds) {
        GeneratorType type = gen.getType();
        List<String> template = hologramFormats.get(type);
        if (template == null || template.isEmpty()) {
            String name = type == GeneratorType.DIAMOND ? ChatColor.AQUA + "Diamants" : ChatColor.GREEN + "Émeraudes";
            return Arrays.asList(name, toRoman(gen.getTier()), seconds + "s");
        }
        List<String> lines = new ArrayList<>();
        for (String line : template) {
            line = line.replace("{time}", String.valueOf(seconds))
                    .replace("{tier}", toRoman(gen.getTier()));
            lines.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return lines;
    }

    private String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }

    private Location hologramLocation(Location base) {
        return base.getBlock().getLocation().add(0.5, hologramOffsetY, 0.5);
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 0L, TICK_RATE);
    }

    private void tick() {
        for (Map.Entry<Generator, Integer> entry : counters.entrySet()) {
            Generator gen = entry.getKey();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                spawn(gen);
                remaining = getDelayCycles(gen);
            }
            entry.setValue(remaining);
            if (hologramsEnabled && (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD)) {
                Location loc = gen.getLocation();
                if (loc != null) {
                    int seconds = (int) Math.ceil(remaining * TICK_RATE / 20.0);
                    Location holoLoc = hologramLocation(loc);
                    plugin.getHologramManager().updateHologram(holoLoc, formatLines(gen, seconds));
                }
            }
        }
    }

    private void spawn(Generator gen) {
        GeneratorSettings gs = getSettings(gen);
        if (gs == null || gen.getLocation() == null || gen.getLocation().getWorld() == null) {
            return;
        }
        Material material;
        switch (gen.getType()) {
            case GOLD -> material = Material.GOLD_INGOT;
            case DIAMOND -> material = Material.DIAMOND;
            case EMERALD -> material = Material.EMERALD;
            default -> material = Material.IRON_INGOT;
        }
        Location dropLocation = gen.getLocation().getBlock().getLocation().add(0.5, 1.0, 0.5);
        var item = dropLocation.getWorld().dropItem(dropLocation, new ItemStack(material, gs.amount()));
        item.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
        if (gen.getType() == GeneratorType.GOLD) {
            plugin.getLogger().info("[DEBUG] Spawned gold at " + dropLocation);
        }
    }

    private GeneratorSettings getSettings(Generator gen) {
        Map<Integer, GeneratorSettings> map = settings.get(gen.getType());
        if (map == null) {
            return null;
        }
        return map.get(gen.getTier());
    }

    private int getDelayCycles(Generator gen) {
        GeneratorSettings gs = getSettings(gen);
        if (gs == null) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.round(gs.delay() * 20.0 / TICK_RATE);
    }

    public int getDelaySeconds(Generator gen) {
        return (int) Math.ceil(getDelayCycles(gen) * TICK_RATE / 20.0);
    }

    public void registerGenerator(Generator gen) {
        counters.put(gen, getDelayCycles(gen));
        if (hologramsEnabled && (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD)) {
            Location loc = gen.getLocation();
            if (loc != null) {
                Location holoLoc = hologramLocation(loc);
                if (!plugin.getHologramManager().hasHologram(holoLoc)) {
                    int seconds = (int) Math.ceil(getDelayCycles(gen) * TICK_RATE / 20.0);
                    plugin.getHologramManager().createHologram(holoLoc, formatLines(gen, seconds));
                }
            }
        }
        if (gen.getType() == GeneratorType.GOLD) {
            plugin.getLogger().info("[DEBUG] Registered gold generator at " + gen.getLocation());
        }
    }

    public void unregisterGenerator(Generator gen) {
        counters.remove(gen);
        if (hologramsEnabled && (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD)) {
            Location loc = gen.getLocation();
            if (loc != null) {
                plugin.getHologramManager().removeHologram(hologramLocation(loc));
            }
        }
    }

    /**
     * Applies a Forge upgrade level to the generators near a team's spawn.
     * The upgrade accelerates both iron and gold generators and, at the final
     * level, spawns a slow emerald generator.
     *
     * @param arena the arena containing the generators
     * @param team  the team whose generators to upgrade
     * @param level the new forge level
     */
    public void upgradeTeamGenerators(Arena arena, Team team, int level) {
        Location spawn = team.getSpawnLocation();
        if (spawn == null) {
            return;
        }
        int ironTier = Math.min(1 + level, settings.getOrDefault(GeneratorType.IRON, Collections.emptyMap()).size());
        int goldTier = Math.min(1 + level, settings.getOrDefault(GeneratorType.GOLD, Collections.emptyMap()).size());
        Location genLocation = null;
        for (Generator gen : arena.getGenerators()) {
            Location loc = gen.getLocation();
            if (loc != null && loc.getWorld() == spawn.getWorld() && loc.distance(spawn) < 10) {
                if (gen.getType() == GeneratorType.IRON) {
                    gen.setTier(ironTier);
                    counters.put(gen, getDelayCycles(gen));
                    if (genLocation == null) {
                        genLocation = loc;
                    }
                } else if (gen.getType() == GeneratorType.GOLD) {
                    gen.setTier(goldTier);
                    counters.put(gen, getDelayCycles(gen));
                    if (genLocation == null) {
                        genLocation = loc;
                    }
                }
            }
        }
        if (level >= 4 && genLocation != null) {
            boolean exists = arena.getGenerators().stream().anyMatch(g -> g.getType() == GeneratorType.EMERALD
                    && g.getLocation() != null && g.getLocation().getWorld() == spawn.getWorld()
                    && g.getLocation().distance(spawn) < 10);
            if (!exists) {
                Generator emerald = new Generator(genLocation.clone(), GeneratorType.EMERALD, 1);
                arena.getGenerators().add(emerald);
                registerGenerator(emerald);
            }
        }
    }

    public void resetGenerators(Arena arena) {
        Iterator<Generator> it = arena.getGenerators().iterator();
        while (it.hasNext()) {
            Generator gen = it.next();
            counters.remove(gen);
            if (hologramsEnabled && (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD)) {
                Location loc = gen.getLocation();
                if (loc != null) {
                    plugin.getHologramManager().removeHologram(hologramLocation(loc));
                }
            }
            gen.setTier(1);
            if (gen.getType() == GeneratorType.EMERALD) {
                Location loc = gen.getLocation();
                if (loc != null) {
                    for (Team team : arena.getTeams().values()) {
                        Location spawn = team.getSpawnLocation();
                        if (spawn != null && loc.getWorld() == spawn.getWorld() && loc.distance(spawn) < 10) {
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Record of delay and amount for a generator tier.
     */
    private record GeneratorSettings(double delay, int amount) {
    }
}
