package com.gmail.mcdlutze.macros.commandexecutor;

import com.gmail.mcdlutze.macros.argument.*;
import com.gmail.mcdlutze.macros.manager.DictatorManager;
import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class DictateCommandExecutor implements CommandExecutor, TabCompleter {

    private final MacroSetManager macroSetManager;
    private final DictatorManager dictatorManager;
    private final ArgumentsParser argumentsParser;
    private final ArgumentsVerifier argumentsVerifier;

    public DictateCommandExecutor(MacroSetManager macroSetManager, DictatorManager dictatorManager) {
        this.macroSetManager = macroSetManager;
        this.dictatorManager = dictatorManager;
        this.argumentsParser = ArgumentsParser.newBuilder().withUnknownMacroName().build();
        this.argumentsVerifier =
                ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireUnknownMacroName().build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ParsedArguments parsedArguments = argumentsParser.parse(args);
        VerifiedArguments verifiedArguments;
        try {
            verifiedArguments = argumentsVerifier.verify(sender, parsedArguments);
        } catch (ArgumentsVerificationException e) {
            sender.sendMessage(e.getMessage());
            return false;
        }

        Player player = verifiedArguments.getPlayer().get();
        String macroName = verifiedArguments.getUnknownMacroName().get();
        Macro macro = new Macro(macroName);

        macroSetManager.getMacroSet(player).putMacro(macroName, macro);
        dictatorManager.startDictating(player, macro);
        player.sendMessage(String.format("Dictating macro \"%s\".", macroName));
        player.sendMessage("Enter \"//\" to stop dictating.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
