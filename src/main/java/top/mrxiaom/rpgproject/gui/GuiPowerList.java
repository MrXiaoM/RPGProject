package top.mrxiaom.rpgproject.gui;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import think.rpgitems.item.RPGItem;
import think.rpgitems.power.Power;
import think.rpgitems.power.PowerManager;
import think.rpgitems.power.PropertyInstance;
import top.mrxiaom.rpgproject.Enums;
import top.mrxiaom.rpgproject.Pair;
import top.mrxiaom.rpgproject.RPGProject;
import top.mrxiaom.rpgproject.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiPowerList implements IGui {
    String handlePlayer;
    RPGItem rpg;
    int row = 6;
    int page;
    boolean close = false;
    boolean remove = true;

    public GuiPowerList(String handlePlayer, RPGItem rpg, int page) {
        this.handlePlayer = handlePlayer;
        this.rpg = rpg;
        this.page = page;
    }

    @Override
    public Inventory createGui(Player player) {
        Inventory inv = Bukkit.createInventory(null, this.row * 9,
                RPGProject.i18n("gui.power-list.title").replace("%page%", String.valueOf(this.page))
                        .replace("%name%", this.rpg.getName()).replace("%display%", this.rpg.getDisplayName()));
        this.updateItems(inv);
        return inv;
    }

    private void updateItems(Inventory inv) {
        inv.clear();
        Map<Integer, ItemStack> items = this.getGUIItems();
        for (int slot : items.keySet()) {
            inv.setItem(slot, items.get(slot));
        }
    }

    private void updateItems(InventoryView inv) {
        inv.getTopInventory().clear();
        Map<Integer, ItemStack> items = this.getGUIItems();
        for (int slot : items.keySet()) {
            inv.setItem(slot, items.get(slot));
        }
    }

    public Map<Integer, ItemStack> getGUIItems() {
        Map<Integer, ItemStack> items = new HashMap<>();
        int i = 0;
        int j = 45 * this.page;
        int k = 0;
        for (Power power : this.rpg.getPowers()) {
            if (i >= j - 45 && i < j) {
                Map<String, cat.nyaa.nyaacore.Pair<Method, PropertyInstance>> props = PowerManager
                        .getProperties(power.getNamespacedKey());

                List<String> lore = new ArrayList<>();
                for (String s : RPGProject.i18n_("gui.power-list.items.power.lore")) {
                    s = s.replace("%display%", power.displayText() != null ? power.displayText() : "")
                            .replace("%description%", "&a" + Util.i18nEmptyWhenNotFound(
                                    "properties." + power.getNamespacedKey().getKey() + ".main_description"));
                    if (s.contains("%key%") && s.contains("%value%")) {
                        int l = 0;
                        for (String name : props.keySet()) {
                            try {
                                Field field = props.get(name).getValue().field();
                                lore.add(s.replace("%key%", name).replace("%value%", field.get(power).toString()));
                                l++;
                            } catch (Throwable t) {
                                // 收声
                            }
                            if (l > 6) {
                                lore.add(RPGProject.i18n("gui.power-list.items.power.more-items").replace("%count%",
                                        String.valueOf(props.keySet().size())));
                                break;
                            }
                        }
                        continue;
                    }
                    lore.add(s);
                }
                items.put(k, Util.buildItem(Util.getPowerIconFromConfig(power.getNamespacedKey()),
                        "&e" + Util.i18n("properties." + power.getNamespacedKey().getKey() + ".main_name"), lore));
                k++;
            }
            i++;
        }
        if (this.page - 1 > 0) {
            items.put(45,
                    Util.buildItem(
                            Enums.valueOf(Material.class, RPGProject.i18n("gui.power-list.items.prev-page.material"),
                                    Material.LIME_STAINED_GLASS_PANE),
                            RPGProject.i18n("gui.power-list.items.prev-page.name"),
                            RPGProject.i18n_("gui.power-list.items.prev-page.lore",
                                    Lists.newArrayList(Pair.of("%page%", String.valueOf(this.page)), Pair.of(
                                            "%max_page%",
                                            String.valueOf((int) Math.ceil(this.rpg.getPowers().size() / 45.0D)))))));
        }
        if (this.page < (double) (this.rpg.getPowers().size() / 45.0D)) {
            items.put(53,
                    Util.buildItem(
                            Enums.valueOf(Material.class, RPGProject.i18n("gui.power-list.items.next-page.material"),
                                    Material.LIME_STAINED_GLASS_PANE),
                            RPGProject.i18n("gui.power-list.items.next-page.name"),
                            RPGProject.i18n_("gui.power-list.items.next-page.lore",
                                    Lists.newArrayList(Pair.of("%page%", String.valueOf(this.page)), Pair.of(
                                            "%max_page%",
                                            String.valueOf((int) Math.ceil(this.rpg.getPowers().size() / 45.0D)))))));
        }
        items.put(47,
                Util.buildItem(
                        Enums.valueOf(Material.class, RPGProject.i18n("gui.power-list.items.add-power.material"),
                                Material.HOPPER),
                        RPGProject.i18n("gui.power-list.items.add-power.name"),
                        RPGProject.i18n_("gui.power-list.items.add-power.lore")));
        items.put(49,
                Util.buildItem(
                        Enums.valueOf(Material.class, RPGProject.i18n("gui.power-list.items.back.material"),
                                Material.BARRIER),
                        RPGProject.i18n("gui.power-list.items.back.name"),
                        RPGProject.i18n_("gui.power-list.items.back.lore")));
        return items;
    }

    @Override
    public void onClick(Player player, ItemStack clickedItem, int clickedSlot, InventoryView inv,
                        InventoryClickEvent event) {
        event.setCancelled(true);
        if (!player.getName().equals(this.handlePlayer) || !player.isOp()) {
            this.close = true;
            this.remove = true;
            player.closeInventory();
            return;
        }
        boolean left = event.isLeftClick();
        boolean right = event.isRightClick();
        boolean shift = event.isShiftClick();
        // 点击技能图标
        if (clickedSlot < 45 && (45 * (this.page - 1) + clickedSlot) < this.rpg.getPowers().size()) {
            if (!shift) {
                if (left && !right) {
                    Power power = rpg.getPowers().get(45 * (this.page - 1) + clickedSlot);
                    this.close = true;
                    this.remove = false;
                    IGui gui = new GuiEditPower(player.getName(), this.rpg, power, false, 1);
                    player.closeInventory();
                    RPGProject.getInstance().getGuiManager().openGui(player, gui);
                    return;
                }
                if (right && !left) {
                    Power power = rpg.getPowers().get(45 * (this.page - 1) + clickedSlot);
                    rpg.removePower(power);
                }
            }
        }
        // 上一页
        if (clickedSlot == 45 && this.page - 1 > 0) {
            this.page--;
            this.close = true;
            this.remove = false;
            IGui gui = new GuiPowerList(player.getName(), this.rpg, page);
            player.closeInventory();
            RPGProject.getInstance().getGuiManager().openGui(player, gui);
            return;
        }
        // 下一页
        if (clickedSlot == 53 && this.page < (double) (this.rpg.getPowers().size() / 45.0D)) {
            this.page++;
            IGui gui = new GuiPowerList(player.getName(), this.rpg, page);
            player.closeInventory();
            RPGProject.getInstance().getGuiManager().openGui(player, gui);
            return;
        }
        // 新建技能
        if (clickedSlot == 47) {
            this.close = true;
            this.remove = false;
            IGui gui = new GuiAddPower(player.getName(), this.rpg, 1);
            player.closeInventory();
            RPGProject.getInstance().getGuiManager().openGui(player, gui);
            return;
        }
        // 返回编辑菜单
        if (clickedSlot == 49) {
            this.close = true;
            this.remove = false;
            IGui gui = new GuiEditor(player.getName(), this.rpg);
            player.closeInventory();
            RPGProject.getInstance().getGuiManager().openGui(player, gui);
            return;
        }
        this.updateItems(inv);
    }

    @Override
    public boolean onClose(Player player, InventoryView inv, InventoryCloseEvent event) {
        if (!this.close) {
            IGui gui = new GuiEditor(player.getName(), this.rpg);
            RPGProject.getInstance().getGuiManager().openGui(player, gui);
        }
        return this.remove;
    }
}
