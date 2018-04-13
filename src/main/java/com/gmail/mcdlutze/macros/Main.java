package com.gmail.mcdlutze.macros;

import com.gmail.mcdlutze.macros.commandexecutor.*;
import com.gmail.mcdlutze.macros.listener.DictationListener;
import com.gmail.mcdlutze.macros.listener.MacroPersistenceListener;
import com.gmail.mcdlutze.macros.manager.ConfigurationManager;
import com.gmail.mcdlutze.macros.manager.DictatorManager;
import com.gmail.mcdlutze.macros.manager.MacroRunnerManager;
import com.gmail.mcdlutze.macros.manager.MacroSetManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    // managers
    private ConfigurationManager configurationManager;
    private MacroSetManager macroSetManager;
    private DictatorManager dictatorManager;
    private MacroRunnerManager macroRunnerManager;

    // listeners
    private DictationListener dictationListener;
    private MacroPersistenceListener macroPersistenceListener;
    // TODO add hard listener

    @Override
    public void onEnable() {
        loadManagers();
        loadListeners();
        loadCommands();
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

    private void loadListeners() {
        macroPersistenceListener = new MacroPersistenceListener(macroSetManager);
        dictationListener = new DictationListener(dictatorManager);

        getServer().getPluginManager().registerEvents(macroPersistenceListener, this);
        getServer().getPluginManager().registerEvents(dictationListener, this);
    }

    private void loadCommands() {
        loadCommand("macroadd", new AddCommandExecutor(macroSetManager));
        loadCommand("macrocopy", new CopyCommandExecutor(macroSetManager));
        loadCommand("macrodelete", new DeleteCommandExecutor(macroSetManager));
        loadCommand("macrodictate", new DictateCommandExecutor(macroSetManager, dictatorManager));
        loadCommand("macroedit", new EditCommandExecutor(macroSetManager));
        loadCommand("macroinsert", new InsertCommandExecutor(macroSetManager));
        loadCommand("macrolist", new ListCommandExecutor(macroSetManager));
        loadCommand("macronew", new NewCommandExecutor(macroSetManager));
        loadCommand("macroremove", new RemoveCommandExecutor(macroSetManager));
        loadCommand("macrorename", new RenameCommandExecutor(macroSetManager));
        loadCommand("macrorun", new RunCommandExecutor(macroSetManager, macroRunnerManager));
        loadCommand("macroview", new ViewCommandExecutor(macroSetManager));
        // TODO add harden & soften
    }

    private <T extends CommandExecutor & TabCompleter> void loadCommand(String name, T commandExecutor) {
        PluginCommand command = getCommand(name);
        command.setExecutor(commandExecutor);
        command.setTabCompleter(commandExecutor);
    }
}
