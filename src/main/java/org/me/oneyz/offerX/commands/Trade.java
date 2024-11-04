package org.me.oneyz.offerX.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.me.oneyz.offerX.managers.TradeManager;

public class Trade implements CommandExecutor {

    private final TradeManager tradeManager;

    public Trade(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("Usage: /trade <player | accept | deny>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "accept":
                tradeManager.acceptTradeRequest(player);
                break;

            case "deny":
                tradeManager.denyTradeRequest(player);
                break;

            default:
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null) {
                    player.sendMessage("Invalid player specified.");
                    return true;
                }


                tradeManager.sendTradeRequest(player, targetPlayer);
                break;
        }
        return true;
    }
}