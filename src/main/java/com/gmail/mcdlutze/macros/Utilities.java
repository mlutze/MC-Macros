package com.gmail.mcdlutze.macros;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utilities {
	
	public static final String METADATA_KEY = "macros";

	public static boolean confirm(String confirmation, CommandSender confirmee) {
		return closeCommand(confirmation, confirmee, true);
	}

	public static boolean deny(String denial, CommandSender deniee) {
		return closeCommand(denial, deniee, false);
	}

	public static boolean closeCommand(String string, CommandSender sender, boolean confirmed) {
		sender.sendMessage(string);
		return confirmed;
	}

	public static String join(String delimiter, List<String> list) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String string : list) {
			if (!first) {
				sb.append(delimiter);
			}
			sb.append(string);
			first = false;
		}
		return sb.toString();
	}

	public static MacroSet getMacroSet(Player player) {
		MacroSet macros;
		try {
			macros = (MacroSet) player.getMetadata(METADATA_KEY).get(0);
		} catch (IndexOutOfBoundsException e) {
			macros = new MacroSet();
			player.setMetadata(METADATA_KEY, macros);
		}
		return macros;
	}
	
	public static void setMacroSet(Player player, MacroSet macroSet) {
		player.removeMetadata(METADATA_KEY, Main.main);
		player.setMetadata(METADATA_KEY, macroSet);
	}

	public static List<String> getMacro(String name, Player player) {
		try {
			return getMacroSet(player).get(name);
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static List<String> fillTemplate(List<String> template, List<String> values) {
		String delimiter = "\0";
		String joined = join(delimiter, template);
		int count = 0;
		for (String value : values) {
			joined = joined.replace(String.format("{%d}", count++), value);
		}
		return Arrays.asList(joined.split(delimiter));
	}
	
}
