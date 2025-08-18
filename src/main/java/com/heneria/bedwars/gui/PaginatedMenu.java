package com.heneria.bedwars.gui;

import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Base menu supporting pagination.
 */
public abstract class PaginatedMenu extends Menu {

    protected int page = 0;

    protected ItemStack filler() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
    }

    protected ItemStack previousButton() {
        return new ItemBuilder(Material.ARROW).setName("&ePage précédente").build();
    }

    protected ItemStack nextButton() {
        return new ItemBuilder(Material.ARROW).setName("&ePage suivante").build();
    }

    protected int getPrevButtonSlot() {
        return 0;
    }

    protected int getNextButtonSlot() {
        return getSize() - 1;
    }

    protected abstract int getItemStartSlot();

    protected abstract int getItemsPerPage();

    protected abstract List<ItemStack> getPaginatedItems();

    protected abstract void handleMenuClick(InventoryClickEvent event);

    protected void onItemSet(int index, int slot) {
    }

    @Override
    public void setupItems() {
        inventory.clear();
        List<ItemStack> items = getPaginatedItems();
        int start = page * getItemsPerPage();
        for (int i = 0; i < getItemsPerPage(); i++) {
            int index = start + i;
            if (index >= items.size()) break;
            int slot = getItemStartSlot() + i;
            inventory.setItem(slot, items.get(index));
            onItemSet(index, slot);
        }
        if (page > 0) {
            inventory.setItem(getPrevButtonSlot(), previousButton());
        }
        if ((page + 1) * getItemsPerPage() < items.size()) {
            inventory.setItem(getNextButtonSlot(), nextButton());
        }
        for (int i = 0; i < getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler());
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (handleBack(event)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == getPrevButtonSlot() && page > 0) {
            page--;
            setupItems();
            if (previousMenu != null) {
                inventory.setItem(getBackButtonSlot(), backButton());
            }
            return;
        }
        if (slot == getNextButtonSlot() && (page + 1) * getItemsPerPage() < getPaginatedItems().size()) {
            page++;
            setupItems();
            if (previousMenu != null) {
                inventory.setItem(getBackButtonSlot(), backButton());
            }
            return;
        }
        handleMenuClick(event);
    }
}
