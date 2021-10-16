package top.mrxiaom.rpgproject;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.cyr1en.cp.CommandPrompter;
import com.google.common.collect.Lists;
import cat.nyaa.nyaacore.Pair;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;
import top.mrxiaom.rpgproject.gui.GuiEditor;
import top.mrxiaom.rpgproject.gui.IGui;

public class RPGProject extends JavaPlugin{
	public static void send(String... msg) {
		send(Bukkit.getConsoleSender(), msg);
	}
	public static void send(CommandSender sender, String... msg) {
		send(sender, Lists.newArrayList(msg));
	}
	public static void send(CommandSender sender, List<String> msg) {
		for(String s : msg) {
			sender.sendMessage(i18n("prefix") + ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	public static String i18n(String key) {
		return ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("lang." + key, key));
	}

	public static List<String> i18n_(String key) {
		return i18n_(key, Lists.newArrayList());
	}
	public static List<String> i18n_(String key, List<Pair<String, String>> replacePairs) {
		List<String> result = new ArrayList<>();
		if(instance.getConfig().contains("lang." + key)) {
			for(String s : instance.getConfig().getStringList("lang." + key)) {
				String str = ChatColor.translateAlternateColorCodes('&', s);
				for(Pair<String, String> target : replacePairs) {
					str = str.replace(target.getKey(), target.getValue());
				}
				result.add(str);
			}
		}
		return result;
	}
	
	CommandPrompter cmdPrompter;
	GuiManager guiManager;
	private static RPGProject instance;
	public static RPGProject getInstance() {
		return RPGProject.instance;
	}
	public GuiManager getGuiManager() {
		return this.guiManager;
	}
	public String getCancelKey() {
		return this.cmdPrompter.getConfiguration().getString("Cancel-Keyword");
	}
	int power_prop_preview_items = 6;
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.reloadConfig();
		this.cmdPrompter = (CommandPrompter) Bukkit.getPluginManager().getPlugin("CommandPrompter");
		Bukkit.getPluginManager().registerEvents(this.guiManager = new GuiManager(this), this);
		instance = this;
	}

	@Override
	public void onDisable() {
		if(this.guiManager != null) {
			this.guiManager.onDisable();
		}
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		this.power_prop_preview_items = this.getConfig().getInt("power-prop-preview-items", 6);
		// do sth
	}

	private RPGItem getItem(String[] args, Player player) {
		if(args.length == 2) {
			return ItemManager.getItem(args[1]).orElse(null);
		}
		return ItemManager.toRPGItemByMeta(player.getInventory().getItemInMainHand()).orElse(null);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			this.saveDefaultConfig();
			this.reloadConfig();
			send(sender, i18n("reload"));
			return true;
		}
		if(args.length == 2 && args[0].equalsIgnoreCase("edit")) {
			if(!(sender instanceof Player)) {
				send(sender, i18n("player-only"));
				return true;
			}
			Player player = (Player) sender;
			RPGItem rpgItem = this.getItem(args, player);
			if(rpgItem == null) {
				send(player, i18n("rpgitem-no-found"));
			}
			IGui gui = new GuiEditor(player.getName(), rpgItem);
			player.closeInventory();
			this.guiManager.openGui(player, gui);
			return true;

		}
		send(sender, i18n_("help"));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
		List<String> result = new ArrayList<>();
		if(args.length == 1) {
			if("edit".startsWith(args[0].toLowerCase())) result.add("edit");
			if("reload".startsWith(args[0].toLowerCase())) result.add("reload");
		}
		else if(args.length == 2 && args[0].equalsIgnoreCase("edit")) {
			for(String name : ItemManager.itemNames()) {
				if(name.toLowerCase().startsWith(args[1].toLowerCase())) {
					result.add(name);
				}
			}
		}
		return result;
	}
	public CommandPrompter getCmdPrompter() {
		return this.cmdPrompter;
	}
}
