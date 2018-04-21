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
public class DeleteCommandExecutorTest {

    @Mock MacroSetManager macroSetManager;

    private DeleteCommandExecutor sut;

    @Mock CommandSender sender;
    @Mock Command command;
    @Mock Player player;
    @Mock MacroSet macroSet;
    @Mock Macro macro;


    @Before
    public void setup() {
        sut = new DeleteCommandExecutor(macroSetManager);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
    }

    @Test
    public void sendErrorMessageTest() {
        sut.onCommand(sender, command, "label", new String[]{});

        verify(sender).sendMessage("This command can only be executed by a player.");
    }

    @Test
    public void successfulDeleteTest() {
        when(macroSet.containsMacro("myMacro")).thenReturn(true);
        when(macroSet.getMacro("myMacro")).thenReturn(macro);
        when(macro.getName()).thenReturn("myMacro");

        String[] args = "myMacro".split(" ");
        sut.onCommand(player, command, "label", args);

        verify(macroSet).removeMacro("myMacro");
        verify(player).sendMessage("Macro \"myMacro\" deleted.");
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

        String[] args = "myMacro text text text".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);
        assertEquals(expected, actual);
    }
}