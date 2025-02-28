package com.flowey258.infinitestoragechests.config;

import com.flowey258.infinitestoragechests.InfiniteStorageChests;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final InfiniteStorageChests plugin;
    private FileConfiguration config;

    // Config defaults
    private int maxUniqueItems;
    private boolean allowInfiniteStacking;
    private List<Material> craftingIngredients;
    private String storageType;
    private int maxChestsPerPlayer;

    public ConfigManager(InfiniteStorageChests plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void loadConfig() {
        // Set defaults if not present
        config.addDefault("max_unique_items", 27);
        config.addDefault("allow_infinite_stacking", true);
        config.addDefault("max_chests_per_player", 3);
        config.addDefault("storage_type", "yaml"); // Options: yaml, sqlite, mysql

        // Default crafting recipe
        List<String> defaultRecipe = new ArrayList<>();
        defaultRecipe.add("OBSIDIAN");
        defaultRecipe.add("OBSIDIAN");
        defaultRecipe.add("OBSIDIAN");
        defaultRecipe.add("ENDER_PEARL");
        defaultRecipe.add("CHEST");
        defaultRecipe.add("ENDER_PEARL");
        defaultRecipe.add("OBSIDIAN");
        defaultRecipe.add("ENDER_PEARL");
        defaultRecipe.add("OBSIDIAN");

        config.addDefault("crafting_recipe", defaultRecipe);
        config.options().copyDefaults(true);
        plugin.saveConfig();

        // Load values
        maxUniqueItems = config.getInt("max_unique_items");
        allowInfiniteStacking = config.getBoolean("allow_infinite_stacking");
        maxChestsPerPlayer = config.getInt("max_chests_per_player");
        storageType = config.getString("storage_type");

        // Load recipe
        craftingIngredients = new ArrayList<>();
        List<String> materialNames = config.getStringList("crafting_recipe");
        for (String materialName : materialNames) {
            try {
                Material material = Material.valueOf(materialName);
                craftingIngredients.add(material);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material in crafting recipe: " + materialName);
                craftingIngredients.add(Material.OBSIDIAN); // Fallback to obsidian
            }
        }
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        loadConfig();
    }

    public int getMaxUniqueItems() {
        return maxUniqueItems;
    }

    public boolean isAllowInfiniteStacking() {
        return allowInfiniteStacking;
    }

    public List<Material> getCraftingIngredients() {
        return craftingIngredients;
    }

    public String getStorageType() {
        return storageType;
    }

    public int getMaxChestsPerPlayer() {
        return maxChestsPerPlayer;
    }
}