package com.gmail.mcdlutze.macros;

import java.util.List;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	static Main main;
	
	private Listener playerJoinListener = new Listener() {
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent e) {
			loadMacros(e.getPlayer());
		}
	};
	
	private Listener playerQuitListener = new Listener() {
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent e) {
			saveMacros(e.getPlayer());
		}
	};

	@Override
	public void onEnable() {
		main = this;
		PluginCommand pluginCommand = getCommand("macro");
		MacroExecutor macroExecutor = new MacroExecutor();
		pluginCommand.setExecutor(macroExecutor);
		pluginCommand.setTabCompleter(macroExecutor);
		getServer().getPluginManager().registerEvents(playerJoinListener, main);
		getServer().getPluginManager().registerEvents(playerQuitListener, main);
	}

	@Override
	public void onDisable() {
		saveMacros();
	}

	private void saveMacros() {
		for (Player player : getServer().getOnlinePlayers()) {
			saveMacros(player, false);
		}
		saveConfig();
	}

	private void saveMacros(Player player, boolean write) {
		MacroSet macroSet = Utilities.getMacroSet(player);
		ConfigurationSection section = getConfig().createSection(player.getUniqueId().toString());
		for (String macro : macroSet.keySet()) {
			section.set(macro, (List<String>) macroSet.get(macro));
		}
		if (write) {
			saveConfig();
		}
	}
	
	private void saveMacros(Player player) {
		saveMacros(player, true);
	}

	private void loadMacros(Player player) {
		ConfigurationSection section = getConfig().getConfigurationSection(player.getUniqueId().toString());
		MacroSet macroSet = new MacroSet();
		for (String macro : section.getKeys(false)) {
			macroSet.put(macro, section.getStringList(macro));
		}
		Utilities.setMacroSet(player, macroSet);
	}
	
}
