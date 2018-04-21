package com.gmail.mcdlutze.macros.commandexecutor;

import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.macro.MacroSet;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditCommandExecutorTest {

    @Mock MacroSetManager macroSetManager;

    @Mock CommandSender sender;
    @Mock Command command;
    @Mock Player player;
    @Mock MacroSet macroSet;
    @Mock Macro macro;

    private EditCommandExecutor sut;

    @Before
    public void setup() {
        sut = new EditCommandExecutor(macroSetManager);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
    }

    @Test
    public void sendErrorMessageTest() {
        sut.onCommand(sender, command, "label", new String[]{});

        verify(sender).sendMessage("This command can only be executed by a player.");
    }

    @Test
    public void successfulEditTest() {
        when(macroSet.containsMacro("myMacro")).thenReturn(true);
        when(macroSet.getMacro("myMacro")).thenReturn(macro);
        when(macro.length()).thenReturn(1);
        when(macro.getName()).thenReturn("myMacro");

        String[] args = "myMacro 0 text text text".split(" ");
        sut.onCommand(player, command, "label", args);

        verify(macro).editLine(0, "text text text");
        verify(player).sendMessage("Line 0 edited in macro \"myMacro\".");
    }

    @Test
    public void suggestMacrosTest() {
        List<String> expected = Collections.singletonList("macro0");
        when(macroSet.names()).thenReturn(new HashSet<>(expected));

        String[] args = "m".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }

    @Test
    public void suggestLinesTest() {
        List<String> expected = Arrays.asList("1", "10");
        when(macroSet.containsMacro("myMacro")).thenReturn(true);
        when(macroSet.getMacro("myMacro")).thenReturn(macro);
        when(macro.length()).thenReturn(11);

        String[] args = "myMacro 1".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }

    @Test
    public void autoFillLineTest() {
        List<String> expected = Collections.singletonList("previously written");
        when(macroSet.containsMacro("myMacro")).thenReturn(true);
        when(macroSet.getMacro("myMacro")).thenReturn(macro);
        when(macro.length()).thenReturn(1);
        when(macro.getLine(0)).thenReturn("previously written");

        String[] args = "myMacro 0 t".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }

    @Test
    public void suggestNothingToNonPlayerTest() {
        List<String> expected = Collections.emptyList();

        String[] args = "m".split(" ");
        List<String> actual = sut.onTabComplete(sender, command, "label", args);

        assertEquals(expected, actual);
    }

    @Test
    public void suggestNothingToInvalidMacroTest() {
        List<String> expected = Collections.emptyList();
        when(macroSet.containsMacro("myMacro")).thenReturn(false);

        String[] args = "myMacro 0".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }

    @Test
    public void suggestNothingToInvalidLineNumberTest() {
        List<String> expected = Collections.emptyList();
        when(macroSet.containsMacro("myMacro")).thenReturn(true);
        when(macroSet.getMacro("myMacro")).thenReturn(macro);

        String[] args = "myMacro invalid t".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }

    @Test
    public void suggestNothingToExtraArgumentsTest() {
        List<String> expected = Collections.emptyList();
        when(macroSet.containsMacro("myMacro")).thenReturn(true);
        when(macroSet.getMacro("myMacro")).thenReturn(macro);
        when(macro.length()).thenReturn(1);

        String[] args = "myMacro 0 text text".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }
}