package com.heneria.bedwars.arena.enums;

/**
 * Represents the possible states of a BedWars arena.
 */
public enum GameState {
    /** Waiting for players in the arena lobby. */
    WAITING,
    /** Countdown before the game begins. */
    STARTING,
    /** The game is currently running. */
    PLAYING,
    /** The game has finished and winners are displayed. */
    ENDING;
}
