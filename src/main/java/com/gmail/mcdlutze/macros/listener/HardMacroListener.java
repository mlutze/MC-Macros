package com.gmail.mcdlutze.macros.listener;

import com.gmail.mcdlutze.macros.macro.MacroSet;
import com.gmail.mcdlutze.macros.manager.DictatorManager;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class HardMacroListener implements Listener {

    private final MacroSetManager macroSetManager;
    private final DictatorManager dictatorManager;
    private final Command runCommand;

    public HardMacroListener(MacroSetManager macroSetManager, DictatorManager dictatorManager, Command runCommand) {
        this.macroSetManager = macroSetManager;
        this.dictatorManager = dictatorManager;
        this.runCommand = runCommand;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (dictatorManager.isDictating(player)) {
            return;
        }

        String[] args = e.getMessage().split("\\s");

        String command = args[0];
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        args[0] = command;

        MacroSet macroSet = macroSetManager.getMacroSet(player);
        if (macroSet.containsMacro(command) && macroSet.getMacro(command).isHard()) {
            runCommand.execute(player, "macrorunhard", args);
            e.setCancelled(true);
        }
    }
}
