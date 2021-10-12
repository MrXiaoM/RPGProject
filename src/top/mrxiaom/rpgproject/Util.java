package top.mrxiaom.rpgproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemFactory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import think.rpgitems.I18n;
import think.rpgitems.RPGItems;
import think.rpgitems.power.Power;
import top.mrxiaom.rpgproject.gui.IGui;

public class Util {

	public static void openGuiSync(Player player, IGui gui) {
		Bukkit.getScheduler().runTask(RPGProject.getInstance(), new Runnable() {
			public void run() {
		    	RPGProject.getInstance().getGuiManager().openGui(player, gui);
			}
		});
	}

	public static String i18n(String key, Object... args) {
		for(String lang : RPGItems.plugin.cfg.enabledLanguages) {
			I18n i18n = I18n.getInstance(lang);
			if(i18n.hasKey(key)) {
				return i18n.format(key, args);
			}
		}
		return key;
	}
	public static String i18nEmptyWhenNotFound(String key, Object... args) {
		for(String lang : RPGItems.plugin.cfg.enabledLanguages) {
			I18n i18n = I18n.getInstance(lang);
			if(i18n.hasKey(key)) {
				return i18n.format(key, args);
			}
		}
		return "";
	}
	
	/**
	 * RPGItems 全技能图标
	 *
	 * @author MrXiaoM
	 * */
	public static Material getPowerIcon(Class<? extends Power> power) {
		if(power.equals(think.rpgitems.power.impl.Airborne.class)) {
			return Material.ELYTRA;
		}
		if(power.equals(think.rpgitems.power.impl.AOE.class)) {
			return Material.LINGERING_POTION;
		}
		if(power.equals(think.rpgitems.power.impl.AOECommand.class)) {
			return Material.COMMAND_BLOCK_MINECART;
		}
		if(power.equals(think.rpgitems.power.impl.AOEDamage.class)) {
			return Material.WOODEN_SWORD;
		}
		if(power.equals(think.rpgitems.power.impl.Arrows.class)) {
			return Material.ARROW;
		}
		if(power.equals(think.rpgitems.power.impl.Attachments.class)) {
			return Material.COMPARATOR;
		}
		if(power.equals(think.rpgitems.power.impl.Attract.class)) {
			return Material.SLIME_BALL;
		}
		if(power.equals(think.rpgitems.power.impl.Beam.class)) {
			return Material.END_ROD;
		}
		if(power.equals(think.rpgitems.power.impl.CancelBowArrow.class)) {
			return Material.BOW;
		}
		if(power.equals(think.rpgitems.power.impl.Charge.class)) {
			return Material.GOLDEN_BOOTS;
		}
		if(power.equals(think.rpgitems.power.impl.Command.class) || power.equals(think.rpgitems.power.impl.CommandHit.class)) {
			return Material.COMMAND_BLOCK;
		}
		if(power.equals(think.rpgitems.power.impl.Consume.class) || power.equals(think.rpgitems.power.impl.ConsumeHit.class)) {
			return Material.YELLOW_DYE;
		}
		if(power.equals(think.rpgitems.power.impl.CriticalHit.class)) {
			return Material.IRON_SWORD;
		}
		if(power.equals(think.rpgitems.power.impl.DeathCommand.class)) {
			return Material.SKELETON_SKULL;
		}
		if(power.equals(think.rpgitems.power.impl.Deflect.class)) {
			return Material.SHIELD;
		}
		if(power.equals(think.rpgitems.power.impl.DelayedCommand.class)) {
			return Material.CLOCK;
		}
		if(power.equals(think.rpgitems.power.impl.Dummy.class)) {
			return Material.BARRIER;
		}
		if(power.equals(think.rpgitems.power.impl.Economy.class)) {
			return Material.GOLD_NUGGET;
		}
		if(power.equals(think.rpgitems.power.impl.EnchantedHit.class)) {
			return Material.ENCHANTING_TABLE;
		}
		if(power.equals(think.rpgitems.power.impl.EvalDamage.class)) {
			return Material.GOLDEN_SWORD;
		}
		if(power.equals(think.rpgitems.power.impl.Explosion.class)) {
			return Material.TNT;
		}
		if(power.equals(think.rpgitems.power.impl.Fire.class)) {
			return Material.CAMPFIRE;
		}
		if(power.equals(think.rpgitems.power.impl.FireballPower.class)) {
			return Material.FIRE_CHARGE;
		}
		if(power.equals(think.rpgitems.power.impl.Flame.class)) {
			return Material.LAVA_BUCKET;
		}
		if(power.equals(think.rpgitems.power.impl.Food.class)) {
			return Material.COOKED_PORKCHOP;
		}
		if(power.equals(think.rpgitems.power.impl.ForceField.class)) {
			return Material.RED_STAINED_GLASS;
		}
		if(power.equals(think.rpgitems.power.impl.Glove.class)) {
			return Material.GHAST_SPAWN_EGG;
		}
		if(power.equals(think.rpgitems.power.impl.GunFu.class)) {
			return Material.CROSSBOW;
		}
		if(power.equals(think.rpgitems.power.impl.Headshot.class)) {
			return Material.PLAYER_HEAD;
		}
		if(power.equals(think.rpgitems.power.impl.Ice.class)) {
			return Material.ICE;
		}
		if(power.equals(think.rpgitems.power.impl.Knockup.class)) {
			return Material.BLAZE_ROD;
		}
		if(power.equals(think.rpgitems.power.impl.LifeSteal.class)) {
			return Material.APPLE;
		}
		if(power.equals(think.rpgitems.power.impl.Lightning.class)) {
			return Material.IRON_HELMET;
		}
		if(power.equals(think.rpgitems.power.impl.Mount.class)) {
			return Material.SADDLE;
		}
		if(power.equals(think.rpgitems.power.impl.NoImmutableTick.class)) {
			return Material.DIAMOND_SWORD;
		}
		if(power.equals(think.rpgitems.power.impl.ParticleBarrier.class)) {
			return Material.RED_DYE;
		}
		if(power.equals(think.rpgitems.power.impl.ParticlePower.class)) {
			return Material.CYAN_DYE;
		}
		if(power.equals(think.rpgitems.power.impl.ParticleTick.class)) {
			return Material.LEATHER_CHESTPLATE;
		}
		if(power.equals(think.rpgitems.power.impl.PotionHit.class)) {
			return Material.SPLASH_POTION;
		}
		if(power.equals(think.rpgitems.power.impl.PotionSelf.class) || power.equals(think.rpgitems.power.impl.PotionTick.class)) {
			return Material.POTION;
		}
		if(power.equals(think.rpgitems.power.impl.ProjectilePower.class)) {
			return Material.SNOWBALL;
		}
		if(power.equals(think.rpgitems.power.impl.Pumpkin.class)) {
			return Material.CARVED_PUMPKIN;
		}
		if(power.equals(think.rpgitems.power.impl.Rainbow.class)) {
			return Material.PINK_WOOL;
		}
		if(power.equals(think.rpgitems.power.impl.RealDamage.class)) {
			return Material.DIAMOND_SWORD;
		}
		if(power.equals(think.rpgitems.power.impl.Repair.class)) {
			return Material.ANVIL;
		}
		if(power.equals(think.rpgitems.power.impl.Rescue.class)) {
			return Material.TOTEM_OF_UNDYING;
		}
		if(power.equals(think.rpgitems.power.impl.Rumble.class)) {
			return Material.GOLDEN_AXE;
		}
		if(power.equals(think.rpgitems.power.impl.Scoreboard.class)) {
			return Material.NAME_TAG;
		}
		if(power.equals(think.rpgitems.power.impl.ShulkerBulletPower.class)) {
			return Material.PURPLE_SHULKER_BOX;
		}
		if(power.equals(think.rpgitems.power.impl.SkyHook.class)) {
			return Material.FISHING_ROD;
		}
		if(power.equals(think.rpgitems.power.impl.SoundPower.class)) {
			return Material.NOTE_BLOCK;
		}
		if(power.equals(think.rpgitems.power.impl.Stuck.class)) {
			return Material.CHAINMAIL_CHESTPLATE;
		}
		if(power.equals(think.rpgitems.power.impl.Teleport.class)) {
			return Material.ENDER_PEARL;
		}
		if(power.equals(think.rpgitems.power.impl.Throw.class)) {
			return Material.OAK_BOAT;
		}
		if(power.equals(think.rpgitems.power.impl.TippedArrows.class)) {
			return Material.TIPPED_ARROW;
		}
		if(power.equals(think.rpgitems.power.impl.TNTCannon.class)) {
			return Material.TNT_MINECART;
		}
		if(power.equals(think.rpgitems.power.impl.Torch.class)) {
			return Material.TORCH;
		}
		if(power.equals(think.rpgitems.power.impl.Translocator.class)) {
			return Material.NETHER_STAR;
		}
		return Material.BOOK;
	}

