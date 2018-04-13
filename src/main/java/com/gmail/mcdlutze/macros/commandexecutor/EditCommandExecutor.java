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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditCommandExecutor implements CommandExecutor, TabCompleter {

    private final MacroSetManager macroSetManager;
    private final ArgumentsParser argumentsParser;
    private final ArgumentsVerifier argumentsVerifier;

    public EditCommandExecutor(MacroSetManager macroSetManager) {
        this.macroSetManager = macroSetManager;
        this.argumentsParser =
                ArgumentsParser.newBuilder().withKnownMacroName().withMacroLineNumber().withText().build();
        this.argumentsVerifier =
                ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                        .requireLineNumber().requireText().build();
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
        int line = verifiedArguments.getLineNumber().get();
        String text = verifiedArguments.getText().get();
        macro.editLine(line, text);

        player.sendMessage(String.format("Line %d edited in macro \"%s\".", line, macro.getName()));
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

        if (!macro.isValid()) {
            return Collections.emptyList();
        }

        VerifiedArgument<Integer> lineNumber = verifiedArguments.getLineNumber();
        if (!lineNumber.isPresent()) {
            return IntStream.range(0, macro.get().length()).mapToObj(String::valueOf).collect(Collectors.toList());
        }

        if (!lineNumber.isValid()) {
            return Collections.emptyList();
        }

        VerifiedArgument<String> text = verifiedArguments.getText();
        if (!text.isPresent()) {
            return Collections.singletonList(macro.get().getLine(lineNumber.get()));
        }

        return Collections.emptyList();
    }
}
