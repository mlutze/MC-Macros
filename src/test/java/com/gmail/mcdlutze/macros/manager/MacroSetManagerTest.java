package com.gmail.mcdlutze.macros.manager;

import com.gmail.mcdlutze.macros.macro.MacroSet;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MacroSetManagerTest {

    private MacroSetManager sut;

    @Mock Plugin plugin;
    @Mock ConfigurationManager configurationManager;
    @Mock Server server;
    @Mock Player player;
    @Mock MacroSet macroSet;


    @Before
    public void setup() {
        sut = new MacroSetManager(plugin, configurationManager);

        when(plugin.getServer()).thenReturn(server);
        when(configurationManager.getMacroSet(player)).thenReturn(macroSet);
    }

    @Test
    public void getMacroSetTest() {
        sut.putMacroSet(player, macroSet);

        MacroSet expected = macroSet;
        MacroSet actual = sut.getMacroSet(player);

        assertEquals(expected, actual);
    }

    @Test
    public void getAbsentMacroSetTest() {
        MacroSet expected = new MacroSet();
        MacroSet actual = sut.getMacroSet(player);

        assertEquals(expected, actual);
    }

    @Test
    public void loadMacrosTest() {
        sut.loadMacros(player);

        MacroSet expected = macroSet;
        MacroSet actual = sut.getMacroSet(player);

        assertEquals(expected, actual);
    }

    @Test
    public void saveMacrosTest() {
        sut.putMacroSet(player, macroSet);

        sut.saveMacros(player);

        verify(configurationManager).putMacroSet(player, macroSet);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void writeMacrosTest() {
        sut.putMacroSet(player, macroSet);

        when((Collection<Player>) server.getOnlinePlayers()).thenReturn(Collections.singletonList(player));

        sut.writeMacros();

        verify(configurationManager).putMacroSet(player, macroSet);
        verify(configurationManager).saveConfig();
    }
}