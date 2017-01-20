package com.gmail.mcdlutze.macros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.gmail.mcdlutze.macros.Utilities;

public class MacroExecutor implements CommandExecutor, TabCompleter {

	private String[] helpTemplates = { "help [{sub-command}]", "new {macro name} [{text}]", "add {macro name} {text}",
			"edit {macro name} {line} {text}", "insert {macro name} {line} {text}", "remove {macro name} {line}",
			"delete {macro name}", "rename {old name} {new name}", "copy {macro name}, {copy name}",
			"dictate {macro name}", "list", "view {macro name}", "run {macro name} [{arguments}]" };

	private String[] searchCommands = { "add", "copy", "edit", "delete", "insert", "remove", "rename", "run", "view" };

	private Map<String, String> helpNotes = new HashMap<String, String>();
	private Map<String, Integer> textStart = new HashMap<String, Integer>();

	public MacroExecutor() {
		helpNotes.put("help", "Display help information.");
		helpNotes.put("new", "Create a new macro. Use \"{number}\" to allow arguments.");
		helpNotes.put("add", "Add an additional line to a macro. Use \"{number}\" to allow arguments.");
		helpNotes.put("delete", "Delete a macro.");
		helpNotes.put("run", "Run a macro with the given arguments");
		helpNotes.put("view", "View a particular macro.");
		helpNotes.put("list", "List your macros.");
		helpNotes.put("edit", "Edit an existing macro.");
		helpNotes.put("insert", "Insert a line into a macro.");
		helpNotes.put("remove", "Remove a line from a macro.");
		helpNotes.put("rename", "Rename a macro.");
		helpNotes.put("copy", "Copy a macro.");
		helpNotes.put("dictate", "Begin recording a new macro through chat.");

		textStart.put("add", 2);
		textStart.put("new", 2);
		textStart.put("edit", 3);
		textStart.put("insert", 3);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			return Utilities.deny("This command can only be executed by a player", sender);
		}

		// handle shortcut
		if (label.equalsIgnoreCase("mr")) {
			String[] temp = new String[args.length + 1];
			System.arraycopy(args, 0, temp, 1, args.length);
			temp[0] = "run";
			args = temp;
		}

		// ensure there is a sub-command and generify it
		if (args.length == 0) {
			return false;
		}
		String subCommand = args[0].toLowerCase();

