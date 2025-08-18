package com.heneria.bedwars.events;

import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Arena arena;
    private final GameState newState;

    public GameStateChangeEvent(Arena arena, GameState newState) {
        this.arena = arena;
        this.newState = newState;
    }

    public Arena getArena() {
        return arena;
    }

    public GameState getNewState() {
        return newState;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
