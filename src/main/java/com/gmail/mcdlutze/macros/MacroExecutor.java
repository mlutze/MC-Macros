package com.gmail.mcdlutze.macros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.gmail.mcdlutze.macros.Utilities;

public class MacroExecutor implements CommandExecutor, TabCompleter {

	private String[] helpTemplates = { "help {sub-command}", "new {macro name} {text}", "add {macro name} [{text}]",
			"remove {macro name}", "list", "view {macro name}", "run {macro name} [{arguments}]" };

	private String[] searchCommands = { "add", "remove", "run", "view" };

	private Map<String, String> helpNotes = new HashMap<String, String>();

	public MacroExecutor() {
		helpNotes.put("help", "Display help information.");
		helpNotes.put("new", "Create a new macro. Use \"{number}\" to allow arguments.");
		helpNotes.put("add", "Add an additional line to the macro. Use \"{number}\" to allow arguments.");
		helpNotes.put("remove", "Remove a macro.");
		helpNotes.put("run", "Run a macro with the given arguments");
		helpNotes.put("view", "View a particular macro.");
		helpNotes.put("list", "List your macros.");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			return Utilities.deny("This command can only be executed by a player", sender);
		}
		
		if (label.equals("mr")) {
			player.chat("/macro run " + Utilities.join(" ", Arrays.asList(args)));
			return true;
		}

		if (args.length == 0) {
			return false;
		}

		String subCommand = args[0];

		if (subCommand.equalsIgnoreCase("help")) {
			if (args.length == 1) {
				for (String template : helpTemplates) {
					player.sendMessage("/macro " + template);
				}
				return true;
			}

			if (helpNotes.containsKey(args[1])) {
				return Utilities.confirm(helpNotes.get(args[1]), player);
			} else {
				return Utilities.deny(String.format("\"%s\" is not a valid sub-command", args[1]), player);
			}
		}

		MacroSet macros = Utilities.getMacroSet(player);

		if (subCommand.equalsIgnoreCase("list")) {
			if (macros.isEmpty()) {
				return Utilities.deny("You do not have any macros.", player);
			}
			player.sendMessage("Listing macros.");
			for (String macro : macros.keySet()) {
				player.sendMessage(macro);
			}
			return true;
		}

		if (args.length < 2) {
			return false;
		}

		String macroName = args[1];

		if (subCommand.equalsIgnoreCase("new")) {

			if (macros.containsKey(macroName)) {
				return Utilities.deny(String.format("A macro with the name \"%s\" already exists.", macroName), player);
			}

			if (args.length == 2) {
				macros.newMacro(macroName);
			} else {
				macros.newMacro(macroName, Utilities.join(" ", getArgList(args)));
			}
			return Utilities.confirm(String.format("Created macro \"%s\".", macroName), player);
		}

		if (subCommand.equalsIgnoreCase("add")) {
			if (args.length < 3) {
				return false;
			}

			if (macros.containsKey(macroName)) {
				macros.addLine(macroName, Utilities.join(" ", getArgList(args)));
				return Utilities.confirm(String.format("Line added to macro \"%s\".", macroName), player);
			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equalsIgnoreCase("remove")) {
			if (macros.containsKey(macroName)) {
				macros.remove(macroName);
				return Utilities.confirm(String.format("Macro \"%s\" removed.", macroName), player);
			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equalsIgnoreCase("run")) {

			if (macros.containsKey(macroName)) {
				List<String> template = macros.get(macroName);
				List<String> lines = Utilities.fillTemplate(template, getArgList(args));

				for (String line : lines) {
					if (line.toLowerCase().startsWith("/macro ") || line.toLowerCase().startsWith("/mr ")) {
						return Utilities.confirm("You cannot call a macro from a macro.", player);
					}
				}

				player.sendMessage(String.format("Running macro \"%s\".", macroName));

				for (String line : lines) {
					player.chat(line);
				}
				return true;

			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equalsIgnoreCase("view")) {
			if (macros.containsKey(macroName)) {
				player.sendMessage(String.format("Viewing macro \"%s\".", macroName));
				for (String line : macros.get(macroName)) {
					player.sendMessage(line);
				}
				return true;
			} else {
				return denyExistence(macroName, player);
			}
		}
		return false;
	}

	private boolean denyExistence(String name, Player player) {
		return Utilities.deny(String.format("The macro \"%s\" does not exist.", name), player);
	}

	private List<String> getArgList(String[] args) {
		if (args.length < 3) {
			return new ArrayList<String>(0);
		}
		return Arrays.asList(args).subList(2, args.length);
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		
		List<String> list = new LinkedList<String>();
		String subCommand = "";
		if (args.length >= 1) {
			subCommand = args[0];
		}
		if (args.length <= 1) {
			for (String key : helpNotes.keySet()) {
				if (key.startsWith(subCommand)) {
					list.add(key);
				}
			}
			return list;
		}
		if (args.length == 2) {
			for (String sc : searchCommands) {
				if (sc.equalsIgnoreCase(subCommand)) {
					for (String macro : Utilities.getMacroSet(player).keySet()) {
						list.add(macro);
					}
					return list;
				}
			}
		}
		return null;
	}
}
