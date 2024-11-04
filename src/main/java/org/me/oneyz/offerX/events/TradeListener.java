package org.me.oneyz.offerX.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.me.oneyz.offerX.managers.TradeManager;
import org.me.oneyz.offerX.utils.ColorUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TradeListener implements Listener {

    private final TradeManager tradeManager;
    private final Set<Integer> disallowedSlots;

    public TradeListener(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
        this.disallowedSlots = new HashSet<>(Arrays.asList(4, 13, 22, 31, 40, 49, 45, 46, 47, 51, 52, 53, 48, 50));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView inventoryView = event.getView();
        Player player = (Player) event.getWhoClicked();

        if (isTradeInventory(inventoryView.getTitle())) {
            Inventory clickedInventory = event.getClickedInventory();
            int slot = event.getSlot();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                // Sprawdzenie, czy przedmiot został przeniesiony
                boolean isMovingFromTrade = clickedInventory == inventoryView.getTopInventory();
                boolean isMovingToTrade = clickedInventory == inventoryView.getBottomInventory();

                if (isMovingFromTrade) {
                    player.sendMessage(ColorUtil.translate("&e[DEBUG] Przenosisz przedmiot w GUI handlu z slotu " + event.getSlot() + " na slot " + event.getRawSlot() + "."));
                } else if (isMovingToTrade) {
                    player.sendMessage(ColorUtil.translate("&e[DEBUG] Przenosisz przedmiot z ekwipunku gracza do GUI handlu z slotu " + event.getSlot() + " na slot " + event.getRawSlot() + "."));
                }
            }

            if (clickedInventory == inventoryView.getTopInventory()) {
                handleTradeInventoryClick(event, player, slot);
            } else if (clickedInventory == inventoryView.getBottomInventory()) {
                handlePlayerInventoryClick(event, player, slot);
            }

            if (event.isShiftClick()) {
                handleShiftClick(event, player, inventoryView);
            }
        }
    }

    private void handleTradeInventoryClick(InventoryClickEvent event, Player player, int slot) {
        ItemStack clickedItem = event.getCurrentItem();

        if (disallowedSlots.contains(slot)) {
            event.setCancelled(true);
            player.sendMessage(ColorUtil.translate("&c[DEBUG] Nie możesz klikać na tym slocie w GUI handlu: " + slot));
        } else {
            player.sendMessage(ColorUtil.translate("&a[DEBUG] Kliknięto w dozwolony slot handlu: " + slot));
        }
    }

    private void handlePlayerInventoryClick(InventoryClickEvent event, Player player, int slot) {
        player.sendMessage(ColorUtil.translate("&b[DEBUG] Kliknięto w ekwipunku gracza na slocie: " + slot));
    }

    private void handleShiftClick(InventoryClickEvent event, Player player, InventoryView inventoryView) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = inventoryView.getTopInventory();
        Inventory bottomInventory = inventoryView.getBottomInventory();

        if (clickedInventory == topInventory) {
            player.sendMessage(ColorUtil.translate("&e[DEBUG] Przenosisz przedmiot z GUI handlu do ekwipunku gracza (SHIFT + CLICK)."));
        } else if (clickedInventory == bottomInventory) {
            player.sendMessage(ColorUtil.translate("&e[DEBUG] Przenosisz przedmiot z ekwipunku gracza do GUI handlu (SHIFT + CLICK)."));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryView inventoryView = event.getView();
        Player player = (Player) event.getWhoClicked();

        if (isTradeInventory(inventoryView.getTitle())) {
            Map<Integer, ItemStack> draggedItems = event.getNewItems();
            boolean draggingToTrade = false;
            boolean draggingFromInventory = false;

            for (int slot : event.getRawSlots()) {
                if (slot < inventoryView.getTopInventory().getSize()) {
                    draggingToTrade = true;
                    if (disallowedSlots.contains(slot)) {
                        event.setCancelled(true);
                        player.sendMessage(ColorUtil.translate("&c[DEBUG] Nie możesz przeciągać przedmiotów do niedozwolonego slotu w GUI handlu: " + slot));
                        return;
                    }
                } else {
                    draggingFromInventory = true;
                }
            }

            if (draggingToTrade && draggingFromInventory) {
                player.sendMessage(ColorUtil.translate("&e[DEBUG] Przeciągnąłeś przedmiot z ekwipunku do GUI handlu."));
            } else if (draggingToTrade) {
                player.sendMessage(ColorUtil.translate("&e[DEBUG] Przeciągnąłeś przedmiot w obrębie GUI handlu."));
            } else if (draggingFromInventory) {
                player.sendMessage(ColorUtil.translate("&e[DEBUG] Przeciągnąłeś przedmiot w obrębie ekwipunku."));
            }
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        Inventory source = event.getSource();
        Inventory destination = event.getDestination();
        ItemStack item = event.getItem();

        if (isTradeInventory(source.getLocation() == null ? "" : source.getLocation().toString()) ||
                isTradeInventory(destination.getLocation() == null ? "" : destination.getLocation().toString())) {
            System.out.println("DEBUG: Próba przeniesienia przedmiotu między ekwipunkiem a GUI handlu.");
            System.out.println("Przedmiot: " + item.getType() + ", Ilość: " + item.getAmount());

            if (disallowedSlots.contains(destination.firstEmpty())) {
                event.setCancelled(true);
                System.out.println("DEBUG: Przenoszenie anulowane - próba przeniesienia do zablokowanego slotu.");
            }
        }
    }

    private boolean isTradeInventory(String title) {
        return title != null && title.startsWith(ColorUtil.translate("&8[&eT&8] &7Trade with"));
    }
}
