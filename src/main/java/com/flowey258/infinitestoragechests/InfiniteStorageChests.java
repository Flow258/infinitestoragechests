package com.flowey258.infinitestoragechests;

import com.flowey258.infinitestoragechests.commands.InfiniteChestCommand;
import com.flowey258.infinitestoragechests.config.ConfigManager;
import com.flowey258.infinitestoragechests.listeners.ChestListeners;
import com.flowey258.infinitestoragechests.listeners.CraftingListeners;
import com.flowey258.infinitestoragechests.storage.StorageManager;
import com.flowey258.infinitestoragechests.utils.ItemManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class InfiniteStorageChests extends JavaPlugin {

    private static InfiniteStorageChests instance;
    private ConfigManager configManager;
    private StorageManager storageManager;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        // Set instance
        instance = this;

        // Initialize config
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize managers
        storageManager = new StorageManager(this);
        itemManager = new ItemManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new ChestListeners(this), this);
        getServer().getPluginManager().registerEvents(new CraftingListeners(this), this);

        // Register commands
        getCommand("infinitechest").setExecutor(new InfiniteChestCommand(this));

        getLogger().info("InfiniteStorageChests has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save data
        if (storageManager != null) {
            storageManager.saveAllData();
        }

        getLogger().info("InfiniteStorageChests has been disabled!");
    }

    public static InfiniteStorageChests getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}