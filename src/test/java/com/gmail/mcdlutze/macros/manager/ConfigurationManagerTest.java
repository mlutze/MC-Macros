package com.gmail.mcdlutze.macros.manager;

import com.gmail.mcdlutze.macros.macro.Macro;
import com.gmail.mcdlutze.macros.macro.MacroSet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationManagerTest {

    private static final String PLAYER_DATA_KEY = "PLAYER_DATA";
    private static final String MACROS_KEY = "MACROS";
    private static final String MACRO_NAME_KEY = "MACRO_NAME";
    private static final String MACRO_CONTENTS_KEY = "MACRO_CONTENTS";

    private ConfigurationManager sut;

    @Mock Plugin plugin;
    @Mock FileConfiguration fileConfiguration;
    @Mock ConfigurationSection macroListConfigurationSection;
    @Mock ConfigurationSection macroConfigurationSection;
    @Mock Player player;


    private UUID uuid = new UUID(0, 0);
    private String path = String.join(".", PLAYER_DATA_KEY, "00000000-0000-0000-0000-000000000000", MACROS_KEY);

    @Before

    public void setup() {
        sut = new ConfigurationManager(plugin);

        when(plugin.getConfig()).thenReturn(fileConfiguration);

        when(fileConfiguration.createSection(path)).thenReturn(macroListConfigurationSection);
        when(fileConfiguration.getConfigurationSection(path)).thenReturn(macroListConfigurationSection);

        when(macroListConfigurationSection.getKeys(false)).thenReturn(Collections.singleton("macroName"));
        when(macroListConfigurationSection.createSection("macroName")).thenReturn(macroConfigurationSection);
        when(macroListConfigurationSection.getConfigurationSection("macroName")).thenReturn(macroConfigurationSection);

        when(macroConfigurationSection.getString(MACRO_NAME_KEY)).thenReturn("macroName");
        when(macroConfigurationSection.getStringList(MACRO_CONTENTS_KEY)).thenReturn(Collections.singletonList("line"));

        when(player.getUniqueId()).thenReturn(uuid);
    }

    @Test
    public void getMacroSetTest() {
        when(fileConfiguration.isConfigurationSection(path)).thenReturn(true);

        MacroSet expected = new MacroSet();
        expected.putMacro("macroName", new Macro("macroName", Collections.singletonList("line"),false));
        MacroSet actual = sut.getMacroSet(player);

        assertEquals(expected, actual);
    }

    @Test
    public void getMacroSetCreateSectionsTest() {
        when(fileConfiguration.isConfigurationSection(path)).thenReturn(false);

        MacroSet expected = new MacroSet();
        expected.putMacro("macroName", new Macro("macroName", Collections.singletonList("line"), false));
        MacroSet actual = sut.getMacroSet(player);

        assertEquals(expected, actual);
    }

    @Test
    public void putMacroSetTest() {
        when(fileConfiguration.isConfigurationSection(path)).thenReturn(true);

        MacroSet macroSet = new MacroSet();
        macroSet.putMacro("macroName", new Macro("macroName", Collections.singletonList("line"), false));
        sut.putMacroSet(player, macroSet);

        verify(macroConfigurationSection).set(MACRO_NAME_KEY, "macroName");
        verify(macroConfigurationSection).set(MACRO_CONTENTS_KEY, Collections.singletonList("line"));
    }

}