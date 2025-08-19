package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles creation and updating of generator holograms using DecentHolograms.
 */
public class HologramManager {

    private final HeneriaBedwars plugin;
    private final boolean enabled;
    private final Map<Generator, Hologram> holograms = new HashMap<>();

    public HologramManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        this.enabled = Bukkit.getPluginManager().getPlugin("DecentHolograms") != null;
        if (!enabled) {
            plugin.getLogger().info("DecentHolograms not found. Generator holograms disabled.");
        }
    }

    public void createGeneratorHologram(Generator gen) {
        if (!enabled || gen.getLocation() == null) return;
        if (gen.getType() != GeneratorType.DIAMOND && gen.getType() != GeneratorType.EMERALD) return;

        Location loc = gen.getLocation().clone().add(0, 2.5, 0);
        String template = plugin.getConfig().getString("generator-holograms." + gen.getType().name().toLowerCase(), "{time}");
        Hologram hologram = DHAPI.createHologram("hbw_gen_" + gen.hashCode(), loc);
        DHAPI.addHologramLine(hologram, ChatColor.translateAlternateColorCodes('&', template.replace("{time}", String.valueOf(plugin.getGeneratorManager().getRemainingSeconds(gen)))));
        holograms.put(gen, hologram);
    }

    public void updateGeneratorCountdown(Generator gen, int seconds) {
        if (!enabled) return;
        Hologram hologram = holograms.get(gen);
        if (hologram == null) return;
        String template = plugin.getConfig().getString("generator-holograms." + gen.getType().name().toLowerCase(), "{time}");
        DHAPI.setHologramLine(hologram, 0, ChatColor.translateAlternateColorCodes('&', template.replace("{time}", String.valueOf(seconds))));
    }

    public void removeGeneratorHolograms(Arena arena) {
        if (!enabled) return;
        for (Generator gen : arena.getGenerators()) {
            Hologram hologram = holograms.remove(gen);
            if (hologram != null) {
                hologram.delete();
            }
        }
    }
}
