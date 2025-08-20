package com.heneria.bedwars.managers;

import com.heneria.bedwars.setup.SetupAction;
import com.heneria.bedwars.setup.NpcCreationProcess;
import org.bukkit.ChatColor;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks players who are in setup mode and what action they are performing.
 */
public class SetupManager {

    private final Map<UUID, SetupAction> setups = new HashMap<>();
    private final Map<UUID, NpcCreationProcess> npcCreations = new HashMap<>();

    /**
     * Puts a player in setup mode with the given action and gives them the setup tool.
     *
     * @param player the player
     * @param action the setup action
     */
    public void startSetup(Player player, SetupAction action) {
        setups.put(player.getUniqueId(), action);
        player.getInventory().addItem(new ItemBuilder(Material.BLAZE_ROD)
                .setName("Outil de Positionnement")
                .build());
    }

    /**
     * Gets the current setup action for a player.
     *
     * @param uuid player uuid
     * @return setup action or null
     */
    public SetupAction getSetupAction(UUID uuid) {
        return setups.get(uuid);
    }

    public NpcCreationProcess getNpcCreation(UUID uuid) {
        return npcCreations.get(uuid);
    }

    /**
     * Removes a player from setup mode.
     *
     * @param player the player
     */
    public void clear(Player player) {
        setups.remove(player.getUniqueId());
    }

    public void clearNpcCreation(Player player) {
        npcCreations.remove(player.getUniqueId());
    }

    public void startNpcCreation(Player player) {
        npcCreations.put(player.getUniqueId(), new NpcCreationProcess());
        player.sendMessage(ChatColor.YELLOW + "Quel skin voulez-vous lui donner ?");
    }
}
