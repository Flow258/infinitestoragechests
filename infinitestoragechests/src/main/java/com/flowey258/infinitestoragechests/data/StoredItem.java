package com.flowey258.infinitestoragechests.data;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class StoredItem {

    private final ItemStack itemStack;

    public StoredItem(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        // Set amount to 1 for comparison purposes
        this.itemStack.setAmount(1);
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StoredItem that = (StoredItem) obj;
        return itemStack.isSimilar(that.itemStack);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(itemStack.getType());

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            hash = 31 * hash + (meta.hasDisplayName() ? meta.getDisplayName().hashCode() : 0);
            hash = 31 * hash + (meta.hasLore() ? meta.getLore().hashCode() : 0);
            hash = 31 * hash + (meta.hasEnchants() ? meta.getEnchants().hashCode() : 0);
        }

        return hash;
    }
}