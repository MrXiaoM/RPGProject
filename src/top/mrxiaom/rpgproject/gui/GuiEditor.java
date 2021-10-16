package top.mrxiaom.rpgproject.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.cyr1en.cp.PromptRegistry;
import com.google.common.collect.Lists;

import cat.nyaa.nyaacore.Pair;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;
import think.rpgitems.item.RPGItem.AttributeMode;
import think.rpgitems.item.RPGItem.DamageMode;
import think.rpgitems.item.RPGItem.EnchantMode;
import top.mrxiaom.rpgproject.PlayerPrompt;
import top.mrxiaom.rpgproject.RPGProject;
import top.mrxiaom.rpgproject.Util;
import top.mrxiaom.rpgproject.Enums;

public class GuiEditor implements IGui {
	String handlePlayer;
	int raw = 3;
	RPGItem rpg;
	boolean remove = true;
	public GuiEditor(String handlePlayer, RPGItem rpg) {
		this.handlePlayer = handlePlayer;
		this.rpg = rpg;
	}
	enum DamageChangeMode{
		BOTH,
		MIN,
		MAX;

		public String getDisplay() {
			return RPGProject.i18n("damage-change-mode." + this.name().toLowerCase());
		}
	}
	DamageChangeMode damageChangeMode = DamageChangeMode.BOTH;
	private void changeMode() {
		int i = this.damageChangeMode.ordinal() + 1;
		if(i >= DamageChangeMode.values().length) i = 0;
		this.damageChangeMode = DamageChangeMode.values()[i];
	}
	private void changeDamageMode() {
		int i = this.rpg.getDamageMode().ordinal() + 1;
		if(i >= DamageMode.values().length) i = 0;
		this.rpg.setDamageMode(DamageMode.values()[i]);
	}
	private void changeAttributeMode() {
		int i = this.rpg.getAttributeMode().ordinal() + 1;
		if(i >= AttributeMode.values().length) i = 0;
		this.rpg.setAttributeMode(AttributeMode.values()[i]);
	}
	private void changeEnchantMode() {
		int i = this.rpg.getEnchantMode().ordinal() + 1;
		if(i >= EnchantMode.values().length) i = 0;
		this.rpg.setEnchantMode(EnchantMode.values()[i]);
	}
	@Override
	public Inventory createGui(Player player) {
		Inventory inv = Bukkit.createInventory(null, this.raw * 9, RPGProject.i18n("gui.editor.title")
				.replace("%display%", this.rpg.getDisplayName())
				.replace("%name%", this.rpg.getName()));

		this.updateItems(inv);
		return inv;
	}
	private void runCmd(Player sender, String cmd) {
		Bukkit.getPluginManager().callEvent(new PlayerCommandPreprocessEvent(sender, cmd));
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
	
	@Override
	public void onClick(Player player, ItemStack clickedItem, int clickedSlot, InventoryView inv,
			InventoryClickEvent event) {
		if(!player.getName().equals(this.handlePlayer)) {
			event.setCancelled(true);
			this.remove = true;
			player.closeInventory();
			return;
		}
		boolean left = event.isLeftClick();
		boolean right = event.isRightClick();
		boolean shift = event.isShiftClick();
		if(clickedSlot < this.raw * 9) {
			event.setCancelled(true);
		}
		
		// 编辑物品名
		if(clickedSlot == 1 && left && !right && !shift) {
			PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.display-name")) {
			    @Override
				public void run() {
			    	GuiEditor.this.rpg.setDisplayName(this.getResponse());
			    	GuiEditor.this.saveItem();
			    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.display-name-1")
			    			.replace("%name%", GuiEditor.this.rpg.getName())
			    			.replace("%display%", this.getResponse()));
			    	
			    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
			    }
			});
			this.remove = true;
			player.closeInventory();
			return;
		}
		if(clickedSlot == 2) {
			if(!shift) {
				// 最后一行追加 Lore
				if(left && !right) {

					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.add-lore")) {
					    @Override
						public void run() {
					    	GuiEditor.this.rpg.addDescription(this.getResponse());
					    	GuiEditor.this.saveItem();
					    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.add-lore-1")
					    			.replace("%name%", GuiEditor.this.rpg.getName())
					    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
					    			.replace("%line%", String.valueOf(GuiEditor.this.rpg.getDescription().size() - 1))
					    			.replace("%content%", this.getResponse()));
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
				// 指定行插入 Lore
				if(right && !left) {
					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.insert-lore")) {
					    @Override
						public void run() {
					    	int line = Util.strToInt(this.getResponse(), -1);
					    	if(line < 0) {
						    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.insert-lore-1")
						    			.replace("%name%", GuiEditor.this.rpg.getName())
						    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
						    			.replace("%line%", this.getResponse()));
						    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    		return;
					    	}

							PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.insert-lore-2")) {
							    @Override
								public void run() {
							    	List<String> desc = GuiEditor.this.rpg.getDescription();
							    	desc.add(line, ChatColor.translateAlternateColorCodes('&', this.getResponse()));
							    	GuiEditor.this.rpg.setDescription(desc);
							    	GuiEditor.this.saveItem();
							    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.insert-lore-3")
							    			.replace("%name%", GuiEditor.this.rpg.getName())
							    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
							    			.replace("%line%", String.valueOf(line))
							    			.replace("%content%", this.getResponse()));
							    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
							    }
							});
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
			}
			else {
				// 删除最后一行 Lore
				if(left && !right) {

			    	List<String> desc = this.rpg.getDescription();
			    	int line = this.rpg.getDescription().size() - 1;
			    	desc.remove(line);
			    	this.rpg.setDescription(desc);
			    	this.saveItem();

			    	RPGProject.send(player, RPGProject.i18n("gui.editor.del-lore-last-line")
			    			.replace("%name%", GuiEditor.this.rpg.getName())
			    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
			    			.replace("%line%", String.valueOf(line)));
					this.updateItems(inv);
					return;
				}
				// 删除指定行 Lore
				if(right && !left) {
					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.del-lore")) {
					    @Override
						public void run() {
					    	int line = Util.strToInt(this.getResponse(), -1);
					    	if(line < 0) {
						    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.del-lore-1")
						    			.replace("%name%", GuiEditor.this.rpg.getName())
						    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
						    			.replace("%line%", this.getResponse()));
						    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    		return;
					    	}
					    	List<String> desc = GuiEditor.this.rpg.getDescription();
					    	desc.remove(line);
					    	GuiEditor.this.rpg.setDescription(desc);
					    	GuiEditor.this.saveItem();
					    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.del-lore-2")
					    			.replace("%name%", GuiEditor.this.rpg.getName())
					    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
					    			.replace("%line%", String.valueOf(line)));
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
			}
		}
		if(clickedSlot == 3 && left && !right && !shift) {
			PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.set-lore")) {
			    @Override
				public void run() {
			    	int line = Util.strToInt(this.getResponse(), -1);
			    	if(line < 0) {
				    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.set-lore-1")
				    			.replace("%name%", GuiEditor.this.rpg.getName())
				    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
				    			.replace("%line%", this.getResponse()));
				    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
			    		return;
			    	}

					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.set-lore-2")) {
					    @Override
						public void run() {
					    	List<String> desc = GuiEditor.this.rpg.getDescription();
					    	desc.set(line, ChatColor.translateAlternateColorCodes('&', this.getResponse()));
					    	GuiEditor.this.rpg.setDescription(desc);
					    	GuiEditor.this.saveItem();
					    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.set-lore-3")
					    			.replace("%name%", GuiEditor.this.rpg.getName())
					    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
					    			.replace("%line%", String.valueOf(line))
					    			.replace("%content%", this.getResponse()));
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
			    }
			});
			this.remove = true;
			player.closeInventory();
			return;
		}
		// 操作伤害
		if(clickedSlot == 4) {
			if(!shift) {
				int offset = (left && !right ? 1 : -1);
				int min = this.rpg.getDamageMin();
				int max = this.rpg.getDamageMax();
				// 伤害 +-1
				if(this.damageChangeMode == DamageChangeMode.MIN || this.damageChangeMode == DamageChangeMode.BOTH) {
					min += offset;
				}
				if(this.damageChangeMode == DamageChangeMode.MAX || this.damageChangeMode == DamageChangeMode.BOTH) {
					max += offset;
				}
				this.rpg.setDamage(min, max);
				this.saveItem();
				this.updateItems(inv);
				return;
			}
			else {
				// 切换模式
				if(left && !right) {
					this.changeMode();
					this.updateItems(inv);
					return;
				}
				// 手动输入伤害
				if(right && !left) {
					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.set-damage")) {
					    @Override
						public void run() {
							GuiEditor.this.runCmd(player, "rpgitem damage " + GuiEditor.this.rpg.getName() + " " + this.getResponse());
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
			}
		}
		// 修改护甲值
		if(clickedSlot == 5) {
			if(!shift) {
				if(left && !right) {
					this.rpg.setArmour(this.rpg.getArmour() + 1);
					this.saveItem();
					this.updateItems(inv);
					return;
				}
				if(right && !left) {
					this.rpg.setArmour(this.rpg.getArmour() - 1);
					this.saveItem();
					this.updateItems(inv);
					return;
				}
			}
			else {
				if(left && !right) {
					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.set-armour")) {
					    @Override
						public void run() {
							GuiEditor.this.runCmd(player, "rpgitem armour " + GuiEditor.this.rpg.getName() + " " + this.getResponse());
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
				if(right && !left) {
					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.set-armour-expression")) {
					    @Override
						public void run() {
							GuiEditor.this.runCmd(player, "rpgitem armorExpression " + GuiEditor.this.rpg.getName() + " " + this.getResponse());
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
			}
		}
		// 修改物品材质
		if(clickedSlot == 6) {
			if(!shift) {
				if(left && !right && clickedItem != null && clickedItem.getType() != Material.AIR) {
					this.rpg.setItem(clickedItem.getType());
					this.saveItem();
					this.updateItems(inv);
					return;
				}
				if(right && !left) {
					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.set-material")) {
					    @Override
						public void run() {
					    	Material material = Enums.valueOf(Material.class, this.getResponse().toUpperCase(), null);
					    	if(material == null) {
						    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.set-material-1")
						    			.replace("%name%", GuiEditor.this.rpg.getName())
						    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
						    			.replace("%material%", this.getResponse()));
					    	}
					    	else {
					    		GuiEditor.this.rpg.setItem(material);
					    		GuiEditor.this.saveItem();
						    	RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.set-material-2")
						    			.replace("%name%", GuiEditor.this.rpg.getName())
						    			.replace("%display%", GuiEditor.this.rpg.getDisplayName())
						    			.replace("%material%", material.name()));
					    	}
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
			}
		}
		// 修改伤害模式
		if(clickedSlot == 7) {
			if(!shift && left && !right) {
				this.changeDamageMode();
				this.saveItem();
				this.updateItems(inv);
			}
		}
		// 修改属性更新模式
		if(clickedSlot == 8) {
			if(!shift && left && !right) {
				this.changeAttributeMode();
				this.saveItem();
				this.updateItems(inv);
			}
		}
		// 修改附魔
		if(clickedSlot == 9) {
			if(!shift) {
				if(clickedItem == null || clickedItem.getType() != Material.ENCHANTED_BOOK) {
					if(left && !right) {
						this.changeEnchantMode();
						this.saveItem();
						this.updateItems(inv);
						return;
					}
				}
				else {
					// 添加/覆盖/移除 附魔
					Map<Enchantment, Integer> rpgEnchs = this.rpg.getEnchantMap();
					if(rpgEnchs == null) rpgEnchs = new HashMap<>();
					Map<Enchantment, Integer> enchs = Util.getEnchantsFromBook(clickedItem);
					if(left && !right) {
						for(Enchantment ench : enchs.keySet()) {
							rpgEnchs.put(ench, enchs.get(ench));
						}
					}
					if(right && !left) {
						for(Enchantment ench : enchs.keySet()) {
							if(rpgEnchs.containsKey(ench)) {
								rpgEnchs.remove(ench);
							}
						}
					}
					this.rpg.setEnchantMap(rpgEnchs);
					this.saveItem();
					this.updateItems(inv);
					return;
				}
			}
		}
		// 修改权限
		if(clickedSlot == 10) {
			if(!shift) {
				// 开关
				if (left && !right) {
					this.rpg.setHasPermission(this.rpg.isHasPermission());
					this.saveItem();
					this.updateItems(inv);
					return;
				}
				// 设置
				if (right && !left) {
					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.set-permission")) {
					    @Override
						public void run() {
					    	GuiEditor.this.rpg.setPermission(this.getResponse());
					    	GuiEditor.this.saveItem();
						    RPGProject.send(player, RPGProject.i18n("gui.editor.prompt.set-permission-1")
						    		.replace("%name%", GuiEditor.this.rpg.getName())
						    		.replace("%display%", GuiEditor.this.rpg.getDisplayName())
						    		.replace("%permission%", this.getResponse()));
					    	
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
			}
		}
		// 修改Lore显示
		if(clickedSlot == 11) {
			if(!shift) {
				// 基础属性
				if (left && !right) {
					this.rpg.setShowArmourLore(!this.rpg.isShowArmourLore());
					this.saveItem();
					this.updateItems(inv);
					return;
				}
				// 技能文本
				if (right && !left) {
					this.rpg.setShowPowerText(!this.rpg.isShowPowerText());
					this.saveItem();
					this.updateItems(inv);
					return;
				}
			}
		}
		// 自定义模型
		if(clickedSlot == 12) {
			if(!shift) {
				// 开关
				if(left && !right) {
					this.rpg.setCustomItemModel(!this.rpg.isCustomItemModel());
					this.saveItem();
					this.updateItems(inv);
					return;
				}
				// 数据值
				if(right && !left) {

					PromptRegistry.registerPrompt(new PlayerPrompt(player, RPGProject.i18n("gui.editor.prompt.set-custom-model-data")) {
					    @Override
						public void run() {
							GuiEditor.this.runCmd(player, "rpgitem customModel " + GuiEditor.this.rpg.getName() + " " + this.getResponse());
					    	RPGProject.getInstance().getGuiManager().openGui(player, new GuiEditor(handlePlayer, rpg));
					    }
					});
					this.remove = true;
					player.closeInventory();
					return;
				}
			}
		}
		// 忽略 WorldGuard
		if(clickedSlot == 13) {
			if(!shift && left && !right) {
				this.rpg.setIgnoreWorldGuard(!this.rpg.isIgnoreWorldGuard());
				this.saveItem();
				this.updateItems(inv);
				return;
			}
		}
		// 打开技能编辑器
		if(clickedSlot == 14) {
			if(!shift && left && !right) {
				this.remove = false;
				player.closeInventory();
				RPGProject.getInstance().getGuiManager().openGui(player, new GuiPowerList(player.getName(), this.rpg, 1));
				return;
			}
		}
		// 修改 ItemFlag
		if(clickedSlot == 15) {
			if(!shift && left && !right) {
				ItemFlag flag = ItemFlag.HIDE_ATTRIBUTES;
				List<ItemFlag> flags = this.rpg.getItemFlags();
				if(flags.contains(flag)) {
					flags.remove(flag);
				}
				else {
					flags.add(flag);
				}
				this.rpg.setItemFlags(flags);
				this.saveItem();
				this.updateItems(inv);
				return;
			}
		}
		if(clickedSlot == 16) {
			if(!shift && left && !right) {
				ItemFlag flag = ItemFlag.HIDE_DESTROYS;
				List<ItemFlag> flags = this.rpg.getItemFlags();
				if(flags.contains(flag)) {
					flags.remove(flag);
				}
				else {
					flags.add(flag);
				}
				this.rpg.setItemFlags(flags);
				this.saveItem();
				this.updateItems(inv);
				return;
			}
		}
		if(clickedSlot == 17) {
			if(!shift && left && !right) {
				ItemFlag flag = ItemFlag.HIDE_ENCHANTS;
				List<ItemFlag> flags = this.rpg.getItemFlags();
				if(flags.contains(flag)) {
					flags.remove(flag);
				}
				else {
					flags.add(flag);
				}
				this.rpg.setItemFlags(flags);
				this.saveItem();
				this.updateItems(inv);
				return;
			}
		}
		if(clickedSlot == 18) {
			if(!shift && left && !right) {
				ItemFlag flag = ItemFlag.HIDE_PLACED_ON;
				List<ItemFlag> flags = this.rpg.getItemFlags();
				if(flags.contains(flag)) {
					flags.remove(flag);
				}
				else {
					flags.add(flag);
				}
				this.rpg.setItemFlags(flags);
				this.saveItem();
				this.updateItems(inv);
				return;
			}
		}
		if(clickedSlot == 19) {
			if(!shift && left && !right) {
				ItemFlag flag = ItemFlag.HIDE_POTION_EFFECTS;
				List<ItemFlag> flags = this.rpg.getItemFlags();
				if(flags.contains(flag)) {
					flags.remove(flag);
				}
				else {
					flags.add(flag);
				}
				this.rpg.setItemFlags(flags);
				this.saveItem();
				this.updateItems(inv);
				return;
			}
		}
		if(clickedSlot == 20) {
			if(!shift && left && !right) {
				ItemFlag flag = ItemFlag.HIDE_UNBREAKABLE;
				List<ItemFlag> flags = this.rpg.getItemFlags();
				if(flags.contains(flag)) {
					flags.remove(flag);
				}
				else {
					flags.add(flag);
				}
				this.rpg.setItemFlags(flags);
				this.saveItem();
				this.updateItems(inv);
			}
		}
	}
	private Map<Integer, ItemStack> getGUIItems() {
		Map<Integer, ItemStack> items = new HashMap<>();
		items.put(0, this.rpg.toItemStack());
		items.put(1, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.edit-display-name.material"), Material.NAME_TAG),
				RPGProject.i18n("gui.editor.items.edit-display-name.name"),
				RPGProject.i18n_("gui.editor.items.edit-display-name.lore")));
		items.put(2, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.add-del-lore.material"), Material.BOOK),
				RPGProject.i18n("gui.editor.items.add-del-lore.name"),
				RPGProject.i18n_("gui.editor.items.add-del-lore.lore")));
		items.put(3, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.set-lore.material"), Material.BOOK),
				RPGProject.i18n("gui.editor.items.set-lore.name"),
				RPGProject.i18n_("gui.editor.items.set-lore.lore")));
		items.put(4, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.set-damage.material"), Material.IRON_SWORD),
				RPGProject.i18n("gui.editor.items.set-damage.name"),
				RPGProject.i18n_("gui.editor.items.set-damage.lore", Lists.newArrayList(
						new Pair<>("%damage%", this.rpg.getDamageMin() == this.rpg.getDamageMax() ? String.valueOf(this.rpg.getDamageMax()) : (this.rpg.getDamageMin() + "-" + this.rpg.getDamageMax())),
						new Pair<>("%mode%", this.damageChangeMode.getDisplay())
				))));
		items.put(5, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.set-armour.material"), Material.IRON_CHESTPLATE),
				RPGProject.i18n("gui.editor.items.set-armour.name"),
				RPGProject.i18n_("gui.editor.items.set-armour.lore", Lists.newArrayList(
						new Pair<>("%armour%", String.valueOf(this.rpg.getArmour())),
						new Pair<>("%armour_expression%", this.rpg.getArmourExpression())
				))));
		items.put(6, Util.buildItem(this.rpg.getItem(), RPGProject.i18n("gui.editor.items.set-material.name"),
				RPGProject.i18n_("gui.editor.items.set-material.lore")));
		items.put(7, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.change-damage-mode.material"), Material.STONE_SWORD), 
				RPGProject.i18n("gui.editor.items.change-damage-mode.name"),
				RPGProject.i18n_("gui.editor.items.change-damage-mode.lore", Lists.newArrayList(
						new Pair<>("%mode%", RPGProject.i18n("gui.editor.items.change-damage-mode.modes." + this.rpg.getDamageMode().name().toUpperCase()))
				))));
		items.put(8, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.change-attribute-mode.material"), Material.GOLDEN_APPLE), 
				RPGProject.i18n("gui.editor.items.change-attribute-mode.name"),
				RPGProject.i18n_("gui.editor.items.change-attribute-mode.lore", Lists.newArrayList(
						new Pair<>("%mode%", RPGProject.i18n("gui.editor.items.change-attribute-mode.modes." + this.rpg.getAttributeMode().name().toUpperCase()))
				))));
		items.put(9, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.enchant.material"), Material.ENCHANTED_BOOK), 
				RPGProject.i18n("gui.editor.items.enchant.name"),
				this.getEnchantLore()));
		items.put(10, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.permission.material"), Material.IRON_INGOT),
				RPGProject.i18n("gui.editor.items.permission.name"),
				RPGProject.i18n_("gui.editor.items.permission.lore", Lists.newArrayList(
						new Pair<>("%has%", this.rpg.isHasPermission() ? RPGProject.i18n("gui.editor.items.permission.true") : RPGProject.i18n("gui.editor.items.permission.false")),
						new Pair<>("%permission%", this.rpg.getPermission())
				))));
		items.put(11, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.set-lore-visible.material"), Material.LEVER),
				RPGProject.i18n("gui.editor.items.set-lore-visible.name"),
				RPGProject.i18n_("gui.editor.items.set-lore-visible.lore", Lists.newArrayList(
						new Pair<>("%show_armour_lore%", this.rpg.isShowArmourLore() ? RPGProject.i18n("gui.editor.items.set-lore-visible.true") : RPGProject.i18n("gui.editor.items.set-lore-visible.false")),
						new Pair<>("%show_power_lore%", this.rpg.isShowPowerText() ? RPGProject.i18n("gui.editor.items.set-lore-visible.true") : RPGProject.i18n("gui.editor.items.set-lore-visible.false"))
				))));
		items.put(12, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.set-custom-model.material"), Material.LEATHER_CHESTPLATE),
				RPGProject.i18n("gui.editor.items.set-custom-model.name"),
				RPGProject.i18n_("gui.editor.items.set-custom-model.lore", Lists.newArrayList(
						new Pair<>("%switch%", this.rpg.isCustomItemModel() ? RPGProject.i18n("gui.editor.items.set-custom-model.true") : RPGProject.i18n("gui.editor.items.set-custom-model.false")),
						new Pair<>("%data%", String.valueOf(this.rpg.getCustomModelData()))
				))));
		items.put(13, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.set-ignore-worldguard.material"), Material.BARRIER),
				RPGProject.i18n("gui.editor.items.set-ignore-worldguard.name"),
				RPGProject.i18n_("gui.editor.items.set-ignore-worldguard.lore", Lists.newArrayList(
						new Pair<>("%switch%", this.rpg.isIgnoreWorldGuard() ? RPGProject.i18n("gui.editor.items.set-ignore-worldguard.true") : RPGProject.i18n("gui.editor.items.set-ignore-worldguard.false"))
				))));
		items.put(14, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.edit-powers.material"), Material.NETHER_STAR), 
				RPGProject.i18n("gui.editor.items.edit-powers.name"),
				RPGProject.i18n_("gui.editor.items.edit-powers.lore")));
		items.put(15, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.edit-item-flag.material"), Material.PAPER),
				RPGProject.i18n("gui.editor.items.edit-item-flag.name").replace("%flag%", RPGProject.i18n("gui.editor.items.edit-item-flag.flags.HIDE_ATTRIBUTES")),
				RPGProject.i18n_("gui.editor.items.edit-item-flag.lore", Lists.newArrayList(
						new Pair<>("%value%", this.rpg.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES) ? RPGProject.i18n("gui.editor.items.edit-item-flag.true") : RPGProject.i18n("gui.editor.items.edit-item-flag.false"))
				))));
		items.put(16, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.edit-item-flag.material"), Material.PAPER),
				RPGProject.i18n("gui.editor.items.edit-item-flag.name").replace("%flag%", RPGProject.i18n("gui.editor.items.edit-item-flag.flags.HIDE_DESTROYS")),
				RPGProject.i18n_("gui.editor.items.edit-item-flag.lore", Lists.newArrayList(
						new Pair<>("%value%", this.rpg.getItemFlags().contains(ItemFlag.HIDE_DESTROYS) ? RPGProject.i18n("gui.editor.items.edit-item-flag.true") : RPGProject.i18n("gui.editor.items.edit-item-flag.false"))
				))));
		items.put(17, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.edit-item-flag.material"), Material.PAPER),
				RPGProject.i18n("gui.editor.items.edit-item-flag.name").replace("%flag%", RPGProject.i18n("gui.editor.items.edit-item-flag.flags.HIDE_ENCHANTS")),
				RPGProject.i18n_("gui.editor.items.edit-item-flag.lore", Lists.newArrayList(
						new Pair<>("%value%", this.rpg.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS) ? RPGProject.i18n("gui.editor.items.edit-item-flag.true") : RPGProject.i18n("gui.editor.items.edit-item-flag.false"))
				))));
		items.put(18, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.edit-item-flag.material"), Material.PAPER),
				RPGProject.i18n("gui.editor.items.edit-item-flag.name").replace("%flag%", RPGProject.i18n("gui.editor.items.edit-item-flag.flags.HIDE_PLACED_ON")),
				RPGProject.i18n_("gui.editor.items.edit-item-flag.lore", Lists.newArrayList(
						new Pair<>("%value%", this.rpg.getItemFlags().contains(ItemFlag.HIDE_PLACED_ON) ? RPGProject.i18n("gui.editor.items.edit-item-flag.true") : RPGProject.i18n("gui.editor.items.edit-item-flag.false"))
				))));
		items.put(19, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.edit-item-flag.material"), Material.PAPER),
				RPGProject.i18n("gui.editor.items.edit-item-flag.name").replace("%flag%", RPGProject.i18n("gui.editor.items.edit-item-flag.flags.HIDE_POTION_EFFECTS")),
				RPGProject.i18n_("gui.editor.items.edit-item-flag.lore", Lists.newArrayList(
						new Pair<>("%value%", this.rpg.getItemFlags().contains(ItemFlag.HIDE_POTION_EFFECTS) ? RPGProject.i18n("gui.editor.items.edit-item-flag.true") : RPGProject.i18n("gui.editor.items.edit-item-flag.false"))
				))));
		items.put(20, Util.buildItem(Enums.valueOf(Material.class, RPGProject.i18n("gui.editor.items.edit-item-flag.material"), Material.PAPER),
				RPGProject.i18n("gui.editor.items.edit-item-flag.name").replace("%flag%", RPGProject.i18n("gui.editor.items.edit-item-flag.flags.HIDE_UNBREAKABLE")),
				RPGProject.i18n_("gui.editor.items.edit-item-flag.lore", Lists.newArrayList(
						new Pair<>("%value%", this.rpg.getItemFlags().contains(ItemFlag.HIDE_UNBREAKABLE) ? RPGProject.i18n("gui.editor.items.edit-item-flag.true") : RPGProject.i18n("gui.editor.items.edit-item-flag.false"))
				))));
		return items;
	}

	private List<String> getEnchantLore(){
		List<String> result = new ArrayList<>();
		for(String s :  RPGProject.i18n_("gui.editor.items.enchant.lore")) {
			s = s.replace("%mode%", RPGProject.i18n("gui.editor.items.enchant.modes." + this.rpg.getEnchantMode().name().toUpperCase()));
			
			if(s.contains("%enchants%")) {
				if(this.rpg.getEnchantMap() != null && !this.rpg.getEnchantMap().isEmpty()) {
					for(Enchantment ench : this.rpg.getEnchantMap().keySet()) {
						result.add(s.replace("%enchants%", Util.getEnchName(ench) + " " + Util.getRomanNumber(this.rpg.getEnchantMap().get(ench))));
					}
					continue;
				}
			}
			result.add(s.replace("%enchants%", RPGProject.i18n("gui.editor.items.enchant.nah")));
		}
		return result;
	}

	public void saveItem() {
		ItemManager.refreshItem();
        ItemManager.save(this.rpg);
	}

	@Override
	public boolean onClose(Player player, InventoryView inv, InventoryCloseEvent event) {
		return this.remove;
	}
}
