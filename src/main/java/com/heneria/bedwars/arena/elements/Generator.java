package com.heneria.bedwars.arena.elements;

import com.heneria.bedwars.arena.enums.GeneratorType;
import org.bukkit.Location;

/**
 * Represents a resource generator within an arena.
 */
public class Generator {

    private Location location;
    private GeneratorType type;
    private int level;
    private int tier = 1;
    private boolean hologramEnabled = true;

    /**
     * Creates a new generator.
     *
     * @param location the location of the generator
     * @param type     the type of resource generated
     * @param level    the current level of the generator
     */
    public Generator(Location location, GeneratorType type, int level) {
        this.location = location;
        this.type = type;
        this.level = level;
    }

    /**
     * Gets the location of the generator.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location of the generator.
     *
     * @param location the new location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Gets the generator type.
     *
     * @return the generator type
     */
    public GeneratorType getType() {
        return type;
    }

    /**
     * Sets the generator type.
     *
     * @param type the new generator type
     */
    public void setType(GeneratorType type) {
        this.type = type;
    }

    /**
     * Gets the level of the generator.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level of the generator.
     *
     * @param level the new level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the tier of the generator.
     *
     * @return the tier
     */
    public int getTier() {
        return tier;
    }

    /**
     * Sets the tier of the generator.
     *
     * @param tier the new tier
     */
    public void setTier(int tier) {
        this.tier = tier;
    }

    /**
     * Checks whether holograms are enabled for this generator.
     *
     * @return true if holograms should be displayed
     */
    public boolean isHologramEnabled() {
        return hologramEnabled;
    }

    /**
     * Sets whether holograms should be displayed for this generator.
     *
     * @param hologramEnabled flag indicating hologram usage
     */
    public void setHologramEnabled(boolean hologramEnabled) {
        this.hologramEnabled = hologramEnabled;
    }
}
