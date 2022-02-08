package top.mrxiaom.rpgproject.prompt;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.mrxiaom.rpgproject.RPGProject;

import java.util.HashMap;
import java.util.Map;

public class PromptManager extends PacketAdapter {
    RPGProject plugin;
    Map<String, IPrompt> prompts = new HashMap<>();

    public PromptManager(RPGProject plugin) {
        super(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.CHAT);
        this.plugin = plugin;
        plugin.getProtocolManager().addPacketListener(this);
    }

    public void runPrompt(IPrompt prompt) {
        if (prompt == null)
            return;
        if (prompts.containsKey(prompt.getPlayer().getName()))
            return;
        prompts.put(prompt.getPlayer().getName(), prompt);
        prompt.startPrompt();
    }

    public boolean isPrompting(Player player) {
        return isPrompting(player.getName());
    }

    public boolean isPrompting(String player) {
        return prompts.containsKey(player);
    }

    public void cancelPrompt(Player player) {
        if (!isPrompting(player))
            return;
        prompts.get(player.getName()).cancelPrompt();
        prompts.remove(player.getName());
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        if (isPrompting(player)) {
            IPrompt prompt = prompts.get(player.getName());
            String chatMsg = event.getPacket().getStrings().read(0);
            // 同步
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (chatMsg.equalsIgnoreCase(prompt.getCancelKey())) {
                    prompt.cancelPrompt();
                    prompts.remove(player.getName());
                } else if (prompt.putPromptResult(chatMsg)) {
                    prompt.finishPrompt();
                    prompts.remove(player.getName());
                }
            });
            event.setCancelled(true);
        }
    }
}
