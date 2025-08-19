package com.heneria.bedwars.events;

/**
 * Types of timed game events that can occur during a match.
 */
public enum GameEventType {
    /**
     * Upgrade the tier of specific generators in the arena.
     */
    UPGRADE_GENERATORS,

    /**
     * Destroy all remaining beds to trigger sudden death.
     */
    SUDDEN_DEATH,

    /**
     * Spawn one or more Ender Dragons to pressure players.
     */
    SPAWN_DRAGONS,
    /**
     * Spawn the special mid-game merchant NPC.
     */
    SPAWN_SPECIAL_NPC,

    /**
     * Remove the special mid-game merchant NPC.
     */
    DESPAWN_SPECIAL_NPC
}

