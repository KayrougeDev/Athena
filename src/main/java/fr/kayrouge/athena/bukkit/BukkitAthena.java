package fr.kayrouge.athena.bukkit;

import fr.kayrouge.athena.bukkit.command.BukkitAthenaCommand;
import fr.kayrouge.athena.bukkit.command.BukkitFurnacesCommand;
import fr.kayrouge.athena.common.CAthena;
import fr.kayrouge.athena.common.event.CFurnaceEvent;
import fr.kayrouge.athena.common.util.CPlatform;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class BukkitAthena extends CAthena {

    public static Logger LOGGER;
    public static BukkitAthena INSTANCE;

    public BukkitAthena() {
        INSTANCE = this;
        LOGGER = this.getLogger();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        LOGGER.info("COUCOU");
        registerCommands();
    }

    @Override
    public void onDisable() {

    }

    @Override
    protected void registerEvents() {
        super.registerEvents();
        PluginManager pm = getServer().getPluginManager();
    }

    private void registerCommands() {
        registerCommand("furnaces", new BukkitFurnacesCommand());
        registerCommand("athena", new BukkitAthenaCommand());
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if(command == null) {
            LOGGER.log(Level.WARNING,"Cannot register command '"+name+"' (not registered in plugin.yml)");
            return;
        }
        command.setExecutor(executor);
        if(executor instanceof Listener) getServer().getPluginManager().registerEvents((Listener)executor, this);
        if(executor instanceof TabCompleter) command.setTabCompleter((TabCompleter)executor);
    }

    @Override
    public CPlatform getPlatform() {
        return CPlatform.BUKKIT;
    }
}
