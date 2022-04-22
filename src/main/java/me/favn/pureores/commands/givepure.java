package me.favn.pureores.commands;

import me.favn.pureores.Pureores;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import java.util.List;
import java.util.Objects;

public class givepure implements TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use that command");
            return true;
        }
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("givepure")) {
            if (args.length == 1) {
                ItemStack item = Objects.requireNonNull(Pureores.items.get(Pureores.NameAndItemname.get(args[0].toUpperCase())), "Key(itemname) cannot be null");
                player.getInventory().addItem(item);
                player.sendMessage("Added 1 " + Pureores.NameAndItemname.get(args[0].toUpperCase()) + " to your inventory!");
            } else if (args.length == 2) {
                try {
                    ItemStack item = Objects.requireNonNull(Pureores.items.get(Pureores.NameAndItemname.get(args[0].toUpperCase())), "Key(itemname) cannot be null");
                    int amount = Integer.parseInt(args[1]);
                    if (amount == 0) {
                        player.sendMessage("Amount cannot be 0!");
                    } else if (amount >= 1) {
                        item.setAmount(amount);
                        player.getInventory().addItem(item);
                        item.setAmount(1);
                        player.sendMessage("Added " + amount + " " + Pureores.NameAndItemname.get(args[0].toUpperCase()) + " to your inventory!");
                    } else {
                        InvalidSyntax(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    InvalidSyntax(player);
                }
            } else {
                player.sendMessage("Something went wrong, please contact a server administrator with the following error code!");
                player.sendMessage("Error code: PIGEON");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Pureores.names;
        }
        return null;
    }

    public void InvalidSyntax(Player player) {
        player.sendMessage("Invalid command syntax, use: /givepure <item> <amount>");
        player.sendMessage("Example: /givepure Diamond 10");

    }
}