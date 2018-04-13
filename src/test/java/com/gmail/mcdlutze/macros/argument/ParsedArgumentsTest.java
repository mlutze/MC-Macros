package com.gmail.mcdlutze.macros.argument;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class ParsedArgumentsTest {

    private ParsedArguments sut;

    @Test
    public void buildAndGetTest() {
        sut = ParsedArguments.newBuilder().withKnownMacroName("knownMacroName").withLineNumber("1")
                .withUnknownMacroName("unknownMacroName").withText("this is text").build();
        assertEquals(Optional.of("knownMacroName"), sut.getKnownMacroName());
        assertEquals(Optional.of("1"), sut.getLineNumber());
        assertEquals(Optional.of("unknownMacroName"), sut.getUnknownMacroName());
        assertEquals(Optional.of("this is text"), sut.getText());
    }

}