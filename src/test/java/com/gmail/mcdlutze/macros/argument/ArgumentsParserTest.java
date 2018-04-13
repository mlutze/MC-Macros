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

}