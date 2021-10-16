package top.mrxiaom.rpgproject.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import cat.nyaa.nyaacore.Pair;
import think.rpgitems.item.RPGItem;
import think.rpgitems.power.Power;
import think.rpgitems.power.PowerManager;
import top.mrxiaom.rpgproject.RPGProject;
import top.mrxiaom.rpgproject.Util;
import top.mrxiaom.rpgproject.Enums;

public class GuiAddPower implements IGui{
	String handlePlayer;
	RPGItem rpg;
	int row = 6;
	int page;
	boolean close = false;
	boolean remove = true;
	public GuiAddPower(String handlePlayer, RPGItem rpg, int page) {
		this.handlePlayer = handlePlayer;
		this.rpg = rpg;
		this.page = page;
	}
	@Override
	public Inventory createGui(Player player) {
		Inventory inv = Bukkit.createInventory(null, this.row * 9,
				RPGProject.i18n("gui.add-power.title")
				.replace("%page%", String.valueOf(this.page))
				.replace("%name%", this.rpg.getName())
				.replace("%display%", this.rpg.getDisplayName()));
		this.updateItems(inv);
		return inv;
	}

	private void updateItems(Inventory inv) {
		inv.clear();
		Map<Integer, ItemStack> items = this.getGUIItems();
		for(int slot : items.keySet()) {
			inv.setItem(slot, items.get(slot));
		}
	}

	private void updateItems(InventoryView inv) {
		inv.getTopInventory().clear();
		Map<Integer, ItemStack> items = this.getGUIItems();
		for(int slot : items.keySet()) {
			inv.setItem(slot, items.get(slot));
		}
	}
	Map<Integer, NamespacedKey> keys = new HashMap<>();
	public Map<Integer, ItemStack> getGUIItems(){
		Map<Integer, ItemStack> items = new HashMap<>();
		this.keys.clear();
		int i = 0;
		int j = 45 * this.page;
		int k = 0;
		for(NamespacedKey power : PowerManager.getPowers().keySet()) {
			if(i >= j - 45 && i < j) {
				this.keys.put(k, power);
				items.put(k, Util.buildItem(Util.getPowerIconFromConfig(power),
					RPGProject.i18n("gui.add-power.items.power.name")
						.replace("%name%", power.getKey())
						.replace("%display%", Util.i18n("properties." + power.getKey() + ".main_name")),
					RPGProject.i18n_("gui.add-power.items.power.lore", Lists.newArrayList(
							new Pair<>("%namespace%", power.getNamespace()),
							new Pair<>("%key%", power.getKey()),
							new Pair<>("%description%", Util.i18nEmptyWhenNotFound("properties." + power.getKey() + ".main_description"))
					))));
				k++;
			}
			i++;
		}
		if(this.page - 1 > 0) {
			items.put(45, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.add-power.items.prev-page.material"), Material.LIME_STAINED_GLASS_PANE), 
					RPGProject.i18n("gui.add-power.items.prev-page.name"),
					RPGProject.i18n_("gui.add-power.items.prev-page.lore", Lists.newArrayList(
							new Pair<>("%page%", String.valueOf(this.page)),
							new Pair<>("%max_page%", String.valueOf((int)Math.ceil(PowerManager.getPowers().size() / 45.0D)))
					))));
		}
		if(this.page < (double)(PowerManager.getPowers().size() / 45.0D)) {
			items.put(53, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.add-power.items.next-page.material"), Material.LIME_STAINED_GLASS_PANE), 
					RPGProject.i18n("gui.add-power.items.next-page.name"),
					RPGProject.i18n_("gui.add-power.items.next-page.lore", Lists.newArrayList(
							new Pair<>("%page%", String.valueOf(this.page)),
							new Pair<>("%max_page%", String.valueOf((int)Math.ceil(PowerManager.getPowers().size() / 45.0D)))
					))));
		}
		items.put(49, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.add-power.items.back.material"), Material.BARRIER), 
				RPGProject.i18n("gui.add-power.items.back.name"),
				RPGProject.i18n_("gui.add-power.items.back.lore")));
		return items;
	}

	@Override
	public void onClick(Player player, ItemStack clickedItem, int clickedSlot, InventoryView inv,
			InventoryClickEvent event) {
		event.setCancelled(true);
		if(!player.getName().equals(this.handlePlayer) || !player.isOp()) {
			this.close = true;
			this.remove = true;
			player.closeInventory();
			return;
		}
		// 上一页
		if(clickedSlot == 45 && this.page - 1 > 0) {
			this.page--;
		}
		// 下一页
		if(clickedSlot == 53 && this.page < (double)(PowerManager.getPowers().size() / 45.0D)) {
			this.page++;
		}
		// 点击添加技能
		if(clickedSlot < 45 && this.keys.containsKey(clickedSlot)) {
			NamespacedKey key = this.keys.get(clickedSlot);
			Power power = PowerManager.instantiate(PowerManager.getPower(key));
		    power.init(new YamlConfiguration());
		    
		    this.close = true;
		    this.remove = false;
	        IGui gui = new GuiEditPower(player.getName(), this.rpg, power, true);
	        player.closeInventory();
			RPGProject.getInstance().getGuiManager().openGui(player, gui);
			return;
		}
		// 返回技能列表菜单
		if(clickedSlot == 49) {
			this.close = true;
			this.remove = false;
			IGui gui = new GuiPowerList(player.getName(), this.rpg, 1);
			player.closeInventory();
			RPGProject.getInstance().getGuiManager().openGui(player, gui);
			return;
		}
		this.updateItems(inv);
	}

	@Override
	public boolean onClose(Player player, InventoryView inv, InventoryCloseEvent event) {
		if(!this.close) {
			IGui gui = new GuiPowerList(player.getName(), this.rpg, 1);
			RPGProject.getInstance().getGuiManager().openGui(player, gui);
		}
		return this.remove;
	}
}
