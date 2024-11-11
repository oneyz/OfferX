package org.me.oneyz.offerX.managers;

import org.bukkit.entity.Player;
import org.me.oneyz.offerX.gui.TradeGUI;

import java.util.HashMap;
import java.util.Map;

public class TradeManager {

    private final Map<Player, Player> tradeRequests = new HashMap<>();
    private final Map<Player, Player> traders = new HashMap<>(); // To store active trades
    private final TradeGUI tradeGUI = new TradeGUI();

    // Sends a trade request from the sender to the receiver
    public void sendTradeRequest(Player sender, Player receiver) {
        tradeRequests.put(receiver, sender);
        receiver.sendMessage(sender.getName() + " has sent you a trade request.");
    }

    // Accepts a trade request from the player
    public void acceptTradeRequest(Player player) {
        Player requester = tradeRequests.get(player);
        if (requester != null) {
            // Add to active traders map
            traders.put(requester, player); // Store both players in the trade map
            tradeGUI.openTradeMenu(requester, player);
            tradeRequests.remove(player); // Remove from trade requests after acceptance
        }
    }

    // Denies a trade request from the player
    public void denyTradeRequest(Player player) {
        tradeRequests.remove(player);
        player.sendMessage("Trade request denied.");
    }

    // Checks if the player has an active trade request
    public boolean hasTradeRequest(Player player) {
        return tradeRequests.containsKey(player);
    }

    // Gets the player who is trading with the sender (if the player is the sender)
    public Player getReceiver(Player sender) {
        return traders.get(sender);
    }

    // Gets the player who is trading with the receiver (if the player is the receiver)
    public Player getSender(Player receiver) {
        return traders.get(receiver);
    }

    // Removes a trade from the active trades map after it is complete or canceled
    public void removeTrade(Player player) {
        if (traders.containsKey(player)) {
            Player partner = traders.get(player);
            traders.remove(player);
            traders.remove(partner); // Remove both players from the active trade map
        }
    }

    // Checks if the player is currently involved in a trade
    public boolean isInTrade(Player player) {
        return traders.containsKey(player);
    }
}