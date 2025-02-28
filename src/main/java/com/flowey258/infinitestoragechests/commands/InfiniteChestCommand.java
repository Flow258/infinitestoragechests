package com.flowey258.infinitestoragechests.commands;

import com.flowey258.infinitestoragechests.InfiniteStorageChests;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InfiniteChestCommand implements CommandExecutor, TabCompleter {

    private final InfiniteStorageChests plugin;

    public InfiniteChestCommand(InfiniteStorageChests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                return handleGiveCommand(sender, args);
            case "reload":
                return handleReloadCommand(sender);
            case "help":
                sendHelpMessage(sender);
                return true;
            case "stats":
                return handleStatsCommand(sender);
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /infinitechest help for commands.");
                return true;
        }
    }

    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("infinitechest.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /infinitechest give <player> [amount]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
            return true;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "Amount must be greater than 0.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[2]);
                return true;
            }
        }

        ItemStack chest = plugin.getItemManager().createInfiniteChest();
        chest.setAmount(amount);

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItemNaturally(target.getLocation(), chest);
            sender.sendMessage(ChatColor.GREEN + "The player's inventory was full. Dropped the chest at their location.");
        } else {
            target.getInventory().addItem(chest);
            sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Infinite Storage Chest(s) to " + target.getName() + ".");
        }

        target.sendMessage(ChatColor.GREEN + "You received " + amount + " Infinite Storage Chest(s)!");
        return true;
    }

    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("infinitechest.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        plugin.getConfigManager().reload();
        plugin.getStorageManager().loadAllData();

        sender.sendMessage(ChatColor.GREEN + "InfiniteStorageChests configuration reloaded.");
        return true;
    }

    private boolean handleStatsCommand(CommandSender sender) {
        if (!sender.hasPermission("infinitechest.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        int totalChests = plugin.getStorageManager().getTotalChests();

        sender.sendMessage(ChatColor.GOLD + "===== InfiniteStorageChests Stats =====");
        sender.sendMessage(ChatColor.YELLOW + "Total chests: " + ChatColor.WHITE + totalChests);
        sender.sendMessage(ChatColor.YELLOW + "Max chests per player: " + ChatColor.WHITE + plugin.getConfigManager().getMaxChestsPerPlayer());
        sender.sendMessage(ChatColor.YELLOW + "Max unique items per chest: " + ChatColor.WHITE + plugin.getConfigManager().getMaxUniqueItems());

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===== InfiniteStorageChests Commands =====");

        if (sender.hasPermission("infinitechest.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/infinitechest give <player> [amount]" + ChatColor.WHITE + " - Give infinite chest to a player");
            sender.sendMessage(ChatColor.YELLOW + "/infinitechest reload" + ChatColor.WHITE + " - Reload plugin configuration");
            sender.sendMessage(ChatColor.YELLOW + "/infinitechest stats" + ChatColor.WHITE + " - Show plugin statistics");
        }

        sender.sendMessage(ChatColor.YELLOW + "/infinitechest help" + ChatColor.WHITE + " - Show this help message");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            if (sender.hasPermission("infinitechest.admin")) {
                completions.addAll(Arrays.asList("give", "reload", "stats"));
            }

            completions.add("help");

            return completions.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("give") && sender.hasPermission("infinitechest.admin")) {
            return null; // Return null to get player list
        }

        return new ArrayList<>();
    }
}