package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages holograms displayed above generators using DecentHolograms.
 */
public class HologramManager {

    private final HeneriaBedwars plugin;
    private final boolean enabled;
    private final Map<Generator, Hologram> holograms = new HashMap<>();
    private String diamondText;
    private String emeraldText;
    private double diamondHeight;
    private double emeraldHeight;

    public HologramManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
        loadConfig();
    }

    private void loadConfig() {
        this.diamondText = plugin.getConfig().getString("generator-holograms.diamond.text", "&bDiamant dans &f{time}");
        this.emeraldText = plugin.getConfig().getString("generator-holograms.emerald.text", "&aÉmeraude dans &f{time}");
        this.diamondHeight = plugin.getConfig().getDouble("generator-holograms.diamond.height", 2.5);
        this.emeraldHeight = plugin.getConfig().getDouble("generator-holograms.emerald.height", 2.5);
    }

    public void create(Generator gen, int seconds) {
        if (!enabled || gen.getLocation() == null) return;
        if (gen.getType() != GeneratorType.DIAMOND && gen.getType() != GeneratorType.EMERALD) return;
        Location loc = gen.getLocation().clone().add(0, getHeight(gen), 0);
        String id = "hbw-" + UUID.randomUUID();
        Hologram hologram = DHAPI.createHologram(id, loc);
        DHAPI.addHologramLine(hologram, format(gen, seconds));
        holograms.put(gen, hologram);
    }

    public void update(Generator gen, int seconds) {
        if (!enabled) return;
        Hologram hologram = holograms.get(gen);
        if (hologram == null) return;
        DHAPI.setHologramLine(hologram, 0, format(gen, seconds));
    }

    public void remove(Generator gen) {
        if (!enabled) return;
        Hologram hologram = holograms.remove(gen);
        if (hologram != null) {
            hologram.delete();
        }
    }

    public void removeAll() {
        if (!enabled) return;
        for (Hologram hologram : holograms.values()) {
            hologram.delete();
        }
        holograms.clear();
    }

    private String format(Generator gen, int seconds) {
        String base = gen.getType() == GeneratorType.DIAMOND ? diamondText : emeraldText;
        return ChatColor.translateAlternateColorCodes('&', base.replace("{time}", String.valueOf(seconds)));
    }

    private double getHeight(Generator gen) {
        return gen.getType() == GeneratorType.DIAMOND ? diamondHeight : emeraldHeight;
    }
}