	public static Map<Enchantment, Integer> getEnchantsFromBook(ItemStack item){
		if(item != null && item.getType() == Material.ENCHANTED_BOOK) {
			ItemMeta im = item.getItemMeta();
			if(im == null) im = CraftItemFactory.instance().getItemMeta(Material.ENCHANTED_BOOK);
			return ((org.bukkit.inventory.meta.EnchantmentStorageMeta)im).getStoredEnchants();
		}
		return new HashMap<>();
	}
	public static void setItemDisplayName(ItemStack item, String name) {
		if(item == null) return;
		ItemMeta im = item.getItemMeta() == null ? CraftItemFactory.instance().getItemMeta(item.getType()) : item.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		item.setItemMeta(im);
	}
	private static final Integer[] frameSmallSlots = new Integer[] {
			 0,   1,   2,   3,   4,   5,   6,   7,   8,
			 9,/*10,  11,  12,  13,  14,  15,  16,*/17,
			18,  19,  20,  21,  22,  23,  24,  25,  26
	};
	public static void setFrameItemsSmall(Inventory inv, ItemStack item) {
		for(int slot : frameSmallSlots) {
			inv.setItem(slot, item);
		}
	}
	public static ItemStack buildFrameItem(Material material) {
		return buildItem(material, "&f&l*", Lists.newArrayList());
	}
	public static ItemStack buildItem(Material material, String name) {
		return buildItem(material, name, Lists.newArrayList());
	}
	public static ItemStack buildItem(Material material, String name, String... lore) {
		return buildItem(material, name, Lists.newArrayList(lore));
	}
	public static ItemStack buildItem(Material material, String name, List<String> lore) {
		ItemStack item = new ItemStack(material, 1);
		ItemMeta im = CraftItemFactory.instance().getItemMeta(material);
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		if(!lore.isEmpty()) {
			List<String> l = new ArrayList<>();
			for(String s : lore) {
				l.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', s));
			}
			im.setLore(l);
		}
		item.setItemMeta(im);
		return item;
	}
	private static final Integer[] frameSlots = new Integer[] {
			 0,   1,   2,   3,   4,   5,   6,   7,   8,
			 9,/*10,  11,  12,  13,  14,  15,  16,*/17,
			18,/*19,  20,  21,  22,  23,  24,  25,*/26,
			27,/*28,  29,  30,  31,  32,  33,  34,*/35,
			36,/*37,  38,  39,  40,  41,  42,  43,*/44,
			45,  46,  47,  48,  49,  50,  51,  52,  53
	};
	public static void setFrameItems(Inventory inv, ItemStack item) {
		for(int slot : frameSlots) {
			inv.setItem(slot, item);
		}
	}
	public static void setRowItems(Inventory inv, int row, ItemStack item) {
		for(int i = 0; i < 8; i++) {
			inv.setItem(((row-1) * 9) + i, item);
		}
	}
	public static ItemStack getEnchantedBook(Enchantment ench, int level) {
		Map<Enchantment, Integer> map = new HashMap<>();
		map.put(ench, level);
		return getEnchantedBook(map);
	}

