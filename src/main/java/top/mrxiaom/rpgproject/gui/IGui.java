package top.mrxiaom.rpgproject.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public interface IGui {
    Inventory createGui(Player player);

    void onClick(final Player player, final ItemStack clickedItem, final int clickedSlot, final InventoryView inv,
                 final InventoryClickEvent event);

    boolean onClose(final Player player, final InventoryView inv, final InventoryCloseEvent event);
}
