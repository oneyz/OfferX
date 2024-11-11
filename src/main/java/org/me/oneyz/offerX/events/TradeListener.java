package org.me.oneyz.offerX.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.me.oneyz.offerX.managers.TradeManager;
import org.me.oneyz.offerX.utils.ColorUtil;

import java.util.Arrays;
import java.util.HashSet;
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
        InventoryView view = event.getView();
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        int fromSlot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();
        InventoryAction action = event.getAction();

        // Sprawdzenie, czy to GUI handlu
        if (isTradeInventory(view.getTitle())) {
            // Ignorowanie nieistotnych akcji
            if (action == InventoryAction.NOTHING || clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            // Obsługa kliknięcia w GUI handlu
            if (clickedInventory == view.getTopInventory()) {
                handleTradeGuiClick(event, player, fromSlot, clickedItem, action, view);
            }
            // Obsługa kliknięcia w ekwipunku gracza
            else if (clickedInventory == view.getBottomInventory()) {
                handlePlayerInventoryClick(event, player, fromSlot, clickedItem, action, view);
            }
        }
    }

    private void handleTradeGuiClick(InventoryClickEvent event, Player player, int fromSlot, ItemStack clickedItem, InventoryAction action, InventoryView view) {
        if (disallowedSlots.contains(fromSlot)) {
            event.setCancelled(true);
            player.sendMessage(ColorUtil.translate("&cNie możesz używać tego slotu w GUI handlu: " + fromSlot));
        } else {
            int toSlot = event.getRawSlot() - view.getTopInventory().getSize(); // oblicza slot docelowy w ekwipunku
            String actionDesc = describeAction(action, clickedItem, fromSlot, toSlot);
            player.sendMessage(ColorUtil.translate("&aAkcja w GUI handlu: " + actionDesc));
        }
    }

    private void handlePlayerInventoryClick(InventoryClickEvent event, Player player, int fromSlot, ItemStack clickedItem, InventoryAction action, InventoryView view) {
        int toSlot = event.getRawSlot(); // miejsce docelowe (GUI handlu)
        String actionDesc = describeAction(action, clickedItem, fromSlot, toSlot);
        player.sendMessage(ColorUtil.translate("&bAkcja w ekwipunku gracza: " + actionDesc));
    }

    private String describeAction(InventoryAction action, ItemStack item, int fromSlot, int toSlot) {
        String itemDesc = item != null && item.getType() != Material.AIR ? item.getType().toString() + " x" + item.getAmount() : "brak przedmiotu";
        String slotDesc = "Z slota " + fromSlot + " do slota " + toSlot;

        switch (action) {
            case PICKUP_ALL:
                return "Zabrano cały przedmiot: " + itemDesc + ". " + slotDesc;
            case PICKUP_HALF:
                return "Zabrano połowę przedmiotu: " + itemDesc + ". " + slotDesc;
            case PICKUP_ONE:
                return "Zabrano jeden z przedmiotów: " + itemDesc + ". " + slotDesc;
            case PICKUP_SOME:
                return "Zabrano część przedmiotu: " + itemDesc + ". " + slotDesc;
            case PLACE_ALL:
                return "Odłożono cały przedmiot: " + itemDesc + ". " + slotDesc;
            case PLACE_ONE:
                return "Odłożono jeden z przedmiotów: " + itemDesc + ". " + slotDesc;
            case PLACE_SOME:
                return "Odłożono część przedmiotu: " + itemDesc + ". " + slotDesc;
            case SWAP_WITH_CURSOR:
                return "Zamieniono przedmiot pod kursorem z: " + itemDesc + ". " + slotDesc;
            case MOVE_TO_OTHER_INVENTORY:
                return "Przeniesiono przedmiot do innego ekwipunku: " + itemDesc + ". " + slotDesc;
            case HOTBAR_SWAP:
                return "Zamieniono przedmiot z hotbara: " + itemDesc + ". " + slotDesc;
            default:
                return "Nieznana akcja: " + action + " na przedmiocie: " + itemDesc;
        }
    }

    private boolean isTradeInventory(String title) {
        return title != null && title.startsWith(ColorUtil.translate("&8[&eT&8] &7Trade with"));
    }
}
