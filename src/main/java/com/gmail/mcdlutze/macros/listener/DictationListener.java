package com.gmail.mcdlutze.macros.listener;

import com.gmail.mcdlutze.macros.manager.DictatorManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DictationListener implements Listener {

    private final DictatorManager dictatorManager;

    public DictationListener(DictatorManager dictatorManager) {
        this.dictatorManager = dictatorManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        onPlayerSend(e.getPlayer(), e.getMessage(), e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        onPlayerSend(e.getPlayer(), e.getMessage(), e);
    }

    private void onPlayerSend(Player player, String message, Cancellable e) {
        if (!dictatorManager.isDictating(player)) {
            return;
        }

        e.setCancelled(true);

        if (message.equals("//")) {
            dictatorManager.stopDictating(player);
        } else {
            dictatorManager.getMacroForPlayer(player).addLine(message);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        dictatorManager.stopDictating(e.getPlayer());
    }
}
