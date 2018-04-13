package com.gmail.mcdlutze.macros.commandexecutor;

import com.gmail.mcdlutze.macros.argument.*;
import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CopyCommandExecutor implements CommandExecutor, TabCompleter {

    private final MacroSetManager macroSetManager;
    private final ArgumentsParser argumentsParser;
    private final ArgumentsVerifier argumentsVerifier;

    public CopyCommandExecutor(MacroSetManager macroSetManager) {
        this.macroSetManager = macroSetManager;
        argumentsParser = ArgumentsParser.newBuilder().withKnownMacroName().withUnknownMacroName().build();
        argumentsVerifier = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                .requireUnknownMacroName().build();
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
        Macro oldMacro = verifiedArguments.getKnownMacro().get();
        String newMacroName = verifiedArguments.getUnknownMacroName().get();
        Macro newMacro = oldMacro.copy();
        newMacro.rename(newMacroName);
        macroSetManager.getMacroSet(player).putMacro(newMacroName, newMacro);

        player.sendMessage(String.format("Macro \"%s\" copied to new macro \"%s\".", oldMacro.getName(), newMacroName));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ParsedArguments parsedArguments = argumentsParser.parse(Arrays.copyOfRange(args, 0, args.length - 1));
        VerifiedArguments verifiedArguments = argumentsVerifier.verifyQuietly(sender, parsedArguments);

        VerifiedArgument<Player> player = verifiedArguments.getPlayer();
        if (!player.isValid()) {
            return Collections.emptyList();
        }

        VerifiedArgument<Macro> macro = verifiedArguments.getKnownMacro();
        if (!macro.isPresent()) {
            return new ArrayList<>(macroSetManager.getMacroSet(player.get()).names());
        }

        return Collections.emptyList();

    }
}
