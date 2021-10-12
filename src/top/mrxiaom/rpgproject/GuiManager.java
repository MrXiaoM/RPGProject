package top.mrxiaom.rpgproject;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

import top.mrxiaom.rpgproject.gui.IGui;

public class GuiManager implements Listener{
	RPGProject main;
	Map<String, IGui> playerGui = new HashMap<>();
	public GuiManager(RPGProject main) {
		this.main = main;
	}

	public void onDisable() {
		for(String p : this.playerGui.keySet()) {
			Player player = Bukkit.getPlayer(p);
			if(player != null && player.isOnline()) {
				player.closeInventory();
				RPGProject.send(player, "&e插件被卸载，正在关闭界面");
			}
		}
		this.playerGui.clear();
	}

	public InventoryView openGui(Player player, IGui gui) {
		System.out.println("open");
		if(this.playerGui.containsKey(player.getName())) {
			this.playerGui.remove(player.getName());
			System.out.println("remove o");
		}
		this.playerGui.put(player.getName(), gui);
		//System.out.println("put o");
		return player.openInventory(gui.createGui(player));
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if(this.playerGui.containsKey(player.getName())) {
			//System.out.println("玩家 " + player.getName() + " 点击GUI " + this.playerGui.get(player.getName()));
			this.playerGui.get(player.getName()).onClick(player, event.getView().getCursor(), event.getRawSlot(), event.getView(), event);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(!(event.getPlayer() instanceof Player)) return;
		Player player = (Player) event.getPlayer();
		if(this.playerGui.containsKey(player.getName())) {
			IGui gui = this.playerGui.get(player.getName());
			if(gui.onClose(player, event.getView(), event)) {
				this.playerGui.remove(player.getName());
				System.out.println("remove c");
			}
		}
	}
}
