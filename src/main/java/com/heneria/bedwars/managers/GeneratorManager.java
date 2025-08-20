package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GeneratorType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

    public GeneratorManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        loadConfiguration();
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
            if (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD) {
                Location loc = gen.getLocation();
                if (loc != null) {
                    int seconds = (int) Math.ceil(remaining * TICK_RATE / 20.0);
                    String title = gen.getType() == GeneratorType.DIAMOND ? ChatColor.AQUA + "Diamant" : ChatColor.GREEN + "Émeraude";
                    Location holoLoc = loc.clone().add(0.5, 2.0, 0.5);
                    plugin.getHologramManager().updateHologram(holoLoc, Arrays.asList(title, seconds + "s"));
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
        Location dropLocation = gen.getLocation().clone().add(0.5, 0.5, 0.5);
        dropLocation.getWorld().dropItem(dropLocation, new ItemStack(material, gs.amount()));
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
        if (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD) {
            Location loc = gen.getLocation();
            if (loc != null) {
                Location holoLoc = loc.clone().add(0.5, 2.0, 0.5);
                if (!plugin.getHologramManager().hasHologram(holoLoc)) {
                    int seconds = (int) Math.ceil(getDelayCycles(gen) * TICK_RATE / 20.0);
                    String title = gen.getType() == GeneratorType.DIAMOND ? ChatColor.AQUA + "Diamant" : ChatColor.GREEN + "Émeraude";
                    plugin.getHologramManager().createHologram(holoLoc, Arrays.asList(title, seconds + "s"));
                }
            }
        }
        if (gen.getType() == GeneratorType.GOLD) {
            plugin.getLogger().info("[DEBUG] Registered gold generator at " + gen.getLocation());
        }
    }

    public void unregisterGenerator(Generator gen) {
        counters.remove(gen);
        if (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD) {
            Location loc = gen.getLocation();
            if (loc != null) {
                Location holoLoc = loc.clone().add(0.5, 2.0, 0.5);
                plugin.getHologramManager().removeHologram(holoLoc);
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
            if (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD) {
                Location loc = gen.getLocation();
                if (loc != null) {
                    Location holoLoc = loc.clone().add(0.5, 2.0, 0.5);
                    plugin.getHologramManager().removeHologram(holoLoc);
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
