package com.gmail.mcdlutze.macros.argument;

import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.macro.MacroSet;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArgumentsVerifierTest {

    private ArgumentsVerifier sut;

    @Mock ParsedArguments parsedArguments;
    @Mock MacroSetManager macroSetManager;
    @Mock CommandSender commandSender;
    @Mock Player player;
    @Mock MacroSet macroSet;
    @Mock Macro macro;

    @Before
    public void setup() {
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
    }

    private void validateMacro() {
        when(parsedArguments.getKnownMacroName()).thenReturn(Optional.of("valid"));
        when(macroSet.containsMacro("valid")).thenReturn(true);
        when(macroSet.getMacro("valid")).thenReturn(macro);
    }

    @Test
    public void successfulValidationTest() throws ArgumentsVerificationException {
        validateMacro();
        when(parsedArguments.getLineNumber()).thenReturn(Optional.of("0"));
        when(parsedArguments.getUnknownMacroName()).thenReturn(Optional.of("unknown"));
        when(parsedArguments.getText()).thenReturn(Optional.of("this is text"));
        when(macro.length()).thenReturn(1);
        when(macroSet.containsMacro("unknown")).thenReturn(false);

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                .requireLineNumber().requireUnknownMacroName().requireText().build();

        VerifiedArguments expected = VerifiedArguments.newBuilder().withKnownMacro(VerifiedArgument.of(macro))
                .withLineNumber(VerifiedArgument.of(0)).withPlayer(VerifiedArgument.of(player))
                .withText(VerifiedArgument.of("this is text")).withUnknownMacroName(VerifiedArgument.of("unknown"))
                .build();
        VerifiedArguments actual = sut.verify(player, parsedArguments);

        assertEquals(expected, actual);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void invalidPlayerLoudTest() throws ArgumentsVerificationException {
        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).build();
        sut.verify(commandSender, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void invalidKnownMacroLoudTest() throws ArgumentsVerificationException {
        when(parsedArguments.getKnownMacroName()).thenReturn(Optional.of("invalid"));
        when(macroSet.containsMacro("invalid")).thenReturn(false);

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName().build();
        sut.verify(player, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void absentKnownMacroLoudTest() throws ArgumentsVerificationException {
        when(parsedArguments.getKnownMacroName()).thenReturn(Optional.empty());

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName().build();
        sut.verify(player, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void invalidLineNumberLoudTest() throws ArgumentsVerificationException {
        validateMacro();
        when(parsedArguments.getLineNumber()).thenReturn(Optional.of("1"));
        when(macro.length()).thenReturn(0);

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                .requireLineNumber().build();
        sut.verify(player, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void nonNumericalLineNumberLoudTest() throws ArgumentsVerificationException {
        validateMacro();
        when(parsedArguments.getLineNumber()).thenReturn(Optional.of("this is not a number"));

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                .requireLineNumber().build();
        sut.verify(player, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void absentLineNumberLoudTest() throws ArgumentsVerificationException {
        validateMacro();
        when(parsedArguments.getLineNumber()).thenReturn(Optional.empty());

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                .requireLineNumber().build();
        sut.verify(player, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void invalidUnknownMacroNameLoudTest() throws ArgumentsVerificationException {
        when(macroSet.containsMacro("invalid")).thenReturn(true);
        when(parsedArguments.getUnknownMacroName()).thenReturn(Optional.of("invalid"));

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireUnknownMacroName().build();
        sut.verify(player, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void nonWordUnknownMacroNameLoudTest() throws ArgumentsVerificationException {
        when(parsedArguments.getUnknownMacroName()).thenReturn(Optional.of("..."));

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireUnknownMacroName().build();
        sut.verify(player, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void absentUnknownMacroNameLoudTest() throws ArgumentsVerificationException {
        when(parsedArguments.getUnknownMacroName()).thenReturn(Optional.empty());

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireUnknownMacroName().build();
        sut.verify(player, parsedArguments);
    }

    @Test(expected = ArgumentsVerificationException.class)
    public void absentTextLoudTest() throws ArgumentsVerificationException {
        when(parsedArguments.getText()).thenReturn(Optional.empty());

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireText().build();
        sut.verify(player, parsedArguments);
    }

    @Test
    public void invalidPlayerQuietTest() {
        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).build();
        VerifiedArgument<Player> player = sut.verifyQuietly(commandSender, parsedArguments).getPlayer();

        assertEquals(VerifiedArgument.invalid(), player);
    }

    @Test
    public void invalidKnownMacroQuietTest() {
        when(parsedArguments.getKnownMacroName()).thenReturn(Optional.of("invalid"));
        when(macroSet.containsMacro("invalid")).thenReturn(false);

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName().build();
        VerifiedArgument<Macro> macro = sut.verifyQuietly(player, parsedArguments).getKnownMacro();

        assertEquals(VerifiedArgument.invalid(), macro);
    }

    @Test
    public void absentKnownMacroQuietTest() {
        when(parsedArguments.getKnownMacroName()).thenReturn(Optional.empty());

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName().build();
        VerifiedArgument<Macro> macro = sut.verifyQuietly(player, parsedArguments).getKnownMacro();

        assertEquals(VerifiedArgument.absent(), macro);
    }

    @Test
    public void invalidLineNumberQuietTest() {
        validateMacro();
        when(parsedArguments.getLineNumber()).thenReturn(Optional.of("1"));
        when(macro.length()).thenReturn(0);

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                .requireLineNumber().build();
        VerifiedArgument<Integer> lineNumber = sut.verifyQuietly(player, parsedArguments).getLineNumber();

        assertEquals(VerifiedArgument.invalid(), lineNumber);
    }

    @Test
    public void nonNumericalLineNumberQuietTest() {
        validateMacro();
        when(parsedArguments.getLineNumber()).thenReturn(Optional.of("this is not a number"));

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                .requireLineNumber().build();
        VerifiedArgument<Integer> lineNumber = sut.verifyQuietly(player, parsedArguments).getLineNumber();

        assertEquals(VerifiedArgument.invalid(), lineNumber);
    }

    @Test
    public void absentLineNumberQuietTest() {
        validateMacro();
        when(parsedArguments.getLineNumber()).thenReturn(Optional.empty());

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireKnownMacroName()
                .requireLineNumber().build();
        VerifiedArgument<Integer> lineNumber = sut.verifyQuietly(player, parsedArguments).getLineNumber();

        assertEquals(VerifiedArgument.absent(), lineNumber);
    }

    @Test
    public void invalidUnknownMacroNameQuietTest() {
        when(macroSet.containsMacro("invalid")).thenReturn(true);
        when(parsedArguments.getUnknownMacroName()).thenReturn(Optional.of("invalid"));

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireUnknownMacroName().build();
        VerifiedArgument<String> unknownMacroName = sut.verifyQuietly(player, parsedArguments).getUnknownMacroName();

        assertEquals(VerifiedArgument.invalid(), unknownMacroName);
    }

    @Test
    public void nonWordUnknownMacroNameQuietTest() throws ArgumentsVerificationException {
        when(parsedArguments.getUnknownMacroName()).thenReturn(Optional.of("..."));

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireUnknownMacroName().build();
        VerifiedArgument<String> unknownMacroName = sut.verifyQuietly(player, parsedArguments).getUnknownMacroName();

        assertEquals(VerifiedArgument.invalid(), unknownMacroName);
    }

    @Test
    public void absentUnknownMacroNameQuietTest() {
        when(parsedArguments.getUnknownMacroName()).thenReturn(Optional.empty());

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireUnknownMacroName().build();
        VerifiedArgument<String> unknownMacroName = sut.verifyQuietly(player, parsedArguments).getUnknownMacroName();

        assertEquals(VerifiedArgument.absent(), unknownMacroName);
    }

    @Test
    public void absentTextQuietTest() {
        when(parsedArguments.getText()).thenReturn(Optional.empty());

        sut = ArgumentsVerifier.newBuilder().withMacroSetManager(macroSetManager).requireText().build();
        VerifiedArgument<String> text = sut.verifyQuietly(player, parsedArguments).getText();

        assertEquals(VerifiedArgument.absent(), text);
    }
}