package me.favn.pureores.commands;

import me.favn.pureores.Pureores;
import me.favn.pureores.config.OresConfig;
import me.favn.pureores.config.OresConfig.Ore;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GivePure implements TabExecutor {

    private final Pureores plugin;

    public static final String COMMAND_NAME = "givepure";

    public GivePure(Pureores plugin) {
        this.plugin = plugin;
        plugin.getCommand(COMMAND_NAME).setExecutor(this);
        plugin.getCommand(COMMAND_NAME).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase(COMMAND_NAME)) {
            try {
                if (args.length == 1) {
                    givePure(sender, args[0]);
                } else if (args.length == 2) {
                    givePure(sender, args[0], args[1]);
                } else if (args.length == 3) {
                    givePure(sender, args[0], args[1], args[2]);
                } else {
                    return false;
                }
            } catch (GivePureException e) {
                sender.sendMessage(e.getMessage());
                return false;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error in /givepure command!", e);
                sender.sendMessage("Something went wrong! An error has been logged.");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // /givepure <ITEM>
            return OresConfig
                    .getConfig(this.plugin)
                    .getOres()
                    .stream()
                    .map(o -> o.getAlias())
                    .collect(Collectors.toList());
        }
        if (args.length == 2 || args.length == 3) {
            // /givepure <item> [PLAYER]
            // /givepure <item> [amount] [PLAYER]
            return plugin
                    .getServer()
                    .getOnlinePlayers()
                    .stream()
                    .map(pl -> pl.getName())
                    .collect(Collectors.toList());
        }
        return null;
    }

    private void givePure(CommandSender sender, String itemName) throws GivePureException {
        if (!(sender instanceof Player)) {
            throw new GivePureException("Console cannot be given pure ores. Please specify a player.");
        }
        givePure(sender, itemName, "1", null);
    }

    private void givePure(CommandSender sender, String itemName, String amountOrPlayerName) throws GivePureException {
        try {
            Integer.parseInt(amountOrPlayerName);
            if (!(sender instanceof Player)) {
                throw new GivePureException("Console cannot be given pure ores. Please specify a player.");
            }
            givePure(sender, itemName, amountOrPlayerName, null);
        } catch (NumberFormatException e) {
            givePure(sender, itemName, "1", amountOrPlayerName);
        }
    }

    private void givePure(CommandSender sender, String itemName, String amount, String playerName)
            throws GivePureException {
        if (sender instanceof ConsoleCommandSender && playerName == null) {
            throw new GivePureException("Console cannot be given pure ores. Please specify a player.");
        }
        Ore foundOre = OresConfig.getConfig(this.plugin).getOre(itemName);
        if (foundOre == null) {
            throw new GivePureException(String.format("%1$s is not a valid pureores item.", itemName));
        }
        int parsedAmount = parseAmount(amount);
        if (parsedAmount <= 0) {
            throw new GivePureException("Amount must be a number larger than 0.");
        }
        Player player = playerName == null ? (Player) sender : plugin.getServer().getPlayerExact(playerName);
        if (player == null) {
            throw new GivePureException(String.format("No player found with name %1$s.", playerName));
        }
        ItemStack item = foundOre.toItemStack(parsedAmount);
        this.plugin.givePure(player, item, false);
        if (playerName != null) {
            sender.sendMessage(
                    String.format("Gave %1$d %2$s to %3$s!", parsedAmount, foundOre.toString(), player.getName()));
        }
    }

    private int parseAmount(String amount) {
        try {
            return Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static class GivePureException extends Exception {
        public GivePureException(String message) {
            super(message);
        }
    }
}
