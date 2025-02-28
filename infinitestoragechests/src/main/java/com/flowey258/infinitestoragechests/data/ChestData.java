package com.flowey258.infinitestoragechests.data;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestData {

    private final Location location;
    private final UUID owner;
    private final Map<StoredItem, Integer> items;

    public ChestData(Location location, UUID owner) {
        this.location = location;
        this.owner = owner;
        this.items = new HashMap<>();
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwner() {
        return owner;
    }

    public void addItem(ItemStack item, int amount) {
        StoredItem storedItem = new StoredItem(item);

        items.put(storedItem, items.getOrDefault(storedItem, 0) + amount);
    }

    public void removeItem(ItemStack item, int amount) {
        StoredItem storedItem = new StoredItem(item);

        if (!items.containsKey(storedItem)) {
            return;
        }

        int currentAmount = items.get(storedItem);
        int newAmount = currentAmount - amount;

        if (newAmount <= 0) {
            items.remove(storedItem);
        } else {
            items.put(storedItem, newAmount);
        }
    }

    public int getItemCount(ItemStack item) {
        StoredItem storedItem = new StoredItem(item);
        return items.getOrDefault(storedItem, 0);
    }

    public Map<ItemStack, Integer> getAllItems() {
        Map<ItemStack, Integer> result = new HashMap<>();

        for (Map.Entry<StoredItem, Integer> entry : items.entrySet()) {
            result.put(entry.getKey().getItemStack(), entry.getValue());
        }

        return result;
    }

    public int getUniqueItemCount() {
        return items.size();
    }
}
