package org.me.oneyz.offerX.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.me.oneyz.offerX.gui.TradeMenu;

import java.util.HashMap;
import java.util.Map;

public class Trade implements CommandExecutor {

    private static final Map<String, String> tradeRequests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /trade <player|accept|deny>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "accept":
                handleAccept(player);
                break;

            case "deny":
                handleDeny(player);
                break;

            default:
                sendTradeRequest(player, args[0]);
                break;
        }
        return true;
    }

    private void sendTradeRequest(Player sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found or is not online.");
            return;
        }

        if (target.getName().equals(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You can't send a trade request to yourself.");
            return;
        }

        if (tradeRequests.containsKey(target.getName())) {
            sender.sendMessage(ChatColor.RED + "This player already has a pending trade request.");
            return;
        }

        tradeRequests.put(target.getName(), sender.getName());

        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&eOFFER&8] &7Player " + sender.getName() + " has sent you a trade request."));

        TextComponent acceptButton = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8[&2ACCEPT&8]"));
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade accept"));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to accept the trade request")));

        TextComponent declineButton = new TextComponent(ChatColor.translateAlternateColorCodes('&', " &8[&cDECLINE&8]"));
        declineButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade deny"));
        declineButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to decline the trade request")));

        target.spigot().sendMessage(acceptButton, declineButton);

        sender.sendMessage(ChatColor.GREEN + "Trade request sent to " + target.getName());
    }

    private void handleAccept(Player receiver) {
        String senderName = tradeRequests.get(receiver.getName());

        if (senderName == null) {
            receiver.sendMessage(ChatColor.RED + "You have no pending trade requests.");
            return;
        }

        Player sender = Bukkit.getPlayer(senderName);
        if (sender == null || !sender.isOnline()) {
            receiver.sendMessage(ChatColor.RED + "The player who sent the trade request is no longer online.");
            tradeRequests.remove(receiver.getName());
            return;
        }

        tradeRequests.remove(receiver.getName());

        TradeMenu tradeMenu = new TradeMenu("&8Trade Menu");
        sender.openInventory(tradeMenu.getInventory());
        receiver.openInventory(tradeMenu.getInventory());

        sender.sendMessage(ChatColor.GREEN + "Trade accepted by " + receiver.getName());
        receiver.sendMessage(ChatColor.GREEN + "You have accepted the trade request from " + sender.getName());
    }

    private void handleDeny(Player receiver) {
        String senderName = tradeRequests.get(receiver.getName());

        if (senderName == null) {
            receiver.sendMessage(ChatColor.RED + "You have no pending trade requests.");
            return;
        }

        Player sender = Bukkit.getPlayer(senderName);
        if (sender != null && sender.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Your trade request to " + receiver.getName() + " was denied.");
        }

        tradeRequests.remove(receiver.getName());
        receiver.sendMessage(ChatColor.RED + "You have denied the trade request from " + senderName);
    }
}
