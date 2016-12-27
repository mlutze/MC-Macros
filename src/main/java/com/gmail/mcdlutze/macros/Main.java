package com.gmail.mcdlutze.macros;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	static Main main;

	@Override
	public void onEnable() {
		main = this;
		PluginCommand pluginCommand = getCommand("macro");
		MacroExecutor macroExecutor = new MacroExecutor();
		pluginCommand.setExecutor(macroExecutor);
		pluginCommand.setTabCompleter(macroExecutor);
	}

	
}
