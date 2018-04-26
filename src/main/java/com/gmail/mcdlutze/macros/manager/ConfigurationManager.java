package com.gmail.mcdlutze.macros.manager;

import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.macro.MacroSet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Set;

public class ConfigurationManager {
    private static final String PLAYER_DATA_KEY = "PLAYER_DATA";
    private static final String MACROS_KEY = "MACROS";
    private static final String MACRO_NAME_KEY = "MACRO_NAME";
    private static final String MACRO_CONTENTS_KEY = "MACRO_CONTENTS";
    private static final String MACRO_HARD_KEY = "MACRO_HARD";

    private final Plugin plugin;

    public ConfigurationManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public MacroSet getMacroSet(Player player) {
        String playerMacrosSectionName = String.join(".", PLAYER_DATA_KEY, player.getUniqueId().toString(), MACROS_KEY);
        ConfigurationSection playerMacrosSection;
        if (plugin.getConfig().isConfigurationSection(playerMacrosSectionName)) {
            playerMacrosSection = plugin.getConfig().getConfigurationSection(playerMacrosSectionName);
        } else {
            playerMacrosSection = plugin.getConfig().createSection(playerMacrosSectionName);
        }

        Set<String> macroKeys = playerMacrosSection.getKeys(false);
        MacroSet macroSet = new MacroSet();
        for (String macroKey : macroKeys) {
            ConfigurationSection config = playerMacrosSection.getConfigurationSection(macroKey);
            macroSet.putMacro(macroKey, createMacroFromConfig(config));
        }

        return macroSet;
    }

    public void putMacroSet(Player player, MacroSet macroSet) {
        String playerMacrosSectionName = String.join(".", PLAYER_DATA_KEY, player.getUniqueId().toString(), MACROS_KEY);
        ConfigurationSection playerMacrosSection = plugin.getConfig().createSection(playerMacrosSectionName);

        for (Macro macro : macroSet.macros()) {
            createConfigFromMacro(playerMacrosSection, macro);
        }
    }

    private ConfigurationSection getOrCreateSection(String... keys) {
        String path = String.join(".", keys);
        if (plugin.getConfig().isConfigurationSection(path)) {
            return plugin.getConfig().getConfigurationSection(path);
        } else {
            return plugin.getConfig().createSection(path);
        }
    }

    private Macro createMacroFromConfig(ConfigurationSection config) {
        String macroName = config.getString(MACRO_NAME_KEY);
        List<String> macroContents = config.getStringList(MACRO_CONTENTS_KEY);
        boolean macroHard = config.getBoolean(MACRO_HARD_KEY);
        return new Macro(macroName, macroContents, macroHard);
    }

    private void createConfigFromMacro(ConfigurationSection parent, Macro macro) {
        ConfigurationSection config = parent.createSection(macro.getName());
        config.set(MACRO_NAME_KEY, macro.getName());
        config.set(MACRO_CONTENTS_KEY, macro.getLines());
        config.set(MACRO_HARD_KEY, macro.isHard());
    }

}
