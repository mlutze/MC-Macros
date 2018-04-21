package com.gmail.mcdlutze.macros.commandexecutor;

import com.gmail.mcdlutze.macros.manager.DictatorManager;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DictateCommandExecutorTest {

    @Mock MacroSetManager macroSetManager;
    @Mock DictatorManager dictatorManager;

    @Mock CommandSender sender;
    @Mock Command command;
    @Mock Player player;
    @Mock MacroSet macroSet;

    private DictateCommandExecutor sut;

    @Before
    public void setup() {
        sut = new DictateCommandExecutor(macroSetManager, dictatorManager);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
    }

    @Test
    public void sendErrorMessageTest() {
        sut.onCommand(sender, command, "label", new String[]{});

        verify(sender).sendMessage("This command can only be executed by a player.");
    }

    @Test
    public void successfulDictateTest() {
        Macro macro = new Macro("myMacro");
        when(macroSet.containsMacro("myMacro")).thenReturn(false);

        String[] args = "myMacro".split(" ");
        sut.onCommand(player, command, "label", args);

        verify(macroSet).putMacro("myMacro", macro);
        verify(dictatorManager).startDictating(player, macro);
        verify(player).sendMessage("Dictating macro \"myMacro\".");
        verify(player).sendMessage("Enter \"//\" to stop dictating.");
    }

    @Test
    public void suggestNothingTest() {
        List<String> expected = Collections.emptyList();

        String[] args = "".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }

}