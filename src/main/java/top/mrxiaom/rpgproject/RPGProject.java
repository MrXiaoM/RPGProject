package top.mrxiaom.rpgproject;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import think.rpgitems.RPGItems;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;
import top.mrxiaom.rpgproject.gui.GuiEditor;
import top.mrxiaom.rpgproject.gui.IGui;
import top.mrxiaom.rpgproject.prompt.PromptManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RPGProject extends JavaPlugin {
    public static void send(String... msg) {
        send(Bukkit.getConsoleSender(), msg);
    }

    public static void send(CommandSender sender, String... msg) {
        send(sender, Lists.newArrayList(msg));
    }

    public static void send(CommandSender sender, List<String> msg) {
        for (String s : msg) {
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
        if (instance.getConfig().contains("lang." + key)) {
            for (String s : instance.getConfig().getStringList("lang." + key)) {
                String str = ChatColor.translateAlternateColorCodes('&', s);
                for (Pair<String, String> target : replacePairs) {
                    str = str.replace(target.getKey(), target.getValue());
                }
                result.add(str);
            }
        }
        return result;
    }

    ProtocolManager protocolManager;
    PromptManager promptManager;
    GuiManager guiManager;
    private static RPGProject instance;

    public static RPGProject getInstance() {
        return RPGProject.instance;
    }

    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public PromptManager getPromptManager() {
        return promptManager;
    }

    int power_prop_preview_items = 6;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.promptManager = new PromptManager(this);
        Bukkit.getPluginManager().registerEvents(this.guiManager = new GuiManager(this), this);
        instance = this;
    }

    @Override
    public void onDisable() {
        if (this.guiManager != null) {
            this.guiManager.onDisable();
        }
        if (protocolManager != null) {
            this.protocolManager.removePacketListeners(this);
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.power_prop_preview_items = this.getConfig().getInt("power-prop-preview-items", 6);
        // do sth
    }

    private RPGItem getItem(String[] args, Player player) {
        if (args.length >= 2) {
            return ItemManager.getItem(args[1]).orElse(null);
        }
        return ItemManager.toRPGItem(player.getInventory().getItemInMainHand()).orElse(null);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp())
            return true;
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            this.saveDefaultConfig();
            this.reloadConfig();
            send(sender, i18n("reload"));
            return true;
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("edit")) {
            if (!(sender instanceof Player)) {
                send(sender, i18n("player-only"));
                return true;
            }
            Player player = (Player) sender;
            RPGItem rpgItem = this.getItem(args, player);
            if (rpgItem == null) {
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
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            if ("edit".startsWith(args[0].toLowerCase()))
                result.add("edit");
            if ("reload".startsWith(args[0].toLowerCase()))
                result.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            Collection<RPGItem> items = ItemManager.items();
            for (RPGItem item : items) {
                if (item.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(item.getName() + " " + item.getDisplayName().replaceAll("&.|§.", ""));
                }
            }
        }
        return result;
    }
}
