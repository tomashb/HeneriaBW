package com.heneria.bedwars.commands;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.commands.subcommands.AdminCommand;
import com.heneria.bedwars.commands.subcommands.JoinCommand;
import com.heneria.bedwars.commands.subcommands.LeaveCommand;
import com.heneria.bedwars.commands.subcommands.SubCommand;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Central command manager handling the main "/bedwars" command and its sub-commands.
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    /**
     * Creates a new command manager.
     *
     * @param plugin the plugin instance
     */
    public CommandManager(HeneriaBedwars plugin) {
        registerSubCommand(new AdminCommand());
        registerSubCommand(new JoinCommand());
        registerSubCommand(new LeaveCommand());
    }

    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender, "errors.not-a-player");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            MessageManager.sendMessage(player, "commands.main-usage", "label", label);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            subCommand.execute(player, Arrays.copyOfRange(args, 1, args.length));
        } else {
            MessageManager.sendMessage(player, "errors.unknown-subcommand");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String name : subCommands.keySet()) {
                if (name.startsWith(args[0].toLowerCase())) {
                    completions.add(name);
                }
            }
            return completions;
        }

        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            return subCommand.tabComplete((Player) sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return Collections.emptyList();
    }
}
