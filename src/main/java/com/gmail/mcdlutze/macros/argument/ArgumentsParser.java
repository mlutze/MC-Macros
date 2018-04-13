package com.gmail.mcdlutze.macros.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.gmail.mcdlutze.macros.argument.ArgumentType.*;

public class ArgumentsParser {

    private final List<ArgumentType> argumentTypes;

    private ArgumentsParser(Builder builder) {
        argumentTypes = Collections.unmodifiableList(builder.argumentTypes);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public ParsedArguments parse(String[] args) {
        ParsedArguments.Builder parsedArgs = ParsedArguments.newBuilder();

        for (int i = 0; i < argumentTypes.size() && i < args.length; i++) {
            ArgumentType argumentType = argumentTypes.get(i);
            String arg = args[i];
            switch (argumentType) {
                case KNOWN_MACRO_NAME:
                    parsedArgs.withKnownMacroName(arg);
                    break;
                case UNKNOWN_MACRO_NAME:
                    parsedArgs.withUnknownMacroName(arg);
                    break;
                case MACRO_LINE_NUMBER:
                    parsedArgs.withLineNumber(arg);
                    break;
                case TEXT:
                    parsedArgs.withText(Arrays.stream(args).skip(i).collect(Collectors.joining(" ")));
                    break;
                default:
                    break;
            }
        }
        return parsedArgs.build();
    }

    public static final class Builder {
        private List<ArgumentType> argumentTypes = new ArrayList<>();

        private Builder() {
        }

        public Builder withKnownMacroName() {
            argumentTypes.add(ArgumentType.KNOWN_MACRO_NAME);
            return this;
        }

        public Builder withUnknownMacroName() {
            argumentTypes.add(UNKNOWN_MACRO_NAME);
            return this;
        }

        public Builder withMacroLineNumber() {
            argumentTypes.add(MACRO_LINE_NUMBER);
            return this;
        }

        public Builder withText() {
            argumentTypes.add(TEXT);
            return this;
        }

        public ArgumentsParser build() {
            return new ArgumentsParser(this);
        }
    }
}
