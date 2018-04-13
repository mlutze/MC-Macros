package com.gmail.mcdlutze.macros.manager;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class MacroRunnerManager {
    private final Set<Player> macroRunners = new HashSet<>();

    public boolean isRunning(Player player) {
        return macroRunners.contains(player);
    }

    public void startRunning(Player player) {
        macroRunners.add(player);
    }

    public void stopRunning(Player player) {
        macroRunners.remove(player);
    }
}
