package com.heneria.bedwars.commands;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.commands.subcommands.AdminCommand;
import com.heneria.bedwars.commands.subcommands.JoinCommand;
import com.heneria.bedwars.commands.subcommands.LeaveCommand;
import com.heneria.bedwars.commands.subcommands.StatsCommand;
import com.heneria.bedwars.commands.subcommands.SubCommand;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Central command manager handling the main "/bedwars" command and its sub-commands.
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final HeneriaBedwars plugin;

    /**
     * Creates a new command manager.
     *
     * @param plugin the plugin instance
     */
    public CommandManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        registerSubCommand(new AdminCommand());
        registerSubCommand(new JoinCommand());
        registerSubCommand(new LeaveCommand());
        registerSubCommand(new StatsCommand());
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

        String name = command.getName().toLowerCase();
        if (name.equals("spawn")) {
            handleSpawn(player);
            return true;
        }
        if (name.equals("hub")) {
            handleHub(player);
            return true;
        }

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

    private void handleSpawn(Player player) {
        ArenaManager manager = plugin.getArenaManager();
        Arena arena = manager.getArenaByPlayer(player.getUniqueId());
        if (arena != null) {
            if (arena.getState() == GameState.PLAYING) {
                MessageManager.sendMessage(player, "errors.command-disabled-in-game");
                return;
            }
            arena.removePlayer(player);
            return;
        }
        Location lobby = plugin.getMainLobby();
        if (lobby == null) {
            MessageManager.sendMessage(player, "errors.lobby-not-set");
            return;
        }
        player.teleport(lobby);
    }

    private void handleHub(Player player) {
        if (!plugin.getConfig().getBoolean("bungeecord.enabled")) {
            handleSpawn(player);
            return;
        }

        ArenaManager manager = plugin.getArenaManager();
        Arena arena = manager.getArenaByPlayer(player.getUniqueId());
        if (arena != null) {
            if (arena.getState() == GameState.PLAYING) {
                MessageManager.sendMessage(player, "errors.command-disabled-in-game");
                return;
            }
            arena.removePlayer(player);
        }

        String server = plugin.getConfig().getString("bungeecord.lobby-server-name", "lobby");
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(b)) {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not connect player to hub server: " + e.getMessage());
            return;
        }
        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
}
