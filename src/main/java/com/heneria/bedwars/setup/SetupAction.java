package com.heneria.bedwars.setup;

import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.arena.enums.GeneratorType;

/**
 * Represents a setup action for a player configuring an arena.
 */
public class SetupAction {

    private final Arena arena;
    private final SetupType type;
    private final TeamColor teamColor;
    private final GeneratorType generatorType;

    public SetupAction(Arena arena, SetupType type) {
        this(arena, type, null, null);
    }

    public SetupAction(Arena arena, SetupType type, TeamColor teamColor) {
        this(arena, type, teamColor, null);
    }

    public SetupAction(Arena arena, SetupType type, GeneratorType generatorType) {
        this(arena, type, null, generatorType);
    }

    private SetupAction(Arena arena, SetupType type, TeamColor teamColor, GeneratorType generatorType) {
        this.arena = arena;
        this.type = type;
        this.teamColor = teamColor;
        this.generatorType = generatorType;
    }

    public Arena getArena() {
        return arena;
    }

    public SetupType getType() {
        return type;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public GeneratorType getGeneratorType() {
        return generatorType;
    }
}
