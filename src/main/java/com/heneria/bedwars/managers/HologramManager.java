package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles creation and upkeep of DecentHolograms holograms for resource generators.
 */
public class HologramManager {

    private final HeneriaBedwars plugin;
    private final boolean enabled;
    private final double height;
    private final Map<GeneratorType, String> templates = new EnumMap<>(GeneratorType.class);
    private final Map<Generator, Hologram> holograms = new HashMap<>();

    public HologramManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        Plugin dh = Bukkit.getPluginManager().getPlugin("DecentHolograms");
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("generator-holograms");
        this.enabled = dh != null && section != null;
        if (section != null) {
            this.height = section.getDouble("height", 2.5);
            templates.put(GeneratorType.DIAMOND, ChatColor.translateAlternateColorCodes('&', section.getString("diamond", "&bDiamants dans &f{time}s")));
            templates.put(GeneratorType.EMERALD, ChatColor.translateAlternateColorCodes('&', section.getString("emerald", "&aÉmeraudes dans &f{time}s")));
        } else {
            this.height = 0.0;
        }
    }

    /**
     * Creates a hologram above the given generator.
     */
    public void createGeneratorHologram(Generator gen) {
        if (!enabled || gen.getLocation() == null) {
            return;
        }
        String template = templates.get(gen.getType());
        if (template == null) {
            return;
        }
        Location loc = gen.getLocation().clone().add(0, height, 0);
        Hologram holo = DHAPI.createHologram(UUID.randomUUID().toString(), loc, Collections.singletonList(template.replace("{time}", "-")));
        holograms.put(gen, holo);
    }

    /**
     * Updates the hologram line with the remaining time.
     *
     * @param gen the generator
     * @param seconds time remaining before spawn
     */
    public void updateGenerator(Generator gen, double seconds) {
        if (!enabled) {
            return;
        }
        Hologram holo = holograms.get(gen);
        if (holo == null) {
            return;
        }
        String template = templates.get(gen.getType());
        if (template == null) {
            return;
        }
        String line = template.replace("{time}", String.format("%.1f", seconds));
        DHAPI.setHologramLine(holo, 0, line);
    }

    /**
     * Removes the hologram of the given generator.
     */
    public void removeGeneratorHologram(Generator gen) {
        Hologram holo = holograms.remove(gen);
        if (holo != null) {
            holo.delete();
        }
    }

    /**
     * Deletes all active holograms.
     */
    public void clear() {
        holograms.values().forEach(Hologram::delete);
        holograms.clear();
    }
}

