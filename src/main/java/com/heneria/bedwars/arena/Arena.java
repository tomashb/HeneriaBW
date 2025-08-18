package com.heneria.bedwars.arena;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.utils.GameUtils;
import com.heneria.bedwars.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Represents a BedWars arena with its configuration and runtime state.
 */
public class Arena {

    private final String name;
    private GameState state = GameState.WAITING;
    private String worldName;
    private boolean enabled;
    private int minPlayers;
    private int maxPlayers;
    private final List<UUID> players = new ArrayList<>();
    private final List<UUID> alivePlayers = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final Map<TeamColor, Team> teams = new EnumMap<>(TeamColor.class);
    private final List<Generator> generators = new ArrayList<>();
    private Location lobbyLocation;
    private Location shopNpcLocation;
    private Location upgradeNpcLocation;
    private final Map<UUID, PlayerData> savedStates = new HashMap<>();
    private BukkitTask countdownTask;
    private int countdownDuration = 10;

    /**
     * Creates a new arena with the given name.
     *
     * @param name the arena name
     */
    public Arena(String name) {
        this.name = name;
    }

    /**
     * Gets the arena name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current state of the arena.
     *
     * @return the game state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets the current state of the arena.
     *
     * @param state the new state
     */
    public void setState(GameState state) {
        this.state = state;
    }

    /**
     * Checks whether the arena is enabled for play.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the arena is enabled for play.
     *
     * @param enabled new enabled state
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the name of the world used by the arena.
     *
     * @return the world name
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Sets the world used by the arena.
     *
     * @param worldName the world name
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /**
     * Gets the minimum number of players required to start the arena.
     *
     * @return minimum player count
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * Sets the minimum number of players required.
     *
     * @param minPlayers minimum player count
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    /**
     * Gets the maximum number of players allowed in the arena.
     *
     * @return maximum player count
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Sets the maximum number of players allowed in the arena.
     *
     * @param maxPlayers maximum player count
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Gets the list of players currently in the arena.
     *
     * @return list of players
     */
    public List<UUID> getPlayers() {
        return players;
    }

    /**
     * Adds a player to the arena.
     *
     * @param uuid player's unique id
     */
    public void addPlayer(UUID uuid) {
        players.add(uuid);
        alivePlayers.add(uuid);
    }

