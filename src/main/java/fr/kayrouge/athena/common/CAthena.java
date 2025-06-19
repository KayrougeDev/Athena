package fr.kayrouge.athena.common;

import fr.kayrouge.athena.common.event.CFurnaceEvent;
import fr.kayrouge.athena.common.util.CPlatform;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class CAthena extends JavaPlugin {

    protected final Logger LOGGER = getLogger();

    @Override
    public void onEnable() {
        super.onEnable();
        LOGGER.info("Starting Athena on "+getPlatform());
        registerEvents();
    }

    protected void registerEvents() {
        getServer().getPluginManager().registerEvents(new CFurnaceEvent(), this);
    }


    public abstract CPlatform getPlatform();
}
