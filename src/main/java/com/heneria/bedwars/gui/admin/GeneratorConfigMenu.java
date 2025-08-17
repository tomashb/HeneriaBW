package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.PositionTool;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Menu to add or remove resource generators.
 */
public class GeneratorConfigMenu extends Menu {

    private final Arena arena;

    public GeneratorConfigMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return "Générateurs";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void setupItems() {
        inventory.setItem(0, new ItemBuilder(Material.IRON_INGOT).setName("Ajouter un générateur de Fer").build());
        inventory.setItem(1, new ItemBuilder(Material.GOLD_INGOT).setName("Ajouter un générateur d'Or").build());
        inventory.setItem(2, new ItemBuilder(Material.DIAMOND).setName("Ajouter un générateur de Diamant").build());
        inventory.setItem(3, new ItemBuilder(Material.EMERALD).setName("Ajouter un générateur d'Émeraude").build());

        int slot = 9;
        List<Generator> gens = arena.getGenerators();
        for (Generator gen : gens) {
            ItemStack item = new ItemBuilder(Material.DROPPER)
                    .setName(gen.getType().name())
                    .addLore("Clic pour supprimer")
                    .build();
            inventory.setItem(slot++, item);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == 0) {
            addGenerator(player, GeneratorType.IRON);
        } else if (slot == 1) {
            addGenerator(player, GeneratorType.GOLD);
        } else if (slot == 2) {
            addGenerator(player, GeneratorType.DIAMOND);
        } else if (slot == 3) {
            addGenerator(player, GeneratorType.EMERALD);
        } else if (slot >= 9) {
            int index = slot - 9;
            if (index < arena.getGenerators().size()) {
                arena.getGenerators().remove(index);
                HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
                setupItems();
                player.updateInventory();
            }
        }
    }

    private void addGenerator(Player player, GeneratorType type) {
        player.closeInventory();
        PositionTool.request(player, loc -> {
            arena.getGenerators().add(new Generator(loc, type, 1));
            HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
            player.sendMessage("Générateur ajouté.");
        });
    }
}
