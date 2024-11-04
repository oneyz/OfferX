package org.me.oneyz.offerX.managers;

import org.bukkit.entity.Player;
import org.me.oneyz.offerX.gui.TradeGUI;

import java.util.HashMap;
import java.util.Map;

public class TradeManager {

    private final Map<Player, Player> tradeRequests = new HashMap<>();
    private final TradeGUI tradeGUI = new TradeGUI();

    public void sendTradeRequest(Player sender, Player receiver) {
        tradeRequests.put(receiver, sender);
        receiver.sendMessage(sender.getName() + " has sent you a trade request.");
    }

    public void acceptTradeRequest(Player player) {
        Player requester = tradeRequests.get(player);
        if (requester != null) {
            tradeGUI.openTradeMenu(requester, player);
            tradeRequests.remove(player);
        }
    }

    public void denyTradeRequest(Player player) {
        tradeRequests.remove(player);
        player.sendMessage("Trade request denied.");
    }

    public boolean hasTradeRequest(Player player) {
        return tradeRequests.containsKey(player);
    }
}
