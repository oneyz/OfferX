package org.me.oneyz.offerX.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.me.oneyz.offerX.managers.TradeManager;
import org.me.oneyz.offerX.utils.ColorUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TradeListener implements Listener {

    private final TradeManager tradeManager;

    // Slot sets for both initiator and acceptor
    private final Set<Integer> initiatorAllowedSlots = new HashSet<>(Arrays.asList(
            0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39
    ));

    private final Set<Integer> acceptorAllowedSlots = new HashSet<>(Arrays.asList(
            5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44
    ));

    public TradeListener(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = event.getView();

        // Ensure it's a trade inventory
        if (isTradeInventory(inventoryView.getTitle())) {
            Inventory clickedInventory = event.getClickedInventory();
            int clickedSlot = event.getSlot();

            // Check if the player is part of an active trade and get allowed slots
            Set<Integer> allowedSlots = getAllowedSlotsForPlayer(player);

            // Cancel the event if the clicked slot is not allowed
            if (allowedSlots == null || !allowedSlots.contains(clickedSlot)) {
                event.setCancelled(true);
                player.sendMessage(ColorUtil.translate("&cYou cannot interact with this slot."));
                return;
            }

            // Handle the inventory action based on the event type
            handleAction(event, player, clickedInventory, clickedSlot);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = event.getView();

        if (isTradeInventory(inventoryView.getTitle())) {
            Map<Integer, ItemStack> newItems = event.getNewItems();
            Set<Integer> allowedSlots = getAllowedSlotsForPlayer(player);

            // Cancel the drag if any slot is outside the allowed slots for the player
            for (Integer slot : newItems.keySet()) {
                if (allowedSlots == null || !allowedSlots.contains(slot)) {
                    event.setCancelled(true);
                    player.sendMessage(ColorUtil.translate("&cYou cannot drag items into this slot."));
                    return;
                }
            }

            // Display dragged item details for valid drag events
            if (!newItems.isEmpty()) {
                StringBuilder slotsInfo = new StringBuilder();
                for (Map.Entry<Integer, ItemStack> entry : newItems.entrySet()) {
                    int slot = entry.getKey();
                    ItemStack item = entry.getValue();
                    String itemType = (item != null && item.getType() != Material.AIR) ? item.getType().toString() : "None";
                    int amount = (item != null) ? item.getAmount() : 0;
                    slotsInfo.append(slot).append(" [").append(amount).append("], ");
                }

                if (slotsInfo.length() >= 2) {
                    slotsInfo.setLength(slotsInfo.length() - 2);
                }

                ItemStack draggedItem = event.getOldCursor();
                String draggedItemType = (draggedItem != null && draggedItem.getType() != Material.AIR) ? draggedItem.getType().toString() : "None";
                int draggedAmount = (draggedItem != null) ? draggedItem.getAmount() : 0;

                StringBuilder message = new StringBuilder();
                message.append(ColorUtil.translate("&e[INFO] Inventory Dragged:")).append("\n");
                message.append(ColorUtil.translate("&7Dragged Item: " + draggedItemType + " (Amount: " + draggedAmount + ")")).append("\n");
                message.append(ColorUtil.translate("&7Slots Affected: " + slotsInfo.toString())).append("\n");

                player.sendMessage(message.toString());
            }
        }
    }

    private void handleAction(InventoryClickEvent event, Player player, Inventory clickedInventory, int clickedSlot) {
        // Example action handling for MOVE_TO_OTHER_INVENTORY
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                handleMoveToOtherInventory(event, player, clickedInventory, clickedSlot);
                break;
            default:
                player.sendMessage(ColorUtil.translate("&cUnhandled action: " + event.getAction().toString()));
                break;
        }
    }

    private void handleMoveToOtherInventory(InventoryClickEvent event, Player player, Inventory clickedInventory, int clickedSlot) {
        String destination = determineInventoryDestination(event, clickedInventory);

        ItemStack clickedItem = event.getCurrentItem();
        String clickedItemType = (clickedItem != null && clickedItem.getType() != Material.AIR)
                ? clickedItem.getType().toString()
                : "None";
        int clickedAmount = (clickedItem != null) ? clickedItem.getAmount() : 0;

        StringBuilder shiftClickMessage = new StringBuilder();
        shiftClickMessage.append(ColorUtil.translate("&e[INFO] Shift-Click Transfer:")).append("\n");
        shiftClickMessage.append(ColorUtil.translate("&7Item: " + clickedItemType + " (Amount: " + clickedAmount + ")")).append("\n");
        shiftClickMessage.append(ColorUtil.translate("&7Destination: " + destination)).append("\n");

        player.sendMessage(shiftClickMessage.toString());
    }

    private Set<Integer> getAllowedSlotsForPlayer(Player player) {
        // Check if the player is in an active trade session
        if (tradeManager.isInTrade(player)) {
            // Check if the player is the initiator or acceptor and return the appropriate slot set
            Player partner = tradeManager.getReceiver(player); // Get the other player in trade
            if (partner != null) {
                if (player.equals(partner)) {
                    // If player is the sender, return allowed slots for the initiator
                    return initiatorAllowedSlots;
                } else {
                    // If player is the acceptor, return allowed slots for the acceptor
                    return acceptorAllowedSlots;
                }
            }
        }
        return null; // No allowed slots if the player isn't in an active trade
    }

    private String determineInventoryDestination(InventoryClickEvent event, Inventory clickedInventory) {
        InventoryView view = event.getView();

        if (clickedInventory != null && clickedInventory.equals(view.getBottomInventory())) {
            return "Trade Inventory";
        } else if (clickedInventory != null && clickedInventory.equals(view.getTopInventory())) {
            return "Player Inventory";
        } else {
            return "Unknown";
        }
    }

    private boolean isTradeInventory(String title) {
        return title != null && title.startsWith(ColorUtil.translate("&8[&eT&8] &7Trade with"));
    }
}
