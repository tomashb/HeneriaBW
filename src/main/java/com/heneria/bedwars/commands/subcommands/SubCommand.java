package com.heneria.bedwars.commands.subcommands;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a sub-command of the main BedWars command.
 */
public interface SubCommand {

    /**
     * Gets the name of the sub-command.
     *
     * @return the sub-command name
     */
    String getName();

    /**
     * Executes the sub-command for the given player.
     *
     * @param player the player executing the command
     * @param args   additional arguments
     */
    void execute(Player player, String[] args);

    /**
     * Provides tab completion for this sub-command.
     *
     * @param player the player
     * @param args   current arguments
     * @return list of tab completions
     */
    List<String> tabComplete(Player player, String[] args);
}
