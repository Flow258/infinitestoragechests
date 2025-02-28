# Infinite Storage Chests - Minecraft Plugin

## Description
Infinite Storage Chests is a Minecraft plugin that introduces a custom, craftable chest with an infinite item storage system. Players can store unlimited quantities of items in a single slot, while still being able to withdraw normal stack sizes. The plugin is designed to provide an efficient storage solution for players who deal with massive amounts of resources.

## Features
- **Custom Craftable Chests**: Players can craft special chests using rare materials.
- **Infinite Stacking**: Store an unlimited amount of a single item in each slot.
- **Custom GUI**: Interactive chest interface for easy inventory management.
- **Search & Sorting**: Quickly find and organize items in the storage.
- **Configurable Limits**: Server admins can customize chest capacity and behavior.
- **Item Withdrawal**: Retrieve items in normal stack sizes to maintain balance.

## Crafting Recipe
To craft an Infinite Storage Chest, use the following materials:
- 4x Obsidian
- 4x Ender Pearls
- 1x Chest

### Crafting Grid
```
[ Obsidian ] [ Ender Pearl ] [ Obsidian ]
[ Ender Pearl ] [ Chest ] [ Ender Pearl ]
[ Obsidian ] [ Ender Pearl ] [ Obsidian ]
```

## Commands
| Command | Description | Permission |
|---------|-------------|-------------|
| `/giveinfinitechest <player>` | Grants a player an Infinite Storage Chest | `infinitechest.admin` |
| `/infinitechest reload` | Reloads the plugin configuration | `infinitechest.admin` |

## Permissions
- `infinitechest.craft` - Allows crafting the Infinite Storage Chest.
- `infinitechest.use` - Allows usage of Infinite Storage Chests.
- `infinitechest.admin` - Grants access to admin commands.

## Configuration (`config.yml`)
Admins can modify the following settings:
```yaml
# Maximum number of unique items per chest
max_unique_items: 27

# Whether to allow infinite stacking
allow_infinite_stacking: true

# Crafting recipe ingredients
crafting_recipe:
  - obsidian
  - obsidian
  - obsidian
  - ender_pearl
  - chest
  - ender_pearl
  - obsidian
  - ender_pearl
  - obsidian
```

## How It Works
1. **Craft the Chest** - Players craft the Infinite Storage Chest using the recipe.
2. **Open the Chest** - Right-click to open the custom GUI.
3. **Store Items** - Deposit items into slots, where they stack infinitely.
4. **Retrieve Items** - Withdraw items in standard stack sizes (e.g., 64 at a time).
5. **Manage Inventory** - Use sorting and search features for easy access.

## Balancing Considerations
- **Expensive Crafting Recipe**: Ensures that the chest is not too easily obtainable.
- **Usage Limits**: Optionally limit the number of Infinite Storage Chests per player.
- **Compatibility**: Designed to work with other plugins, including economy and land-claim systems.

## Future Enhancements
- **Upgradable Chests**: Higher-tier chests with more features.
- **Linked Chests**: Multiple chests sharing a single storage pool.
- **Remote Access**: Allowing players to access storage from anywhere.

## Installation
1. Download the plugin `.jar` file.
2. Place it in the `plugins` folder of your Minecraft server.
3. Restart the server to generate the configuration files.
4. Modify `config.yml` as needed.
5. Enjoy unlimited storage!

## Support
For support, bug reports, and feature requests, please contact me or visit the official support forum.

---
Enjoy seamless item storage with Infinite Storage Chests!

