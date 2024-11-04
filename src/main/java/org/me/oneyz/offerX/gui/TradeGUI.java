package org.me.oneyz.offerX.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.me.oneyz.offerX.utils.ColorUtil;

import java.util.Arrays;

public class TradeGUI {

    public void openTradeMenu(Player player1, Player player2) {
        Inventory tradeMenu1 = Bukkit.createInventory(null, 54, ColorUtil.translate("&8[&eT&8] &7Trade with " + player2.getName()));
        Inventory tradeMenu2 = Bukkit.createInventory(null, 54, ColorUtil.translate("&8[&eT&8] &7Trade with " + player1.getName()));

        ItemStack redGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack greenConcrete = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta greenMeta = greenConcrete.getItemMeta();

        if (greenMeta != null) {
            greenMeta.setDisplayName("§aGotowy");
            greenMeta.setLore(Arrays.asList(" ", "§c* §7Oczekiwanie na gotowość",
                    "§c* §7Po kliknięciu nie ma odwrotu!", " "));
            greenConcrete.setItemMeta(greenMeta);
        }

        for (int slot : new int[]{4, 13, 22, 31, 40, 49, 45, 46, 47, 51, 52, 53}) {
            tradeMenu1.setItem(slot, redGlassPane);
            tradeMenu2.setItem(slot, redGlassPane);
        }

        tradeMenu1.setItem(48, greenConcrete);
        tradeMenu2.setItem(48, greenConcrete);
        tradeMenu1.setItem(50, greenConcrete);
        tradeMenu2.setItem(50, greenConcrete);

        player1.openInventory(tradeMenu1);
        player2.openInventory(tradeMenu2);
    }
}
