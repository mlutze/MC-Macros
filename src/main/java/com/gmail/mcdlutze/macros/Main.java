package com.gmail.mcdlutze.macros;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	static Main main;
	
	Map<Player, String> dictators = new HashMap<Player, String>();
	
	private Listener playerJoinListener = new Listener() {
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent e) {
			loadMacros(e.getPlayer());
		}
	};
	
	private Listener playerQuitListener = new Listener() {
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent e) {
			dictators.remove(e.getPlayer());
			saveMacros(e.getPlayer());
		}
	};
	
	private Listener dictationListener = new Listener() {
		@EventHandler
		public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
			Player player = e.getPlayer();
			if (dictators.containsKey(player)) {
				if (e.getMessage().equals("//")) {
					dictators.remove(player);
				} else {
					String macro = dictators.get(player);
					Utilities.getMacro(macro, player).add(e.getMessage());
				}
				e.setCancelled(true);
			}
		}
		@EventHandler
		public void onPlayerChat(AsyncPlayerChatEvent e) {
			Player player = e.getPlayer();
			if (dictators.containsKey(player)) {
				String macro = dictators.get(player);
				Utilities.getMacro(macro, player).add(e.getMessage());
				e.setCancelled(true);
			}
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
		getServer().getPluginManager().registerEvents(dictationListener, main);
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
