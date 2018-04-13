package com.gmail.mcdlutze.macros.argument;

import com.gmail.mcdlutze.macros.macro.Macro;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

public class VerifiedArgumentsTest {

    private VerifiedArguments sut;

    @Mock Macro macro;
    @Mock Player player;

    @Test
    public void buildAndGetTest() {
        sut = VerifiedArguments.newBuilder().withKnownMacro(VerifiedArgument.of(macro))
                .withLineNumber(VerifiedArgument.of(1)).withPlayer(VerifiedArgument.of(player))
                .withUnknownMacroName(VerifiedArgument.of("unknownMacroName"))
                .withText(VerifiedArgument.of("this is text")).build();

        assertEquals(VerifiedArgument.of(macro), sut.getKnownMacro());
        assertEquals(VerifiedArgument.of(1), sut.getLineNumber());
        assertEquals(VerifiedArgument.of(player), sut.getPlayer());
        assertEquals(VerifiedArgument.of("unknownMacroName"), sut.getUnknownMacroName());
        assertEquals(VerifiedArgument.of("this is text"), sut.getText());
    }

    @Test
    public void defaultAbsentTest() {
        sut = VerifiedArguments.newBuilder().build();

        assertEquals(VerifiedArgument.absent(), sut.getText());
        assertEquals(VerifiedArgument.absent(), sut.getUnknownMacroName());
        assertEquals(VerifiedArgument.absent(), sut.getPlayer());
        assertEquals(VerifiedArgument.absent(), sut.getLineNumber());
        assertEquals(VerifiedArgument.absent(), sut.getKnownMacro());
    }

}