    /**
     * Removes a player from the arena.
     *
     * @param uuid player's unique id
     */
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
        alivePlayers.remove(uuid);
        spectators.remove(uuid);
    }

    /**
     * Adds a player to this arena and moves them to the lobby.
     *
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        addPlayer(player.getUniqueId());
        savedStates.put(player.getUniqueId(), new PlayerData(player));
        player.getInventory().clear();
        player.teleport(lobbyLocation);
        player.setLevel(0);
        player.setExp(0f);
        Team team = getLeastPopulatedTeam();
        if (team != null) {
            team.addMember(player.getUniqueId());
            MessageUtils.sendMessage(player, "&aVous avez rejoint l'équipe " + team.getColor().getDisplayName() + "");
        }
        broadcast("&e" + player.getName() + " a rejoint l'arène. (&b" + players.size() + "&e/" + maxPlayers + ")");
        if (players.size() >= minPlayers && state == GameState.WAITING) {
            startCountdown();
        }
    }

    /**
     * Removes a player from this arena and restores their previous state.
     *
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
        Team team = getTeam(player.getUniqueId());
        if (team != null) {
            team.removeMember(player.getUniqueId());
        }
        PlayerData data = savedStates.remove(player.getUniqueId());
        if (data != null) {
            data.restore(player);
        }
        player.setLevel(0);
        player.setExp(0f);
        broadcast("&c" + player.getName() + " a quitté l'arène.");
        if (state == GameState.STARTING && players.size() < minPlayers) {
            cancelCountdown();
        }
    }

    public Team getTeam(UUID uuid) {
        for (Team team : teams.values()) {
            if (team.isMember(uuid)) {
                return team;
            }
        }
        return null;
    }

    public Team getTeam(Player player) {
        return getTeam(player.getUniqueId());
    }

    /**
     * Récupère une équipe à partir de l'emplacement d'un bloc de lit, en utilisant une vérification de distance.
     * @param location L'emplacement du bloc de lit cassé.
     * @return L'équipe propriétaire du lit, ou null si aucune équipe ne correspond.
     */
    public Team getTeamFromBedLocation(Location location) {
        for (Team team : teams.values()) {
            Location bedLocation = team.getBedLocation();

            // On s'assure que le lit de l'équipe a bien été configuré
            if (bedLocation == null) continue;

            // On vérifie qu'on est bien dans le même monde
            if (!bedLocation.getWorld().equals(location.getWorld())) continue;

            // LA CORRECTION : On vérifie si le bloc cassé est à proximité (distance de 2 blocs max)
            if (bedLocation.distanceSquared(location) <= 4) { // distanceSquared est plus performant
                return team; // Correspondance trouvée !
            }
        }
        return null; // Aucune correspondance
    }

    private Team getLeastPopulatedTeam() {
        Team result = null;
        int count = Integer.MAX_VALUE;
        for (Team team : teams.values()) {
            if (team.getMembers().size() < count) {
                count = team.getMembers().size();
                result = team;
            }
        }
        return result;
    }

    public void broadcast(String message) {
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                MessageUtils.sendMessage(p, message);
            }
        }
    }

    public void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        }
    }

    public void eliminatePlayer(Player player) {
        removeAlivePlayer(player.getUniqueId());
        addSpectator(player.getUniqueId());
        broadcast("&c" + player.getName() + " a été éliminé !");
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
    }

    public void removeSpectator(UUID uuid) {
        spectators.remove(uuid);
    }

    public void addAlivePlayer(UUID uuid) {
        alivePlayers.add(uuid);
    }

    public void removeAlivePlayer(UUID uuid) {
        alivePlayers.remove(uuid);
    }

    private void startCountdown() {
        state = GameState.STARTING;
        final int total = countdownDuration;
        countdownTask = new BukkitRunnable() {
            int time = total;

            @Override
            public void run() {
                if (time <= 0) {
                    cancel();
                    startGame();
                    return;
                }
                broadcast("&eLa partie commence dans &6" + time + "s");
                for (UUID id : players) {
                    Player p = Bukkit.getPlayer(id);
                    if (p != null) {
                        p.setLevel(time);
                        p.setExp((float) time / total);
                        p.sendTitle("", "§eDémarrage dans " + time + "s", 0, 20, 0);
                    }
                }
                time--;
            }
        }.runTaskTimer(HeneriaBedwars.getInstance(), 0L, 20L);
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        state = GameState.WAITING;
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                p.setExp(0f);
                p.setLevel(0);
            }
        }
        broadcast("&cDécompte annulé : pas assez de joueurs.");
    }

    /**
     * Starts the game for all players currently in the arena.
     */
    public void startGame() {
        state = GameState.PLAYING;
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p == null) {
                continue;
            }
            Team team = getTeam(id);
            if (team != null && team.getSpawnLocation() != null) {
                p.teleport(team.getSpawnLocation());
            }
            GameUtils.giveDefaultKit(p);
        }
        for (Generator gen : generators) {
            HeneriaBedwars.getInstance().getGeneratorManager().registerGenerator(gen);
        }
    }

    /**
     * Gets the teams registered in the arena.
     *
     * @return map of teams by color
     */
    public Map<TeamColor, Team> getTeams() {
        return teams;
    }

    /**
     * Gets the generators available in the arena.
     *
     * @return list of generators
     */
    public List<Generator> getGenerators() {
        return generators;
    }

    /**
     * Gets the lobby spawn location of the arena.
     *
     * @return lobby location
     */
    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    /**
     * Sets the lobby spawn location of the arena.
     *
     * @param lobbyLocation lobby location
     */
    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    /**
     * Gets the shop NPC location.
     *
     * @return location or null
     */
    public Location getShopNpcLocation() {
        return shopNpcLocation;
    }

    /**
     * Sets the shop NPC location.
     *
     * @param shopNpcLocation location
     */
    public void setShopNpcLocation(Location shopNpcLocation) {
        this.shopNpcLocation = shopNpcLocation;
    }

    /**
     * Gets the upgrade NPC location.
     *
     * @return location or null
     */
    public Location getUpgradeNpcLocation() {
        return upgradeNpcLocation;
    }

    /**
     * Sets the upgrade NPC location.
     *
     * @param upgradeNpcLocation location
     */
    public void setUpgradeNpcLocation(Location upgradeNpcLocation) {
        this.upgradeNpcLocation = upgradeNpcLocation;
    }

    public Team checkForWinner() {
        Set<Team> remaining = new HashSet<>();
        for (Team team : teams.values()) {
            for (UUID member : team.getMembers()) {
                if (alivePlayers.contains(member)) {
                    remaining.add(team);
                    break;
                }
            }
        }
        return remaining.size() == 1 ? remaining.iterator().next() : null;
    }

    public void endGame(Team winner) {
        if (state != GameState.PLAYING) {
            return;
        }
        state = GameState.ENDING;
        broadcast("&aL'équipe " + winner.getColor().getDisplayName() + " remporte la partie !");
        broadcastTitle("§aVictoire !", "§fÉquipe " + winner.getColor().getChatColor() + winner.getColor().getDisplayName() + "§f", 10, 70, 20);
        for (Generator gen : generators) {
            HeneriaBedwars.getInstance().getGeneratorManager().unregisterGenerator(gen);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskLater(HeneriaBedwars.getInstance(), 200L);
    }

    public void reset() {
        for (UUID id : new ArrayList<>(players)) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                PlayerData data = savedStates.remove(id);
                if (data != null) {
                    data.restore(p);
                }
            }
        }
        players.clear();
        alivePlayers.clear();
        spectators.clear();
        savedStates.clear();
        for (Team team : teams.values()) {
            team.getMembers().clear();
            team.setHasBed(true);
        }
        state = GameState.WAITING;
    }
}
