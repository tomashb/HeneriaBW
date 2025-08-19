package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Manages creation and update of generator holograms through DecentHolograms.
 */
public class HologramManager {

    private final HeneriaBedwars plugin;
    private final boolean hooked;
    private final Map<Generator, Hologram> holograms = new HashMap<>();
    private final Map<GeneratorType, List<String>> formats = new EnumMap<>(GeneratorType.class);
    private final double offsetY;

    public HologramManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        Plugin dh = plugin.getServer().getPluginManager().getPlugin("DecentHolograms");
        this.hooked = dh != null;
        var section = plugin.getConfig().getConfigurationSection("generator-holograms");
        if (section != null && section.isConfigurationSection("formats")) {
            for (String key : section.getConfigurationSection("formats").getKeys(false)) {
                try {
                    GeneratorType type = GeneratorType.valueOf(key);
                    formats.put(type, section.getStringList("formats." + key));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        this.offsetY = section != null ? section.getDouble("offset-y", 2.5) : 2.5;
    }

    private boolean enabled() {
        var section = plugin.getConfig().getConfigurationSection("generator-holograms");
        return hooked && section != null && section.getBoolean("enabled", false);
    }

    /**
     * Creates an hologram for the given generator.
     *
     * @param generator generator to display
     */
    public void createGeneratorHologram(Generator generator) {
        if (!enabled() || generator.getLocation() == null) {
            return;
        }
        List<String> lines = formats.get(generator.getType());
        if (lines == null || lines.isEmpty()) {
            return;
        }
        Location loc = generator.getLocation().clone().add(0, offsetY, 0);
        Hologram holo = DHAPI.createHologram("gen_" + UUID.randomUUID(), loc, lines);
        holograms.put(generator, holo);
        updateGeneratorHologram(generator, plugin.getGeneratorManager().getRemainingSeconds(generator));
    }

    /**
     * Updates the countdown line of a generator hologram.
     *
     * @param generator generator whose hologram to update
     * @param seconds   seconds remaining
     */
    public void updateGeneratorHologram(Generator generator, int seconds) {
        if (!enabled()) {
            return;
        }
        Hologram holo = holograms.get(generator);
        if (holo == null) {
            return;
        }
        List<String> lines = formats.get(generator.getType());
        if (lines == null || lines.size() < 2) {
            return;
        }
        String line = lines.get(1).replace("{time}", String.valueOf(seconds));
        DHAPI.setHologramLine(holo, 1, line);
    }

    /**
     * Removes all holograms associated with the given arena.
     *
     * @param arena arena whose holograms to remove
     */
    public void removeArenaHolograms(Arena arena) {
        if (!enabled()) {
            return;
        }
        Iterator<Map.Entry<Generator, Hologram>> it = holograms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Generator, Hologram> entry = it.next();
            if (arena.getGenerators().contains(entry.getKey())) {
                DHAPI.removeHologram(entry.getValue());
                it.remove();
            }
        }
    }
}

