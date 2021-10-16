package top.mrxiaom.rpgproject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.cyr1en.cp.listener.Prompt;
import com.cyr1en.cp.PromptRegistry;
import com.google.common.collect.Lists;

/**
 * 没有条件就自己创造条件
 *
 * @author MrXiaoM
 */
public abstract class PlayerPrompt extends Prompt implements Runnable{
	String cancelKey;
	String response = "";
	public PlayerPrompt(Player sender, String msg) {
		super(RPGProject.getInstance().getCmdPrompter(), sender, new LinkedList<>(Lists.newArrayList(msg.replace("%cancel%", RPGProject.getInstance().getCancelKey()))), "");
        this.cancelKey = RPGProject.getInstance().getCancelKey();
	}
	public String getResponse() {
		return this.response;
	}
	@Override
    @EventHandler(priority=EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(this.getSender())) {
            return;
        }
        if (event.getMessage().equalsIgnoreCase(this.cancelKey)) {
            this.cancel();
        } else {
        	this.response = event.getMessage();
        	Bukkit.getScheduler().runTask(RPGProject.getInstance(), this::run);
            this.shutdownScheduler();
            PromptRegistry.deregisterPrompt(this);
        }
        event.setCancelled(true);
    }
	private void cancel() {
		try {
			Method m = Prompt.class.getDeclaredMethod("cancel");
			m.setAccessible(true);
			m.invoke(this);
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}
	private void shutdownScheduler() {
		try {
			Field field = Prompt.class.getDeclaredField("scheduler");
			field.setAccessible(true);
			Object obj = field.get(this);
			ExecutorService.class.getDeclaredMethod("shutdownNow").invoke(obj);
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}
}