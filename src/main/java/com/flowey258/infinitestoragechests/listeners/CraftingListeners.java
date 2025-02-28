package com.flowey258.infinitestoragechests.listeners;

import com.flowey258.infinitestoragechests.InfiniteStorageChests;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingListeners implements Listener {

    private final InfiniteStorageChests plugin;

    public CraftingListeners(InfiniteStorageChests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        Recipe recipe = event.getRecipe();

        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            NamespacedKey key = shapedRecipe.getKey();

            // Check if this is our infinite chest recipe
            if (key.equals(plugin.getItemManager().getInfiniteChestKey())) {
                // Check if the player has permission
                if (event.getView().getPlayer() instanceof Player) {
                    Player player = (Player) event.getView().getPlayer();

                    if (!player.hasPermission("infinitechest.craft")) {
                        inventory.setResult(null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            NamespacedKey key = shapedRecipe.getKey();

            // Check if this is our infinite chest recipe
            if (key.equals(plugin.getItemManager().getInfiniteChestKey())) {
                Player player = (Player) event.getWhoClicked();

                // Check if the player has permission
                if (!player.hasPermission("infinitechest.craft")) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You don't have permission to craft Infinite Storage Chests.");
                    return;
                }

                player.sendMessage(ChatColor.GREEN + "You have crafted an Infinite Storage Chest!");
            }
        }
    }
}