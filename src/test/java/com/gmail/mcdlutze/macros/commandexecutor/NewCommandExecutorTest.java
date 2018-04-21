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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NewCommandExecutorTest {

    @Mock MacroSetManager macroSetManager;

    @Mock CommandSender sender;
    @Mock Command command;
    @Mock Player player;
    @Mock MacroSet macroSet;

    private NewCommandExecutor sut;

    @Before
    public void setup() {
        sut = new NewCommandExecutor(macroSetManager);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
    }

    @Test
    public void sendErrorMessageTest() {
        sut.onCommand(sender, command, "label", new String[]{});

        verify(sender).sendMessage("This command can only be executed by a player.");
    }

    @Test
    public void successfulNewTest() {
        Macro macro = new Macro("myMacro");
        macro.addLine("text text text");
        when(macroSet.containsMacro("myMacro")).thenReturn(false);

        String[] args = "myMacro text text text".split(" ");
        sut.onCommand(player, command, "label", args);

        verify(macroSet).putMacro("myMacro", macro);
        verify(player).sendMessage("Created macro \"myMacro\".");
    }

    @Test
    public void suggestNothingTest() {
        List<String> expected = Collections.emptyList();

        String[] args = "".split(" ");
        List<String> actual = sut.onTabComplete(player, command, "label", args);

        assertEquals(expected, actual);
    }

}