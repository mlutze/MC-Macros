package com.gmail.mcdlutze.macros.argument;

import com.gmail.mcdlutze.macros.macro.Macro;
import org.bukkit.entity.Player;

import java.util.Objects;

public class VerifiedArguments {
    private final VerifiedArgument<Player> player;
    private final VerifiedArgument<Macro> knownMacro;
    private final VerifiedArgument<Integer> lineNumber;
    private final VerifiedArgument<String> unknownMacroName;
    private final VerifiedArgument<String> text;
    private final VerifiedArgument<String[]> arguments;

    private VerifiedArguments(Builder builder) {
        player = builder.player;
        knownMacro = builder.knownMacro;
        lineNumber = builder.lineNumber;
        unknownMacroName = builder.unknownMacroName;
        text = builder.text;
        arguments = builder.arguments;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public VerifiedArgument<Player> getPlayer() {
        return player;
    }

    public VerifiedArgument<Macro> getKnownMacro() {
        return knownMacro;
    }

    public VerifiedArgument<Integer> getLineNumber() {
        return lineNumber;
    }

    public VerifiedArgument<String> getUnknownMacroName() {
        return unknownMacroName;
    }

    public VerifiedArgument<String> getText() {
        return text;
    }

    public VerifiedArgument<String[]> getArguments() {
        return arguments;
    }

    public static final class Builder {
        private VerifiedArgument<Player> player = VerifiedArgument.absent();
        private VerifiedArgument<Macro> knownMacro = VerifiedArgument.absent();
        private VerifiedArgument<Integer> lineNumber = VerifiedArgument.absent();
        private VerifiedArgument<String> unknownMacroName = VerifiedArgument.absent();
        private VerifiedArgument<String> text = VerifiedArgument.absent();
        private VerifiedArgument<String[]> arguments = VerifiedArgument.absent();

        private Builder() {
        }

        public Builder withPlayer(VerifiedArgument<Player> player) {
            this.player = player;
            return this;
        }

        public Builder withKnownMacro(VerifiedArgument<Macro> knownMacro) {
            this.knownMacro = knownMacro;
            return this;
        }

        public Builder withLineNumber(VerifiedArgument<Integer> lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder withUnknownMacroName(VerifiedArgument<String> unknownMacroName) {
            this.unknownMacroName = unknownMacroName;
            return this;
        }

        public Builder withText(VerifiedArgument<String> text) {
            this.text = text;
            return this;
        }

        public Builder withArguments(VerifiedArgument<String[]> arguments) {
            this.arguments = arguments;
            return this;
        }

        public VerifiedArguments build() {
            return new VerifiedArguments(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VerifiedArguments that = (VerifiedArguments) o;
        return Objects.equals(player, that.player) &&
                Objects.equals(knownMacro, that.knownMacro) &&
                Objects.equals(lineNumber, that.lineNumber) &&
                Objects.equals(unknownMacroName, that.unknownMacroName) &&
                Objects.equals(text, that.text) &&
                Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, knownMacro, lineNumber, unknownMacroName, text, arguments);
    }
}
