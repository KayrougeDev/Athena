package fr.kayrouge.athena.common;

import fr.kayrouge.athena.common.artifact.Artifacts;
import fr.kayrouge.athena.common.event.CFurnaceEvent;
import fr.kayrouge.athena.common.event.CMiscEvents;
import fr.kayrouge.athena.common.util.CPlatform;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class CAthena extends JavaPlugin {

    protected final Logger LOGGER = getLogger();

    @Override
    public void onEnable() {
        super.onEnable();
        LOGGER.info("Starting Athena on "+getPlatform());
        registerEvents();

        Artifacts.init();
    }

    protected void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CFurnaceEvent(), this);
        pm.registerEvents(new CMiscEvents(), this);
    }


    public abstract CPlatform getPlatform();
}
