package com.gmail.mcdlutze.macros.argument;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ParsedArguments {
    private final String lineNumber;
    private final String knownMacroName;
    private final String unknownMacroName;
    private final String text;
    private final String[] arguments;

    private ParsedArguments(Builder builder) {
        lineNumber = builder.lineNumber;
        knownMacroName = builder.knownMacroName;
        unknownMacroName = builder.unknownMacroName;
        text = builder.text;
        arguments = builder.arguments;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Optional<String> getLineNumber() {
        return Optional.ofNullable(lineNumber);
    }

    public Optional<String> getKnownMacroName() {
        return Optional.ofNullable(knownMacroName);
    }

    public Optional<String> getUnknownMacroName() {
        return Optional.ofNullable(unknownMacroName);
    }

    public Optional<String> getText() {
        return Optional.ofNullable(text);
    }

    public Optional<String[]> getArguments() {
        return Optional.ofNullable(arguments);
    }

    public static final class Builder {
        private String lineNumber;
        private String knownMacroName;
        private String unknownMacroName;
        private String text;
        private String[] arguments;

        private Builder() {
        }

        public Builder withLineNumber(String lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder withKnownMacroName(String knownMacroName) {
            this.knownMacroName = knownMacroName;
            return this;
        }

        public Builder withUnknownMacroName(String unknownMacroName) {
            this.unknownMacroName = unknownMacroName;
            return this;
        }

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public Builder withArguments(String[] arguments) {
            this.arguments = arguments;
            return this;
        }

        public ParsedArguments build() {
            return new ParsedArguments(this);
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
        ParsedArguments that = (ParsedArguments) o;
        return Objects.equals(lineNumber, that.lineNumber) &&
                Objects.equals(knownMacroName, that.knownMacroName) &&
                Objects.equals(unknownMacroName, that.unknownMacroName) &&
                Objects.equals(text, that.text) &&
                Arrays.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, knownMacroName, unknownMacroName, text, arguments);
    }
}
