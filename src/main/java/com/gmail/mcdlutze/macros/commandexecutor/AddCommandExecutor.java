package com.gmail.mcdlutze.macros.commandexecutor;

import com.gmail.mcdlutze.macros.argument.*;
import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddCommandExecutor implements CommandExecutor, TabCompleter {

    private final MacroSetManager macroSetManager;
    private final ArgumentsParser argumentsParser;
    private final ArgumentsVerifier argumentsVerifier;

    public AddCommandExecutor(MacroSetManager macroSetManager) {
        this.macroSetManager = macroSetManager;
        this.argumentsParser = ArgumentsParser.newBuilder().withKnownMacroName().withText().build();
        this.argumentsVerifier =
                ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                        .requireText().build();
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
        Macro macro = verifiedArguments.getKnownMacro().get();
        String text = verifiedArguments.getText().get();
        macro.addLine(text);

        player.sendMessage(String.format("Line added to macro \"%s\".", macro.getName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ParsedArguments parsedArguments = argumentsParser.parse(Arrays.copyOfRange(args, 0, args.length - 1));
        VerifiedArguments verifiedArguments = argumentsVerifier.verifyQuietly(sender, parsedArguments);
        String prefix = args[args.length - 1];

        VerifiedArgument<Player> player = verifiedArguments.getPlayer();
        if (!player.isValid()) {
            return Collections.emptyList();
        }

        VerifiedArgument<Macro> macro = verifiedArguments.getKnownMacro();
        if (!macro.isPresent()) {
            return macroSetManager.getMacroSet(player.get()).names().stream().filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();

    }
}
