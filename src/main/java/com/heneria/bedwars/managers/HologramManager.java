package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles creation and updating of holograms above generators.
 * This manager only works if DecentHolograms is present on the server.
 */
public class HologramManager {

    private final HeneriaBedwars plugin;
    private final boolean enabled;
    private final Map<Generator, Hologram> holograms = new HashMap<>();
    private final double height;
    private final String diamondText;
    private final String emeraldText;

    public HologramManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        Plugin dh = Bukkit.getPluginManager().getPlugin("DecentHolograms");
        this.enabled = dh != null;
        this.height = plugin.getConfig().getDouble("generator-holograms.height", 2.5);
        this.diamondText = plugin.getConfig().getString("generator-holograms.diamond", "&bDiamants dans &f%time%s");
        this.emeraldText = plugin.getConfig().getString("generator-holograms.emerald", "&aÉmeraudes dans &f%time%s");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void createGeneratorHologram(Generator gen) {
        if (!enabled || gen.getLocation() == null) {
            return;
        }
        if (gen.getType() != GeneratorType.DIAMOND && gen.getType() != GeneratorType.EMERALD) {
            return;
        }
        Location loc = gen.getLocation().clone().add(0, height, 0);
        String id = "gen-" + gen.hashCode();
        Hologram hologram = DHAPI.createHologram(id, loc);
        DHAPI.addHologramLine(hologram, formatText(gen, 0));
        holograms.put(gen, hologram);
    }

    public void updateGeneratorHologram(Generator gen, int seconds) {
        if (!enabled) {
            return;
        }
        Hologram hologram = holograms.get(gen);
        if (hologram != null) {
            DHAPI.setHologramLine(hologram, 0, formatText(gen, seconds));
        }
    }

    public void removeGeneratorHologram(Generator gen) {
        if (!enabled) {
            return;
        }
        Hologram hologram = holograms.remove(gen);
        if (hologram != null) {
            hologram.delete();
        }
    }

    public void removeAll() {
        if (!enabled) {
            return;
        }
        holograms.values().forEach(Hologram::delete);
        holograms.clear();
    }

    private String formatText(Generator gen, int seconds) {
        String template = gen.getType() == GeneratorType.DIAMOND ? diamondText : emeraldText;
        return template.replace("%time%", String.valueOf(seconds));
    }
}
