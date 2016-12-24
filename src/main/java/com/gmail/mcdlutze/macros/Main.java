package com.gmail.mcdlutze.macros;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	static Main main;

	@Override
	public void onEnable() {
		main = this;
		getCommand("macro").setExecutor(new MacroExecutor());
	}
}
