package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GeneratorType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                spawn(entry.getKey());
                remaining = getDelayCycles(entry.getKey());
            }
            entry.setValue(remaining);
            int seconds = (int) Math.ceil(remaining * TICK_RATE / 20.0);
            plugin.getHologramManager().updateGeneratorCountdown(entry.getKey(), seconds);
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
        gen.getLocation().getWorld().dropItemNaturally(gen.getLocation(), new ItemStack(material, gs.amount()));
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

    public void registerGenerator(Generator gen) {
        counters.put(gen, getDelayCycles(gen));
    }

    public void unregisterGenerator(Generator gen) {
        counters.remove(gen);
    }

    public int getRemainingSeconds(Generator gen) {
        Integer cycles = counters.get(gen);
        if (cycles == null) {
            return -1;
        }
        return (int) Math.ceil(cycles * TICK_RATE / 20.0);
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
        for (Generator gen : arena.getGenerators()) {
            if (gen.getLocation() != null && gen.getLocation().getWorld() == spawn.getWorld()
                    && gen.getLocation().distance(spawn) < 10) {
                if (gen.getType() == GeneratorType.IRON) {
                    gen.setTier(ironTier);
                    counters.put(gen, getDelayCycles(gen));
                } else if (gen.getType() == GeneratorType.GOLD) {
                    gen.setTier(goldTier);
                    counters.put(gen, getDelayCycles(gen));
                }
            }
        }
        if (level >= 4) {
            boolean exists = arena.getGenerators().stream().anyMatch(g -> g.getType() == GeneratorType.EMERALD
                    && g.getLocation() != null && g.getLocation().getWorld() == spawn.getWorld()
                    && g.getLocation().distance(spawn) < 10);
            if (!exists) {
                Generator emerald = new Generator(spawn.clone(), GeneratorType.EMERALD, 1);
                arena.getGenerators().add(emerald);
                registerGenerator(emerald);
            }
        }
    }

    /**
     * Record of delay and amount for a generator tier.
     */
    private record GeneratorSettings(double delay, int amount) {
    }
}
