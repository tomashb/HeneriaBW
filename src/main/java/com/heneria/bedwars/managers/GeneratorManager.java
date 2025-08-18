package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
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

    /**
     * Record of delay and amount for a generator tier.
     */
    private record GeneratorSettings(double delay, int amount) {
    }
}
