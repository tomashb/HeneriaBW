package com.heneria.bedwars.gui.admin.creation;

/**
 * Holds temporary arena creation data for an administrator during the wizard process.
 */
public class ArenaCreationWizard {

    private final String name;
    private int playersPerTeam = 1;
    private int teamCount = 2;

    public ArenaCreationWizard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPlayersPerTeam() {
        return playersPerTeam;
    }

    public void setPlayersPerTeam(int playersPerTeam) {
        this.playersPerTeam = playersPerTeam;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }
}
