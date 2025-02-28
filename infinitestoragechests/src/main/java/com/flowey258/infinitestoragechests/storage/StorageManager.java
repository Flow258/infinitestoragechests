package com.flowey258.infinitestoragechests.storage;

import com.flowey258.infinitestoragechests.InfiniteStorageChests;
import com.flowey258.infinitestoragechests.data.ChestData;
import com.flowey258.infinitestoragechests.data.StoredItem;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageManager {

    private final InfiniteStorageChests plugin;
    private final Map<String, ChestData> chestDataMap;
    private final File dataFolder;

    public StorageManager(InfiniteStorageChests plugin) {
        this.plugin = plugin;
        this.chestDataMap = new HashMap<>();
        this.dataFolder = new File(plugin.getDataFolder(), "chests");

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        loadAllData();
    }

    public int getTotalChests() {
        return chestDataMap.size();
    }

    public void createChest(Location location, UUID owner) {
        String locationKey = locationToString(location);
        ChestData chestData = new ChestData(location, owner);
        chestDataMap.put(locationKey, chestData);
        saveChestData(chestData);
    }

    public ChestData getChestData(Location location) {
        return chestDataMap.get(locationToString(location));
    }

    public boolean isInfiniteChest(Location location) {
        return chestDataMap.containsKey(locationToString(location));
    }

    public void removeChest(Location location) {
        String locationKey = locationToString(location);
        chestDataMap.remove(locationKey);

        // Delete the file
        File chestFile = new File(dataFolder, locationKey + ".yml");
        if (chestFile.exists()) {
            chestFile.delete();
        }
    }

    public void addItem(Location location, ItemStack item, int amount) {
        ChestData chestData = getChestData(location);
        if (chestData == null) {
            return;
        }

        chestData.addItem(item, amount);
        saveChestData(chestData);
    }

    public void removeItem(Location location, ItemStack item, int amount) {
        ChestData chestData = getChestData(location);
        if (chestData == null) {
            return;
        }

        chestData.removeItem(item, amount);
        saveChestData(chestData);
    }

    public int getItemCount(Location location, ItemStack item) {
        ChestData chestData = getChestData(location);
        if (chestData == null) {
            return 0;
        }

        return chestData.getItemCount(item);
    }

    public Map<ItemStack, Integer> getAllItems(Location location) {
        ChestData chestData = getChestData(location);
        if (chestData == null) {
            return new HashMap<>();
        }

        return chestData.getAllItems();
    }

    public int getUniqueItemCount(Location location) {
        ChestData chestData = getChestData(location);
        if (chestData == null) {
            return 0;
        }

        return chestData.getUniqueItemCount();
    }

    public int getPlayerChestCount(UUID playerUUID) {
        int count = 0;
        for (ChestData chestData : chestDataMap.values()) {
            if (chestData.getOwner().equals(playerUUID)) {
                count++;
            }
        }
        return count;
    }

    // Data saving and loading methods
    private void saveChestData(ChestData chestData) {
        String locationKey = locationToString(chestData.getLocation());
        File chestFile = new File(dataFolder, locationKey + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        // Save basic data
        config.set("owner", chestData.getOwner().toString());
        config.set("location", locationKey);

        // Save items
        int index = 0;
        for (Map.Entry<ItemStack, Integer> entry : chestData.getAllItems().entrySet()) {
            config.set("items." + index + ".item", entry.getKey());
            config.set("items." + index + ".amount", entry.getValue());
            index++;
        }

        try {
            config.save(chestFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save chest data: " + e.getMessage());
        }
    }

    private void loadChestData(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Load basic data
        UUID owner = UUID.fromString(config.getString("owner"));
        String locationStr = config.getString("location");
        Location location = locationFromString(locationStr);

        if (location == null) {
            plugin.getLogger().warning("Could not load location from: " + locationStr);
            return;
        }

        ChestData chestData = new ChestData(location, owner);

        // Load items
        if (config.contains("items")) {
            for (String key : config.getConfigurationSection("items").getKeys(false)) {
                ItemStack item = config.getItemStack("items." + key + ".item");
                int amount = config.getInt("items." + key + ".amount");

                if (item != null) {
                    chestData.addItem(item, amount);
                }
            }
        }

        chestDataMap.put(locationStr, chestData);
    }

    public void loadAllData() {
        chestDataMap.clear();

        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                loadChestData(file);
            }
        }

        plugin.getLogger().info("Loaded " + chestDataMap.size() + " infinite chests");
    }

    public void saveAllData() {
        for (ChestData chestData : chestDataMap.values()) {
            saveChestData(chestData);
        }

        plugin.getLogger().info("Saved " + chestDataMap.size() + " infinite chests");
    }

    // Helper methods
    public static String locationToString(Location location) {
        return location.getWorld().getName() + "_" +
                location.getBlockX() + "_" +
                location.getBlockY() + "_" +
                location.getBlockZ();
    }

    public static Location locationFromString(String locationString) {
        String[] parts = locationString.split("_");
        if (parts.length != 4) {
            return null;
        }

        try {
            String worldName = parts[0];
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);

            return new Location(org.bukkit.Bukkit.getWorld(worldName), x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}