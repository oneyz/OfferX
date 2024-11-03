package org.me.oneyz.offerX.gui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradeMenu {

    private final Inventory tradeInventory;

    public TradeMenu(String title) {
        this.tradeInventory = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', title));
        prepareTradeMenu();
    }

    private void prepareTradeMenu() {
        ItemStack grayPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = grayPane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            grayPane.setItemMeta(meta);
        }

        for (int i = 0; i < tradeInventory.getSize(); i++) {
            tradeInventory.setItem(i, grayPane);
        }
    }

    public Inventory getInventory() {
        return tradeInventory;
    }
}
