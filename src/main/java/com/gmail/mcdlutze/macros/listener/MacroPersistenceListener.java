package com.gmail.mcdlutze.macros.listener;

import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class MacroPersistenceListener implements Listener {

    private final MacroSetManager macroSetManager;

    public MacroPersistenceListener(MacroSetManager macroSetManager) {
        this.macroSetManager = macroSetManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        macroSetManager.loadMacros(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        macroSetManager.saveMacros(e.getPlayer());
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent e) {
        macroSetManager.writeMacros();
    }

}