	public static ItemStack getEnchantedBook(Map<Enchantment, Integer> map) {
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta im = item.getItemMeta();
		if(im == null) im = CraftItemFactory.instance().getItemMeta(Material.ENCHANTED_BOOK);
		for(Enchantment ench : map.keySet()) {
			((org.bukkit.inventory.meta.EnchantmentStorageMeta)im).addStoredEnchant(ench, map.get(ench), true);
		}
		item.setItemMeta(im);
		return item;
	}

	public static String getRomanNumber(int level) {
		switch(level) {
			case 0: return "";
			case 1: return "I";
			case 2: return "II";
			case 3: return "III";
			case 4: return "IV";
			case 5: return "V";
			case 6: return "VI";
			case 7: return "VII";
			case 8: return "VIII";
			case 9: return "IX";
			case 10: return "X";
			case 11: return "XI";
			case 12: return "XII";
			case 13: return "XIII";
			case 14: return "XIV";
			case 15: return "XV";
			case 16: return "XVI";
			case 17: return "XVII";
			case 18: return "XVIII";
			case 19: return "XIX";
			case 20: return "XX";
			default:
				return String.valueOf(level);
		}
	}

	@SuppressWarnings("deprecation")
	public static String getEnchName(Enchantment ench) {
		switch(ench.getName().toUpperCase()) {
			case "PROTECTION_ENVIRONMENTAL":
				return "保护";
			case "PROTECTION_FIRE":
				return "火焰保护";
			case "PROTECTION_FALL":
				return "摔落保护";
			case "PROTECTION_EXPLOSIONS":
				return "爆炸保护";
			case "PROTECTION_PROJECTILE":
				return "弹射物保护";
			case "OXYGEN":
				return "水下呼吸";
			case "WATER_WORKER":
				return "水下速掘";
			case "THORNS":
				return "荆棘";
			case "DEPTH_STRIDER":
				return "深海探索者";
			case "FROST_WALKER":
				return "冰霜行者";
			case "BINDING_CURSE":
				return "绑定诅咒";
			case "DAMAGE_ALL":
				return "锋利";
			case "DAMAGE_UNDEAD":
				return "亡灵杀手";
			case "DAMAGE_ARTHROPODS":
				return "节肢杀手";
			case "KNOCKBACK":
				return "击退";
			case "FIRE_ASPECT":
				return "火焰附加";
			case "LOOT_BONUS_MOBS":
				return "抢夺";
			case "SWEEPING_EDGE":
				return "横扫之刃";
			case "DIG_SPEED":
				return "效率";
			case "SILK_TOUCH":
				return "精准采集";
			case "DURABILITY":
				return "耐久";
			case "LOOT_BONUS_BLOCKS":
				return "时运";
			case "ARROW_DAMAGE":
				return "力量";
			case "ARROW_KNOCKBACK":
				return "冲击";
			case "ARROW_FIRE":
				return "火矢";
			case "ARROW_INFINITE":
				return "无限";
			case "LUCK":
				return "海之眷顾";
			case "LURE":
				return "钓饵";
			case "LOYALTY":
				return "忠诚";
			case "IMPALING":
				return "穿刺";
			case "RIPTIDE":
				return "激流";
			case "CHANNELING":
				return "引雷";
			case "MULTISHOT":
				return "多重射击";
			case "QUICK_CHARGE":
				return "快速装填";
			case "PIERCING":
				return "穿透";
			case "MENDING":
				return "经验修补";
			case "VANISHING_CURSE":
				return "消失诅咒";
			default:
				return ench.getName();
		}
	}
	public static int strToInt(String str, int nullValue) {
		try {
			return Integer.parseInt(str);
		} catch(Throwable t) {
			return nullValue;
		}
	}
}
