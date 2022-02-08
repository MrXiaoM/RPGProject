package top.mrxiaom.rpgproject.prompt;

import org.bukkit.entity.Player;

public interface IPrompt {
    Player getPlayer();

    boolean putPromptResult(String result);

    void startPrompt();

    void finishPrompt();

    void cancelPrompt();

    String getCancelKey();
}
