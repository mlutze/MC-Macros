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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CopyCommandExecutorTest {

    @Mock MacroSetManager macroSetManager;

    private CopyCommandExecutor sut;

    @Mock CommandSender sender;
    @Mock Command command;
    @Mock Player player;
    @Mock MacroSet macroSet;
    @Mock Macro oldMacro;
    @Mock Macro newMacro;


    @Before
    public void setup() {
        sut = new CopyCommandExecutor(macroSetManager);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
    }

    @Test
    public void sendErrorMessageTest() {
        sut.onCommand(sender, command, "label", new String[]{});

        verify(sender).sendMessage("This command can only be executed by a player.");
    }

    @Test
    public void successfulCopyTest() {
        when(macroSet.containsMacro("oldMacro")).thenReturn(true);
        when(macroSet.getMacro("oldMacro")).thenReturn(oldMacro);
        when(oldMacro.copy()).thenReturn(newMacro);
        when(oldMacro.getName()).thenReturn("oldMacro");

        String[] args = "oldMacro newMacro".split(" ");
        sut.onCommand(player, command, "label", args);

        verify(newMacro).rename("newMacro");
        verify(macroSet).putMacro("newMacro", newMacro);
        verify(player).sendMessage("Macro \"oldMacro\" copied to new macro \"newMacro\".");
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
    public void suggestNothingToNonPlayerTest() {
        List<String> expected = Collections.emptyList();

        String[] args = "m".split(" ");
        List<String> actual = sut.onTabComplete(sender, command, "label", args);

        assertEquals(expected, actual);
    }

    @Test
    public void suggestNothingToExtraArgumentsTest() {
        List<String> expected = Collections.emptyList();

        String[] args = "macroName text text text".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);
        assertEquals(expected, actual);
    }
}