package com.gmail.mcdlutze.macros.listener;

import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.manager.DictatorManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DictationListenerTest {

    private DictationListener sut;

    @Mock DictatorManager dictatorManager;

    @Mock Player player;
    @Mock Macro macro;

    @Before
    public void setup() {
        sut = new DictationListener(dictatorManager);
        when(dictatorManager.getMacroForPlayer(player)).thenReturn(macro);
        when(macro.getName()).thenReturn("myMacro");
    }

    @Test
    public void ignoreNonDictatorChatTest() {
        AsyncPlayerChatEvent asyncPlayerChatEvent =
                new AsyncPlayerChatEvent(true, player, "message", Collections.emptySet());

        when(dictatorManager.isDictating(player)).thenReturn(false);
        sut.onPlayerChat(asyncPlayerChatEvent);

        assertFalse(asyncPlayerChatEvent.isCancelled());
        verifyZeroInteractions(macro);
        verifyZeroInteractions(player);
    }

    @Test
    public void ignoreNonDictatorCommandTest() {
        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
                new PlayerCommandPreprocessEvent(player, "message", Collections.emptySet());

        when(dictatorManager.isDictating(player)).thenReturn(false);
        sut.onPlayerCommand(playerCommandPreprocessEvent);

        assertFalse(playerCommandPreprocessEvent.isCancelled());
        verifyZeroInteractions(macro);
        verifyZeroInteractions(player);
    }

    @Test
    public void stopDictatingOnSlashSlashTest() {
        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
                new PlayerCommandPreprocessEvent(player, "//", Collections.emptySet());

        when(dictatorManager.isDictating(player)).thenReturn(true);
        sut.onPlayerCommand(playerCommandPreprocessEvent);

        assertTrue(playerCommandPreprocessEvent.isCancelled());
        verify(dictatorManager).stopDictating(player);
        verifyZeroInteractions(macro);
        verifyZeroInteractions(player);
    }

    @Test
    public void addDictatorChatTest() {
        AsyncPlayerChatEvent asyncPlayerChatEvent =
                new AsyncPlayerChatEvent(true, player, "message", Collections.emptySet());

        when(dictatorManager.isDictating(player)).thenReturn(true);
        sut.onPlayerChat(asyncPlayerChatEvent);

        assertTrue(asyncPlayerChatEvent.isCancelled());
        verify(macro).addLine("message");
        verify(player).sendMessage("Line added to macro \"myMacro\".");
    }

    @Test
    public void addDictatorCommandTest() {
        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
                new PlayerCommandPreprocessEvent(player, "message", Collections.emptySet());

        when(dictatorManager.isDictating(player)).thenReturn(true);
        sut.onPlayerCommand(playerCommandPreprocessEvent);

        assertTrue(playerCommandPreprocessEvent.isCancelled());
        verify(macro).addLine("message");
        verify(player).sendMessage("Line added to macro \"myMacro\".");
    }

    @Test
    public void stopDictatingOnQuitTest() {
        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(player, "message");

        sut.onPlayerQuit(playerQuitEvent);

        verify(dictatorManager).stopDictating(player);
    }
}