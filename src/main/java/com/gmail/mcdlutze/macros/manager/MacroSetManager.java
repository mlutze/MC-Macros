package com.gmail.mcdlutze.macros.manager;

import com.gmail.mcdlutze.macros.macro.MacroSet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class MacroSetManager {

    private final Plugin plugin;
    private final ConfigurationManager configurationManager;
    private final Map<Player, MacroSet> macroSets;

    public MacroSetManager(Plugin plugin, ConfigurationManager configurationManager) {
        this.plugin = plugin;
        this.configurationManager = configurationManager;
        this.macroSets = new HashMap<>();
    }

    public MacroSet getMacroSet(Player player) {
        return macroSets.computeIfAbsent(player, p -> new MacroSet());
    }

    public void putMacroSet(Player player, MacroSet macroSet) {
        macroSets.put(player, macroSet);
    }

    public void loadMacros(Player player) {
        MacroSet macroSet = configurationManager.getMacroSet(player);
        putMacroSet(player, macroSet);
    }

    public void saveMacros(Player player) {
        MacroSet macroSet = getMacroSet(player);
        configurationManager.putMacroSet(player, macroSet);
    }

    public void writeMacros() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            saveMacros(player);
        }
        configurationManager.saveConfig();
    }
}
