package com.flowey258.infinitestoragechests.listeners;

import com.flowey258.infinitestoragechests.InfiniteStorageChests;
import com.flowey258.infinitestoragechests.data.ChestData;
import com.flowey258.infinitestoragechests.gui.ChestGUI;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestListeners implements Listener {

    private final InfiniteStorageChests plugin;
    private final Map<UUID, Long> searchPrompts;

    public ChestListeners(InfiniteStorageChests plugin) {
        this.plugin = plugin;
        this.searchPrompts = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();

        // Check if the item is an infinite chest
        if (plugin.getItemManager().isInfiniteChest(item)) {
            Block block = event.getBlockPlaced();

            // Check if the player has permission
            if (!player.hasPermission("infinitechest.use")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You don't have permission to place Infinite Storage Chests.");
                return;
            }

            // Check maximum chests per player
            int maxChests = plugin.getConfigManager().getMaxChestsPerPlayer();
            int playerChestCount = plugin.getStorageManager().getPlayerChestCount(player.getUniqueId());

            if (playerChestCount >= maxChests && maxChests > 0) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You have reached your limit of " + maxChests + " Infinite Storage Chests.");
                return;
            }

            // Create a new chest data
            plugin.getStorageManager().createChest(block.getLocation(), player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You have placed an Infinite Storage Chest!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();

        // Check if the block is an infinite chest
        if (block.getType() == Material.CHEST && plugin.getStorageManager().isInfiniteChest(block.getLocation())) {
            Player player = event.getPlayer();
            ChestData chestData = plugin.getStorageManager().getChestData(block.getLocation());

            // Check if the player is the owner or has admin permission
            if (!chestData.getOwner().equals(player.getUniqueId()) && !player.hasPermission("infinitechest.admin")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't break someone else's Infinite Storage Chest!");
                return;
            }

            // Check if the chest is empty
            if (chestData.getUniqueItemCount() > 0 && player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't break a chest that contains items. Empty it first!");
                return;
            }

            // Remove the chest data
            plugin.getStorageManager().removeChest(block.getLocation());

            // Drop the infinite chest item if not in creative mode
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.setDropItems(false);
                block.getWorld().dropItemNaturally(block.getLocation(), plugin.getItemManager().createInfiniteChest());
            }

            player.sendMessage(ChatColor.GREEN + "You have broken an Infinite Storage Chest!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        // Check if the block is an infinite chest
        if (block.getType() == Material.CHEST && plugin.getStorageManager().isInfiniteChest(block.getLocation())) {
            event.setCancelled(true);

            // Check if the player has permission
            if (!player.hasPermission("infinitechest.use")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use Infinite Storage Chests.");
                return;
            }

            // Open the chest GUI
            ChestGUI gui = new ChestGUI(plugin, player, block.getLocation());
            gui.open();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ChestGUI gui = ChestGUI.getOpenGui(player);

        if (gui != null && event.getView().getTitle().contains("Infinite Storage Chest")) {
            event.setCancelled(true);

            // Handle click in the chest GUI
            if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                gui.handleClick(event.getRawSlot(), event.isRightClick(), event.isShiftClick());
            }
            // Handle click in player inventory (deposit item)
            else if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                ItemStack clickedItem = event.getCurrentItem().clone();
                int amount;

                // Determine amount to deposit
                if (event.isShiftClick()) {
                    // Deposit all
                    amount = clickedItem.getAmount();
                } else if (event.isRightClick()) {
                    // Deposit half (rounded up)
                    amount = (int) Math.ceil(clickedItem.getAmount() / 2.0);
                } else {
                    // Deposit one
                    amount = 1;
                }

                // Check if the chest has room
                Location chestLocation = gui.getChestLocation();
                int maxItems = plugin.getConfigManager().getMaxUniqueItems();
                int currentItems = plugin.getStorageManager().getUniqueItemCount(chestLocation);

                if (currentItems >= maxItems && plugin.getStorageManager().getItemCount(chestLocation, clickedItem) == 0) {
                    player.sendMessage(ChatColor.RED + "This chest is full! It can only store " + maxItems + " unique items.");
                    return;
                }

                // Remove items from player inventory
                ItemStack toRemove = clickedItem.clone();
                toRemove.setAmount(amount);
                player.getInventory().removeItem(toRemove);

                // Add items to chest
                plugin.getStorageManager().addItem(chestLocation, clickedItem, amount);

                // Update the GUI
                gui.updateInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();

        // Remove from open GUIs
        ChestGUI.removeOpenGui(player);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Check if the player is in search mode
        if (searchPrompts.containsKey(player.getUniqueId())) {
            long timestamp = searchPrompts.get(player.getUniqueId());
            long currentTime = System.currentTimeMillis();

            // Check if the search prompt is still valid (30 seconds)
            if (currentTime - timestamp <= 30000) {
                event.setCancelled(true);

                String message = event.getMessage();

                // Check if the player wants to cancel
                if (message.equalsIgnoreCase("cancel")) {
                    player.sendMessage(ChatColor.YELLOW + "Search cancelled.");
                    searchPrompts.remove(player.getUniqueId());
                    return;
                }

                // Handle the search query
                ChestGUI gui = ChestGUI.getOpenGui(player);
                if (gui != null) {
                    gui.setSearchQuery(message);
                    player.sendMessage(ChatColor.GREEN + "Searching for: " + message);
                }

                searchPrompts.remove(player.getUniqueId());
            }
        }
    }

    public void addSearchPrompt(Player player) {
        searchPrompts.put(player.getUniqueId(), System.currentTimeMillis());
    }
}