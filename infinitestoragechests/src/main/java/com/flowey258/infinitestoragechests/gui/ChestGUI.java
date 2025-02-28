package com.flowey258.infinitestoragechests.gui;

import com.flowey258.infinitestoragechests.InfiniteStorageChests;
import com.flowey258.infinitestoragechests.data.ChestData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestGUI {

    private final InfiniteStorageChests plugin;
    private final Player player;
    private final Location chestLocation;
    private final Map<Integer, ItemStack> displayedItems;
    private Inventory inventory;
    private int currentPage;
    private boolean isSortedByName;
    private String searchQuery;

    // For tracking open GUIs
    private static final Map<Player, ChestGUI> openGuis = new HashMap<>();

    public ChestGUI(InfiniteStorageChests plugin, Player player, Location chestLocation) {
        this.plugin = plugin;
        this.player = player;
        this.chestLocation = chestLocation;
        this.displayedItems = new HashMap<>();
        this.currentPage = 0;
        this.isSortedByName = true;
        this.searchQuery = "";

        createInventory();
        openGuis.put(player, this);
    }

    private void createInventory() {
        String title = ChatColor.GOLD + "Infinite Storage Chest";
        inventory = Bukkit.createInventory(null, 54, title); // 6 rows of 9 slots

        updateInventory();
    }

    public void open() {
        player.openInventory(inventory);
    }

    public void updateInventory() {
        inventory.clear();
        displayedItems.clear();

        // Get chest data
        ChestData chestData = plugin.getStorageManager().getChestData(chestLocation);
        if (chestData == null) {
            player.closeInventory();
            return;
        }

        // Get all items
        Map<ItemStack, Integer> allItems = chestData.getAllItems();
        List<Map.Entry<ItemStack, Integer>> sortedItems = new ArrayList<>(allItems.entrySet());

        // Sort items
        if (isSortedByName) {
            sortedItems.sort((a, b) -> a.getKey().getType().name().compareTo(b.getKey().getType().name()));
        } else {
            sortedItems.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // Sort by quantity descending
        }

        // Apply search filter
        if (!searchQuery.isEmpty()) {
            sortedItems.removeIf(entry -> !entry.getKey().getType().name().toLowerCase().contains(searchQuery.toLowerCase()));
        }

        // Calculate pagination
        int itemsPerPage = 45; // 6 rows - 1 row for controls = 5 rows of items = 45 slots
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, sortedItems.size());

        // Add items to inventory
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<ItemStack, Integer> entry = sortedItems.get(i);
            ItemStack item = entry.getKey().clone();
            int amount = entry.getValue();

            // Create display item with lore showing the actual amount
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Stored: " + ChatColor.WHITE + amount);
            lore.add(ChatColor.GRAY + "Left-click to take a stack");
            lore.add(ChatColor.GRAY + "Right-click to take one");
            lore.add(ChatColor.GRAY + "Shift+click to take all");
            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            displayedItems.put(slot, entry.getKey().clone());
            slot++;
        }

        // Add control buttons in the bottom row
        addControlButtons(sortedItems.size(), itemsPerPage);
    }

    private void addControlButtons(int totalItems, int itemsPerPage) {
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        int bottomRow = 45; // Starting index of the bottom row

        // Previous page button
        if (currentPage > 0) {
            inventory.setItem(bottomRow + 3, createButton(Material.ARROW, ChatColor.YELLOW + "Previous Page"));
        }

        // Page indicator
        inventory.setItem(bottomRow + 4, createButton(Material.PAPER,
                ChatColor.YELLOW + "Page " + (currentPage + 1) + " of " + Math.max(1, totalPages)));

        // Next page button
        if (currentPage < totalPages - 1) {
            inventory.setItem(bottomRow + 5, createButton(Material.ARROW, ChatColor.YELLOW + "Next Page"));
        }

        // Sort toggle button
        String sortText = isSortedByName ? "Sort by Quantity" : "Sort by Name";
        inventory.setItem(bottomRow + 1, createButton(Material.HOPPER, ChatColor.YELLOW + sortText));

        // Search button
        inventory.setItem(bottomRow + 7, createButton(Material.COMPASS,
                ChatColor.YELLOW + "Search" + (searchQuery.isEmpty() ? "" : ": " + searchQuery)));
    }

    private ItemStack createButton(Material material, String name) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(name);
        button.setItemMeta(meta);
        return button;
    }

    public void handleClick(int slot, boolean isRightClick, boolean isShiftClick) {
        // Handle control buttons
        if (slot >= 45) {
            handleControlButtonClick(slot, isRightClick);
            return;
        }

        // Handle item clicks
        if (displayedItems.containsKey(slot)) {
            ItemStack clickedItem = displayedItems.get(slot);
            ChestData chestData = plugin.getStorageManager().getChestData(chestLocation);

            if (chestData == null) {
                player.closeInventory();
                return;
            }

            int amount;
            if (isShiftClick) {
                // Take all
                amount = chestData.getItemCount(clickedItem);
            } else if (isRightClick) {
                // Take one
                amount = 1;
            } else {
                // Take a stack
                amount = Math.min(clickedItem.getMaxStackSize(), chestData.getItemCount(clickedItem));
            }

            // Create the item to give
            ItemStack itemToGive = clickedItem.clone();
            itemToGive.setAmount(Math.min(amount, itemToGive.getMaxStackSize()));

            // Remove item from chest and give to player
            plugin.getStorageManager().removeItem(chestLocation, clickedItem, amount);

            // Add items to player inventory, handling stacks
            int remaining = amount;
            while (remaining > 0) {
                ItemStack stack = clickedItem.clone();
                int stackSize = Math.min(remaining, stack.getMaxStackSize());
                stack.setAmount(stackSize);

                HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(stack);
                if (notAdded.isEmpty()) {
                    remaining -= stackSize;
                } else {
                    // Add back items that couldn't be added
                    int notAddedAmount = notAdded.values().stream().mapToInt(ItemStack::getAmount).sum();
                    plugin.getStorageManager().addItem(chestLocation, clickedItem, notAddedAmount);
                    player.sendMessage(ChatColor.RED + "Your inventory is full!");
                    break;
                }
            }

            // Update the GUI
            updateInventory();
        }
    }

    private void handleControlButtonClick(int slot, boolean isRightClick) {
        int bottomRow = 45;

        // Previous page
        if (slot == bottomRow + 3 && currentPage > 0) {
            currentPage--;
            updateInventory();
        }

        // Next page
        if (slot == bottomRow + 5) {
            currentPage++;
            updateInventory();
        }

        // Sort toggle
        if (slot == bottomRow + 1) {
            isSortedByName = !isSortedByName;
            updateInventory();
        }

        // Search
        if (slot == bottomRow + 7) {
            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + "Type the search term in chat or 'cancel' to cancel:");
            // The search input will be handled by a chat listener
        }
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        this.currentPage = 0; // Reset to first page when searching
        updateInventory();
        open();
    }

    public Location getChestLocation() {
        return chestLocation;
    }

    public static ChestGUI getOpenGui(Player player) {
        return openGuis.get(player);
    }

    public static void removeOpenGui(Player player) {
        openGuis.remove(player);
    }
}