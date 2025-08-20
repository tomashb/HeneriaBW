package com.heneria.bedwars.setup;

import org.bukkit.Material;

/**
 * Holds the temporary data while an admin goes through the NPC creation wizard.
 */
public class NpcCreationProcess {

    public enum Step {
        SKIN,
        MODE,
        NAME,
        ITEM,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        WAITING_CONFIRM
    }

    private Step step = Step.SKIN;
    private String skin;
    private String mode;
    private String name;
    private Material item;
    private Material chestplate;
    private Material leggings;
    private Material boots;

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getItem() {
        return item;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public Material getChestplate() {
        return chestplate;
    }

    public void setChestplate(Material chestplate) {
        this.chestplate = chestplate;
    }

    public Material getLeggings() {
        return leggings;
    }

    public void setLeggings(Material leggings) {
        this.leggings = leggings;
    }

    public Material getBoots() {
        return boots;
    }

    public void setBoots(Material boots) {
        this.boots = boots;
    }
}

