package com.gmail.mcdlutze.macros;

import com.gmail.mcdlutze.macros.commandexecutor.*;
import com.gmail.mcdlutze.macros.listener.DictationListener;
import com.gmail.mcdlutze.macros.listener.HardMacroListener;
import com.gmail.mcdlutze.macros.listener.MacroPersistenceListener;
import com.gmail.mcdlutze.macros.manager.ConfigurationManager;
import com.gmail.mcdlutze.macros.manager.DictatorManager;
import com.gmail.mcdlutze.macros.manager.MacroRunnerManager;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

// STOPSHIP: 2018-04-26 update README
public class Main extends JavaPlugin {

    // managers
    private ConfigurationManager configurationManager;
    private MacroSetManager macroSetManager;
    private DictatorManager dictatorManager;
    private MacroRunnerManager macroRunnerManager;

    // listeners
    private DictationListener dictationListener;
    private MacroPersistenceListener macroPersistenceListener;
    private HardMacroListener hardMacroListener;

    @Override
    public void onEnable() {
        loadManagers();
        loadCommands();
        loadListeners();
    }

    @Override
    public void onDisable() {
        macroSetManager.writeMacros();
    }

    private void loadManagers() {
        configurationManager = new ConfigurationManager(this);
        macroSetManager = new MacroSetManager(this, configurationManager);
        dictatorManager = new DictatorManager();
        macroRunnerManager = new MacroRunnerManager();
    }

    private void loadCommands() {
        loadCommand("macroadd", new AddCommandExecutor(macroSetManager));
        loadCommand("macrocopy", new CopyCommandExecutor(macroSetManager));
        loadCommand("macrodelete", new DeleteCommandExecutor(macroSetManager));
        loadCommand("macrodictate", new DictateCommandExecutor(macroSetManager, dictatorManager));
        loadCommand("macroedit", new EditCommandExecutor(macroSetManager));
        loadCommand("macroharden", new HardenCommandExecutor(macroSetManager));
        loadCommand("macroinsert", new InsertCommandExecutor(macroSetManager));
        loadCommand("macrolist", new ListCommandExecutor(macroSetManager));
        loadCommand("macronew", new NewCommandExecutor(macroSetManager));
        loadCommand("macroremove", new RemoveCommandExecutor(macroSetManager));
        loadCommand("macrorename", new RenameCommandExecutor(macroSetManager));
        loadCommand("macrosoften", new SoftenCommandExecutor(macroSetManager));
        loadCommand("macrorun", new RunCommandExecutor(macroSetManager, macroRunnerManager));
        loadCommand("macroview", new ViewCommandExecutor(macroSetManager));
    }

    private void loadListeners() {
        macroPersistenceListener = new MacroPersistenceListener(macroSetManager);
        dictationListener = new DictationListener(dictatorManager);
        hardMacroListener = new HardMacroListener(macroSetManager, dictatorManager, getCommand("macrorun"));

        getServer().getPluginManager().registerEvents(macroPersistenceListener, this);
        getServer().getPluginManager().registerEvents(dictationListener, this);
        getServer().getPluginManager().registerEvents(hardMacroListener, this);
    }

    private <T extends CommandExecutor & TabCompleter> void loadCommand(String name, T commandExecutor) {
        PluginCommand command = getCommand(name);
        command.setExecutor(commandExecutor);
        command.setTabCompleter(commandExecutor);
    }
}
