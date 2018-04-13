package com.gmail.mcdlutze.macros.manager;

import com.gmail.mcdlutze.macros.macro.Macro;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DictatorManager {
    private final Map<Player, Macro> dictators = new HashMap<>();

    public boolean isDictating(Player player) {
        return dictators.containsKey(player);
    }

    public Macro getMacroForPlayer(Player player) {
        return dictators.get(player);
    }

    public void stopDictating(Player player) {
        dictators.remove(player);
    }

    public void startDictating(Player player, Macro macro) {
        dictators.put(player, macro);
    }
}