		// first handle sub-commands with no arguments
		if (subCommand.equals("help")) {
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

		if (subCommand.equals("list")) {
			if (macros.isEmpty()) {
				return Utilities.deny("You do not have any macros.", player);
			}
			player.sendMessage("Listing macros.");
			for (String macro : macros.keySet()) {
				player.sendMessage(macro);
			}
			return true;
		}

		// ensure there is at least one argument
		if (args.length < 2) {
			return false;
		}

		String macroName = args[1];

		if (subCommand.equals("new")) {

			if (macros.containsKey(macroName)) {
				return assertExistence(macroName, player);
			}

			if (args.length == 2) {
				macros.newMacro(macroName);
			} else {
				macros.newMacro(macroName, getText(subCommand, args));
			}
			return Utilities.confirm(String.format("Created macro \"%s\".", macroName), player);
		}

		if (subCommand.equals("add")) {
			if (args.length < 3) {
				return false;
			}

			if (macros.containsKey(macroName)) {
				macros.addLine(macroName, getText(subCommand, args));
				return Utilities.confirm(String.format("Line added to macro \"%s\".", macroName), player);
			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equals("edit")) {
			if (args.length < 3) {
				return false;
			}
			int line;
			try {
				line = Integer.parseInt(args[2]);
				if (line < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				return false;
			}

			if (macros.containsKey(macroName)) {
				String text = getText(subCommand, args);
				try {
					macros.editLine(macroName, line, text);
				} catch (IndexOutOfBoundsException e) {
					return Utilities.deny(String.format("Macro \"%s\" does not have line %d.", macroName, line),
							player);
				}
				if (text.isEmpty()) {
					return Utilities.confirm(String.format("Line %d deleted in macro \"%s\".", line, macroName),
							player);
				} else {
					return Utilities.confirm(String.format("Line %d edited in macro \"%s\".", line, macroName), player);
				}
			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equals("insert")) {
			if (args.length < 3) {
				return false;
			}
			int line;
			try {
				line = Integer.parseInt(args[2]);
				if (line < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				return false;
			}

			if (macros.containsKey(macroName)) {
				String text = getText(subCommand, args);
				if (text.isEmpty()) {
					return Utilities.deny("Cannot insert empty line.", player);
				}
				try {
					macros.insertLine(macroName, line, text);
				} catch (IndexOutOfBoundsException e) {
					return Utilities.deny(String.format("Macro \"%s\" does not have line %d.", macroName, line),
							player);
				}
				return Utilities.confirm(String.format("Line %d inserted into macro \"%s\".", line, macroName), player);

			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equals("remove")) {
			if (args.length < 3) {
				return false;
			}
			int line;
			try {
				line = Integer.parseInt(args[2]);
				if (line < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				return false;
			}

			if (macros.containsKey(macroName)) {
				try {
					macros.removeLine(macroName, line);
				} catch (IndexOutOfBoundsException e) {
					return Utilities.deny(String.format("Macro \"%s\" does not have line %d.", macroName, line),
							player);
				}
				return Utilities.confirm(String.format("Line %d removed from macro \"%s\".", line, macroName), player);

			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equals("delete")) {
			if (macros.containsKey(macroName)) {
				macros.remove(macroName);
				return Utilities.confirm(String.format("Macro \"%s\" deleted.", macroName), player);
			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equals("rename")) {
			if (args.length < 3) {
				return false;
			}
			if (macros.containsKey(macroName)) {
				if (macros.containsKey(args[2])) {
					return assertExistence(args[2], player);
				}
				macros.rename(macroName, args[2]);
				return Utilities.confirm(String.format("Macro \"%s\" renamed to \"%s\".", macroName, args[2]), player);
			} else {
				return denyExistence(macroName, player);
			}
		}

		if (subCommand.equals("copy")) {
			if (args.length < 3) {
				return false;
			}
			if (macros.containsKey(macroName)) {
				if (macros.containsKey(args[2])) {
					return assertExistence(args[2], player);
				}
				macros.copy(macroName, args[2]);
				return Utilities.confirm(String.format("Macro \"%s\" copied to \"%s\".", macroName, args[2]), player);
			} else {
				return denyExistence(macroName, player);
			}
		}
		
		if (subCommand.equals("dictate")) {
			if (macros.containsKey(macroName)) {
				return assertExistence(macroName, player);
			}
			macros.newMacro(macroName);
			Main.main.dictators.put(player, macroName);
			player.sendMessage(String.format("Dictating new macro \"%s\".", macroName));
			return Utilities.confirm("Type \"//\" to end dictation.", player);
		}

		if (subCommand.equals("run")) {
			if (macros.containsKey(macroName)) {
				List<String> template = macros.get(macroName);
				List<String> macroArgs;
				try {
					macroArgs = Utilities.combineQuotedArgs(getArgs(args));
				} catch (IllegalArgumentException e) {
					return Utilities.confirm("Error: unmatched quotation mark in argument list.", player);
				}
				List<String> lines = Utilities.fillTemplate(template, macroArgs);
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

		if (subCommand.equals("view")) {
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

	private boolean assertExistence(String name, Player player) {
		return Utilities.deny(String.format("A macro with the name \"%s\" already exists.", name), player);
	}

	private List<String> getArgs(String[] args) {
		if (args.length < 3) {
			return new ArrayList<String>(0);
		}
		return Arrays.asList(args).subList(2, args.length);
	}

	private String getText(String subCommand, String[] args) {
		List<String> words = Arrays.asList(args).subList(textStart.get(subCommand), args.length);
		return Utilities.join(" ", words);
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;

		if (label.equalsIgnoreCase("mr")) {
			String[] temp = new String[args.length + 1];
			System.arraycopy(args, 0, temp, 1, args.length);
			temp[0] = "run";
			args = temp;
		}

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
						if (macro.startsWith(args[1])) {
							list.add(macro);
						}
					}
					return list;
				}
			}
		}
		if (subCommand.equals("edit")) {
			List<String> macroLines = Utilities.getMacro(args[1], player);
			if (macroLines != null) {
				if (args.length == 3) {
					for (int i = 0; i < macroLines.size(); i++) {
						list.add(String.format("%d", i));
					}
					return list;
				}
				if (args.length == 4) {
					try {
						int line = Integer.parseInt(args[2]);
						list.add(macroLines.get(line));
						return list;
					} catch (NumberFormatException e) {
						// swallow
					}
				}
			}
		}
		if (subCommand.equals("insert")) {
			List<String> macroLines = Utilities.getMacro(args[1], player);
			if (macroLines != null) {
				if (args.length == 3) {
					for (int i = 0; i <= macroLines.size(); i++) {
						list.add(String.format("%d", i));
					}
					return list;
				}
			}
		}
		if (subCommand.equals("remove")) {
			List<String> macroLines = Utilities.getMacro(args[1], player);
			if (macroLines != null) {
				if (args.length == 3) {
					for (int i = 0; i < macroLines.size(); i++) {
						list.add(String.format("%d", i));
					}
					return list;
				}
			}
		}
		return list;
	}
}
