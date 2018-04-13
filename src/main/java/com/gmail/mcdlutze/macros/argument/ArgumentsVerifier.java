package com.gmail.mcdlutze.macros.argument;

import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.macro.MacroSet;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

public class ArgumentsVerifier {

    private final MacroSetManager macroSetManager;
    private final boolean requireKnownMacroName;
    private final boolean requireUnknownMacroName;
    private final boolean requireLineNumber;
    private final boolean requireText;

    private ArgumentsVerifier(Builder builder) {
        Objects.requireNonNull(builder.macroSetManager);
        requireKnownMacroName = builder.requireKnownMacroName;
        requireUnknownMacroName = builder.requireUnknownMacroName;
        requireLineNumber = builder.requireLineNumber;
        requireText = builder.requireText;
        macroSetManager = builder.macroSetManager;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public VerifiedArguments verify(CommandSender sender, ParsedArguments parsedArguments)
            throws ArgumentsVerificationException {
        return verify(sender, parsedArguments, false);
    }

    public VerifiedArguments verifyQuietly(CommandSender sender, ParsedArguments parsedArgs) {
        try {
            return verify(sender, parsedArgs, true);
        } catch (ArgumentsVerificationException e) {
            throw new RuntimeException("This should not be possible.", e);
        }
    }

    private VerifiedArguments verify(CommandSender sender, ParsedArguments parsedArguments, boolean quiet)
            throws ArgumentsVerificationException {
        VerifiedArguments.Builder verifiedArguments = VerifiedArguments.newBuilder();

        MacroSet macroSet;
        VerifiedArgument<Player> player = verifyPlayer(sender, quiet);
        verifiedArguments.withPlayer(player);

        if (!player.isValid()) {
            return verifiedArguments.build();
        }

        macroSet = macroSetManager.getMacroSet(player.get());

        if (requireKnownMacroName) {
            VerifiedArgument<Macro> macro = verifyKnownMacroName(macroSet, parsedArguments, quiet);
            verifiedArguments.withKnownMacro(macro);

            if (requireLineNumber && macro.isValid()) {
                VerifiedArgument<Integer> lineNumber = verifyLineNumber(macro.get(), parsedArguments, quiet);
                verifiedArguments.withLineNumber(lineNumber);
            }
        }

        if (requireUnknownMacroName) {
            VerifiedArgument<String> unknownMacroName = verifyUnknownMacroName(macroSet, parsedArguments, quiet);
            verifiedArguments.withUnknownMacroName(unknownMacroName);
        }

        if (requireText) {
            VerifiedArgument<String> text = verifyText(parsedArguments, quiet);
            verifiedArguments.withText(text);
        }

        return verifiedArguments.build();
    }

    private VerifiedArgument<Player> verifyPlayer(CommandSender sender, boolean quiet)
            throws ArgumentsVerificationException {
        if (!(sender instanceof Player)) {
            if (quiet) {
                return VerifiedArgument.invalid();
            } else {
                throw new ArgumentsVerificationException("This command can only be executed by a player.");
            }
        }
        return VerifiedArgument.of((Player) sender);
    }

    private VerifiedArgument<Macro> verifyKnownMacroName(MacroSet macroSet, ParsedArguments parsedArguments,
                                                         boolean quiet) throws ArgumentsVerificationException {
        Optional<String> macroNameOptional = parsedArguments.getKnownMacroName();
        if (!macroNameOptional.isPresent()) {
            if (quiet) {
                return VerifiedArgument.absent();
            } else {
                throw new ArgumentsVerificationException("Macro name required.");
            }
        }
        String macroName = macroNameOptional.get();

        if (!macroSet.containsMacro(macroName)) {
            if (quiet) {
                return VerifiedArgument.invalid();
            } else {
                throw new ArgumentsVerificationException(String.format("Macro \"%s\" not found.", macroName));
            }
        }
        return VerifiedArgument.of(macroSet.getMacro(macroName));
    }

    private VerifiedArgument<Integer> verifyLineNumber(Macro macro, ParsedArguments parsedArguments, boolean quiet)
            throws ArgumentsVerificationException {
        Optional<String> lineNumberOptional = parsedArguments.getLineNumber();
        if (!lineNumberOptional.isPresent()) {
            if (quiet) {
                return VerifiedArgument.absent();
            } else {
                throw new ArgumentsVerificationException("Line number required.");
            }
        }
        String lineNumberString = lineNumberOptional.get();

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(lineNumberString);
        } catch (NumberFormatException e) {
            if (quiet) {
                return VerifiedArgument.invalid();
            } else {
                throw new ArgumentsVerificationException(
                        String.format("Invalid line number: \"%s\".", lineNumberString), e);
            }
        }

        if (lineNumber < 0 || lineNumber >= macro.length()) {
            if (quiet) {
                return VerifiedArgument.invalid();
            } else {
                throw new ArgumentsVerificationException(
                        String.format("Macro \"%s\" does not contain line \"%d\"", macro.getName(), lineNumber));
            }
        }
        return VerifiedArgument.of(lineNumber);
    }

    private VerifiedArgument<String> verifyUnknownMacroName(MacroSet macroSet, ParsedArguments parsedArguments,
                                                            boolean quiet)
            throws ArgumentsVerificationException {
        Optional<String> unknownMacroNameOptional = parsedArguments.getUnknownMacroName();
        if (!unknownMacroNameOptional.isPresent()) {
            if (quiet) {
                return VerifiedArgument.absent();
            } else {
                throw new ArgumentsVerificationException("New macro name required.");
            }
        }
        String unknownMacroName = unknownMacroNameOptional.get();
        if (!unknownMacroName.matches("\\w+")) {
            if (quiet) {
                return VerifiedArgument.invalid();
            } else {
                throw new ArgumentsVerificationException("Macro name must only contain letters, numbers, or underscores.");
            }
        }
        // TODO prevent new macro from beginning with "macro"

        if (macroSet.containsMacro(unknownMacroName)) {
            if (quiet) {
                return VerifiedArgument.invalid();
            } else {
                throw new ArgumentsVerificationException(
                        String.format("Macro \"%s\" already exists.", unknownMacroName));
            }
        }
        return VerifiedArgument.of(unknownMacroName);
    }

    private VerifiedArgument<String> verifyText(ParsedArguments parsedArguments, boolean quiet)
            throws ArgumentsVerificationException {
        Optional<String> textOptional = parsedArguments.getText();
        if (!textOptional.isPresent()) {
            if (quiet) {
                return VerifiedArgument.absent();
            } else {
                throw new ArgumentsVerificationException("Text required.");
            }
        }
        return VerifiedArgument.of(textOptional.get());
    }

    public static final class Builder {
        private MacroSetManager macroSetManager;
        private boolean requireKnownMacroName = false;
        private boolean requireUnknownMacroName = false;
        private boolean requireLineNumber = false;
        private boolean requireText = false;

        private Builder() {
        }

        public Builder withMacroSetManager(MacroSetManager macroSetManager) {
            this.macroSetManager = macroSetManager;
            return this;
        }

        public Builder requireKnownMacroName() {
            this.requireKnownMacroName = true;
            return this;
        }

        public Builder requireUnknownMacroName() {
            this.requireUnknownMacroName = true;
            return this;
        }

        public Builder requireLineNumber() {
            this.requireLineNumber = true;
            return this;
        }

        public Builder requireText() {
            this.requireText = true;
            return this;
        }


        public ArgumentsVerifier build() {
            return new ArgumentsVerifier(this);
        }
    }
}
