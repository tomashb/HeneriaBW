package com.heneria.bedwars.events;

import com.heneria.bedwars.arena.enums.GeneratorType;

import java.util.List;

/**
 * Represents a scheduled event in the game's timeline.
 */
public class TimedEvent {

    private final int time; // in seconds
    private final GameEventType type;
    private final List<GeneratorType> targets;
    private final int newTier;
    private final String message;

    public TimedEvent(int time, GameEventType type, List<GeneratorType> targets, int newTier, String message) {
        this.time = time;
        this.type = type;
        this.targets = targets;
        this.newTier = newTier;
        this.message = message;
    }

    public int getTime() {
        return time;
    }

    public GameEventType getType() {
        return type;
    }

    public List<GeneratorType> getTargets() {
        return targets;
    }

    public int getNewTier() {
        return newTier;
    }

    public String getMessage() {
        return message;
    }
}

