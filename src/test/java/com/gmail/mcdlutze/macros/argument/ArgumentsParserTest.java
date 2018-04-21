package com.gmail.mcdlutze.macros.argument;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArgumentsParserTest {

    private ArgumentsParser sut;

    @Test
    public void allOptionsTest() {
        sut = ArgumentsParser.newBuilder().withKnownMacroName().withMacroLineNumber().withUnknownMacroName().withText()
                .build();
        String[] args = "knownMacroName 1 unknownMacroName this is text".split(" ");
        ParsedArguments expected = ParsedArguments.newBuilder().withKnownMacroName("knownMacroName").withLineNumber("1")
                .withUnknownMacroName("unknownMacroName").withText("this is text").build();
        ParsedArguments actual = sut.parse(args);


        assertEquals(expected, actual);
    }

    @Test
    public void combineQuotedArgumentsTest() {
        sut = ArgumentsParser.newBuilder().withArguments().build();
        String[] args = "\"arg one\" \"arg t\"wo with a thing in the middle\" arg3 \"\" \"\"quoted arg five\"\"".split(" ");
        String[] expectedArgs = {"arg one", "arg t\"wo with a thing in the middle", "arg3", "", "\"quoted arg five\""};
        ParsedArguments expected = ParsedArguments.newBuilder().withArguments(expectedArgs).build();
        ParsedArguments actual = sut.parse(args);

        assertEquals(expected, actual);
    }

}