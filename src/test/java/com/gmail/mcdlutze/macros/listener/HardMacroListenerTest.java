package com.gmail.mcdlutze.macros.listener;

import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.macro.MacroSet;
import com.gmail.mcdlutze.macros.manager.DictatorManager;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HardMacroListenerTest {

    private HardMacroListener sut;

    @Mock MacroSetManager macroSetManager;
    @Mock DictatorManager dictatorManager;
    @Mock Command runCommand;

    @Mock Player player;
    @Mock MacroSet macroSet;
    @Mock Macro macro;

    @Before
    public void setup() {
        sut = new HardMacroListener(macroSetManager, dictatorManager, runCommand);
    }

    @Test
    public void successfulRunTest() {
        when(dictatorManager.isDictating(player)).thenReturn(false);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
        when(macroSet.containsMacro("myMacro")).thenReturn(true);
        when(macroSet.getMacro("myMacro")).thenReturn(macro);
        when(macro.isHard()).thenReturn(true);

        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
                new PlayerCommandPreprocessEvent(player, "/myMacro arg0 arg1", Collections.emptySet());

        sut.onPlayerCommand(playerCommandPreprocessEvent);

        String[] expectedArgs = "myMacro arg0 arg1".split(" ");

        verify(runCommand).execute(player, "macrorunhard", expectedArgs);
    }

    @Test
    public void dictatingTest() {
        when(dictatorManager.isDictating(player)).thenReturn(true);

        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
                new PlayerCommandPreprocessEvent(player, "/myMacro arg0 arg1", Collections.emptySet());

        sut.onPlayerCommand(playerCommandPreprocessEvent);

        verifyZeroInteractions(runCommand);

    }

    @Test
    public void nonMacroCommandTest() {
        when(dictatorManager.isDictating(player)).thenReturn(false);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
        when(macroSet.containsMacro("myMacro")).thenReturn(false);

        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
                new PlayerCommandPreprocessEvent(player, "/myMacro arg0 arg1", Collections.emptySet());

        sut.onPlayerCommand(playerCommandPreprocessEvent);

        verifyZeroInteractions(runCommand);
    }

    @Test
    public void emptyMessageTest() {
        when(dictatorManager.isDictating(player)).thenReturn(false);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);

        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
                new PlayerCommandPreprocessEvent(player, "", Collections.emptySet());

        sut.onPlayerCommand(playerCommandPreprocessEvent);

        verifyZeroInteractions(runCommand);
    }

    @Test
    public void nonHardMacroTest() {
        when(dictatorManager.isDictating(player)).thenReturn(false);
        when(macroSetManager.getMacroSet(player)).thenReturn(macroSet);
        when(macroSet.containsMacro("myMacro")).thenReturn(true);
        when(macroSet.getMacro("myMacro")).thenReturn(macro);
        when(macro.isHard()).thenReturn(false);

        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
                new PlayerCommandPreprocessEvent(player, "/myMacro arg0 arg1", Collections.emptySet());

        sut.onPlayerCommand(playerCommandPreprocessEvent);

        verifyZeroInteractions(runCommand);
    }
}