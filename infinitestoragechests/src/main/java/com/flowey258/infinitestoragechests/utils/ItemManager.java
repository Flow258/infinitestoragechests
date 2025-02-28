package com.flowey258.infinitestoragechests.utils;

import com.flowey258.infinitestoragechests.InfiniteStorageChests;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private final InfiniteStorageChests plugin;
    private final NamespacedKey infiniteChestKey;

    public ItemManager(InfiniteStorageChests plugin) {
        this.plugin = plugin;
        this.infiniteChestKey = new NamespacedKey(plugin, "infinite_chest");
        registerRecipes();
    }

    public ItemStack createInfiniteChest() {
        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();

        // Set display name
        meta.setDisplayName(ChatColor.GOLD + "Infinite Storage Chest");

        // Set lore
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "A chest with infinite storage capacity");
        lore.add(ChatColor.GRAY + "Items can stack infinitely");
        lore.add(ChatColor.GRAY + "Right-click to open");
        meta.setLore(lore);

        // Add persistent data to identify this as an infinite chest
        meta.getPersistentDataContainer().set(infiniteChestKey, PersistentDataType.INTEGER, 1);

        chest.setItemMeta(meta);
        return chest;
    }

    public boolean isInfiniteChest(ItemStack item) {
        if (item == null || item.getType() != Material.CHEST) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(infiniteChestKey, PersistentDataType.INTEGER);
    }

    private void registerRecipes() {
        // Create the recipe for the infinite chest
        ShapedRecipe recipe = new ShapedRecipe(infiniteChestKey, createInfiniteChest());

        // Set the shape
        recipe.shape("OOO", "ECE", "OEO");

        // Set the ingredients
        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('E', Material.ENDER_PEARL);
        recipe.setIngredient('C', Material.CHEST);

        // Register the recipe
        plugin.getServer().addRecipe(recipe);
    }

    public NamespacedKey getInfiniteChestKey() {
        return infiniteChestKey;
    }
}