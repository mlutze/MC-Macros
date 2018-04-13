package com.gmail.mcdlutze.macros.commandexecutor;

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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListCommandExecutorTest {


    @Mock MacroSetManager macroSetManager;

    @Mock CommandSender sender;
    @Mock Command command;
    @Mock Player player;
    @Mock MacroSet macroSet;

    private ListCommandExecutor sut;

    @Before
    public void setup() {
        sut = new ListCommandExecutor(macroSetManager);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
    }

    @Test
    public void sendErrorMessageTest() {
        sut.onCommand(sender, command, "label", new String[]{});

        verify(sender).sendMessage("This command can only be executed by a player.");
    }

    @Test
    public void noMacrosTest() {
        when(macroSet.names()).thenReturn(Collections.emptySet());

        sut.onCommand(player, command, "label", new String[]{});

        verify(player).sendMessage("You do not have any macros.");
    }

    @Test
    public void successfulListTest() {
        when(macroSet.names()).thenReturn(Collections.singleton("macroName"));

        sut.onCommand(player, command, "label", new String[]{});

        verify(player).sendMessage(new String[]{"macroName"});
    }

    @Test
    public void suggestNothingTest() {
        List<String> expected = Collections.emptyList();

        String[] args = "".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }
}
